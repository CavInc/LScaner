package cav.lscaner.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1 ;
    public static final String DATABASE_NAME = "lscaner.db3";

    public static final String SCAN_TABLE_SPEC = "scan_table_spec";
    public static final String SCAN_TABLE = "scan_table";
    public static final String STORE_PRODUCT = "store_product";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updatedDB(db,0,DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updatedDB(db,oldVersion,newVersion);
    }

    private void updatedDB(SQLiteDatabase db,int oldVersion,int newVersion){
        if (oldVersion < 1 ){
            db.execSQL("create table "+STORE_PRODUCT+"("+
                    "barcode text not null primary key,"+
                    "name text,"+
                    "articul text,"+
                    "price float default 0,"+
                    "egais text,"+
                    "baseprice float default 0,"+
                    "ostatok float defaulut 0)");

            db.execSQL("CREATE INDEX \""+STORE_PRODUCT+"_EA\" on "+STORE_PRODUCT+" (egais ASC)");

            db.execSQL("create table "+SCAN_TABLE+"("+
                    "id integer not null primary key AUTOINCREMENT," +
                    "name_file text,"+
                    "date text,"+
                    "time text,"+
                    "type integer default 0)"); // 0 - товары 1 -егаис

            db.execSQL("create table "+SCAN_TABLE_SPEC+"("+
                    "head_id integer not null,"+
                    "pos_id integer not null,"+
                    "barcode text not null,"+
                    "quantity number default 0,"+
                    "primary key(head_id,pos_id,barcode))");
        }

    }
}