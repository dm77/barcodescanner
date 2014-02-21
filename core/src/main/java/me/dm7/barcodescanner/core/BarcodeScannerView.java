package me.dm7.barcodescanner.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.FrameLayout;

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
        addView(mPreview);
        addView(mViewFinderView);
    }

    public void startCamera() {
        mCamera = CameraUtils.getCameraInstance();
        if(mCamera != null) {
            mViewFinderView.setupViewFinder();
            mPreview.setCamera(mCamera, this);
            mPreview.initCameraPreview();
        }
    }

    public void stopCamera() {
        if(mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
    }

    public synchronized Rect getFramingRectInPreview(int width, int height) {
        if (mFramingRectInPreview == null) {
            Rect framingRect = mViewFinderView.getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point screenResolution = DisplayUtils.getScreenResolution(getContext());
            Point cameraResolution = new Point(width, height);

            if (cameraResolution == null || screenResolution == null) {
                // Called early, before init even finished
                return null;
            }

            rect.left = rect.left * cameraResolution.x / screenResolution.x;
            rect.right = rect.right * cameraResolution.x / screenResolution.x;
            rect.top = rect.top * cameraResolution.y / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;

            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }
}
