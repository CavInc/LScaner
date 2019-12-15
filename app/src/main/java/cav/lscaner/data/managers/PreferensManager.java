package cav.lscaner.data.managers;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import cav.lscaner.data.models.FieldOutFile;
import cav.lscaner.data.models.FileFieldModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.LScanerApp;

public class PreferensManager {
    private static final String FIELD_BARCODE = "FIELD_BARCODE";
    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String FIELD_ARTICUL = "FILED_ARTICUL";
    private static final String FIELD_PRICE = "FIELD_PRICE";
    private static final String FIELD_EGAIS = "FIELD_EGAIS";
    private static final String FIELD_BASE_PRICE = "FIELD_BASE_PRICE";
    private static final String FIELD_OSTATOK = "FIELD_OSTATOK";
    private static final String FIELD_CODETV = "FIELD_CODETV";

    private static final String FIELD_OUT_BARCODE = "FIELD_OUT_BARCODE";
    private static final String FIELD_OUT_QUANTITY = "FIELD_OUT_QUANTITY";
    private static final String FIELD_OUT_PRICE = "FIELD_OUT_PRICE";
    private static final String FIELD_OUT_ARTICUL = "FIELD_OUT_ARTICUL";
    private static final String FIELD_OUT_EGAIS = "FIELD_OUT_EGAIS";
    private static final String FIELD_OUT_BASE_PRICE = "FIELD_OUT_BASE_PRICE";
    private static final String FIELD_OUT_CODETV = "FIELD_OUT_CODETV";

    private static final String FIELD_OUT_EGAIS_CODE = "FIELD_OUT_EGAIS_CODE";
    private static final String FIELD_OUT_EGAIS_ARTICUL = "FIELD_OUT_EGAIS_ARTICUL";
    private static final String FIELD_OUT_EGAIS_QUANTITY = "FIELD_OUT_EGAIS_QUANTITY";
    private static final String FIELD_OUT_EGAIS_EGAIS = "FIELD_OUT_EGAIS_EGAIS";
    private static final String FIELD_OUT_EGAIS_PRICE = "FIELD_OUT_EGAIS_PRICE";
    private static final String FIELD_OUT_EGAIS_BASEPRICE = "FIELD_OUT_EGAIS_BASEPRICE";
    private static final String FIELD_OUT_EGAIS_CODETV = "FIELD_OUT_EGAIS_CODETV";


    private static final String FIELD_OUT_CP_BARCODE = "FIELD_OUT_CP_BARCODE";
    private static final String FIELD_OUT_CP_QUANTITY = "FIELD_OUT_CP_QUANTITY";
    private static final String FIELD_OUT_CP_PRICE = "FIELD_OUT_CP_PRICE";
    private static final String FIELD_OUT_CP_ARTICUL = "FIELD_OUT_CP_ARTICUL";
    private static final String FIELD_OUT_CP_BASE_PRICE = "FIELD_OUT_CP_BASE_PRICE";
    private static final String FIELD_OUT_CP_EGAIS = "FIELD_OUT_CP_EGAIS";
    private static final String FIELD_OUT_CP_CODETV = "FIELD_OUT_CP_CODETV";

    private static final String FIELD_OUT_P_BARCODE = "FIELD_OUT_P_BARCODE";
    private static final String FIELD_OUT_P_QUANTITY = "FIELD_OUT_P_QUANTITY";
    private static final String FIELD_OUT_P_PRICE = "FIELD_OUT_P_PRICE";
    private static final String FIELD_OUT_P_ARTICUL = "FIELD_OUT_P_ARTICUL";
    private static final String FIELD_OUT_P_BASE_PRICE = "FIELD_OUT_P_BASE_PRICE";
    private static final String FIELD_OUT_P_EGAIS = "FIELD_OUT_P_EGAIS";
    private static final String FIELD_OUT_P_CODETV = "FIELD_OUT_P_CODETV";

