package cav.lscaner.utils;

import android.hardware.Camera;

import java.util.List;


public class CameraUtils {
    private Camera mCamera;

    public CameraUtils(){
    }

    public void OpenCamera(){
        mCamera = Camera.open(0);
    }

    public void getCameraParams(){
        Camera.Parameters parametrs = mCamera.getParameters();
        List<String> focusModes = parametrs.getSupportedFocusModes();
        for (String l:focusModes){
            System.out.println("CAM UTILS "+l);
        }

    }

    public boolean isCameraExists(){
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras > 0) {
            return true;
        }
        return false;
    }

}