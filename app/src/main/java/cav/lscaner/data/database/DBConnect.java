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
        String sql="select sts.head_id,sts.barcode,sts.pos_id,sts.quantity,sp.name from "+DBHelper.SCAN_TABLE_SPEC+" sts \n" +
                " left join "+DBHelper.STORE_PRODUCT+" sp on sts.barcode = sp.barcode where sts.head_id="+idFile+" order by sts.pos_id desc";
        return database.rawQuery(sql,null);
    }

    // добавили позицию в файл
    public void addScannedPositon(int idFile,String barcode,Float quantity,int position){
        open();

        if (position == -1) {
            Cursor cursor = database.rawQuery("select max(pos_id)+1 as pos from " + DBHelper.SCAN_TABLE_SPEC + " where head_id=" + idFile, null);
            cursor.moveToFirst();
            position = cursor.getInt(0);
            if (position == 0) position = 1;
        }

        ContentValues values = new ContentValues();
        values.put("head_id",idFile);
        values.put("barcode",barcode);
        values.put("quantity",quantity);
        values.put("pos_id", position);
        open();
        database.insertWithOnConflict(DBHelper.SCAN_TABLE_SPEC,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }
    // удалили позицию в файле
    public void delScannedPosition(int idFile,int posId){
        open();
        database.delete(DBHelper.SCAN_TABLE_SPEC,"head_id="+idFile+" and pos_id="+posId,null);
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

    //Удалить файл
    public void deleteFile(int idFile){
        open();
        database.delete(DBHelper.SCAN_TABLE,"id="+idFile,null);
        database.delete(DBHelper.SCAN_TABLE_SPEC,"head_id="+idFile,null);
        close();
    }
}
