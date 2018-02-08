package cav.lscaner.utils;


import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;


//https://www.androidhive.info/2017/08/android-barcode-scanner-using-google-mobile-vision-building-movie-tickets-app/

public class CustomBarcodeDetector implements Detector.Processor {
    private boolean lockDeteck = false;

    private BarcodeDetectorCallback mBarcodeDetectorCallback;

    @Override
    public void release() {
        Log.d("DP","RELEASE");

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        final SparseArray barcodes = detections.getDetectedItems();
        if (barcodes.size() != 0 && lockDeteck == false) {
            if (mBarcodeDetectorCallback != null){
                lockDeteck = true;
                mBarcodeDetectorCallback.OnBarcode(((Barcode) barcodes.valueAt(0)).displayValue);
                this.release();
            }
        }
    }

    public void setBarcodeDetectorCallback(BarcodeDetectorCallback callback){
        mBarcodeDetectorCallback = callback;
    }



    public interface BarcodeDetectorCallback {
        public void OnBarcode(String barcode);
    }
}