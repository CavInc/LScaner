package cav.lscaner.data.managers;

import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.LScanerApp;

public class PreferensManager {
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

    public ArrayList<String> getScalePrefix(){
        String pref = mSharedPreferences.getString(ConstantManager.SCALE_PREFIX,"22,23");
        ArrayList<String> x = new ArrayList<>(Arrays.asList(pref.split(",")));
        return x;
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
        return mSharedPreferences.getString(ConstantManager.STORE_FILE_NAME,null);
    }

    public void setStoreFileName(String name){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.STORE_FILE_NAME,name);
        editor.apply();
    }
}