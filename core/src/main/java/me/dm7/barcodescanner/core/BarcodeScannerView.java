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
    private ViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private ViewFinder.FramingRectConfiguration mFramingRectConfiguration;

    public BarcodeScannerView(Context context) {
        super(context);
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public final void setupLayout(Camera camera) {
        removeAllViews();

        mPreview = new CameraPreview(getContext(), camera, this);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.setBackgroundColor(Color.BLACK);
        relativeLayout.addView(mPreview);
        addView(relativeLayout);

        mViewFinderView = createViewFinderView(getContext());
        addView(mViewFinderView);
    }

    /**
     * Set a custom configuration for the framing rectangle
     *
     * @param framingRectConfiguration the configuration for the framing rectangle
     */
    public void setFramingRectConfiguration(ViewFinder.FramingRectConfiguration framingRectConfiguration){
        this.mFramingRectConfiguration = framingRectConfiguration;
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected ViewFinder createViewFinderView(Context context) {
        ViewFinder viewFinder = new ViewFinderView(context);
        if(mFramingRectConfiguration != null){
            viewFinder.setFramingRectConfiguration(mFramingRectConfiguration);
        }
        return viewFinder;
    }

    public void startCamera(int cameraId) {
        if(mCameraHandlerThread == null) {
            mCameraHandlerThread = new CameraHandlerThread(this);
        }
        mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(Camera camera) {
        mCamera = camera;
        if(mCamera != null) {
            setupLayout(mCamera);
            mViewFinderView.setupViewFinder();
            if(mFlashState != null) {
                setFlash(mFlashState);
            }
            setAutoFocus(mAutofocusState);
        }
    }

    public void startCamera() {
        startCamera(-1);
    }

    public void stopCamera() {
        if(mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
        if(mCameraHandlerThread != null) {
            mCameraHandlerThread.quit();
            mCameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if(mPreview != null) {
            mPreview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if(mPreview != null) {
            mPreview.showCameraPreview();
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
        mFlashState = flag;
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
        mAutofocusState = state;
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }
}
