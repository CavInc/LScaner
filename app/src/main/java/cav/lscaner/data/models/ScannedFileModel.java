package cav.lscaner.data.models;

import java.util.Date;

public class ScannedFileModel {
    private int mId;
    private String mName;
    private Date mCreateDate;
    private String mTime;
    private int mType;

    public ScannedFileModel(int id, String name, Date createDate, String time) {
        mId = id;
        mName = name;
        mCreateDate = createDate;
        mTime = time;
    }

    public ScannedFileModel(int id, String name, Date createDate, String time, int type) {
        mId = id;
        mName = name;
        mCreateDate = createDate;
        mTime = time;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Date getCreateDate() {
        return mCreateDate;
    }

    public String getTime() {
        return mTime;
    }

    public int getType() {
        return mType;
    }
}

