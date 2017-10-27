package cav.lscaner.data.managers;

import android.content.SharedPreferences;

import cav.lscaner.utils.LScanerApp;

public class PreferensManager {
    private SharedPreferences mSharedPreferences;

    public PreferensManager() {
        mSharedPreferences = LScanerApp.getSharedPreferences();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}