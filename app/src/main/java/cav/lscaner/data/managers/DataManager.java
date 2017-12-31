package cav.lscaner.data.managers;

import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;

import cav.lscaner.data.database.DBConnect;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.LScanerApp;

public class DataManager{
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferensManager mPreferensManager;

    private DBConnect mDB;

    private String mLastError;

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
    // запрос данных устройства
    public String getAndroidID(){
        return  Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // проверяем включен ли интернетик
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // возвращает путь к локальной папки приложения
    public String getStorageAppPath(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;
        File path = new File (Environment.getExternalStorageDirectory(), "LScannerV2");
        if (! path.exists()) {
            if (!path.mkdirs()){
                return null;
            }
        }
        return path.getPath();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public String getLastError() {
        return mLastError;
    }

    public void setLastError(String mlastError) {
        this.mLastError = mlastError;
    }

    // =============================== запросы к базе данных =======================================
    // запрос списка файлов
    public ArrayList<ScannedFileModel> getScannedFile(){
        ArrayList<ScannedFileModel> rec = new ArrayList<>();
        mDB.open();
        Cursor cursor = mDB.getScannedFile();
        while (cursor.moveToNext()){
            rec.add(new ScannedFileModel(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name_file")),
                    Func.getStrToDate(cursor.getString(cursor.getColumnIndex("date")),"yyyy-MM-dd"),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getInt(cursor.getColumnIndex("type"))));
        }
        mDB.close();
        return rec;
    }

    // отсканированые данные
    public ArrayList<ScannedDataModel> getScannedData(int idFile,int mode){
        ArrayList<ScannedDataModel> rec = new ArrayList<>();
        mDB.open();
        Cursor cursor = mDB.getScannedData(idFile,mode);
        while (cursor.moveToNext()){
            rec.add(new ScannedDataModel(
                    cursor.getInt(cursor.getColumnIndex("head_id")),
                    cursor.getInt(cursor.getColumnIndex("pos_id")),
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getFloat(cursor.getColumnIndex("quantity")),
                    cursor.getString(cursor.getColumnIndex("articul")),
                    cursor.getDouble(cursor.getColumnIndex("price")),
                    cursor.getDouble(cursor.getColumnIndex("ostatok")),
                    cursor.getDouble(cursor.getColumnIndex("oldprice")),
                    cursor.getDouble(cursor.getColumnIndex("baseprice"))
            ));
        }
        mDB.close();
        return rec;
    }

    // список товаров
    public ArrayList<StoreProductModel> getStoreProdect(){
        ArrayList<StoreProductModel> rec = new ArrayList<>();
        mDB.open();
        Cursor cursor = mDB.getStoreProduct();
        while (cursor.moveToNext()){
            rec.add(new StoreProductModel(
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("articul")),
                    cursor.getDouble(cursor.getColumnIndex("price")),
                    cursor.getDouble(cursor.getColumnIndex("ostatok"))
            ));
        }
        mDB.close();
        return rec;
    }

    // обновляет данные после загрузки файла базы данных
    // в существующих файлах
    public void refreshDataInFiles(){
        mDB.open();
        mDB.refreshAllFile();
        /*
        Cursor  cursor = mDB.getLinkedRec();
        mDB.getDatabase().beginTransaction();
        while (cursor.moveToNext()){
            mDB.updateArticul(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("pos_id")),
                    cursor.getString(cursor.getColumnIndex("articul")));

        }
        mDB.getDatabase().setTransactionSuccessful();
        */
        mDB.close();
    }


}