package me.dm7.barcodescanner.core

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.hardware.Camera
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout

abstract class BarcodeScannerView : FrameLayout, Camera.PreviewCallback {

    private var mCameraWrapper: CameraWrapper? = null
    private var mPreview: CameraPreview? = null
    var mViewFinderView: IViewFinder? = null
    private var mFramingRectInPreview: Rect? = null
    private var mCameraHandlerThread: CameraHandlerThread? = null
    private var mFlashState: Boolean? = null
    private var mAutofocusState = true
    private var mShouldScaleToFill = true

    private var mIsLaserEnabled = true
    @ColorInt
    private var mLaserColor = resources.getColor(R.color.viewfinder_laser)
    @ColorInt
    private var mBorderColor = resources.getColor(R.color.viewfinder_border)
    private var mMaskColor = resources.getColor(R.color.viewfinder_mask)
    private var mBorderWidth = resources.getInteger(R.integer.viewfinder_border_width)
    private var mBorderLength = resources.getInteger(R.integer.viewfinder_border_length)
    private var mRoundedCorner = false
    private var mCornerRadius = 0
    private var mSquaredFinder = false
    private var mBorderAlpha = 1.0f
    private var mViewFinderOffset = 0
    private var mAspectTolerance = 0.1f

    constructor(context: Context) : super(context){
        mViewFinderView = createViewFinderView(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val a = context.obtainStyledAttributes(
                attributeSet, R.styleable.BarcodeScannerView, 0, 0
        )
        try {
            setShouldScaleToFill(a.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true))
            mIsLaserEnabled = a.getBoolean(R.styleable.BarcodeScannerView_laserEnabled, mIsLaserEnabled)
            mLaserColor = a.getColor(R.styleable.BarcodeScannerView_laserColor, mLaserColor)
            mBorderColor = a.getColor(R.styleable.BarcodeScannerView_borderColor, mBorderColor)

            Log.e("DATA_BORDER", " - " + mBorderColor + " - " + mLaserColor)

            mMaskColor = a.getColor(R.styleable.BarcodeScannerView_maskColor, mMaskColor)
            mBorderWidth = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderWidth, mBorderWidth)
            mBorderLength = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderLength, mBorderLength)

