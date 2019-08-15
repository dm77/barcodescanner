package me.dm7.barcodescanner.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.CornerPathEffect
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.View

open class ViewFinderView : View, IViewFinder {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    companion object {
        private const val TAG = "ViewFinderView"
        private const val PORTRAIT_WIDTH_RATIO = 6f / 8
        private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f

        private const val LANDSCAPE_HEIGHT_RATIO = 5f / 8
        private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        private const val MIN_DIMENSION_DIFF = 50

        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8
        private val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)

        private const val POINT_SIZE = 10
        private const val ANIMATION_DELAY = 80L

    }

    private var mFramingRect: Rect? = null
    private var scannerAlpha: Int = 0

    private val mDefaultLaserColor = resources.getColor(R.color.viewfinder_laser)
    private val mDefaultMaskColor = resources.getColor(R.color.viewfinder_mask)
    private val mDefaultBorderColor = resources.getColor(R.color.viewfinder_border)
    private val mDefaultBorderStrokeWidth = resources.getInteger(R.integer.viewfinder_border_width)
    private val mDefaultBorderLineLength = resources.getInteger(R.integer.viewfinder_border_length)

    private var mLaserPaint: Paint = Paint()
    private var mFinderMaskPaint: Paint = Paint()
    private var mBorderPaint: Paint = Paint()
    private var mBorderLineLength: Int = mDefaultBorderLineLength
    private var mSquareViewFinder: Boolean = false
    private var mIsLaserEnabled: Boolean? = null
    private var mBordersAlpha: Float? = null
    private var mViewFinderOffset = 0

    init {
        mLaserPaint.apply {
            color = mDefaultLaserColor
            style = Paint.Style.FILL
        }

        //finder mask paint
        mFinderMaskPaint.apply {
            color = mDefaultMaskColor
        }

        //border paint
        mBorderPaint.apply {
            color = mDefaultBorderColor
            style = Paint.Style.STROKE
            strokeWidth = mDefaultBorderStrokeWidth.toFloat()
            isAntiAlias = true
        }
    }

    override fun setLaserColor(laserColor: Int) {
        mLaserPaint.color = laserColor
    }

    override fun setMaskColor(maskColor: Int) {
        mFinderMaskPaint.color = maskColor
    }

    override fun setBorderColor(borderColor: Int) {
        mBorderPaint.color = borderColor
    }

    override fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        mBorderPaint.strokeWidth = borderStrokeWidth.toFloat()
    }

    override fun setBorderLineLength(borderLineLength: Int) {
        mBorderLineLength = borderLineLength
    }

    override fun setLaserEnabled(isLaserEnabled: Boolean) {
        mIsLaserEnabled = isLaserEnabled
    }

    override fun setBorderCornerRounded(isBorderCornersRounded: Boolean) {
        if (isBorderCornersRounded) {
            mBorderPaint.strokeJoin = Paint.Join.ROUND
        } else {
            mBorderPaint.strokeJoin = Paint.Join.BEVEL
        }
    }

    override fun setBorderAlpha(alpha: Float) {
        val colorAlpha = (255 * alpha).toInt()
        mBordersAlpha = alpha
        mBorderPaint.alpha = colorAlpha
    }

    override fun setBorderCornerRadius(borderCornersRadius: Int) {
        mBorderPaint.pathEffect = CornerPathEffect(borderCornersRadius.toFloat())
    }

    override fun setViewFinderOffset(offset: Int) {
        mViewFinderOffset = offset
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
    override fun setSquareViewFinder(isSquareViewFinder: Boolean) {
        mSquareViewFinder = isSquareViewFinder
    }

    override fun setupViewFinder() {
        updateFramingRect()
        invalidate()
    }

    override fun getFramingRect(): Rect? {
        return mFramingRect
    }

    override fun onDraw(canvas: Canvas?) {
        if (getFramingRect() == null) {
            return
        }

        canvas?.let {
            drawViewFinderMask(it)
            drawViewFinderBorder(it)

            if (mIsLaserEnabled == true) {
                drawLaser(it)
            }
        }
    }

    fun drawViewFinderMask(canvas: Canvas) {
        canvas.let { c ->
            val width = c.width.toFloat()
            val height = c.height.toFloat()
            val framingRect = getFramingRect()

            framingRect?.let {
                c.drawRect(0f, 0f, width, it.top.toFloat(), mFinderMaskPaint)
                c.drawRect(0f, it.top.toFloat(), it.left.toFloat(), (it.bottom + 1).toFloat(), mFinderMaskPaint)
                c.drawRect((it.right + 1).toFloat(), it.top.toFloat(), width, (it.bottom + 1).toFloat(), mFinderMaskPaint)
                c.drawRect(0f, (it.bottom + 1).toFloat(), width, height, mFinderMaskPaint)
            }
        }
    }

    fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = getFramingRect()

        // Top-left corner
        val path = Path()
        framingRect?.let {
            path.moveTo(it.left.toFloat(), (it.top + mBorderLineLength).toFloat())
            path.lineTo(it.left.toFloat(), it.top.toFloat())
            path.lineTo((it.right).toFloat(), it.top.toFloat())
            //path.lineTo((it.left + mBorderLineLength).toFloat(), it.top.toFloat())
            canvas.drawPath(path, mBorderPaint)

            // Top-right corner
            path.moveTo(it.right.toFloat(), (it.top + mBorderLineLength).toFloat())
            path.lineTo(it.right.toFloat(), it.top.toFloat())
            path.lineTo((it.right - mBorderLineLength).toFloat(), it.top.toFloat())
            canvas.drawPath(path, mBorderPaint)

            // Bottom-right corner
            path.moveTo(it.right.toFloat(), (it.bottom - mBorderLineLength).toFloat())
            path.lineTo(it.right.toFloat(), it.bottom.toFloat())
            path.lineTo((it.right - mBorderLineLength).toFloat(), it.bottom.toFloat())
            canvas.drawPath(path, mBorderPaint)

            // Bottom-left corner
            path.moveTo(it.left.toFloat(), (it.bottom - mBorderLineLength).toFloat())
            path.lineTo(it.left.toFloat(), it.bottom.toFloat())
            path.lineTo(it.right.toFloat(), it.bottom.toFloat())
            //path.lineTo((it.left + mBorderLineLength).toFloat(), it.bottom.toFloat())
            canvas.drawPath(path, mBorderPaint)
        }
    }

    fun drawLaser(canvas: Canvas) {
        val framingRect = getFramingRect()

        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint.alpha = SCANNER_ALPHA[scannerAlpha]
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size

        framingRect?.let {
            val middle = it.height() / 2 + it.top
            canvas.drawRect(
                    (it.left + 2).toFloat(),
                    (middle - 1).toFloat(),
                    (it.right - 1).toFloat(),
                    (middle + 2).toFloat(),
                    mLaserPaint
            )

            postInvalidateDelayed(
                    ANIMATION_DELAY,
                    it.left - POINT_SIZE,
                    it.top - POINT_SIZE,
                    it.right + POINT_SIZE,
                    it.bottom + POINT_SIZE
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateFramingRect()
    }

    @Synchronized
    fun updateFramingRect() {
        val viewResolution = Point(width, height)
        var width: Int
        var height: Int
        val orientation = DisplayUtils.getScreenOrientation(context)

        if (mSquareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                width = height
            } else {
                width = (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                height = width
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * LANDSCAPE_HEIGHT_RATIO).toInt()
                width = (LANDSCAPE_WIDTH_HEIGHT_RATIO * height).toInt()
            } else {
                width = (getWidth() * PORTRAIT_WIDTH_RATIO).toInt()
                height = (PORTRAIT_WIDTH_HEIGHT_RATIO * width).toInt()
            }
        }

        if (width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF
        }

        if (height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF
        }

        val leftOffset = (viewResolution.x - width) / 2
        val topOffset = (viewResolution.y - height) / 2
        mFramingRect = Rect(
                leftOffset + mViewFinderOffset,
                topOffset + mViewFinderOffset,
                leftOffset + width - mViewFinderOffset,
                topOffset + height - mViewFinderOffset
        )
    }
}