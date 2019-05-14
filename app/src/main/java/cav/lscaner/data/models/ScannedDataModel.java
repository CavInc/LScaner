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
    private Double mBasePrice = 0.0;
    private String mCodeArticul = null;
    private String mEgais;
    private Double mOldPrice = 0.0;

    private int fileType; // тип файла


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

    public ScannedDataModel(String barCode, String articul) {
        mBarCode = barCode;
        mArticul = articul;
    }

    public ScannedDataModel(String name) {
        mName = name;
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

    public ScannedDataModel(int idFile, int posId, String barCode, String name, Float quantity, String articul, Double price, Double ostatok,Double oldPrice) {
        mIdFile = idFile;
        mPosId = posId;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mArticul = articul;
        mPrice = price;
        mOstatok = ostatok;
        mOldPrice = oldPrice;
        mSumm = Double.valueOf(Func.round((float) (quantity * price),2));
    }

    public ScannedDataModel(int idFile, int posId, String barCode, String name, Float quantity,
                            String articul, Double price, Double ostatok, Double oldPrice, Double basePrice) {
        mIdFile = idFile;
        mPosId = posId;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mArticul = articul;
        mPrice = price;
        mOstatok = ostatok;
        mOldPrice = oldPrice;
        mBasePrice = basePrice;
        mSumm = Double.valueOf(Func.round((float) (quantity * price),2));
    }

    public ScannedDataModel(int idFile, int posId, String barCode, String name, Float quantity,
                            String articul, Double price, Double ostatok, Double oldPrice,
                            Double basePrice,int fileType) {
        mIdFile = idFile;
        mPosId = posId;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
        mArticul = articul;
        mPrice = price;
        mOstatok = ostatok;
        mOldPrice = oldPrice;
        mBasePrice = basePrice;
        this.fileType = fileType;
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

    public Double getBasePrice() {
        return mBasePrice;
    }

    public String getCodeArticul() {
        return mCodeArticul;
    }

    public String getEgais() {
        return mEgais;
    }

    public int getFileType() {
        return fileType;
    }

    public Double getOldPrice() {
        return mOldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        mOldPrice = oldPrice;
    }

    @Override
    public String toString() {
        if (mName == null ){
            return "Новый";
        }
        return mName;
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
            if (this.getArticul() != null && tmp.getArticul() != null) {
                if (tmp.getBarCode().equals(this.mBarCode) && tmp.getArticul().equals(this.mArticul)) {
                    return true;
                }
            } else if (tmp.getBarCode().equals(this.mBarCode)){
                //Log.d("ABE","True "+this.mSpId+" "+tmp.mSpId);
                return true;
            } else if (tmp.getName().toUpperCase().equals(this.mName.toUpperCase())) {
                return true;
            } else if (tmp.getName().toUpperCase().indexOf(this.mName.toUpperCase()) != -1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + mBarCode.hashCode();
        return result;
    }
}