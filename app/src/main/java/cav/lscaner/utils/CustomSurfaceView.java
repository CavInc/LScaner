package cav.lscaner.utils;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private CameraSource mCameraSource;

    public CustomSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public void setCameraSource(CameraSource cameraSource){
        mCameraSource = cameraSource;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraSource.stop();
    }
}