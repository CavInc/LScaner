package cav.lscaner.data.models;

public class StoreProductModel {
    private String mBarcode;
    private String mName;
    private String mArticul = null;

    public StoreProductModel(String barcode, String name) {
        mBarcode = barcode;
        mName = name;
    }

    public StoreProductModel(String barcode, String name, String articul) {
        mBarcode = barcode;
        mName = name;
        mArticul = articul;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public String getName() {
        return mName;
    }

    public String getArticul() {
        return mArticul;
    }
}