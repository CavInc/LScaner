package cav.lscaner.data.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cav.lscaner.data.managers.PreferensManager;
import cav.lscaner.data.models.GetLicenseModel;
import cav.lscaner.data.models.LicenseModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class Request {

    private PreferensManager mPreferensManager;
    // заполняем заголовок запроса
    private void setHeadParams(HttpURLConnection conn){
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
    }

    public Request(PreferensManager pref){
        mPreferensManager = pref;
    }

    private String getRequestMessage( HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        StringBuilder x = new StringBuilder();
        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            x.append(output);
        }
        return x.toString();
    }

    /*
       запрос регистрация нового устройства
       https://kempir.com/lscanner/api/requestlicense.php

       {
         "phone":"+79126555055",
         "clientName":"ЧП РОГА И КОПЫТА",
         "deviceId":"FFAAFF"
       }

     */
    public GetLicenseModel registryLicense(String phone, String client, String device){
        GetLicenseModel ret = null;
        String getPoint = "/api/requestlicense.php";
        try {
            URL url = new URL(ConstantManager.BASE_URL + getPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            setHeadParams(conn);

            JSONObject jdata = new JSONObject();
            jdata.put("phone",phone);
            jdata.put("clientName",client);
            jdata.put("deviceId",device);

            OutputStream os = conn.getOutputStream();
            os.write(jdata.toString().getBytes("UTF-8"));
            os.flush();
            os.close();


            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String res = getRequestMessage(conn);
                if (res !=null) {
                    JSONObject jObj = new JSONObject(res);
                    String status = jObj.getString("status");
                    System.out.println(status);
                    ret = new GetLicenseModel(true,jObj.getString("message"),status);
                }
            } else {
                String res = conn.getResponseMessage();
                ret = new GetLicenseModel(false,res);
            }
            conn.disconnect();
        } catch (Exception e){
            e.printStackTrace();
            ret = new GetLicenseModel(false,e.getLocalizedMessage());
            return ret;
        }
        return ret;
    }


    /*
      запрос лицензии на уже зарегестрированное устройство
      https://kempir.com/lscanner/api/getdevlicense.php
      {
       "deviceId":"FFFFFF",
       "requestDate":"2019-07-19"
       }

     */
    public LicenseModel getLicense(String deviceID){
        LicenseModel ret = null;
        String getPoint = "/api/getdevlicense.php";

        try {
            URL url = new URL(ConstantManager.BASE_URL + getPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            setHeadParams(conn);

            JSONObject jdata = new JSONObject();
            jdata.put("deviceId",deviceID);
            jdata.put("requestDate","");

            OutputStream os = conn.getOutputStream();
            os.write(jdata.toString().getBytes("UTF-8"));
            os.flush();
            os.close();



            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String res = getRequestMessage(conn);
                if (res !=null) {
                    JSONObject jObj = new JSONObject(res);
                    if (jObj.has("data")) {
                        JSONArray data = jObj.getJSONArray("data");
                        if (data.length() != 0) {
                            JSONObject lx = (JSONObject) data.get(0);
                            int licType = lx.getInt("license_type");
                            int workDay = lx.getInt("work_day_license");
                            String actionLic = lx.getString("action_license_date");
                            ret = new LicenseModel(licType,workDay,actionLic);
                        } else {
                           ret = new LicenseModel(false,ConstantManager.LICENSE_NO_LICENSE);
                        }
                    }
                }
            } else {
                String res = conn.getResponseMessage();
                ret = new LicenseModel(false,res);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return new LicenseModel(false,e.getLocalizedMessage());
        }
        return ret;
    }

    /*
       удалить
     */
    public void deleteDevice(String deviceId){
        String getPoint = "/api/deletedevice.php";

        Map <String,String> params = new HashMap<>();
        params.put("id",deviceId);

        try {
            URL url = new URL(ConstantManager.BASE_URL + getPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(Func.getParamsString(params));
            out.flush();
            out.close();




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}