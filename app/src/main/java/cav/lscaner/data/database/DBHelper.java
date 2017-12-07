package cav.lscaner.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1 ;
    public static final String DATABASE_NAME = "lscanerv2.db3";

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
                    " id integer not null primary key AUTOINCREMENT,"+
                    "barcode text,"+
                    "name text,"+
                    "articul text,"+
                    "price float default 0,"+
                    "egais text,"+
                    "baseprice float default 0,"+
                    "ostatok float default 0)");

            db.execSQL("CREATE INDEX \""+STORE_PRODUCT+"_EA\" on "+STORE_PRODUCT+" (egais ASC)");
            db.execSQL("CREATE INDEX \""+STORE_PRODUCT+"_BR\" on "+STORE_PRODUCT+" (barcode ASC)");

            db.execSQL("create table "+SCAN_TABLE+"("+
                    "id integer not null primary key AUTOINCREMENT," +
                    "name_file text,"+
                    "date text,"+
                    "time text,"+
                    "type integer default 0)"); // 0 - товары 1 -егаис 2 - поступление 3 - переоценка

            db.execSQL("create table "+SCAN_TABLE_SPEC+"("+
                    "head_id integer not null,"+
                    "pos_id integer not null,"+
                    "barcode text not null,"+
                    "articul text,"+
                    "quantity number default 0,"+
                    "primary key(head_id,pos_id,barcode))");
            db.execSQL("CREATE INDEX \""+SCAN_TABLE_SPEC+"_BA\" on "+SCAN_TABLE_SPEC+" (barcode,articul)");
        }

    }
}