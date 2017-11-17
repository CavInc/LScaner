package cav.lscaner.data.models;

public class FieldOutFile {
    private int mBarcode;
    private int mQuantity;
    private int mPrice;

    public FieldOutFile(int barcode, int quantity, int price) {
        mBarcode = barcode;
        mQuantity = quantity;
        mPrice = price;
    }

    public int getBarcode() {
        return mBarcode;
    }

    public void setBarcode(int barcode) {
        mBarcode = barcode;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }
}