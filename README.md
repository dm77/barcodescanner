Introduction
============

Android library projects that provides easy to use and extensible Barcode Scanner views based on ZXing and ZBar.

Screenshots
===========
<img src="https://raw.github.com/dm77/barcodescanner/master/screenshots/main_activity.png" width="266">
<img src="https://raw.github.com/dm77/barcodescanner/master/screenshots/scanner.png" width="266">
<img src="https://raw.github.com/dm77/barcodescanner/master/screenshots/scan_results.png" width="266">


Minor BREAKING CHANGE in 1.8.4
==============================
Version 1.8.4 introduces a couple of new changes:

* Open Camera and handle preview frames in a separate HandlerThread (#1, #99): Though this has worked fine in my testing on 3 devices, I would advise you to test on your own devices before blindly releasing apps with this version. If you run into any issues please file a bug report.
* Do not automatically stopCamera after a result is found #115: This means that upon a successful scan only the cameraPreview is stopped but the camera is not released. So previously if your code was calling mScannerView.startCamera() in the handleResult() method, please replace that with a call to mScannerView.resumeCameraPreview(this);

ZXing
=====

Installation
------------

Add the following dependency to your build.gradle file.

```
repositories {
   jcenter()
}
implementation 'me.dm7.barcodescanner:zxing:1.9.13'
```

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

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}

```

Please take a look at the [zxing-sample](https://github.com/dm77/barcodescanner/tree/master/zxing-sample) project for a full working example.

Advanced Usage
--------------

Take a look at the [FullScannerActivity.java](https://github.com/dm77/barcodescanner/blob/master/zxing-sample/src/main/java/me/dm7/barcodescanner/zxing/sample/FullScannerActivity.java) or [FullScannerFragment.java](https://github.com/dm77/barcodescanner/blob/master/zxing-sample/src/main/java/me/dm7/barcodescanner/zxing/sample/FullScannerFragment.java) classes to get an idea on advanced usage.

Interesting methods on the ZXingScannerView include:

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


For HUAWEI mobile phone like P9, P10, when scanning using the default settings, it won't work due to the
"preview size",  please adjust the parameter as below:

```java
mScannerView = (ZXingScannerView) findViewById(R.id.zx_view);

// this paramter will make your HUAWEI phone works great!
mScannerView.setAspectTolerance(0.5f);
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

```
repositories {
   jcenter()
}
implementation 'me.dm7.barcodescanner:zbar:1.9.13'
```

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

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}

```

Please take a look at the [zbar-sample](https://github.com/dm77/barcodescanner/tree/master/zbar-sample)  project for a full working example.

Advanced Usage
--------------


Take a look at the [FullScannerActivity.java](https://github.com/dm77/barcodescanner/blob/master/zbar-sample/src/main/java/me/dm7/barcodescanner/zbar/sample/FullScannerActivity.java) or [FullScannerFragment.java](https://github.com/dm77/barcodescanner/blob/master/zbar-sample/src/main/java/me/dm7/barcodescanner/zbar/sample/FullScannerFragment.java) classes to get an idea on advanced usage.

Interesting methods on the ZBarScannerView include:

```java
// Toggle flash:
void setFlash(boolean);

// Toogle autofocus:
void setAutoFocus(boolean);

// Specify interested barcode formats:
void setFormats(List<BarcodeFormat> formats);
```

Specify front-facing or rear-facing cameras by using the `void startCamera(int cameraId);` method.

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
BarcodeFormat.QR_CODE
BarcodeFormat.CODE93
BarcodeFormat.CODE128
```

Rebuilding ZBar Libraries
=========================

```
mkdir some_work_dir
cd work_dir
wget http://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.14.tar.gz
tar zxvf libiconv-1.14.tar.gz
```

Patch the localcharset.c file:
vim libiconv-1.14/libcharset/lib/localcharset.c

On line 48, add the following line of code:

```
#undef HAVE_LANGINFO_CODESET
```

Save the file and continue with steps below:
```
cd libiconv-1.14
./configure
cd ..
hg clone http://hg.code.sf.net/p/zbar/code zbar-code
cd zbar-code/android
android update project -p . -t 'android-19'
```

Open jni/Android.mk file and add fPIC flag to LOCAL_C_FLAGS.
Open jni/Application.mk file and specify APP_ABI targets as needed.

```
ant -Dndk.dir=$NDK_HOME  -Diconv.src=some_work_dir/libiconv-1.14 zbar-clean zbar-all
```

Upon completion you can grab the .so and .jar files from the libs folder.

Credits
=======

Almost all of the code for these library projects is based on:

1. CameraPreview app from Android SDK APIDemos
2. The ZXing project: https://github.com/zxing/zxing
3. The ZBar Android SDK: http://sourceforge.net/projects/zbar/files/AndroidSDK/

Contributors
============

https://github.com/dm77/barcodescanner/graphs/contributors

License
=======
Apache License, Version 2.0
