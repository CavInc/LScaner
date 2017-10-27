package cav.lscaner.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LScanerApp extends Application{
    public static SharedPreferences sSharedPreferences;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getBaseContext();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {
        return sContext;
    }

}
