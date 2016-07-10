package me.dm7.barcodescanner.core;

import android.hardware.Camera;
import android.support.annotation.NonNull;

public class CameraWrapper {
    public final Camera camera;
    public final int cameraId;

    private CameraWrapper(@NonNull Camera camera, int cameraId) {
        if (camera == null) {
            throw new NullPointerException("Camera cannot be null");
        }
        this.camera = camera;
        this.cameraId = cameraId;
    }

    public static CameraWrapper getWrapper(Camera camera, int cameraId) {
        if (camera == null) {
            return null;
        } else {
            return new CameraWrapper(camera, cameraId);
        }
    }
}
