package me.dm7.barcodescanner.zxing

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

import java.util.EnumMap

import me.dm7.barcodescanner.core.BarcodeScannerView
import me.dm7.barcodescanner.core.DisplayUtils

open class ZXingScannerView : BarcodeScannerView {

    companion object {
        private const val TAG = "ZXingScannerView"

        fun ALL_FORMATS(): MutableList<BarcodeFormat> = mutableListOf(
                BarcodeFormat.AZTEC,
                BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.ITF,
                BarcodeFormat.MAXICODE,
                BarcodeFormat.PDF_417,
                BarcodeFormat.QR_CODE,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION
        )
    }

    interface ResultHandler {
        fun handleResult(rawResult: Result)
    }

    private var mMultiFormatReader: MultiFormatReader? = null

    private var mFormats: MutableList<BarcodeFormat>? = null
    private var mResultHandler: ResultHandler? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        initMultiFormatReader()
    }

    fun setFormats(formats: MutableList<BarcodeFormat>) {
        mFormats = formats
        initMultiFormatReader()
    }

    fun setResultHandler(resultHandler: ResultHandler) {
        mResultHandler = resultHandler
    }

    fun getFormats(): MutableList<BarcodeFormat> {
        mFormats?.let {
            return it
        }
        return ALL_FORMATS()

    }

    fun initMultiFormatReader() {
        val hints = EnumMap<DecodeHintType, MutableList<*>>(DecodeHintType::class.java)
        hints[DecodeHintType.POSSIBLE_FORMATS] = getFormats()
        mMultiFormatReader = MultiFormatReader().apply {
            setHints(hints)
        }
    }

    override fun onPreviewFrame(byteArray: ByteArray?, camera: Camera?) {
        if (mResultHandler == null) {
            return
        }

        try {
            var data = byteArray
            val parameters = camera?.parameters
            val size = parameters?.previewSize
            var width = size?.width
            var height = size?.height

            if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
                val rotationCount = getRotationCount()
                if (rotationCount == 1 || rotationCount == 3) {
                    val tmp = width
                    width = height
                    height = tmp
                }
                data = getRotatedData(data, camera)
            }

            var rawResult: Result? = null
            val source = buildLuminanceSource(data, width, height)

            if (source != null) {
                var bitmap = BinaryBitmap(HybridBinarizer(source))
                try {
                    rawResult = mMultiFormatReader?.decodeWithState(bitmap)
                } catch (re: ReaderException) {
                    Log.e(TAG, re.toString(), re)
                } catch (npe: NullPointerException) {
                    Log.e(TAG, npe.toString(), npe)
                } catch (aoe: ArrayIndexOutOfBoundsException) {
                    Log.e(TAG, aoe.toString(), aoe)
                } finally {
                    mMultiFormatReader?.reset()
                }

                if (rawResult == null) {
                    val invertedSource = source.invert()
                    bitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                    try {
                        rawResult = mMultiFormatReader?.decodeWithState(bitmap)
                    } catch (e: NotFoundException) {
                        Log.e(TAG, e.toString(), e)
                    } finally {
                        mMultiFormatReader?.reset()
                    }
                }
            }

            val finalRawResult = rawResult

            if (finalRawResult != null) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    // Stopping the preview can take a little long.
                    // So we want to set result handler to null to discard subsequent calls to
                    // onPreviewFrame.
                    val tmpResultHandler = mResultHandler
                    mResultHandler = null

                    stopCameraPreview()

                    tmpResultHandler?.handleResult(finalRawResult)
                }
            } else {
                camera?.setOneShotPreviewCallback(this)
            }
        } catch (e: RuntimeException) {
            Log.e(TAG, e.toString(), e)
        }
    }

    fun resumeCameraPreview(resultHandler: ResultHandler) {
        mResultHandler = resultHandler
        super.resumeCameraPreview()
    }

    fun buildLuminanceSource(data: ByteArray?, width: Int?, height: Int?): PlanarYUVLuminanceSource? {
        val rect = getFramingRectInPreview(width ?: 0, height ?: 0) ?: return null
        // Go ahead and assume it's YUV rather than die.
        var source: PlanarYUVLuminanceSource? = null

        try {
            source = PlanarYUVLuminanceSource(data, width ?: 0, height ?: 0, rect.left, rect.top,
                    rect.width(), rect.height(), false)
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
        }
        return source
    }
}