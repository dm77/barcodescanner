package me.dm7.barcodescanner.zbar

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log

import net.sourceforge.zbar.Config
import net.sourceforge.zbar.Image
import net.sourceforge.zbar.ImageScanner
import net.sourceforge.zbar.Symbol

import me.dm7.barcodescanner.core.BarcodeScannerView
import me.dm7.barcodescanner.core.DisplayUtils
import java.nio.charset.StandardCharsets

class ZBarScannerView : BarcodeScannerView {

    companion object {
        private const val TAG = "ZBarScannerView"
    }

    init {
        System.loadLibrary("iconv")
    }

    interface ResultHandler {
        fun handleResult(rawResult: Result)
    }

    constructor(context: Context) : super(context) {
        setupScanner()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupScanner()
    }

    private var mScanner: ImageScanner? = null
    private var mFormats: MutableList<BarcodeFormat>? = null
    private var mResultHandler: ResultHandler? = null


    fun setFormats(formats: MutableList<BarcodeFormat>) {
        mFormats = formats
        setupScanner()
    }

    fun setResultHandler(resultHandler: ResultHandler) {
        mResultHandler = resultHandler
    }

    fun getFormats(): MutableList<BarcodeFormat> {
        mFormats?.let {
            return it
        } ?: return BarcodeFormat.ALL_FORMATS
    }

    fun setupScanner() {
        mScanner = ImageScanner()
        mScanner?.setConfig(0, Config.X_DENSITY, 3)
        mScanner?.setConfig(0, Config.Y_DENSITY, 3)

        mScanner?.setConfig(Symbol.NONE, Config.ENABLE, 0)
        for (format: BarcodeFormat in getFormats()) {
            mScanner?.setConfig(format.id, Config.ENABLE, 1)
        }
    }

    override fun onPreviewFrame(dataByteArray: ByteArray, camera: Camera) {
        var data: ByteArray? = dataByteArray
        if (mResultHandler == null) {
            return
        }

        try {
            val parameters = camera.parameters
            val size = parameters.previewSize
            var width = size.width
            var height = size.height

            if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
                val rotationCount = getRotationCount()
                if (rotationCount == 1 || rotationCount == 3) {
                    val tmp = width
                    width = height
                    height = tmp
                }
                data = getRotatedData(data, camera)
            }

            val rect = getFramingRectInPreview(width, height)
            val barcode = Image(width, height, "Y800")
            barcode.data = data
            rect?.let {
                barcode.setCrop(it.left, it.top, it.width(), it.height())
            }

            val result = mScanner?.scanImage(barcode)

            if (result != 0) {
                val syms = mScanner?.results
                val rawResult = Result()
                syms?.let {
                    for (sym in it) {
                        // In order to retreive QR codes containing null bytes we need to
                        // use getDataBytes() rather than getData() which uses C strings.
                        // Weirdly ZBar transforms all data to UTF-8, even the data returned
                        // by getDataBytes() so we have to decode it as UTF-8.
                        val symData: String
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            symData = String(sym.dataBytes, StandardCharsets.UTF_8)
                        } else {
                            symData = sym.data
                        }
                        if (!TextUtils.isEmpty(symData)) {
                            rawResult.mContents = symData
                            rawResult.mBarcodeFormat = BarcodeFormat.getFormatById(sym.type)
                            break
                        }
                    }
                }

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    // Stopping the preview can take a little long.
                    // So we want to set result handler to null to discard subsequent calls to
                    // onPreviewFrame.
                    val tmpResultHandler = mResultHandler
                    mResultHandler = null

                    stopCameraPreview()
                    tmpResultHandler?.handleResult(rawResult)

                }
            } else {
                camera.setOneShotPreviewCallback(this)
            }
        } catch (e: RuntimeException) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e)
        }
    }

    fun resumeCameraPreview(resultHandler: ResultHandler) {
        mResultHandler = resultHandler
        super.resumeCameraPreview()
    }
}