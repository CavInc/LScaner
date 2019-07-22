package cav.lscaner.data.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cav.lscaner.utils.ConstantManager;

public class Request {

    // заполняем заголовок запроса
    private void setHeadParams(HttpURLConnection conn){
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
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
    public void registryLicense(String phone,String client,String device){
        String getPoint = "/api/requestlicense.php";
        try {
            URL url = new URL(ConstantManager.BASE_URL + getPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            setHeadParams(conn);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String res = getRequestMessage(conn);
                if (res !=null) {
                    JSONObject jObj = new JSONObject(res);

                }
            }
            conn.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
      запрос лицензии на уже зарегестрированное устройство
      https://kempir.com/lscanner/api/getdevlicense.php
      {
       "deviceId":"FFFFFF",
       "requestDate":"2019-07-19"
       }

     */


}