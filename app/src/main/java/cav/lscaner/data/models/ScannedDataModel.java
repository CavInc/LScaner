package cav.lscaner.data.models;

/**
 * модель для данных сканирования
 */
public class ScannedDataModel {
    private int mIdFile;
    private String mBarCode;
    private String mName;
    private Float mQuantity;

    public ScannedDataModel(int idFile, String barCode, String name, Float quantity) {
        mIdFile = idFile;
        mBarCode = barCode;
        mName = name;
        mQuantity = quantity;
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