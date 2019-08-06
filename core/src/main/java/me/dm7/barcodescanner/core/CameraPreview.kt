package me.dm7.barcodescanner.core

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.os.Handler
import android.util.AttributeSet
import android.util.Log

import android.content.res.Configuration
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import android.view.View

class CameraPreview : SurfaceView, SurfaceHolder.Callback {

    companion object {
        private const val TAG = "CameraPreview2"
    }

    constructor(
            context: Context,
            cameraWrapper: CameraWrapper,
            previewCallback: Camera.PreviewCallback
    ) : super(context) {
        this.mCameraWrapper = cameraWrapper
        this.mPreviewCallback = previewCallback
    }

    constructor(
            context: Context,
            attrs: AttributeSet,
            cameraWrapper: CameraWrapper,
            previewCallback: Camera.PreviewCallback
    ) : super(context, attrs) {
        this.mCameraWrapper = cameraWrapper
        this.mPreviewCallback = previewCallback
    }

    private var mCameraWrapper: CameraWrapper? = null
    private var mAutoFocusHandler: Handler? = null
    private var mPreviewing = true
    private var mAutoFocus = true
    private var mSurfaceCreated = false
    private var mShouldScaleToFill = true
    private var mPreviewCallback: Camera.PreviewCallback? = null
    private var mAspectTolerance = 0.1f

    init {
        setCamera(mCameraWrapper, mPreviewCallback)
        mAutoFocusHandler = Handler()
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }


    fun setCamera(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback?) {
        mCameraWrapper = cameraWrapper
        mPreviewCallback = previewCallback
    }

    fun setShouldScaleToFill(scaleToFill: Boolean) {
        mShouldScaleToFill = scaleToFill
    }

    fun setAspectTolerance(aspectTolerance: Float) {
        mAspectTolerance = aspectTolerance
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurfaceCreated = true
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (holder?.surface == null) {
            return
        }
        stopCameraPreview()
        showCameraPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSurfaceCreated = false
        stopCameraPreview()
    }

