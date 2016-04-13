package me.dm7.barcodescanner.core;

import android.hardware.Camera;
import android.support.annotation.NonNull;

public class CameraWrapper {
    public final Camera camera;
    public final int cameraId;

    public CameraWrapper(@NonNull Camera camera, int cameraId) {
        if (camera == null) {
            throw new NullPointerException("Camera cannot be null");
        }
        this.camera = camera;
        this.cameraId = cameraId;
    }
}
