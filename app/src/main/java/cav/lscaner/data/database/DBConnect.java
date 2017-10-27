package cav.lscaner.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBConnect {

    private SQLiteDatabase database;
    private DBHelper mDBHelper;

    public DBConnect(Context context){
        mDBHelper = new DBHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION);
    }

    public void open(){
        database = mDBHelper.getWritableDatabase();
    }

    public void close(){
        if (database!=null) {
            database.close();
        }
    }
    public boolean isOpen(){
        return database.isOpen();
    }

    //-------------------- Запросы к базе ------------------------------

    public Cursor getScannedFile(){
        return database.query(DBHelper.SCAN_TABLE,new String [] {"id","name_file","date","time"},null,null,null,null,"date");
    }

    public void addFileName(String name,String date,String time){
        open();
        ContentValues values = new ContentValues();
        values.put("name_file",name);
        values.put("date",date);
        values.put("time",time);
        database.insert(DBHelper.SCAN_TABLE,null,values);
        close();
    }

}