            mRoundedCorner = a.getBoolean(R.styleable.BarcodeScannerView_roundedCorner, mRoundedCorner)
            mCornerRadius = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_cornerRadius, mCornerRadius)
            mSquaredFinder = a.getBoolean(R.styleable.BarcodeScannerView_squaredFinder, mSquaredFinder)
            mBorderAlpha = a.getFloat(R.styleable.BarcodeScannerView_borderAlpha, mBorderAlpha)
            mViewFinderOffset = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_finderOffset, mViewFinderOffset)
        } catch (e : Exception){
            Log.e("ERROR_TYPE_ARRAY", e.toString() , e)
        } finally {
            a.recycle()
        }
        mViewFinderView = createViewFinderView(context)
    }

    fun setupLayout(cameraWrapper: CameraWrapper) {
        removeAllViews()

        mPreview = CameraPreview(context, cameraWrapper, this)
        mPreview?.setAspectTolerance(mAspectTolerance)
        mPreview?.setShouldScaleToFill(mShouldScaleToFill)

        if (!mShouldScaleToFill) {
            val relativeLayout = RelativeLayout(context)
            relativeLayout.gravity = Gravity.CENTER
            relativeLayout.setBackgroundColor(Color.BLACK)
            relativeLayout.addView(mPreview)
            addView(relativeLayout)
        } else {
            addView(mPreview)
        }

        if (mViewFinderView is View) {
            addView(mViewFinderView as View)
        } else {
            throw IllegalArgumentException("IViewFinder object returned by " + "'createViewFinderView()' should be instance of android.view.View")
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */

    open protected fun createViewFinderView(context: Context): IViewFinder {
        val viewFinderView = ViewFinderView(context)
        viewFinderView.setBorderColor(mBorderColor)
        viewFinderView.setLaserColor(mLaserColor)
        viewFinderView.setLaserEnabled(mIsLaserEnabled)
        viewFinderView.setBorderStrokeWidth(mBorderWidth)
        viewFinderView.setBorderLineLength(mBorderLength)
        viewFinderView.setMaskColor(mMaskColor)

        viewFinderView.setBorderCornerRounded(mRoundedCorner)
        viewFinderView.setBorderCornerRadius(mCornerRadius)
        viewFinderView.setSquareViewFinder(mSquaredFinder)
        viewFinderView.setViewFinderOffset(mViewFinderOffset)
        return viewFinderView
    }

    fun setLaserColor(laserColor: Int) {
        mLaserColor = laserColor
        mViewFinderView?.setLaserColor(mLaserColor)
        mViewFinderView?.setupViewFinder()
    }

    fun setMaskColor(maskColor: Int) {
        mMaskColor = maskColor
        mViewFinderView?.setMaskColor(mMaskColor)
        mViewFinderView?.setupViewFinder()
    }

    fun setBorderColor(borderColor: Int) {
        mBorderColor = borderColor
        mViewFinderView?.setBorderColor(mBorderColor)
        mViewFinderView?.setupViewFinder()
    }

    fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        mBorderWidth = borderStrokeWidth
        mViewFinderView?.setBorderStrokeWidth(mBorderWidth)
        mViewFinderView?.setupViewFinder()
    }

    fun setBorderLineLength(borderLineLength: Int) {
        mBorderLength = borderLineLength
        mViewFinderView?.setBorderLineLength(mBorderLength)
        mViewFinderView?.setupViewFinder()
    }

    fun setLaserEnabled(isLaserEnabled: Boolean) {
        mIsLaserEnabled = isLaserEnabled
        mViewFinderView?.setLaserEnabled(mIsLaserEnabled)
        mViewFinderView?.setupViewFinder()
    }

    fun setIsBorderCornerRounded(isBorderCornerRounded: Boolean) {
        mRoundedCorner = isBorderCornerRounded
        mViewFinderView?.setBorderCornerRounded(mRoundedCorner)
        mViewFinderView?.setupViewFinder()
    }

    fun setBorderCornerRadius(borderCornerRadius: Int) {
        mCornerRadius = borderCornerRadius
        mViewFinderView?.setBorderCornerRadius(mCornerRadius)
        mViewFinderView?.setupViewFinder()
    }

    fun setSquareViewFinder(isSquareViewFinder: Boolean) {
        mSquaredFinder = isSquareViewFinder
        mViewFinderView?.setSquareViewFinder(mSquaredFinder)
        mViewFinderView?.setupViewFinder()
    }

    fun setBorderAlpha(borderAlpha: Float) {
        mBorderAlpha = borderAlpha
        mViewFinderView?.setBorderAlpha(mBorderAlpha)
        mViewFinderView?.setupViewFinder()
    }

    fun startCamera(cameraId: Int) {
        if (mCameraHandlerThread == null) {
            mCameraHandlerThread = CameraHandlerThread(this)
        }
        mCameraHandlerThread?.startCamera(cameraId)
    }

    fun setupCameraPreview(cameraWrapper: CameraWrapper?) {
        mCameraWrapper = cameraWrapper
        mCameraWrapper?.let {
            setupLayout(it)
            mViewFinderView?.setupViewFinder()
            mFlashState?.let { flashState ->
                setFlash(flashState)
            }
            setAutoFocus(mAutofocusState)
        }
    }

    fun startCamera() {
        startCamera(CameraUtils.getDefaultCameraId())
    }

    fun stopCamera() {
        mCameraWrapper?.let {
            mPreview?.let { p ->
                p.stopCameraPreview()
                p.setCamera(null, null)
            }
            it.mCamera?.release()
            mCameraWrapper = null
        }
        mCameraHandlerThread?.let {
            it.quit()
            mCameraHandlerThread = null
        }
    }

    fun stopCameraPreview() {
        mPreview?.let {
            it.stopCameraPreview()
        }
    }

    protected fun resumeCameraPreview() {
        mPreview?.let {
            it.showCameraPreview()
        }
    }

    @Synchronized
    fun getFramingRectInPreview(previewWidth: Int, previewHeight: Int): Rect? {
        if (mFramingRectInPreview == null) {
            val framingRect = mViewFinderView?.getFramingRect()
            val viewFinderViewWidth = mViewFinderView?.getWidth() ?: 0
            val viewFinderViewHeight = mViewFinderView?.getHeight() ?: 0
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null
            }

            val rect = Rect(framingRect)

            if (previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth
                rect.right = rect.right * previewWidth / viewFinderViewWidth
            }

            if (previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight
            }

            mFramingRectInPreview = rect
        }
        return mFramingRectInPreview
    }

    fun setFlash(flag: Boolean) {
        mFlashState = flag
        if (mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper?.mCamera)) {

            val parameters = mCameraWrapper?.mCamera?.parameters
            if (flag) {
                if (parameters?.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return
                }
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                if (parameters?.flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return
                }
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }
            mCameraWrapper?.mCamera?.parameters = parameters
        }
    }

    fun getFlash(): Boolean {
        if (mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper?.mCamera)) {
            val parameters = mCameraWrapper?.mCamera?.parameters
            return parameters?.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)
        }
        return false
    }

    fun toggleFlash() {
        if (mCameraWrapper != null && CameraUtils.isFlashSupported(mCameraWrapper?.mCamera)) {
            val parameters = mCameraWrapper?.mCamera?.parameters
            if (parameters?.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            } else {
                parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            }
            mCameraWrapper?.mCamera?.parameters = parameters
        }
    }

    fun setAutoFocus(state: Boolean) {
        mAutofocusState = state
        mPreview?.let {
            it.setAutoFocus(state)
        }
    }

    fun setShouldScaleToFill(shouldScaleToFill: Boolean) {
        mShouldScaleToFill = shouldScaleToFill
    }

    fun setAspectTolerance(aspectTolerance: Float) {
        mAspectTolerance = aspectTolerance
    }

    fun getRotatedData(data: ByteArray?, camera: Camera?): ByteArray? {
        var data = data
        val parameters = camera?.parameters
        val size = parameters?.previewSize
        var width = size?.width ?: 0
        var height = size?.height ?: 0

        val rotationCount = getRotationCount()
        data?.let {
            if (rotationCount == 1 || rotationCount == 3) {
                for (i in 0 until rotationCount) {
                    val rotatedData = ByteArray(it.size)
                    for (y in 0 until height) {
                        for (x in 0 until width)
                            rotatedData[x * height + height - y - 1] = it[x + y * width]
                    }
                    data = rotatedData
                    val tmp = width
                    width = height
                    height = tmp
                }
            }
        }
        return data
    }

    fun getRotationCount(): Int {
        val displayOrientation = mPreview?.getDisplayOrientation() ?: 0
        return displayOrientation / 90
    }
}