    fun showCameraPreview() {
        if (mCameraWrapper != null) {
            try {
                holder.addCallback(this)
                mPreviewing = true
                setupCameraParameters()
                mCameraWrapper?.mCamera?.setPreviewDisplay(holder)
                mCameraWrapper?.mCamera?.setDisplayOrientation(getDisplayOrientation())
                mCameraWrapper?.mCamera?.setOneShotPreviewCallback(mPreviewCallback)
                mCameraWrapper?.mCamera?.startPreview()
                if (mAutoFocus) {
                    if (mSurfaceCreated) { // check if surface created before using autofocus
                        safeAutoFocus()
                    } else {
                        scheduleAutoFocus() // wait 1 sec and then do check again
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString(), e)
            }
        }
    }

    fun safeAutoFocus() {
        try {
            mCameraWrapper?.mCamera?.autoFocus(autoFocusCB)
        } catch (re: RuntimeException) {
            // Horrible hack to deal with autofocus errors on Sony devices
            // See https://github.com/dm77/barcodescanner/issues/7 for example
            scheduleAutoFocus() // wait 1 sec and then do check again
        }
    }


    fun stopCameraPreview() {
        if (mCameraWrapper != null) {
            try {
                mPreviewing = false
                holder.removeCallback(this)
                mCameraWrapper?.mCamera?.cancelAutoFocus()
                mCameraWrapper?.mCamera?.setOneShotPreviewCallback(null)
                mCameraWrapper?.mCamera?.stopPreview()
            } catch (e: Exception) {
                Log.e(TAG, e.toString(), e)
            }
        }
    }

    fun setupCameraParameters() {
        val optimalSize = getOptimalPreviewSize()
        val parameters = mCameraWrapper?.mCamera?.parameters
        optimalSize?.let {
            parameters?.setPreviewSize(it.width, it.height)
        }
        mCameraWrapper?.mCamera?.parameters = parameters
        adjustViewSize(optimalSize)
    }

    fun adjustViewSize(cameraSize: Camera.Size?) {
        cameraSize?.let {
            val previewSize = convertSizeToLandscapeOrientation(Point(width, height))
            val cameraRatio = it.width.toFloat() / it.height
            val screenRatio = previewSize.x.toFloat() / previewSize.y
            if (screenRatio > cameraRatio) {
                setViewSize((previewSize.y * cameraRatio).toInt(), previewSize.y)
            } else {
                setViewSize(previewSize.x, (previewSize.x / cameraRatio).toInt())
            }
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private fun convertSizeToLandscapeOrientation(size: Point): Point {
        return if (getDisplayOrientation() % 180 == 0) {
            size
        } else {
            Point(size.y, size.x)
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private fun setViewSize(width: Int, height: Int) {
        val layoutParams = layoutParams
        var tmpWidth: Int
        var tmpHeight: Int
        if (getDisplayOrientation() % 180 == 0) {
            tmpWidth = width
            tmpHeight = height
        } else {
            tmpWidth = height
            tmpHeight = width
        }

        if (mShouldScaleToFill) {
            val parentWidth = (parent as View).width
            val parentHeight = (parent as View).height
            val ratioWidth = parentWidth.toFloat() / tmpWidth.toFloat()
            val ratioHeight = parentHeight.toFloat() / tmpHeight.toFloat()

            val compensation = if (ratioWidth > ratioHeight) {
                ratioWidth
            } else {
                ratioHeight
            }

            tmpWidth = Math.round(tmpWidth * compensation)
            tmpHeight = Math.round(tmpHeight * compensation)
        }

        layoutParams.width = tmpWidth
        layoutParams.height = tmpHeight
        setLayoutParams(layoutParams)
    }

    fun getDisplayOrientation(): Int {
        if (mCameraWrapper == null) {
            //If we don't have a camera set there is no orientation so return dummy value
            return 0
        }

        val info = Camera.CameraInfo()
        if (mCameraWrapper?.mCameraId == -1) {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
        } else {
            mCameraWrapper?.mCameraId?.let {
                Camera.getCameraInfo(it, info)
            }
        }

        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val rotation = display.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    private fun getOptimalPreviewSize() : Camera.Size? {
        if(mCameraWrapper == null) {
            return null
        }

        val sizes = mCameraWrapper?.mCamera?.parameters?.supportedPreviewSizes
        var w = width
        var h = height
        if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
            val portraitWidth = h
            h = w
            w = portraitWidth
        }

        val targetRatio =  (w / h).toDouble()
        if (sizes == null) return null

        var optimalSize : Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        val targetHeight = h

        // Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio = (size.width / size.height).toDouble()
            if (Math.abs(ratio - targetRatio) > mAspectTolerance) continue
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

    fun setAutoFocus(state : Boolean) {
        if(mCameraWrapper != null && mPreviewing) {
            if(state == mAutoFocus) {
                return
            }
            mAutoFocus = state
            if(mAutoFocus) {
                if (mSurfaceCreated) { // check if surface created before using autofocus
                    Log.v(TAG, "Starting autofocus")
                    safeAutoFocus()
                } else {
                    scheduleAutoFocus() // wait 1 sec and then do check again
                }
            } else {
                Log.v(TAG, "Cancelling autofocus")
                mCameraWrapper?.mCamera?.cancelAutoFocus()
            }
        }
    }

    private val doAutoFocus = Runnable {
        if (mCameraWrapper != null && mPreviewing && mAutoFocus && mSurfaceCreated) {
            safeAutoFocus()
        }
    }

    // Mimic continuous auto-focusing
    val autoFocusCB = object : Camera.AutoFocusCallback {
        override fun onAutoFocus(success: Boolean, camera: Camera?) {
            scheduleAutoFocus()
        }
    }

    fun scheduleAutoFocus() {
        mAutoFocusHandler?.postDelayed(doAutoFocus, 1000)
    }
}