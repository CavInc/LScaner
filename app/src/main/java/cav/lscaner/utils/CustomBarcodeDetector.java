package cav.lscaner.utils;


import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

public class CustomBarcodeDetector implements Detector.Processor {

    private BarcodeDetectorCallback mBarcodeDetectorCallback;

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        final SparseArray barcodes = detections.getDetectedItems();
        if (barcodes.size() != 0) {
            if (mBarcodeDetectorCallback != null){
             mBarcodeDetectorCallback.OnBarcode(((Barcode) barcodes.valueAt(0)).displayValue);
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