    private static final String FIELD_OUT_ALKO_BARCODE = "FIELD_OUT_ALKO_BARCODE";
    private static final String FIELD_OUT_ALKO_QUANTITY = "FIELD_OUT_ALKO_QUANTITY";
    private static final String FIELD_OUT_ALKO_PRICE = "FIELD_OUT_ALKO_PRICE";
    private static final String FIELD_OUT_ALKO_ARTICUL = "FIELD_OUT_ALKO_ARTICUL";
    private static final String FIELD_OUT_ALKO_BASE_PRICE = "FIELD_OUT_ALKO_BASE_PRICE";
    private static final String FIELD_OUT_ALKO_EGAIS = "FIELD_OUT_ALKO_EGAIS";
    private static final String FIELD_OUT_ALKO_CODETV = "FIELD_OUT_ALKO_CODETV";

    private static final String FFIN_LEN = "FFIN_LEN";
    private static final String FFIN = "FFIN_";
    private static final String FFOUT_LEN = "FFOUT_LEN";
    private static final String FFOUT = "FFOUT_";
    private static final String FFEGAIS_LEN = "FFEGAIS_LEN";
    private static final String FFEGAIS = "FFEGAIS";
    private static final String FFCP_LEN = "FFCP_LEN";
    private static final String FFCP = "FFCP";
    private static final String FFPRIHOD_LEN = "FFPRIHOD_LEN";
    private static final String FFPRIHOD = "FFPRIHOD";
    private static final String LOCAL_SERVER = "LOCAL_SERVER";

    private static final String LICENSE_TYPE ="LICENSE_TYPE";
    private static final String LICENSE_WORK_DAY = "LWD";
    private static final String LICENSE_REGISTRY_PHONE = "LRPHONE";
    private static final String LICENSE_REGISTRY_NAME = "LRNAME";
    private static final String LICENSE_REFRESH = "LREFRESH";
    private static final String LICENSE_LAST_DAY_REFRESH = "LLDR"; // дата последнего запроса лицензии
    private static final String LICENSE_ACTIVATE_DATE = "LAD";


    private SharedPreferences mSharedPreferences;

    public PreferensManager() {
        mSharedPreferences = LScanerApp.getSharedPreferences();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public String getDelimiterStoreFile(){
        return mSharedPreferences.getString(ConstantManager.DELIMETER_STORE,"#");
    }

    public String getDelimeterScanned(){
        return mSharedPreferences.getString(ConstantManager.DELIMETER_SCANER,"#");
    }

    public void setDelimiterStoreFile(String delim){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.DELIMETER_STORE,delim);
        editor.apply();
    }

    // префикс весового товара
    public ArrayList<String> getScalePrefix(){
        String pref = mSharedPreferences.getString(ConstantManager.SCALE_PREFIX,"22,23");
        ArrayList<String> x = new ArrayList<>(Arrays.asList(pref.split(",")));
        return x;
    }
    public String getScalePrefixStr(){
        return  mSharedPreferences.getString(ConstantManager.SCALE_PREFIX,"22,23");
    }

