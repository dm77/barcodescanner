package me.dm7.barcodescanner.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;

public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static Camera getCameraInstance(int cameraId){
        Camera c = null;
        try {
            // attempt to get a Camera instance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
                && cameraId >= 0 && cameraId < Camera.getNumberOfCameras()) {
              c = Camera.open(cameraId);
            } else {
              c = Camera.open();
            }
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
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
