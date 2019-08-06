package me.dm7.barcodescanner.core

import android.hardware.Camera
import java.lang.Exception

object CameraUtils {

    fun getCameraInstance(): Camera? {
        return getCameraInstance(getDefaultCameraId())
    }

    fun getCameraInstance(cameraId: Int): Camera? {
        var c: Camera? = null
        try {
            c = if (cameraId == -1) {
                Camera.open() // attempt to get a Camera instance
            } else {
                Camera.open(cameraId) // attempt to get a Camera instance
            }
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }
        return c // returns null if camera is unavailable
    }

    fun getDefaultCameraId(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraInfo = Camera.CameraInfo()
        var defaultCameraId = -1
        for (i in 0..numberOfCameras) {
            defaultCameraId = i
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i
            }
        }
        return defaultCameraId
    }

    fun isFlashSupported(camera: Camera?): Boolean {
        if (camera != null) {
            val parameters = camera.parameters

            if (parameters.flashMode == null) {
                return false
            }

            val supportedFlashModes = parameters.supportedFlashModes
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF) {
                return false
            }
        } else {
            return false
        }
        return true
    }
}