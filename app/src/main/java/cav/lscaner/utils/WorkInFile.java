package cav.lscaner.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FileFieldModel;
import cav.lscaner.data.models.ScannedDataModel;

public class WorkInFile {
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/138-urok-75-hranenie-dannyh-rabota-s-fajlami.html

    private String codeStr;

    public WorkInFile(Integer codeFile){
        switch (codeFile){
            case 1:
                codeStr="windows-1251";
                break;
            case 2:
                codeStr="UTF-8";
                break;
        }

    }

    private String savedFile;

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
    public void saveFile(int idFile,String fname, DataManager manager){
        if (manager.isExternalStorageWritable()){
            String delim = manager.getPreferensManager().getDelimiterStoreFile();
            String path = manager.getStorageAppPath();
            Log.d("WC",path);
            File outfile = new File(path,fname);
            ArrayList<ScannedDataModel> models = manager.getScannedData(idFile);

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
                for (ScannedDataModel l : models){
                    String cls = new String((l.getBarCode()+delim+l.getQuantity()).getBytes("UTF-8"),codeStr);
                    bw.write(cls+"\r\n");
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            savedFile = outfile.toString();
        }

    }

    public String getSavedFile() {
        return savedFile;
    }

    public void loadProductFile(String fname,DataManager manager){
        // проверяем доступность SD
        if (!manager.isExternalStorageWritable()) return;
        String delim = manager.getPreferensManager().getDelimiterStoreFile();
        String path = manager.getStorageAppPath();
        //Log.d("LC",path);
        File stFile = new File(path,fname);

        // удаляем старые данные
        manager.getDB().deleteStore();
        // получили список номеров полей
        FileFieldModel fieldFile = manager.getPreferensManager().getFieldFileModel();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    new FileInputStream(stFile),codeStr);
            //BufferedReader br = new BufferedReader(new FileReader(stFile));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String str = "";
            String[] lm;
            // читаем содержимое
            manager.getDB().open();
            SQLiteDatabase db = manager.getDB().getDatabase();
            db.beginTransaction();
            try {
                while ((str = br.readLine()) != null) {
                    //Log.d("LC STR :", str);
                    if (str.length() != 0) {
                        str = str.replaceAll(delim+delim,delim+" "+delim);
                        lm = str.split(delim);
                        //manager.getDB().addStore(lm[0],lm[2]);
                        // обработать поля здесь или передать их в процедуру дальшн  ?
                        // что делать с товаром без кода но с егаис маркой.
                        if (lm[0].length() != 0) {
                            manager.getDB().addStoreMulti(lm[0], lm[2]);
                        }
                    }
                }
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
            manager.getDB().close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
}