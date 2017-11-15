package cav.lscaner.data.managers;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import cav.lscaner.data.models.FileFieldModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.LScanerApp;

public class PreferensManager {
    private static final String FIELD_BARCODE = "FIELD_BARCODE";
    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String FIELD_ARTICUL = "FILED_ARTICUL";
    private static final String FIELD_PRICE = "FIELD_PRICE";
    private static final String FIELD_EGAIS = "FIELD_EGAIS";
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

    // сохраняем id файла

    // настроки полей
    public FileFieldModel getFieldFileModel(){
        FileFieldModel md = new FileFieldModel(
                mSharedPreferences.getInt(FIELD_BARCODE,1),
                mSharedPreferences.getInt(FIELD_NAME,3),
                mSharedPreferences.getInt(FIELD_ARTICUL,-1),
                mSharedPreferences.getInt(FIELD_PRICE,-1),
                mSharedPreferences.getInt(FIELD_EGAIS,1)
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
        editor.apply();
    }


}