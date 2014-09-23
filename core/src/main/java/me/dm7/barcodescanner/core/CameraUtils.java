package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static boolean isFlashSupported(Context context) {

        PackageManager packageManager = context.getPackageManager();
        // if device support camera flash?
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return false;
        }

        /*
         * It appears that the above check is not sufficient for some devices.
         *
         * Per comments on the post linked below, the FEATURE_CAMERA_FLASH check was not
         * sufficient for these devices:  2013 Nexus 7 and Wildfire S.
         *
         * Credit for fixes to SO user Erik B.
         *
         * See: http://stackoverflow.com/a/19599365/868173
         */
        Camera camera = getCameraInstance();
        if (camera) {
            if (camera.getParameters().getFlashMode() == null) {
                return false;
            }

            List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
