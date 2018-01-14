package cav.lscaner.utils;

import android.hardware.Camera;

import java.util.List;


public class CameraUtils {
    private Camera mCamera;

    public CameraUtils(){
    }

    private void OpenCamera(){
        mCamera = Camera.open(0);
    }

    private void getCameraParams(){
        Camera.Parameters parametrs = mCamera.getParameters();
        List<String> focusModes = parametrs.getSupportedFocusModes();

    }

    public boolean isCameraExists(){
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras > 0) {
            return true;
        }
        return false;
    }

}