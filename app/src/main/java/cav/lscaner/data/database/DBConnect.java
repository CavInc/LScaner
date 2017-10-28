package cav.lscaner.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cav.lscaner.data.models.StoreProductModel;

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

    public Cursor getScannedData(int idFile){
        String sql="select sts.head_id,sts.barcode,sts.quantity,sp.name from "+DBHelper.SCAN_TABLE_SPEC+" sts \n" +
                " left join "+DBHelper.STORE_PRODUCT+" sp on sts.barcode = sp.barcode";
        return database.rawQuery(sql,null);
    }

    // добавили позицию в файл
    public void addScannedPositon(int idFile,String barcode,Float quantity){
        ContentValues values = new ContentValues();
        values.put("head_id",idFile);
        values.put("barcode",barcode);
        values.put("quantity",quantity);
        open();
        database.insertWithOnConflict(DBHelper.SCAN_TABLE_SPEC,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    // поиск по списку товаров
    public StoreProductModel searchStore(String barcode){
        open();
        Cursor cursor = database.query(DBHelper.STORE_PRODUCT,new String[]{"barcode","name"},"barcode="+barcode,null,null,null,null);
        cursor.moveToFirst();
        StoreProductModel model = null;
        if (cursor.getCount() != 0) {
            model = new StoreProductModel(cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")));
        }
        close();
        return model;
    }

    // добавить в список товаров
    public void addStore(String barcode,String name){
        open();
        ContentValues values = new ContentValues();
        values.put("barcode",barcode);
        values.put("name",name);
        database.insertWithOnConflict(DBHelper.STORE_PRODUCT,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }
}
