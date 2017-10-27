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
}