Google Mobile Vision
=====

Installation
------------

Add the following dependency to your build.gradle file.

`compile 'me.dm7.barcodescanner:vision:1.9'`

Simple Usage
------------

1.) Add camera permission to your AndroidManifest.xml file:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

2.) A very basic activity would look like this:

```java
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

```

Please take a look at the [vision-sample] (https://github.com/dm77/barcodescanner/tree/master/vision-sample) project for a full working example.

Advanced Usage
--------------

Take a look at the [FullScannerActivity.java] (https://github.com/dm77/barcodescanner/blob/master/vision/sample/src/main/java/me/dm7/barcodescanner/vision/sample/FullScannerActivity.java) classes to get an idea on advanced usage.

Interesting methods on the VisionScannerView include:

```java
// Toggle flash:
void setFlash(boolean);

// Toogle autofocus:
void setAutoFocus(boolean);

// Specify interested barcode formats:
void setFormats(List<BarcodeFormat> formats);

// Specify the cameraId to start with:
void startCamera(int cameraId);
```

Specify front-facing or rear-facing cameras by using the `void startCamera(int cameraId);` method.

Supported Formats:

```java
     BarcodeFormat.CODE_128
     BarcodeFormat.CODE_39
     BarcodeFormat.CODE_93
     BarcodeFormat.CODABAR
     BarcodeFormat.DATA_MATRIX
     BarcodeFormat.EAN_13
     BarcodeFormat.EAN_8
     BarcodeFormat.ITF
     BarcodeFormat.QR_CODE
     BarcodeFormat.UPC_A
     BarcodeFormat.UPC_E
     BarcodeFormat.PDF417
     BarcodeFormat.AZTEC
```
