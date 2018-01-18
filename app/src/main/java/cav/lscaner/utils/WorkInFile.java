package cav.lscaner.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FieldOutFile;
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
    public void saveFile(int idFile,String fname, DataManager manager,int filetype){
        if (manager.isExternalStorageWritable()){
            String delim = manager.getPreferensManager().getDelimiterStoreFile();
            String path = manager.getStorageAppPath();

            FieldOutFile fieldFile = null;

            switch (filetype) {
                case ConstantManager.FILE_TYPE_PRODUCT:
                    fieldFile = manager.getPreferensManager().getFieldOutFile();
                    break;
                case ConstantManager.FILE_TYPE_EGAIS:
                    fieldFile = manager.getPreferensManager().getFieldOutEgaisFile();
                    break;
                case ConstantManager.FILE_TYPE_CHANGE_PRICE:
                    fieldFile = manager.getPreferensManager().getFieldOutChangePriceFile();
                    break;
                case ConstantManager.FILE_TYPE_PRIHOD:
                    fieldFile = manager.getPreferensManager().getFieldOutPrixodFile();
                    break;
                case ConstantManager.FILE_TYPE_ALCOMARK:
                    fieldFile = manager.getPreferensManager().getFieldOutAlcomarkFile();
                    break;
            }

            //Log.d("WC",path);
            File outfile = new File(path,fname);
            ArrayList<ScannedDataModel> models = manager.getScannedData(idFile,filetype);

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
                for (ScannedDataModel l : models){
                    String cls = getFieldStr(l,fieldFile,delim);
                    cls = new String(cls.getBytes("UTF-8"),codeStr);
                    bw.write(cls+"\r\n");
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            savedFile = outfile.toString();
        }

    }

    private String getFieldStr(ScannedDataModel model, FieldOutFile fieldFile,String delim) {
        String[] br = new String[fieldFile.getCountField()];
        if (fieldFile.getBarcode() != -1){
            br[fieldFile.getBarcode()-1] = model.getBarCode();
        }
        if (fieldFile.getQuantity() != -1) {
            br[fieldFile.getQuantity()-1] = String.valueOf(model.getQuantity());
        }
        if (fieldFile.getArticul() != -1 && model.getArticul() != null) {
            br[fieldFile.getArticul()-1] = model.getArticul();
        }
        if (fieldFile.getPrice() != -1) {
            br[fieldFile.getPrice()-1] = String.valueOf(model.getPrice());
        }
        if (fieldFile.getBasePrice() != -1) {
            br[fieldFile.getBasePrice()-1] = String.valueOf(model.getBasePrice());
        }
        if (fieldFile.getCodeTV() != -1 ) {
            br[fieldFile.getCodeTV()-1] = String.valueOf(model.getCodeArticul());
        }
        if (fieldFile.getEGAIS() != -1) {
            br[fieldFile.getEGAIS()-1] = model.getEgais();
        }

        int iMax = br.length - 1;

        StringBuilder out = new StringBuilder();
        for (int i = 0;i<br.length;i++){
            if (br[i] != null) {
                out.append(br[i]);
            }
            if (i < iMax) {
                out.append(delim);
            }
        }

        return out.toString().replaceFirst("#+$", "");
    }

    public String getSavedFile() {
        return savedFile;
    }

    public int loadProductFile(String fname,DataManager manager){
        // проверяем доступность SD
        if (!manager.isExternalStorageWritable()) return ConstantManager.RET_NO_SD;
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
            String articul = null;
            String egais = null;
            Double price = 0.0;
            Double baseprice = 0.0;
            Float ostatok = 0.0f;
            // читаем содержимое
            manager.getDB().open();
            SQLiteDatabase db = manager.getDB().getDatabase();
            db.beginTransaction();
            try {
                while ((str = br.readLine()) != null) {
                    //Log.d("LC STR :", str);
                    str = str.trim(); // добавить в V1 тоже
                    if (str.length() != 0) {
                        str = str.replaceAll(delim+delim,delim+" "+delim);
                        str = str.replaceAll(delim+delim,delim+" "+delim);
                        char lastS =  str.toCharArray()[str.length() - 1];
                        if (lastS == '#') {
                            str = str + " ";
                        }
                        lm = str.split(delim);
                        if (lm.length < fieldFile.getMaxIndex()) {
                            Log.d("WF","OOPS !!!!");
                            return ConstantManager.RET_NO_FIELD_MANY;
                        }
                        /*
                        if (lm[0].equals("4670012690986")) {
                            Log.d("WK","OKPS");
                        }
                        */

                        //manager.getDB().addStore(lm[0],lm[2]);
                        // обработать поля здесь или передать их в процедуру дальшн  ?
                        // что делать с товаром без кода но с егаис маркой.
                        if (fieldFile.getArticul() == -1){
                            articul ="";
                        } else {
                            articul = lm[fieldFile.getArticul()-1];
                        }
                        if (fieldFile.getPrice() == -1) {
                            price = 0.0;
                        } else {
                            price = Double.valueOf(lm[fieldFile.getPrice()-1].replaceAll("\\s+",""));
                        }
                        if (fieldFile.getEGAIS() == -1) {
                            egais = null;
                        } else {
                            egais = lm[fieldFile.getEGAIS()-1];
                        }
                        if (fieldFile.getBasePrice() == -1) {
                            baseprice = 0.0;
                        } else {
                            baseprice = Double.valueOf(lm[fieldFile.getBasePrice()-1].replaceAll("\\s+",""));
                        }
                        if (fieldFile.getOstatok() == -1) {
                            ostatok = 0.0f;
                        } else {
                            ostatok = Float.valueOf(lm[fieldFile.getOstatok()-1]);
                        }

                        //Log.d("WK",lm[fieldFile.getEGAIS()-1]);
                        if (lm[fieldFile.getBar()-1].length() != 0) {
                            //Log.d("WE",lm[fieldFile.getBar()-1]+lm[fieldFile.getName()-1]);
                            manager.getDB().addStoreMulti(lm[fieldFile.getBar()-1],
                                    lm[fieldFile.getName()-1],articul,price,egais,
                                    baseprice,ostatok);
                        }
                    }
                }
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
            manager.getDB().close();
            br.close();
            stFile.delete(); // удалили файл загрузки

            // обновили файлы для привязки названий
            manager.refreshDataInFiles();
        } catch (Exception e) {
            e.printStackTrace();
            manager.setLastError(e.getLocalizedMessage());
            return ConstantManager.RET_ERROR;
        }
        return ConstantManager.RET_OK;

    }
}