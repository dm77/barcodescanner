Introduction
============

Android library projects that provides easy to use and extensible Barcode Scanner views based on ZXing and ZBar.

Screenshots
===========
![Portrait](https://raw.github.com/dm77/barcodescanner/master/screenshots/portrait_small.png)

![Landscape](https://raw.github.com/dm77/barcodescanner/master/screenshots/landscape_small.png)

ZXing
=====

Installation
------------

Add the following dependency to your build.gradle file.

compile 'me.dm7.barcodescanner:zxing:1.0'

Simple Usage
------------

1.) Add camera permission to your AndroidManifest.xml file:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

2.) A very basic activity would look like this:

```java
public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
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
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
    }
}

```

Please take a look at the [zxing/sample] (https://github.com/dm77/barcodescanner/tree/master/zxing/sample) project for a full working example.

Advanced Usage
--------------

Take a look at the [ScannerActivity.java] (https://github.com/dm77/barcodescanner/blob/master/zxing/sample/src/main/java/me/dm7/barcodescanner/zxing/sample/ScannerActivity.java) or [ScannerFragment.java] (https://github.com/dm77/barcodescanner/blob/master/zxing/sample/src/main/java/me/dm7/barcodescanner/zxing/sample/ScannerFragment.java) classes to get an idea on advanced usage.

Interesting methods on the ZXingScannerView include:

```java
// Toggle flash:
void setFlash(boolean);

// Toogle autofocus:
void setAutoFocus(boolean);

// Specify interested barcode formats:
void setFormats(List<BarcodeFormat> formats);
```

Supported Formats:

```java
BarcodeFormat.UPC_A
BarcodeFormat.UPC_E
BarcodeFormat.EAN_13
BarcodeFormat.EAN_8
BarcodeFormat.RSS_14
BarcodeFormat.CODE_39
BarcodeFormat.CODE_93
BarcodeFormat.CODE_128
BarcodeFormat.ITF
BarcodeFormat.CODABAR
BarcodeFormat.QR_CODE
BarcodeFormat.DATA_MATRIX
BarcodeFormat.PDF_417
```

ZBar
====

Installation
------------

Add the following dependency to your build.gradle file.

compile 'me.dm7.barcodescanner:zbar:1.0'

Simple Usage
------------

1.) Add camera permission to your AndroidManifest.xml file:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

2.) A very basic activity would look like this:

```java
public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
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
        Log.v(TAG, rawResult.getContents()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
    }
}

```

Please take a look at the [zbar/sample] (https://github.com/dm77/barcodescanner/tree/master/zbar/sample)  project for a full working example.

Advanced Usage
--------------


Take a look at the [ScannerActivity.java] (https://github.com/dm77/barcodescanner/blob/master/zbar/sample/src/main/java/me/dm7/barcodescanner/zbar/sample/ScannerActivity.java) or [ScannerFragment.java] (https://github.com/dm77/barcodescanner/blob/master/zbar/sample/src/main/java/me/dm7/barcodescanner/zbar/sample/ScannerFragment.java) classes to get an idea on advanced usage.

Interesting methods on the ZBarScannerView include:

```java
// Toggle flash:
void setFlash(boolean);

// Toogle autofocus:
void setAutoFocus(boolean);

// Specify interested barcode formats:
void setFormats(List<BarcodeFormat> formats);
```

Supported Formats:

```
BarcodeFormat.PARTIAL
BarcodeFormat.EAN8
BarcodeFormat.UPCE
BarcodeFormat.ISBN10
BarcodeFormat.UPCA
BarcodeFormat.EAN13
BarcodeFormat.ISBN13
BarcodeFormat.I25
BarcodeFormat.DATABAR
BarcodeFormat.DATABAR_EXP
BarcodeFormat.CODABAR
BarcodeFormat.CODE39
BarcodeFormat.PDF417
BarcodeFormat.QRCODE
BarcodeFormat.CODE93
BarcodeFormat.CODE128
```

Credits
=======

Almost all of the code for these library projects is based on:

1. CameraPreview app from Android SDK APIDemos
2. The ZXing project: https://github.com/zxing/zxing
3. The ZBar Android SDK: http://sourceforge.net/projects/zbar/files/AndroidSDK/

License
=======
Apache License, Version 2.0
