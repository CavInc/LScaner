package cav.lscaner.data.managers;

import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;

import java.util.ArrayList;

import cav.lscaner.data.database.DBConnect;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.LScanerApp;

public class DataManager{
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferensManager mPreferensManager;

    private DBConnect mDB;

    public static DataManager getInstance() {
        if (INSTANCE==null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public DataManager(){
        this.mPreferensManager = new PreferensManager();
        this.mContext = LScanerApp.getContext();
        mDB = new DBConnect(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    public PreferensManager getPreferensManager() {
        return mPreferensManager;
    }

    public DBConnect getDB() {
        return mDB;
    }

    // служебные запросы разные
    public String getAndroidID(){
        return null;
    }

    // запросы к базе данных
    // запрос списка файлов
    public ArrayList<ScannedFileModel> getScannedFile(){
        ArrayList<ScannedFileModel> rec = new ArrayList<>();
        mDB.open();
        Cursor cursor = mDB.getScannedFile();
        while (cursor.moveToNext()){
            rec.add(new ScannedFileModel(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name_file")),
                    Func.getStrToDate(cursor.getString(cursor.getColumnIndex("date")),"yyyy-MM-dd"),
                    cursor.getString(cursor.getColumnIndex("time"))));
        }
        mDB.close();
        return rec;
    }

    // отсканированые данные
    public ArrayList<ScannedDataModel> getScannedData(int idFile){
        ArrayList<ScannedDataModel> rec = new ArrayList<>();
        mDB.open();
        Cursor cursor = mDB.getScannedData(idFile);
        while (cursor.moveToNext()){
            rec.add(new ScannedDataModel(
                    cursor.getInt(cursor.getColumnIndex("head_id")),
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getFloat(cursor.getColumnIndex("quantity"))
            ));

        }
        mDB.close();
        return rec;
    }


}