package me.dm7.barcodescanner.vision;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.nio.ByteBuffer;
import java.util.List;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;

public class VisionScannerView extends BarcodeScannerView {
    private static final String TAG = "VisionScannerView";

    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;
    private BarcodeDetector barcodeDetector;

    public VisionScannerView(Context context) {
        super(context);
        setupScanner();
    }

    public VisionScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public List<BarcodeFormat> getFormats() {
        if (mFormats == null) {
            return BarcodeFormat.ALL_FORMATS;
        }
        return mFormats;
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        setupScanner();
    }

    public void setupScanner() {


        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(getSupportedFormatBitmapMask()).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Handled via public method
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    // TODO: 3/22/17
//                    onItemDetected(barcodes.valueAt(0));
                }
            }
        });


    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mResultHandler == null) {
            return;
        }

        try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            int width = size.width;
            int height = size.height;

            if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                int tmp = width;
                width = height;
                height = tmp;
                data = rotatedData;
            }

            SparseArray<Barcode> barcodeSparseArray = barcodeDetector.detect(constructFrame(width, height, data));

            if (barcodeSparseArray.size() != 0) {
                final Result rawResult = Result.newBuilder().withBarcode(barcodeSparseArray.valueAt(0)).build();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Stopping the preview can take a little long.
                        // So we want to set result handler to null to discard subsequent calls to
                        // onPreviewFrame.
                        ResultHandler tmpResultHandler = mResultHandler;
                        mResultHandler = null;

                        stopCameraPreview();
                        if (tmpResultHandler != null) {
                            tmpResultHandler.handleResult(rawResult);
                        }
                    }
                });
            } else {
                camera.setOneShotPreviewCallback(this);
            }
        } catch (RuntimeException e) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e);
        }
    }

    private Frame constructFrame(int width, int height, byte[] data) {


        ByteBuffer pendingFrameData = ByteBuffer.wrap(data);

        return new Frame.Builder()
                .setImageData(pendingFrameData, width,
                        height, ImageFormat.NV21)
                .build();

    }

    public void resumeCameraPreview(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    private int getSupportedFormatBitmapMask() {
        final List<BarcodeFormat> formats = getFormats();


        if (formats == null || formats.size() == 0) {
            return Barcode.ALL_FORMATS;
        } else if (formats.size() == 1) {
            return formats.get(0).getId();
        } else {
            int bitmapMask = formats.get(0).getId();

            for (int i = 1; i < formats.size(); i++) {
                bitmapMask |= formats.get(i).getId();
            }

            return bitmapMask;
        }


    }

    public interface ResultHandler {
        void handleResult(Result rawResult);
    }
}
