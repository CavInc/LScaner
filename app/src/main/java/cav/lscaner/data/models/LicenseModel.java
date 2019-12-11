package cav.lscaner.data.models;

public class LicenseModel {
    private int mLicenseType;
    private int mLicenseDay;
    private String mActionLicense;

    private boolean mStatus;
    private String mMsg;

    public LicenseModel(int licenseType, int licenseDay, String actionLicense) {
        mLicenseType = licenseType;
        mLicenseDay = licenseDay;
        mActionLicense = actionLicense;
    }

    public LicenseModel(int licenseType, int licenseDay, String actionLicense, boolean status, String msg) {
        mLicenseType = licenseType;
        mLicenseDay = licenseDay;
        mActionLicense = actionLicense;
        mStatus = status;
        mMsg = msg;
    }

    public LicenseModel(boolean status, String msg) {
        mStatus = status;
        mMsg = msg;
    }

    public int getLicenseType() {
        return mLicenseType;
    }

    public int getLicenseDay() {
        return mLicenseDay;
    }

    public String getActionLicense() {
        return mActionLicense;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public String getMsg() {
        return mMsg;
    }
}