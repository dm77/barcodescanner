package me.dm7.barcodescanner.core;

import android.hardware.Camera;

import java.util.List;

public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        return getCameraInstance(false);
    }

    /**
     * A safe way to get an instance of the Camera object. If {@code tryFrontFacing} is
     * true, and a default cam could not be found, a front facing cam will be tried.
     *
     * @param tryFrontFacing if true, and no default cam could be found, a front facing
     *                       cam will be tried.
     *
     * @return a camera instance or null if one could not be found.
     */
    public static Camera getCameraInstance(boolean tryFrontFacing) {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }

        if (c == null && !tryFrontFacing) {
            return c; // returns null if camera is unavailable and we don't need to try a front facing cam
        }

        // Try to find a front facing cam.
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();

            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        c = Camera.open(i);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return c;
    }

    public static boolean isFlashSupported(Camera camera) {
        /* Credits: Top answer at http://stackoverflow.com/a/19599365/868173 */
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();

            if (parameters.getFlashMode() == null) {
                return false;
            }

            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
