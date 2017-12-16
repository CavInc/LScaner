package cav.lscaner.data.models;

public class FieldOutFile {
    private int mBarcode;
    private int mQuantity;
    private int mPrice;
    private int mArticul; // код
    private int mBasePrice;
    private int mEGAIS;
    private int mCodeTV; // артикул

    public FieldOutFile(int barcode, int quantity, int price) {
        mBarcode = barcode;
        mQuantity = quantity;
        mPrice = price;
    }

    public FieldOutFile(int barcode, int quantity, int price, int articul) {
        mBarcode = barcode;
        mQuantity = quantity;
        mPrice = price;
        mArticul = articul;
    }

    public FieldOutFile(int barcode, int quantity, int price, int articul, int basePrice, int EGAIS, int codeTV) {
        mBarcode = barcode;
        mQuantity = quantity;
        mPrice = price;
        mArticul = articul;
        mBasePrice = basePrice;
        mEGAIS = EGAIS;
        mCodeTV = codeTV;
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

    public int getArticul() {
        return mArticul;
    }

    public void setArticul(int articul) {
        mArticul = articul;
    }

    public int getBasePrice() {
        return mBasePrice;
    }

    public int getEGAIS() {
        return mEGAIS;
    }

    public int getCodeTV() {
        return mCodeTV;
    }
}