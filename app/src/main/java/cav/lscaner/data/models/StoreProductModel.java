package cav.lscaner.data.models;

public class StoreProductModel {
    private String mBarcode;
    private String mName;

    public StoreProductModel(String barcode, String name) {
        mBarcode = barcode;
        mName = name;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public String getName() {
        return mName;
    }
}