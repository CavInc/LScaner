package cav.lscaner.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedDataModel;

public class WorkInFile {
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/138-urok-75-hranenie-dannyh-rabota-s-fajlami.html

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
                    bw.write(l.getBarCode()+delim+l.getQuantity()+"\r\n");
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
}