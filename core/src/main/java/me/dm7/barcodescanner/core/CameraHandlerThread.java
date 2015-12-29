package me.dm7.barcodescanner.core;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

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
                Log.v(LOG_TAG, "Trying to setup camera now");
                mScannerView.setupCameraPreview(CameraUtils.getCameraInstance(cameraId));
            }
        });
    }
}
