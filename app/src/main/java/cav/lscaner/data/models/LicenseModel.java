package cav.lscaner.data.models;

import com.journeyapps.barcodescanner.Util;

import java.util.Date;

import cav.lscaner.utils.Func;

public class LicenseModel {
    private int mLicenseType;
    private int mLicenseDay;
    private String mActionLicense; // дата лицензии
    private Date mActionLicenseData;

    private boolean mStatus;
    private String mMsg;

    public LicenseModel(int licenseType, int licenseDay, String actionLicense) {
        mLicenseType = licenseType;
        mLicenseDay = licenseDay;
        mActionLicense = actionLicense;
        mActionLicenseData = Func.getStrToDate(actionLicense,"yyyy-MM-dd");
        mStatus = true;
    }

    public LicenseModel(int licenseType, int licenseDay, String actionLicense, boolean status, String msg) {
        mLicenseType = licenseType;
        mLicenseDay = licenseDay;
        mActionLicense = actionLicense;
        mActionLicenseData = Func.getStrToDate(actionLicense,"yyyy-MM-dd");
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

    public Date getActionLicenseData() {
        return mActionLicenseData;
    }
}