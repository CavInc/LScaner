package cav.lscaner.utils;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.LicenseModel;

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
        return formatTime(h,m);
    }

    // собираем строку
    public static String formatTime(int hour,int minute){
        String h = String.valueOf(hour);
        String m = String.valueOf(minute);
        if (h.length()!=2) h = "0"+h;
        if (m.length()!=2) m = "0"+m;
        return h+":"+m;
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
    public static boolean checkSerialNumber(String serialNumber,String deviceID){
        /*
        String mx = md5(deviceID);
        StringBuffer mxx = new StringBuffer(mx);
        mxx.reverse();
        mx = md5(mxx.toString());
        mxx = new StringBuffer(mx);
        mx = md5(mxx.toString());
        mx = mx.substring(mx.length()-8);
        */
        String mx = md5(deviceID);
        StringBuffer mxx = new StringBuffer(mx);
        mxx.reverse();
        mx = md5(mx.toString());
        mxx = new StringBuffer(mx);
        mxx.append("soli");
        mxx.reverse();
        mx = md5(mxx.toString());
        mx = mx.substring(mx.length()-8);

        if (mx.equals(serialNumber)) return true;
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

    public static BigDecimal roundUp(double value, int digits){
        return new BigDecimal(""+value).setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal roundUp(float value, int digits){
        return new BigDecimal(""+value).setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    public static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    // 20N00001CGUMZYCB99J1NKN31105001000056NQQMS5VP4HTF5SB46ZSQQJD8BNJP891
    // 22N00000XOKBM724XT22N0S41008003008626KE949YKAWNGY007FGGV2N0ER26EY6TJ

    // выделяет алко код
    public static String toEGAISAlcoCode(String pdf417){
        String verPCEgais = pdf417.substring(0,2);
        String rawCode = pdf417.substring(3,19);
        BigInteger big = new BigInteger(rawCode, 36);
        return String.format("%019d",big);
    }

    public static String viewOstatok(Double ostatok) {
        DecimalFormat format = new DecimalFormat("###.###");
        return format.format(ostatok).replaceAll(",",".");
    }

    public static boolean checkEAN(String barcode){
        //return true; // debug


        int easum = 0;
        if (barcode.length() == 8 ){
            barcode = "00000" + barcode;
        }
        // костыль для UPC-A которые не с 0
        if (barcode.length() == 12) {
            barcode = "0" + barcode;
        }

        if (barcode.length() != 13) return false;
        char[] x = barcode.toCharArray();
        for (int i=0;i<13;i++){
            easum = easum + Integer.parseInt(String.valueOf(x[i]))*(i%2*2+1);
        }
        if ((easum % 10) == 0) return true;
        return false;

    }

    public static void addLog(String fname,String msg) {
        try {
            FileWriter writer = new FileWriter(fname, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(msg+'\n');
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    //Convert pixel to dip
    public static int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    */

    public static Integer[] intArrayToIntegerArray(int[] value){
        Integer[] ret = new Integer[value.length];
        for (int i=0;i<value.length;i++){
            ret[i]=value[i];
        }
        return ret;
    }

    public static ArrayList<Integer> intArrayToArrayList(int[] value){
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0 ;i<value.length;i++){
            ret.add(value[i]);
        }
        return ret;
    }

    // вибрация
    public static void playMessage(Context context){
        long mills = 300L;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);
    }


    // формируем имя файла
    public static String createFileName(String fileName,int fileType){
        String fname = "_"+fileName+".txt";
        int filetype = fileType;

        if (filetype == ConstantManager.FILE_TYPE_PRODUCT) {
            fname = ConstantManager.PREFIX_FILE_TOVAR+fname;
        }
        if (filetype == ConstantManager.FILE_TYPE_EGAIS) {
            fname = ConstantManager.PREFIX_FILE_EGAIS+fname;
        }
        if (filetype == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            fname = ConstantManager.PREFIX_FILE_CHANGEPRICE+fname;
        }
        if (filetype == ConstantManager.FILE_TYPE_PRIHOD) {
            fname = ConstantManager.PREFIX_FILE_PRIHOD+fname;
        }
        if (filetype == ConstantManager.FILE_TYPE_ALCOMARK) {
            fname = ConstantManager.PREFIX_FILE_ALCOMARK+fname;
        }
        return fname;
    }

    // количество дней между текущей датой и переданной
    public static int getCountDay(Date date){
        Date currentDate = new Date();
        long diff = currentDate.getTime() - date.getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    // сораняем лицензию
    public static void storeLicense(DataManager manager, LicenseModel licenseModel){
        manager.getPreferensManager().setLicenseType(licenseModel.getLicenseType());
        manager.getPreferensManager().setLicenseWorkDay(licenseModel.getLicenseDay());
        manager.getPreferensManager().setLicenseActivate(licenseModel.getActionLicense());
        // так как мы получили лицензию то активуруем даже если она временная
        manager.getPreferensManager().setDemo(false);
        manager.getPreferensManager().setLicenseLastDayRefresh(getDateToStr(new Date(),"yyyy-MM-dd"));
    }

    public static String getParamsString(Map <String,String> param) throws UnsupportedEncodingException {
        StringBuilder args = new StringBuilder("&");
        for(Map.Entry<String,String> entry : param.entrySet()){
            args.append(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return args.toString();
    }


}