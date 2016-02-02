package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;

public class ViewFinderView extends ViewFinder {
    private static final String TAG = "ViewFinderView";

    private boolean initComplete = false;
    private Rect mFramingRect;
    private int scannerAlpha;

    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;

    public ViewFinderView(Context context) {
        super(context);
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void init() {
        if(initComplete){
            return;
        }

        //set up laser paint
        mLaserPaint = new Paint();
        mLaserPaint.setColor(getFramingRectConfiguration().getLaserColor());
        mLaserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(getFramingRectConfiguration().getMaskColor());

        //border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(getFramingRectConfiguration().getBorderColor());
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(getFramingRectConfiguration().getBorderStrokeWidth());

        mBorderLineLength = getFramingRectConfiguration().getBorderLineLength();

        initComplete = true;
    }

    public void setupViewFinder() {
        init();
        updateFramingRect();
        invalidate();
    }

    @Override
    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mFramingRect == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);
        drawLaser(canvas);
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawRect(0, 0, width, mFramingRect.top, mFinderMaskPaint);
        canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mFinderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1, mFramingRect.top - 1 + mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1 + mBorderLineLength, mFramingRect.top - 1, mBorderPaint);

        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1, mFramingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1 + mBorderLineLength, mFramingRect.bottom + 1, mBorderPaint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1, mFramingRect.top - 1 + mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1 - mBorderLineLength, mFramingRect.top - 1, mBorderPaint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1, mFramingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1 - mBorderLineLength, mFramingRect.bottom + 1, mBorderPaint);
    }

    public void drawLaser(Canvas canvas) {
        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint.setAlpha(getFramingRectConfiguration().getScannerAlpha()[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % getFramingRectConfiguration().getScannerAlpha().length;
        int middle = mFramingRect.height() / 2 + mFramingRect.top;
        canvas.drawRect(mFramingRect.left + 2, middle - 1, mFramingRect.right - 1, middle + 2, mLaserPaint);

        postInvalidateDelayed(getFramingRectConfiguration().getAnimationDelay(),
                mFramingRect.left - getFramingRectConfiguration().getPointSize(),
                mFramingRect.top - getFramingRectConfiguration().getPointSize(),
                mFramingRect.right + getFramingRectConfiguration().getPointSize(),
                mFramingRect.bottom + getFramingRectConfiguration().getPointSize());
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if(orientation != Configuration.ORIENTATION_PORTRAIT) {
            width = findDesiredDimensionInRange(getFramingRectConfiguration().getLandscapeWidthRatio(), viewResolution.x,
                    getFramingRectConfiguration().getMinFrameWidth(), getFramingRectConfiguration().getLandscapeMaxFrameWidth());
            height = findDesiredDimensionInRange(getFramingRectConfiguration().getLandscapeHeightRatio(), viewResolution.y,
                    getFramingRectConfiguration().getMinFrameHeight(), getFramingRectConfiguration().getLandscapeMaxFrameHeight());
        } else {
            width = findDesiredDimensionInRange(getFramingRectConfiguration().getPortraitWidthRatio(), viewResolution.x,
                    getFramingRectConfiguration().getMinFrameWidth(), getFramingRectConfiguration().getPortraitMaxFrameWidth());
            height = findDesiredDimensionInRange(getFramingRectConfiguration().getPortraitHeightRatio(), viewResolution.y,
                    getFramingRectConfiguration().getMinFrameHeight(), getFramingRectConfiguration().getPortraitMaxFrameHeight());
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int hardMin, int hardMax) {
        int dim = (int) (ratio * resolution);
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }
}
