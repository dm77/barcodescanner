package me.dm7.barcodescanner.vision.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.vision.BarcodeFormat;
import me.dm7.barcodescanner.vision.Result;
import me.dm7.barcodescanner.vision.VisionScannerView;

public class SimpleScannerActivity extends Activity implements VisionScannerView.ResultHandler {
    private static final String TAG = "SimpleScannerActivity";

    private VisionScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new VisionScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getBarcode().displayValue); // Prints scan results
        Log.v(TAG, BarcodeFormat.getFormatById(rawResult.getBarcode().format).toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);

        Toast.makeText(SimpleScannerActivity.this, rawResult.getBarcode().displayValue, Toast.LENGTH_SHORT).show();
    }
}