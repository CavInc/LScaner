package cav.lscaner.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Func {

    public  static String getNowDate(String mask){
        SimpleDateFormat format = new SimpleDateFormat(mask);
        Date data = new Date();
        return format.format(data);
    }

    public static String getNowTime(){
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        return String.valueOf(h)+":"+String.valueOf(m);
    }

    public static String getDateToStr(Date date, String mask){
        SimpleDateFormat format = new SimpleDateFormat(mask);
        return format.format(date);
    }

    public static Date getStrToDate(String date,String mask){
        SimpleDateFormat format = new SimpleDateFormat(mask);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // преобразовывает array string в строку с разделителями
    public static String arrayStringToString(ArrayList<String> data,String delim){
        String[] l = data.toArray(new String[data.size()]);
        String res = null;
        for (String x:l){
            res.concat(x+delim);
        }
        return res.substring(0,res.length()-1);
    }

    // проверка на валидность серийного номера
    public static boolean checkSerialNumber(String serialNumber){
       return false;
    }


    // получаем md5 строки
    public static final String md5(String val){
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(val.getBytes());

            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);

            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}