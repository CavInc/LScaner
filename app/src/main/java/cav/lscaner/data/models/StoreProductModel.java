package cav.lscaner.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StoreProductModel implements Parcelable {
    private String mBarcode;
    private String mName;
    private String mArticul = null;
    private Double mPrice = 0.0;
    private Double mOstatok = 0.0;
    private String mCodeTV;

    public StoreProductModel(String barcode, String name) {
        mBarcode = barcode;
        mName = name;
    }

    public StoreProductModel(String barcode, String name, String articul) {
        mBarcode = barcode;
        mName = name;
        mArticul = articul;
    }

    public StoreProductModel(String barcode, String name, String articul, Double price) {
        mBarcode = barcode;
        mName = name;
        mArticul = articul;
        mPrice = price;
    }

    public StoreProductModel(String barcode, String name, String articul, Double price, Double ostatok) {
        mBarcode = barcode;
        mName = name;
        mArticul = articul;
        mPrice = price;
        mOstatok = ostatok;
    }

    protected StoreProductModel(Parcel in) {
        mBarcode = in.readString();
        mName = in.readString();
        mArticul = in.readString();
        mPrice = in.readDouble();
        mOstatok = in.readDouble();
        mCodeTV = in.readString();
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

    public Double getPrice() {
        return mPrice;
    }

    public Double getOstatok() {
        return mOstatok;
    }

    public String getCodeTV() {
        return mCodeTV;
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
        parcel.writeDouble(mPrice);
        parcel.writeDouble(mOstatok);
        parcel.writeString(mCodeTV);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
                /* Удостоверимся, что ссылки имеют тот же самый тип */
        if(!(getClass() == obj.getClass())) {
            return false;
        }else {
            StoreProductModel tmp = (StoreProductModel) obj;
            if (tmp.getName().toUpperCase().equals(this.mName.toUpperCase())) return true;
            if (tmp.mName.toUpperCase().indexOf(this.getName().toUpperCase())!=-1){
                return true;
            }
        }
        return false;
    }
}