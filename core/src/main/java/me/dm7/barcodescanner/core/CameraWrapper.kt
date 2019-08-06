package me.dm7.barcodescanner.core

import android.hardware.Camera

class CameraWrapper private constructor(var mCamera: Camera?, val mCameraId: Int) {

    init {
        if (mCamera == null) {
            throw NullPointerException("Camera cannot be null")
        }
    }

    companion object {

        fun getWrapper(camera: Camera?, cameraId: Int): CameraWrapper? {
            return if (camera == null) {
                null
            } else {
                CameraWrapper(camera, cameraId)
            }
        }
    }
}