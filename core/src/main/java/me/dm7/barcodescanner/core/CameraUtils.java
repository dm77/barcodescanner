package me.dm7.barcodescanner.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    @SuppressLint("NewApi")
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
                Camera.getCameraInfo(camNo, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    c = Camera.open(camNo);
                }else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    c = Camera.open(camNo);
                }
            }
            if (c == null) {
                c = Camera.open();
            }
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static boolean isFlashSupported(Context context){
        PackageManager packageManager = context.getPackageManager();
        // if device support camera flash?
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        }
        return false;
    }
}
