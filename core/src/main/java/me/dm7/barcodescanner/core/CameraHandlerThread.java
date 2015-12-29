package me.dm7.barcodescanner.core;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

// This code is mostly based on the top answer here: http://stackoverflow.com/questions/18149964/best-use-of-handlerthread-over-other-similar-classes
public class CameraHandlerThread extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";

    private Handler mHandler = null;
    private BarcodeScannerView mScannerView;

    public CameraHandlerThread(BarcodeScannerView scannerView) {
        super("CameraHandlerThread");
        mScannerView = scannerView;
        start();
    }

    public void startCamera(final int cameraId) {
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScannerView.setupCameraPreview(CameraUtils.getCameraInstance(cameraId));
            }
        });
    }
}
