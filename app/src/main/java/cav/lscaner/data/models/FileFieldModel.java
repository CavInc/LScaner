package cav.lscaner.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FileFieldModel implements Parcelable {
    private int mBar;
    private int mName;
    private int mArticul;// код
    private int mPrice;
    private int mEGAIS;
    private int mBasePrice;
    private int mOstatok;
    private int mCodeTV; // артикул

    private int mMaxIndex = 0;


    public FileFieldModel() {
        mBar = -1;
        mName = -1;
        mArticul = -1;// код
        mPrice = -1;
        mEGAIS = -1;
        mBasePrice = -1;
        mOstatok = -1;
        mCodeTV = -1; // артикул
    }

    public FileFieldModel(int bar, int name, int articul, int price, int EGAIS) {
        mBar = bar;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mEGAIS = EGAIS;
        setMaxIndex();
    }

    public FileFieldModel(int bar, int name, int articul, int price, int EGAIS, int basePrice, int ostatok) {
        mBar = bar;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mEGAIS = EGAIS;
        mBasePrice = basePrice;
        mOstatok = ostatok;
        setMaxIndex();
    }

    public FileFieldModel(int bar, int name, int articul, int price, int EGAIS, int basePrice, int ostatok, int codeTV) {
        mBar = bar;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mEGAIS = EGAIS;
        mBasePrice = basePrice;
        mOstatok = ostatok;
        mCodeTV = codeTV;
    }

    public FileFieldModel(Parcel parcel) {
        mBar = parcel.readInt();
        mName = parcel.readInt();
        mArticul = parcel.readInt();
        mPrice = parcel.readInt();
        mEGAIS = parcel.readInt();
        mBasePrice = parcel.readInt();
        mOstatok = parcel.readInt();
        mCodeTV = parcel.readInt();
        setMaxIndex();
    }

    private void setMaxIndex(){
        if (mBar > mMaxIndex) mMaxIndex = mBar;
        if (mName > mMaxIndex) mMaxIndex = mName;
        if (mArticul > mMaxIndex) mMaxIndex = mArticul;
        if (mPrice > mMaxIndex) mMaxIndex = mPrice;
        if (mEGAIS > mMaxIndex) mMaxIndex = mEGAIS;
        if (mBasePrice > mMaxIndex) mMaxIndex = mBasePrice;
        if (mOstatok > mMaxIndex) mMaxIndex = mOstatok;
        if (mCodeTV > mMaxIndex) mMaxIndex = mCodeTV;
    }

    public int getBar() {
        return mBar;
    }

    public int getName() {
        return mName;
    }

    public int getArticul() {
        return mArticul;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getEGAIS() {
        return mEGAIS;
    }

    public void setBar(int bar) {
        mBar = bar;
    }

    public void setName(int name) {
        mName = name;
    }

    public void setArticul(int articul) {
        mArticul = articul;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public void setEGAIS(int EGAIS) {
        mEGAIS = EGAIS;
    }

    public int getBasePrice() {
        return mBasePrice;
    }

    public void setBasePrice(int basePrice) {
        mBasePrice = basePrice;
    }

    public int getOstatok() {
        return mOstatok;
    }

    public void setOstatok(int ostatok) {
        mOstatok = ostatok;
    }

    public int getMaxIndex() {
        return mMaxIndex;
    }

    public int getCodeTV() {
        return mCodeTV;
    }

    // возвращает код
    // "Штрих-код","Код","Наименование",Остаток","Цена","Цена закупочная","Код ЕГАИС","Артикул"
    public int get(int i){
        switch (i){
            case 0:
                return mBar;
            case 1:
                return mArticul;
            case 2:
                return mName;
            case 3:
                return mOstatok;
            case 4:
                return mPrice;
            case 5:
                return mBasePrice;
            case 6:
                return mEGAIS;
            case 7:
                return mCodeTV;
        }
        return -1;
    }

    public static final Creator<FileFieldModel> CREATOR = new Creator<FileFieldModel>(){

        @Override
        public FileFieldModel createFromParcel(Parcel parcel) {
            return new FileFieldModel(parcel);
        }

        @Override
        public FileFieldModel[] newArray(int size) {
            return new FileFieldModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mBar);
        parcel.writeInt(mName);
        parcel.writeInt(mArticul);
        parcel.writeInt(mPrice);
        parcel.writeInt(mEGAIS);
        parcel.writeInt(mBasePrice);
        parcel.writeInt(mOstatok);
        parcel.writeInt(mCodeTV);
    }
}
