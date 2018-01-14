package cav.lscaner.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.ConstantManager;

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

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    //-------------------- Запросы к базе ------------------------------

    public Cursor getScannedFile(){
        return database.query(DBHelper.SCAN_TABLE,
                new String [] {"id","name_file","date","time","type"},
                null,null,null,null,"date");
    }

    public void addFileName(String name,String date,String time,int idFile,int type_file){
        open();
        ContentValues values = new ContentValues();
        values.put("name_file",name);
        values.put("date",date);
        values.put("time",time);
        values.put("type",type_file);
        if (idFile != -1) {
            values.put("id",idFile);
        }
        database.insertWithOnConflict(DBHelper.SCAN_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    public Cursor getScannedData(int idFile,int mode){
        String sql;
        if (mode == ConstantManager.FILE_TYPE_PRODUCT) {
            sql = "select sts.head_id,sts.barcode,sts.pos_id,sts.quantity,sp.name,sp.articul,sp.baseprice,sp.price,sp.ostatok,sp.price as oldprice from " +
                    DBHelper.SCAN_TABLE_SPEC + " sts \n" +
                    " left join " + DBHelper.STORE_PRODUCT + " sp on sts.barcode = sp.barcode and sts.articul=sp.articul where sts.head_id=" + idFile + " order by sts.pos_id desc";
        } else if (mode == ConstantManager.FILE_TYPE_PRIHOD || mode == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            sql = "select sts.head_id,sts.barcode,sts.pos_id,sts.quantity,sp.name,sp.articul,sts.baseprice,sts.price,sp.ostatok,sp.price as oldprice from " +
                    DBHelper.SCAN_TABLE_SPEC + " sts \n" +
                    " left join " + DBHelper.STORE_PRODUCT + " sp on sts.barcode = sp.barcode and sts.articul=sp.articul where sts.head_id=" + idFile + " order by sts.pos_id desc";
        } else {
            sql = "select sts.head_id,sts.barcode,sts.pos_id,sts.quantity,sp.name,sp.articul,sp.baseprice,sp.price,sp.ostatok,sp.price as oldprice from " +
                    DBHelper.SCAN_TABLE_SPEC + " sts \n" +
                    " left join " + DBHelper.STORE_PRODUCT + " sp on sts.barcode = sp.egais where sts.head_id=" + idFile + " order by sts.pos_id desc";
        }
        return database.rawQuery(sql,null);
    }

    // добавили позицию в файл
    public void addScannedPositon(int idFile,String barcode,Float quantity,int position,String articul){
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
        values.put("articul",articul);
        values.put("pos_id", position);

        database.insertWithOnConflict(DBHelper.SCAN_TABLE_SPEC,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    // добавили позицию в файл
    public void addScannedPricePosition(int idFile, StoreProductModel productModel, int position) {
        open();

        if (position == -1) {
            Cursor cursor = database.rawQuery("select max(pos_id)+1 as pos from " + DBHelper.SCAN_TABLE_SPEC + " where head_id=" + idFile, null);
            cursor.moveToFirst();
            position = cursor.getInt(0);
            if (position == 0) position = 1;
        }

        ContentValues values = new ContentValues();
        values.put("head_id",idFile);
        values.put("barcode",productModel.getBarcode());
        values.put("articul",productModel.getArticul());
        values.put("pos_id", position);
        values.put("price",productModel.getPrice());

        database.insertWithOnConflict(DBHelper.SCAN_TABLE_SPEC,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    public void addScannedPrihodPosition(int idFile, StoreProductModel productModel, int position) {
        open();

        if (position == -1) {
            Cursor cursor = database.rawQuery("select max(pos_id)+1 as pos from " + DBHelper.SCAN_TABLE_SPEC + " where head_id=" + idFile, null);
            cursor.moveToFirst();
            position = cursor.getInt(0);
            if (position == 0) position = 1;
        }

        ContentValues values = new ContentValues();
        values.put("head_id",idFile);
        values.put("barcode",productModel.getBarcode());
        values.put("articul",productModel.getArticul());
        values.put("pos_id", position);
        values.put("price",productModel.getPrice());
        values.put("quantity",productModel.getQuantity());
        values.put("baseprice",productModel.getPrice());

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
    public ArrayList<StoreProductModel> searchStoreArray(String barcode){
        ArrayList<StoreProductModel> rec = new ArrayList<>();
        open();
        Cursor cursor = database.query(DBHelper.STORE_PRODUCT,
                new String[]{"barcode","name","articul","price","ostatok","baseprice","egais","codetv"},
                "barcode='"+barcode+"'",null,null,null,null);
        while (cursor.moveToNext()){
            rec.add(new StoreProductModel(cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("articul")),
                    cursor.getDouble(cursor.getColumnIndex("price")),
                    cursor.getDouble(cursor.getColumnIndex("ostatok")),
                    cursor.getString(cursor.getColumnIndex("codetv")),
                    cursor.getDouble(cursor.getColumnIndex("baseprice"))));

        }
        close();
        return rec;
    }

    // поиск по АЛКОКОДУ
    public StoreProductModel searchStoreEgais(String alcocode){
        StoreProductModel model = null;
        open();
        Cursor cursor = database.query(DBHelper.STORE_PRODUCT,new String[]{"egais","name"},"egais='"+alcocode+"'",null,null,null,null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            model = new StoreProductModel(cursor.getString(cursor.getColumnIndex("egais")),
                    cursor.getString(cursor.getColumnIndex("name")));
        }
        close();
        return model;
    }
    public ArrayList<StoreProductModel> searchStoreEgaisArray(String alcocode){
        ArrayList<StoreProductModel> rec = new ArrayList<>();
        open();
        Cursor cursor = database.query(DBHelper.STORE_PRODUCT,
                new String[]{"egais","name","articul","price","ostatok","barcode"},
                "egais='"+alcocode+"'",null,null,null,null);
        while (cursor.moveToNext()){
            rec.add(new StoreProductModel(cursor.getString(cursor.getColumnIndex("egais")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("articul")),
                    cursor.getDouble(cursor.getColumnIndex("price")),
                    cursor.getDouble(cursor.getColumnIndex("ostatok"))));
        }
        close();
        return rec;
    }

    // очистить списко товаров

    public void deleteStore(){
        open();
        database.delete(DBHelper.STORE_PRODUCT,null,null);
        database.execSQL("update sqlite_sequence set seq=1 where name='store_product';");
        close();
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
    // добавить в список товаров в мульти режиме
    public void addStoreMulti(String barcode,String name,String articul,Double price,String egais,Double baseprice,Float ostatok){
        ContentValues values = new ContentValues();
        values.put("barcode",barcode);
        values.put("name",name);
        values.put("articul",articul);
        values.put("price",price);
        values.put("egais",egais);
        values.put("baseprice",baseprice);
        values.put("ostatok",ostatok);
        database.insert(DBHelper.STORE_PRODUCT,null,values);
    }

    //Удалить файл
    public void deleteFile(int idFile){
        open();
        database.delete(DBHelper.SCAN_TABLE,"id="+idFile,null);
        database.delete(DBHelper.SCAN_TABLE_SPEC,"head_id="+idFile,null);
        close();
    }

    // количество файлов в базе
    public int getCountFile(){
        open();
        Cursor cursor = database.rawQuery("select count(1) from "+DBHelper.SCAN_TABLE,null);
        cursor.moveToFirst();
        int res = cursor.getInt(0);
        close();
        return res;
    }

    // количество записей в базе
    public int getCountRecInFile(int idFile){
        open();
        Cursor cursor = database.rawQuery("select count(1) from "+DBHelper.SCAN_TABLE_SPEC+" where head_id ="+idFile,null);
        cursor.moveToFirst();
        int res = cursor.getInt(0);
        close();
        return res;
    }

    // показать все записи товара
    public Cursor getStoreProduct(){
        return database.query(DBHelper.STORE_PRODUCT,
                new String[]{"barcode","name","articul","price","ostatok","egais"},null,null,null,null,"articul");
    }

    // возвращаем записи для обновления
    public Cursor getLinkedRec(){
        String sql = "select st.id,stc.pos_id,sp.articul from scan_table st\n" +
                "  JOIN scan_table_spec stc on st.id=stc.head_id\n" +
                "  join store_product sp on stc.barcode=sp.barcode\n" +
                "where st.type=0";
        return database.rawQuery(sql,null);
    }
    // обновляем запись в файле
    public void updateArticul(int idFile,int pos,String articul){
        ContentValues values = new ContentValues();
        values.put("articul",articul);
        database.update(DBHelper.SCAN_TABLE_SPEC,values,"head_id="+idFile+" and  pos_id="+pos,null);
    }

    public void refreshAllFile(){
        String sql = "insert or replace into scan_table_spec (head_id,pos_id,barcode,articul,quantity) \n" +
                "select st.id as head_id,stc.pos_id,stc.barcode,sp.articul,stc.quantity from scan_table st \n" +
                "  JOIN scan_table_spec stc on st.id=stc.head_id\n" +
                "  join store_product sp on stc.barcode=sp.barcode \n" +
                "where st.type=0 ";
        //database.rawQuery(sql,null);
        database.execSQL(sql);
    }



}
