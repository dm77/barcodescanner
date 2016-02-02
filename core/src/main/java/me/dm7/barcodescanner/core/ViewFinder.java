package me.dm7.barcodescanner.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public abstract class ViewFinder extends View {

    public static final int MIN_FRAME_WIDTH = 240;
    public static final int MIN_FRAME_HEIGHT = 240;

    public static final float LANDSCAPE_WIDTH_RATIO = 5f/8;
    public static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    public static final int LANDSCAPE_MAX_FRAME_WIDTH = (int) (1920 * LANDSCAPE_WIDTH_RATIO); // = 5/8 * 1920
    public static final int LANDSCAPE_MAX_FRAME_HEIGHT = (int) (1080 * LANDSCAPE_HEIGHT_RATIO); // = 5/8 * 1080

    public static final float PORTRAIT_WIDTH_RATIO = 7f/8;
    public static final float PORTRAIT_HEIGHT_RATIO = 3f/8;
    public static final int PORTRAIT_MAX_FRAME_WIDTH = (int) (1080 * PORTRAIT_WIDTH_RATIO); // = 7/8 * 1080
    public static final int PORTRAIT_MAX_FRAME_HEIGHT = (int) (1920 * PORTRAIT_HEIGHT_RATIO); // = 3/8 * 1920

    public static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    public static final int POINT_SIZE = 10;
    public static final long ANIMATION_DELAY = 80l;

    public final FramingRectConfiguration DEFAULT_FRAMING_RECT_CONF = new FramingRectConfiguration(getContext());
    private FramingRectConfiguration mFramingRectConfiguration;


    public ViewFinder(Context context) {
        super(context);
    }

    public ViewFinder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFinder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewFinder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FramingRectConfiguration getFramingRectConfiguration() {
        return mFramingRectConfiguration==null ? DEFAULT_FRAMING_RECT_CONF : mFramingRectConfiguration;
    }

    public void setFramingRectConfiguration(FramingRectConfiguration framingRectConfiguration) {
        this.mFramingRectConfiguration = framingRectConfiguration;
    }

    /**
     * Method that executes when Camera preview is starting.
     * It is recommended to update framing rect here and invalidate view after that. <br/>
     * For example see: {@link ViewFinderView#setupViewFinder()}
     */
    public abstract void setupViewFinder();



    /**
     * Provides {@link Rect} that identifies area where barcode scanner can detect visual codes
     * <p>Note: This rect is a area representation in absolute pixel values. <br/>
     * For example: <br/>
     * If View's size is 1024x800 so framing rect might be 500x400</p>
     *
     * @return {@link Rect} that identifies barcode scanner area
     */
    public abstract Rect getFramingRect();


    /**
     * Defines parameters that are used to draw the Framing Rectangle
     */
    public static class FramingRectConfiguration {

        private final Context context;

        public FramingRectConfiguration(Context context) {
            this.context = context;
        }

        public int getMinFrameWidth() { return MIN_FRAME_WIDTH; }

        public int getMinFrameHeight() { return MIN_FRAME_HEIGHT; }

        public float getLandscapeWidthRatio() { return LANDSCAPE_WIDTH_RATIO; }

        public float getLandscapeHeightRatio() { return LANDSCAPE_HEIGHT_RATIO; }

        public int getLandscapeMaxFrameWidth() { return LANDSCAPE_MAX_FRAME_WIDTH; }

        public int getLandscapeMaxFrameHeight() { return LANDSCAPE_MAX_FRAME_HEIGHT; }

        public float getPortraitWidthRatio() { return PORTRAIT_WIDTH_RATIO; }

        public float getPortraitHeightRatio() { return PORTRAIT_HEIGHT_RATIO; }

        public int getPortraitMaxFrameWidth() { return PORTRAIT_MAX_FRAME_WIDTH; }

        public int getPortraitMaxFrameHeight() { return PORTRAIT_MAX_FRAME_HEIGHT; }

        public int[] getScannerAlpha() { return SCANNER_ALPHA; }

        public int getPointSize() { return POINT_SIZE; }

        public long getAnimationDelay() { return ANIMATION_DELAY; }

        public int getLaserColor() { return ContextCompat.getColor(context, R.color.viewfinder_laser); }

        public int getMaskColor() { return ContextCompat.getColor(context, R.color.viewfinder_mask); }

        public int getBorderColor() { return ContextCompat.getColor(context, R.color.viewfinder_border); }

        public int getBorderStrokeWidth() { return context.getResources().getInteger(R.integer.viewfinder_border_width); }

        public int getBorderLineLength() { return context.getResources().getInteger(R.integer.viewfinder_border_length); }

    }

}
