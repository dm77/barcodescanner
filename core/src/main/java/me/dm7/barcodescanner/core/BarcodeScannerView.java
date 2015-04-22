package me.dm7.barcodescanner.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback  {
    private Camera mCamera;
    private CameraPreview mPreview;
    private ViewFinderView mViewFinderView;
    private Rect mFramingRectInPreview;

    public BarcodeScannerView(Context context) {
        super(context);
        setupLayout();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupLayout();
    }

    public void setupLayout() {
        mPreview = new CameraPreview(getContext());
        mViewFinderView = new ViewFinderView(getContext());

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.setBackgroundColor(Color.BLACK);
        relativeLayout.addView(mPreview);
        addView(relativeLayout);

        addView(mViewFinderView);
    }

    public void startCamera(int cameraId) {
        startCamera(CameraUtils.getCameraInstance(cameraId));
    }

    public void startCamera(Camera camera) {
        mCamera = camera;
        if(mCamera != null) {
            mViewFinderView.setupViewFinder();
            mPreview.setCamera(mCamera, this);
            mPreview.initCameraPreview();
        }
    }

    public void startCamera() {
        startCamera(CameraUtils.getCameraInstance());
    }

    public void stopCamera() {
        if(mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (mFramingRectInPreview == null) {
            Rect framingRect = mViewFinderView.getFramingRect();
            int viewFinderViewWidth = mViewFinderView.getWidth();
            int viewFinderViewHeight = mViewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);
            rect.left = rect.left * previewWidth / viewFinderViewWidth;
            rect.right = rect.right * previewWidth / viewFinderViewWidth;
            rect.top = rect.top * previewHeight / viewFinderViewHeight;
            rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;

            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }

    public void setFlash(boolean flag) {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {

            Camera.Parameters parameters = mCamera.getParameters();
            if(flag) {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
        }
    }

    public boolean getFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }
}
