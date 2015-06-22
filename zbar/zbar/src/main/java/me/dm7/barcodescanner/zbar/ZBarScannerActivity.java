package me.dm7.barcodescanner.zbar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class ZBarScannerActivity extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;

    public static final String SCAN_RESULT = "SCAN_RESULT";
    public static final String SCAN_BARCODE_FORMAT = "SCAN_BARCODE_FORMAT";
    public static final String SCAN_FLASH = "SCAN_FLASH";
    public static final String SCAN_AUTO_FOCUS = "SCAN_AUTO_FOCUS";
    public static final String ERROR_INFO = "ERROR_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isCameraAvailable()) {
            cancelRequest();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean flash = getIntent().getBooleanExtra(SCAN_FLASH, false);
        boolean autofocus = getIntent().getBooleanExtra(SCAN_AUTO_FOCUS, false);
        int[] formats = getIntent().getIntArrayExtra(SCAN_BARCODE_FORMAT);

        List<BarcodeFormat> barcodeFormats = new ArrayList<>();

        mScannerView = new ZBarScannerView(this);

        mScannerView.setFlash(flash);
        mScannerView.setAutoFocus(autofocus);
        if (formats != null) {
            for (int format : formats) {
                barcodeFormats.add(BarcodeFormat.getFormatById(format));
            }
            mScannerView.setFormats(barcodeFormats);
        }

        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String resultContent = result.getContents();
        Intent dataIntent = new Intent();
        dataIntent.putExtra(SCAN_RESULT, resultContent);
        dataIntent.putExtra(SCAN_BARCODE_FORMAT, result.getBarcodeFormat().getName());
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }
}
