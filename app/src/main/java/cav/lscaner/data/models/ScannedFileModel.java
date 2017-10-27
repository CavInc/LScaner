package cav.lscaner.data.models;

import java.util.Date;

public class ScannedFileModel {
    private int mId;
    private String mName;
    private Date mCreateDate;
    private String mTime;

    public ScannedFileModel(int id, String name, Date createDate, String time) {
        mId = id;
        mName = name;
        mCreateDate = createDate;
        mTime = time;
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
}

