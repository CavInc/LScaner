package cav.lscaner.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import cav.lscaner.data.managers.DataManager;

public class WorkInFile {
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/138-urok-75-hranenie-dannyh-rabota-s-fajlami.html

    public File getLocalAppFile(Context context,String fileName){
        return new File(context.getFilesDir(),fileName);
    }

    public File getTempFile(Context context, String fileName) {
        File file = null;
        try {
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // сохраняет файл на ...
    public void saveFile(String fname, DataManager manager){


    }

}