    // сохраняем чрезе стринг
    public void setScalePrefix(String prefix){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.SCALE_PREFIX,prefix);
        editor.apply();
    }
    // сохраняем через массив
    public void setScalePrefix(ArrayList<String> prefix){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

    }

    public int getSizeScale(){
        return mSharedPreferences.getInt(ConstantManager.SCALE_SIZE,7);
    }

    public void setSizeScale(int size){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ConstantManager.SCALE_SIZE,size);
        editor.apply();
    }

    public String getStoreFileName(){
        return mSharedPreferences.getString(ConstantManager.STORE_FILE_NAME,"db.txt"); // дефолтовое имя определенное заказчиком
    }

    public void setStoreFileName(String name){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.STORE_FILE_NAME,name);
        editor.apply();
    }

    // возврат демо версии
    public boolean getDemo(){
        return mSharedPreferences.getBoolean(ConstantManager.DEMO_VERSION,true);
    }

    public void setDemo(boolean mode){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ConstantManager.DEMO_VERSION,mode);
        editor.apply();
    }

    // показывает регестриционный номер
    public String getRegistrationNumber(){
        return mSharedPreferences.getString(ConstantManager.REGISTRY_NUMBER,null);
    }
    // сохраняем регистрационный номер
    public void setRegistrationNumber(String reg){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.REGISTRY_NUMBER,reg);
        editor.apply();
    }
    // кодировка файла

    public int getCodeFile(){
        return mSharedPreferences.getInt(ConstantManager.CODE_FILE,1);
    }

    public void setCodeFile(int code){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ConstantManager.CODE_FILE,code);
        editor.apply();

    }

    // настроки логальлного сервера
    public String getLocalServer(){
        return mSharedPreferences.getString(LOCAL_SERVER,null);
    }
    public void setLocalServer(String server){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LOCAL_SERVER,server);
        editor.apply();
    }


    private int[] getFileField(String fileLen,String fileField){
        int size = mSharedPreferences.getInt(fileLen,0);
        int[] l = new int[size];
        for (int i=0;i<l.length;i++) {
            l[i] = mSharedPreferences.getInt(fileField+i,-1);
        }
        return l;
    }
    private void setFileField(int[] field,String fileLen,String fileField){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i=0;i<field.length;i++){
            editor.putInt(fileField+i,field[i]);
        }
        editor.putInt(fileLen,field.length);
        editor.apply();
    }

    // настроки полей
    public FileFieldModel getFieldFileModel(){
        FileFieldModel md = new FileFieldModel(
                mSharedPreferences.getInt(FIELD_BARCODE,1),
                mSharedPreferences.getInt(FIELD_NAME,3),
                mSharedPreferences.getInt(FIELD_ARTICUL,-1),
                mSharedPreferences.getInt(FIELD_PRICE,-1),
                mSharedPreferences.getInt(FIELD_EGAIS,-1),
                mSharedPreferences.getInt(FIELD_BASE_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OSTATOK,-1),
                mSharedPreferences.getInt(FIELD_CODETV,-1)
        );
        return md;
    }

    public void setFieldFileModel(FileFieldModel field){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FIELD_BARCODE,field.getBar());
        editor.putInt(FIELD_NAME,field.getName());
        editor.putInt(FIELD_ARTICUL,field.getArticul());
        editor.putInt(FIELD_PRICE,field.getPrice());
        editor.putInt(FIELD_EGAIS,field.getEGAIS());
        editor.putInt(FIELD_BASE_PRICE,field.getBasePrice());
        editor.putInt(FIELD_OSTATOK,field.getOstatok());
        editor.putInt(FIELD_CODETV,field.getCodeTV());
        editor.apply();
    }

    public int[] getFieldFileActive(){
        int[] l = getFileField(FFIN_LEN,FFIN);
        if (l.length == 0) return new int[] {0,1,2,3,4,5,6,7};
        return l;
    }

    public void setFieldFileActive(int[] field){
        setFileField(field,FFIN_LEN,FFIN);
    }



    // насройки полей выходного файла
    public FieldOutFile getFieldOutFile(){
        FieldOutFile md = new FieldOutFile(
                mSharedPreferences.getInt(FIELD_OUT_BARCODE,1),
                mSharedPreferences.getInt(FIELD_OUT_QUANTITY,3),
                mSharedPreferences.getInt(FIELD_OUT_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_ARTICUL,2),
                mSharedPreferences.getInt(FIELD_OUT_BASE_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS,-1),
                mSharedPreferences.getInt(FIELD_OUT_CODETV,-1)
        );
        return md;
    }

    public void setFieldOutFile (FieldOutFile field){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FIELD_OUT_BARCODE,field.getBarcode());
        editor.putInt(FIELD_OUT_QUANTITY,field.getQuantity());
        editor.putInt(FIELD_OUT_PRICE,field.getPrice());
        editor.putInt(FIELD_OUT_ARTICUL,field.getArticul());
        editor.putInt(FIELD_OUT_EGAIS,field.getEGAIS());
        editor.putInt(FIELD_OUT_BASE_PRICE,field.getBasePrice());
        editor.putInt(FIELD_OUT_CODETV,field.getCodeTV());
        editor.apply();
    }

    public int[] getFieldOutActive(){
        int[] l = getFileField(FFOUT_LEN,FFOUT);
        if (l.length == 0) return new int[] {0,1,2};
        return l;
    }

    public void setFieldOutActive(int[] field){
        setFileField(field,FFOUT_LEN,FFOUT);
    }

    // настройка полей выходного файла ЕГАИС
    public FieldOutFile getFieldOutEgaisFile(){
        //FieldOutFile(int barcode, int quantity, int price, int articul, int basePrice, int EGAIS, int codeTV) {
        FieldOutFile md = new FieldOutFile(
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_CODE,1),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_QUANTITY,3),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_ARTICUL,2),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_BASEPRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_EGAIS,4),
                mSharedPreferences.getInt(FIELD_OUT_EGAIS_CODETV,-1)
        );
        return md;
    }

    public void setFieldOutEgaisFile(FieldOutFile field){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FIELD_OUT_EGAIS_CODE,field.getBarcode());
        editor.putInt(FIELD_OUT_EGAIS_ARTICUL,field.getArticul());
        editor.putInt(FIELD_OUT_EGAIS_QUANTITY,field.getQuantity());
        editor.putInt(FIELD_OUT_EGAIS_EGAIS,field.getEGAIS());
        editor.putInt(FIELD_OUT_EGAIS_PRICE,field.getPrice());
        editor.putInt(FIELD_OUT_EGAIS_BASEPRICE,field.getBasePrice());
        editor.putInt(FIELD_OUT_EGAIS_CODETV,field.getCodeTV());
        editor.apply();
    }

    public int[] getFieldEGAISActive(){
        int[] l = getFileField(FFEGAIS_LEN,FFEGAIS);
        if (l.length == 0) return new int[] {0,1,2,5};
        return l;
    }

    public void setFieldEGAISActive(int[] field){
        setFileField(field,FFEGAIS_LEN,FFEGAIS);
    }

    // настройка полей выходоного файла переоценки
    public FieldOutFile getFieldOutChangePriceFile(){
        FieldOutFile md = new FieldOutFile(
                mSharedPreferences.getInt(FIELD_OUT_CP_BARCODE,1),
                mSharedPreferences.getInt(FIELD_OUT_CP_QUANTITY,-1),
                mSharedPreferences.getInt(FIELD_OUT_CP_PRICE,3),
                mSharedPreferences.getInt(FIELD_OUT_CP_ARTICUL,2),
                mSharedPreferences.getInt(FIELD_OUT_CP_BASE_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_CP_EGAIS,-1),
                mSharedPreferences.getInt(FIELD_OUT_CP_CODETV,-1)
        );
        return md;
    }
    public void setFieldOutChangePriceFile(FieldOutFile field){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FIELD_OUT_CP_BARCODE,field.getBarcode());
        editor.putInt(FIELD_OUT_CP_QUANTITY,field.getQuantity());
        editor.putInt(FIELD_OUT_CP_PRICE,field.getPrice());
        editor.putInt(FIELD_OUT_CP_ARTICUL,field.getArticul());
        editor.putInt(FIELD_OUT_CP_EGAIS,field.getEGAIS());
        editor.putInt(FIELD_OUT_CP_BASE_PRICE,field.getBasePrice());
        editor.putInt(FIELD_OUT_CP_CODETV,field.getCodeTV());
        editor.apply();
    }

    public int[] getFieldChangePriceActive(){
        int[] l = getFileField(FFCP_LEN,FFCP);
        if (l.length == 0) return new int[]{0,1,3};
        return l;
    }

    public void setFieldChangePriceActive(int[] field){
        setFileField(field,FFCP_LEN,FFCP);
    }

    // настройка полей выходного файла прихода
    public FieldOutFile getFieldOutPrixodFile() {
        FieldOutFile md = new FieldOutFile(
                mSharedPreferences.getInt(FIELD_OUT_P_BARCODE,1),
                mSharedPreferences.getInt(FIELD_OUT_P_QUANTITY,3),
                mSharedPreferences.getInt(FIELD_OUT_P_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_P_ARTICUL,2),
                mSharedPreferences.getInt(FIELD_OUT_P_BASE_PRICE,4),
                mSharedPreferences.getInt(FIELD_OUT_P_EGAIS,-1),
                mSharedPreferences.getInt(FIELD_OUT_P_CODETV,-1)
        );
        return md;
    }

    public void setFieldOutPrixodFile(FieldOutFile field){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FIELD_OUT_P_BARCODE,field.getBarcode());
        editor.putInt(FIELD_OUT_P_QUANTITY,field.getQuantity());
        editor.putInt(FIELD_OUT_P_PRICE,field.getPrice());
        editor.putInt(FIELD_OUT_P_ARTICUL,field.getArticul());
        editor.putInt(FIELD_OUT_P_EGAIS,field.getEGAIS());
        editor.putInt(FIELD_OUT_P_BASE_PRICE,field.getBasePrice());
        editor.putInt(FIELD_OUT_P_CODETV,field.getCodeTV());
        editor.apply();
    }

    public int[] getFieldPrihoxActive(){
        int[] l = getFileField(FFPRIHOD_LEN,FFPRIHOD);
        if (l.length == 0) return new int[]{0,1,2,4};
        return l;
    }

    public void setFieldPrihoxPriceActive(int[] field){
        setFileField(field,FFPRIHOD_LEN,FFPRIHOD);
    }

    // насройки полей выходного файла alkomarok
    public FieldOutFile getFieldOutAlcomarkFile(){
        FieldOutFile md = new FieldOutFile(
                mSharedPreferences.getInt(FIELD_OUT_ALKO_BARCODE,1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_QUANTITY,-1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_ARTICUL,-1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_BASE_PRICE,-1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_EGAIS,-1),
                mSharedPreferences.getInt(FIELD_OUT_ALKO_CODETV,-1)
        );
        return md;
    }

    public String getLicenseRegistryName(){
        return mSharedPreferences.getString(LICENSE_REGISTRY_NAME,null);
    }

    public void setLicenseRegistryName(String name){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LICENSE_REGISTRY_NAME,name);
        editor.apply();
    }

    // тип лицензии
    public int getLicenseType(){
        return mSharedPreferences.getInt(LICENSE_TYPE,0);
    }

    public void setLicenseType(int type){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LICENSE_TYPE,type);
        editor.apply();
    }

    // телефон
    public String getLicenseRegistryPhone(){
        return mSharedPreferences.getString(LICENSE_REGISTRY_PHONE,null);
    }

    public void setLicenseRegistryPhone(String phone){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LICENSE_REGISTRY_PHONE,phone);
        editor.apply();
    }

    // количество дней
    public int getLicenseWorkDay(){
        return mSharedPreferences.getInt(LICENSE_WORK_DAY,30);
    }

    public void setLicenseWorkDay(int workDay){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LICENSE_WORK_DAY,workDay);
        editor.apply();
    }

    // запрос лицензии
    public boolean getLicenseRefresh(){
        return mSharedPreferences.getBoolean(LICENSE_REFRESH,true);
    }

    public void setLicenseRefresh(boolean val){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(LICENSE_REFRESH,val);
        editor.apply();
    }

    // дата последнего запроса лицензии
    public String getLicenseLastDayRefresh(){
        return mSharedPreferences.getString(LICENSE_LAST_DAY_REFRESH,null);
    }

    public void setLicenseLastDayRefresh(String date){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LICENSE_LAST_DAY_REFRESH,date);
        editor.apply();
    }

    // дата активации лицензии с сервера
    public String getLicenseActivate(){
        return mSharedPreferences.getString(LICENSE_ACTIVATE_DATE,null);
    }

    public void setLicenseActivate(String date) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LICENSE_ACTIVATE_DATE,date);
        editor.apply();
    }

}