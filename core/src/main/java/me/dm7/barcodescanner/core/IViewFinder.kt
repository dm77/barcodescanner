package me.dm7.barcodescanner.core

import android.graphics.Rect

interface IViewFinder {

    fun setLaserColor(laserColor: Int)
    fun setMaskColor(maskColor: Int)
    fun setBorderColor(borderColor: Int)
    fun setBorderStrokeWidth(borderStrokeWidth: Int)
    fun setBorderLineLength(borderLineLength: Int)
    fun setLaserEnabled(isLaserEnabled: Boolean)

    fun setBorderCornerRounded(isBorderCornersRounded: Boolean)
    fun setBorderAlpha(alpha: Float)
    fun setBorderCornerRadius(borderCornersRadius: Int)
    fun setViewFinderOffset(offset: Int)
    fun setSquareViewFinder(isSquareViewFinder: Boolean)
    /**
     * Method that executes when Camera preview is starting.
     * It is recommended to update framing rect here and invalidate view after that. <br></br>
     * For example see: [ViewFinderView.setupViewFinder]
     */
    fun setupViewFinder()

    /**
     * Provides [Rect] that identifies area where barcode scanner can detect visual codes
     *
     * Note: This rect is a area representation in absolute pixel values. <br></br>
     * For example: <br></br>
     * If View's size is 1024x800 so framing rect might be 500x400
     *
     * @return [Rect] that identifies barcode scanner area
     */
    fun getFramingRect(): Rect?

    /**
     * Width of a [android.view.View] that implements this interface
     *
     * Note: this is already implemented in [android.view.View],
     * so you don't need to override method and provide your implementation
     *
     * @return width of a view
     */
    fun getWidth(): Int

    /**
     * Height of a [android.view.View] that implements this interface
     *
     * Note: this is already implemented in [android.view.View],
     * so you don't need to override method and provide your implementation
     *
     * @return height of a view
     */
    fun getHeight(): Int
}