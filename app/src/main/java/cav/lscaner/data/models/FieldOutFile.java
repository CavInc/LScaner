package cav.lscaner.data.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FieldOutFile {
    private int mBarcode;
    private int mQuantity;
    private int mPrice;
    private int mArticul; // код
    private int mBasePrice;
    private int mEGAIS;
    private int mCodeTV; // артикул

    public FieldOutFile() {
        mBarcode = -1;
        mQuantity = -1;
        mPrice = -1;
        mArticul = -1; // код
        mBasePrice = -1;
        mEGAIS = -1;
        mCodeTV = -1; // артикул
    }

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

    public void setBasePrice(int basePrice) {
        mBasePrice = basePrice;
    }

    public void setEGAIS(int EGAIS) {
        mEGAIS = EGAIS;
    }

    public void setCodeTV(int codeTV) {
        mCodeTV = codeTV;
    }

    // возвращает значение в соотсветсвии с таблицей размещения полей
    //"Штрих-код","Код","Кол-во.","Цена","Цена закупочная","Код ЕГАИС","Артикул"};
    public int get(int i){
        switch (i){
            case 0:
                return mBarcode;
            case 1:
                return mArticul;
            case 2:
                return mQuantity;
            case 3:
                return mPrice;
            case 4:
                return mBasePrice;
            case 5:
                return mEGAIS;
            case 6:
                return mCodeTV;
        }
        return -1;
    }

    // возвращает массив индексов
    // String[] outField = new String[] {"Штрих-код","Код","Кол-во.","Цена","Цена закупочная","Код ЕГАИС","Артикул"};

    public int[] getArrayIndex(){
        ArrayList<Integer> f = new ArrayList<>();
        HashMap <Integer,Integer> m = new HashMap<>();
        if (mBarcode!=-1) m.put(mBarcode,0);
        if (mArticul!=-1) m.put(mArticul,1);
        if (mQuantity!=-1) m.put(mQuantity,2);
        if (mPrice!=-1) m.put(mPrice,3);
        if (mBasePrice!=-1) m.put(mBasePrice,4);
        if (mEGAIS!=-1) m.put(mEGAIS,5);
        if (mCodeTV!=-1) m.put(mCodeTV,6);

        for (int i=1;i<=m.size();i++){
            f.add(m.get(i));
        }
        int[] ret = new int[f.size()];
        for (int i = 0;i<f.size();i++){
           ret[i] = f.get(i);
        }
        return ret;
    }

    public int getCountField(){
        HashMap <Integer,Integer> m = new HashMap<>();
        if (mBarcode!=-1) m.put(mBarcode,0);
        if (mArticul!=-1) m.put(mArticul,1);
        if (mQuantity!=-1) m.put(mQuantity,2);
        if (mPrice!=-1) m.put(mPrice,3);
        if (mBasePrice!=-1) m.put(mBasePrice,4);
        if (mEGAIS!=-1) m.put(mEGAIS,5);
        if (mCodeTV!=-1) m.put(mCodeTV,6);

        return m.size();
    }

}