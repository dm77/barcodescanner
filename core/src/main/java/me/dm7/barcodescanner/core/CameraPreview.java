package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";

    private Camera mCamera;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
    private boolean mAutoFocus = true;
    private boolean mSurfaceCreated = false;
    private Camera.PreviewCallback mPreviewCallback;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCamera(Camera camera, Camera.PreviewCallback previewCallback) {
        mCamera = camera;
        mPreviewCallback = previewCallback;
        mAutoFocusHandler = new Handler();
    }

    public void initCameraPreview() {
        if(mCamera != null) {
            getHolder().addCallback(this);
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            if(mPreviewing) {
                requestLayout();
            } else {
                showCameraPreview();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if(surfaceHolder.getSurface() == null) {
            return;
        }
        stopCameraPreview();
        showCameraPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = false;
        stopCameraPreview();
    }

    public void showCameraPreview() {
        if(mCamera != null) {
            try {
                mPreviewing = true;
                setupCameraParameters();
                mCamera.setPreviewDisplay(getHolder());
                mCamera.setDisplayOrientation(getDisplayOrientation());
                mCamera.setOneShotPreviewCallback(mPreviewCallback);
                mCamera.startPreview();
                if(mAutoFocus) {
                    mCamera.autoFocus(autoFocusCB);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void stopCameraPreview() {
        if(mCamera != null) {
            try {
                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
            } catch(Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void setupCameraParameters() {
        Camera.Size optimalSize = getOptimalPreviewSize();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        mCamera.setParameters(parameters);
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private Camera.Size getOptimalPreviewSize() {
        if(mCamera == null) {
            return null;
        }

        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        Point screenResolution = DisplayUtils.getScreenResolution(getContext());
        int w = screenResolution.x;
        int h = screenResolution.y;
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            w = screenResolution.y;
            h = screenResolution.x;
        }


        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void setAutoFocus(boolean state) {
        if(mCamera != null && mPreviewing) {
            if(state == mAutoFocus) {
                return;
            }
            mAutoFocus = state;
            if(mAutoFocus) {
                Log.v(TAG, "Starting autofocus");
                mCamera.autoFocus(autoFocusCB);
            } else {
                Log.v(TAG, "Cancelling autofocus");
                mCamera.cancelAutoFocus();
            }
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(mCamera != null && mPreviewing && mAutoFocus && mSurfaceCreated) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}