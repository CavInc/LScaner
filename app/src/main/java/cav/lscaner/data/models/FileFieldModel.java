package cav.lscaner.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FileFieldModel implements Parcelable {
    private int mBar;
    private int mName;
    private int mArticul;
    private int mPrice;
    private int mEGAIS;
    private int mBasePrice;
    private int mOstatok;


    public FileFieldModel(int bar, int name, int articul, int price, int EGAIS) {
        mBar = bar;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mEGAIS = EGAIS;
    }

    public FileFieldModel(int bar, int name, int articul, int price, int EGAIS, int basePrice, int ostatok) {
        mBar = bar;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mEGAIS = EGAIS;
        mBasePrice = basePrice;
        mOstatok = ostatok;
    }

    public FileFieldModel(Parcel parcel) {
        mBar = parcel.readInt();
        mName = parcel.readInt();
        mArticul = parcel.readInt();
        mPrice = parcel.readInt();
        mEGAIS = parcel.readInt();
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
    }
}
