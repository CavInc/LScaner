package cav.lscaner.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StoreProductModel implements Parcelable {
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

    protected StoreProductModel(Parcel in) {
        mBarcode = in.readString();
        mName = in.readString();
        mArticul = in.readString();
    }

    public static final Creator<StoreProductModel> CREATOR = new Creator<StoreProductModel>() {
        @Override
        public StoreProductModel createFromParcel(Parcel in) {
            return new StoreProductModel(in);
        }

        @Override
        public StoreProductModel[] newArray(int size) {
            return new StoreProductModel[size];
        }
    };

    public String getBarcode() {
        return mBarcode;
    }

    public String getName() {
        return mName;
    }

    public String getArticul() {
        return mArticul;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mBarcode);
        parcel.writeString(mName);
        parcel.writeString(mArticul);
    }
}