package cav.lscaner.data.models;

public class GetLicenseModel {
    private boolean mSuccess;
    private String mMesg;
    private String mRequestServer;

    public GetLicenseModel(boolean success, String mesg) {
        mSuccess = success;
        mMesg = mesg;
    }

    public GetLicenseModel(boolean success, String mesg, String requestServer) {
        mSuccess = success;
        mMesg = mesg;
        mRequestServer = requestServer;
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
}