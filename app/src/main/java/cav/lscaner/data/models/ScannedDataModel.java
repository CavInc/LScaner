package cav.lscaner.data.models;

import cav.lscaner.utils.Func;

/**
 * модель для данных сканирования
 */
public class ScannedDataModel {
    private int mIdFile;
    private int mPosId;
    private String mBarCode;
    private String mName;
    private Float mQuantity;
    private String mArticul = null;
    private Double mSumm = 0.0;
    private Double mPrice = 0.0;
    private Double mOstatok = 0.0;

    public ScannedDataModel(int idFile,int posId, String barCode, String name, Float quantity) {
        mIdFile = idFile;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mPosId = posId;
    }

    public ScannedDataModel(int idFile, int posId, String barCode, String name, Float quantity, String articul, Double price) {
        mIdFile = idFile;
        mPosId = posId;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mArticul = articul;
        mPrice = price;
        mSumm = Double.valueOf(Func.round((float) (quantity * price),2));
    }

    public ScannedDataModel(int idFile, int posId, String barCode, String name, Float quantity, String articul, Double price, Double ostatok) {
        mIdFile = idFile;
        mPosId = posId;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mArticul = articul;
        mPrice = price;
        mOstatok = ostatok;
        mSumm = Double.valueOf(Func.round((float) (quantity * price),2));
    }

    public int getIdFile() {
        return mIdFile;
    }

    public String getBarCode() {
        return mBarCode;
    }

    public String getName() {
        return mName;
    }

    public Float getQuantity() {
        return mQuantity;
    }

    public int getPosId() {
        return mPosId;
    }

    public String getArticul() {
        return mArticul;
    }

    public Double getSumm() {
        return mSumm;
    }

    public Double getPrice() {
        return mPrice;
    }

    public Double getOstatok() {
        return mOstatok;
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
            ScannedDataModel tmp = (ScannedDataModel) obj;
            //if (tmp.getSportsman().equals(this.mSportsman)) return true;
            if (tmp.getBarCode().equals(this.mBarCode)){
                //Log.d("ABE","True "+this.mSpId+" "+tmp.mSpId);
                return true;
            }
            else return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + mBarCode.hashCode();
        return result;
    }
}