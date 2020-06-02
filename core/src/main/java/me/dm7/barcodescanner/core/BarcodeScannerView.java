package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback  {

    private CameraWrapper mCameraWrapper;
    private CameraPreview mPreview;
    private IViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private boolean mShouldScaleToFill = true;

    private boolean mIsLaserEnabled = true;
    @ColorInt private int mLaserColor = getResources().getColor(R.color.viewfinder_laser);
    @ColorInt private int mBorderColor = getResources().getColor(R.color.viewfinder_border);
    private int mMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private int mBorderWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private int mBorderLength = getResources().getInteger(R.integer.viewfinder_border_length);
    private boolean mRoundedCorner = false;
    private int mCornerRadius = 0;
    private boolean mSquaredFinder = false;
    private float mBorderAlpha = 1.0f;
    private int mViewFinderOffset = 0;
    private float mAspectTolerance = 0.1f;

    public BarcodeScannerView(Context context) {
        super(context);
        init();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BarcodeScannerView,
                0, 0);

        try {
            setShouldScaleToFill(a.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true));
            mIsLaserEnabled = a.getBoolean(R.styleable.BarcodeScannerView_laserEnabled, mIsLaserEnabled);
            mLaserColor = a.getColor(R.styleable.BarcodeScannerView_laserColor, mLaserColor);
            mBorderColor = a.getColor(R.styleable.BarcodeScannerView_borderColor, mBorderColor);
            mMaskColor = a.getColor(R.styleable.BarcodeScannerView_maskColor, mMaskColor);
            mBorderWidth = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderWidth, mBorderWidth);
            mBorderLength = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderLength, mBorderLength);

            mRoundedCorner = a.getBoolean(R.styleable.BarcodeScannerView_roundedCorner, mRoundedCorner);
            mCornerRadius = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_cornerRadius, mCornerRadius);
            mSquaredFinder = a.getBoolean(R.styleable.BarcodeScannerView_squaredFinder, mSquaredFinder);
            mBorderAlpha = a.getFloat(R.styleable.BarcodeScannerView_borderAlpha, mBorderAlpha);
            mViewFinderOffset = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_finderOffset, mViewFinderOffset);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mViewFinderView = createViewFinderView(getContext());
    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        removeAllViews();

        mPreview = new CameraPreview(getContext(), cameraWrapper, this);
        mPreview.setAspectTolerance(mAspectTolerance);
        mPreview.setShouldScaleToFill(mShouldScaleToFill);
        if (!mShouldScaleToFill) {
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.setBackgroundColor(Color.BLACK);
            relativeLayout.addView(mPreview);
            addView(relativeLayout);
        } else {
            addView(mPreview);
        }

        if (mViewFinderView instanceof View) {
            addView((View) mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by " +
                    "'createViewFinderView()' should be instance of android.view.View");
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected IViewFinder createViewFinderView(Context context) {
        ViewFinderView viewFinderView = new ViewFinderView(context);
        viewFinderView.setBorderColor(mBorderColor);
        viewFinderView.setLaserColor(mLaserColor);
        viewFinderView.setLaserEnabled(mIsLaserEnabled);
        viewFinderView.setBorderStrokeWidth(mBorderWidth);
        viewFinderView.setBorderLineLength(mBorderLength);
        viewFinderView.setMaskColor(mMaskColor);

        viewFinderView.setBorderCornerRounded(mRoundedCorner);
        viewFinderView.setBorderCornerRadius(mCornerRadius);
        viewFinderView.setSquareViewFinder(mSquaredFinder);
        viewFinderView.setViewFinderOffset(mViewFinderOffset);
        return viewFinderView;
    }

    public void setLaserColor(int laserColor) {
        mLaserColor = laserColor;
        mViewFinderView.setLaserColor(mLaserColor);
        mViewFinderView.setupViewFinder();
    }
    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
        mViewFinderView.setMaskColor(mMaskColor);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        mViewFinderView.setBorderColor(mBorderColor);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderWidth = borderStrokeWidth;
        mViewFinderView.setBorderStrokeWidth(mBorderWidth);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderLineLength(int borderLineLength) {
        mBorderLength = borderLineLength;
        mViewFinderView.setBorderLineLength(mBorderLength);
        mViewFinderView.setupViewFinder();
    }
    public void setLaserEnabled(boolean isLaserEnabled) {
        mIsLaserEnabled = isLaserEnabled;
        mViewFinderView.setLaserEnabled(mIsLaserEnabled);
        mViewFinderView.setupViewFinder();
    }
    public void setIsBorderCornerRounded(boolean isBorderCornerRounded) {
        mRoundedCorner = isBorderCornerRounded;
        mViewFinderView.setBorderCornerRounded(mRoundedCorner);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderCornerRadius(int borderCornerRadius) {
        mCornerRadius = borderCornerRadius;
        mViewFinderView.setBorderCornerRadius(mCornerRadius);
        mViewFinderView.setupViewFinder();
    }
    public void setSquareViewFinder(boolean isSquareViewFinder) {
        mSquaredFinder = isSquareViewFinder;
        mViewFinderView.setSquareViewFinder(mSquaredFinder);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderAlpha(float borderAlpha) {
        mBorderAlpha = borderAlpha;
        mViewFinderView.setBorderAlpha(mBorderAlpha);
        mViewFinderView.setupViewFinder();
    }

    public void startCamera(int cameraId) {
        if(mCameraHandlerThread == null) {
            mCameraHandlerThread = new CameraHandlerThread(this);
        }
        mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        mCameraWrapper = cameraWrapper;
        if(mCameraWrapper != null) {
            setupLayout(mCameraWrapper);
            mViewFinderView.setupViewFinder();
            if(mFlashState != null) {
                setFlash(mFlashState);
            }
            setAutoFocus(mAutofocusState);
        }
    }

    public void startCamera() {
        startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if(mCameraWrapper != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCameraWrapper.mCamera.release();
            mCameraWrapper = null;
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

            if(previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth;
                rect.right = rect.right * previewWidth / viewFinderViewWidth;
            }

            if(previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight;
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            }

            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }

    public void setFlash(boolean flag) {
        mFlashState = flag;
        if(mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper.mCamera)) {

            Camera.Parameters parameters = mCameraWrapper.mCamera.getParameters();
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
            mCameraWrapper.mCamera.setParameters(parameters);
        }
    }

    public boolean getFlash() {
        if(mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = mCameraWrapper.mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleFlash() {
        if(mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = mCameraWrapper.mCamera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCameraWrapper.mCamera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        mAutofocusState = state;
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }

    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        mShouldScaleToFill = shouldScaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        mAspectTolerance = aspectTolerance;
    }

    public byte[] getRotatedData(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

        int rotationCount = getRotationCount();

        if(rotationCount == 1 || rotationCount == 3) {
            for (int i = 0; i < rotationCount; i++) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                data = rotatedData;
                int tmp = width;
                width = height;
                height = tmp;
            }
        }

        return data;
    }

    public int getRotationCount() {
        int displayOrientation = mPreview.getDisplayOrientation();
        return displayOrientation / 90;
    }
}

