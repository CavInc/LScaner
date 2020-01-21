package cav.lscaner.data.models;

public class GetLicenseModel {
    private boolean mSuccess;
    private String mMesg;
    private String mRequestServer;
    private boolean mLicense;

    public GetLicenseModel(boolean success, String mesg) {
        mSuccess = success;
        mMesg = mesg;
    }

    public GetLicenseModel(boolean success, String mesg, String requestServer) {
        mSuccess = success;
        mMesg = mesg;
        mRequestServer = requestServer;
    }

    public GetLicenseModel (boolean success, String mesg, String requestServer,boolean license){
        mSuccess = success;
        mMesg = mesg;
        mRequestServer = requestServer;
        mLicense = license;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMesg() {
        return mMesg;
    }

    public String getRequestServer() {
        return mRequestServer;
    }

    public boolean isLicense() {
        return mLicense;
    }
}