package com.example.djelassi.boushaba.meteo;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by oualid on 10/12/2016.
 */




public class ApiDonnees {


        private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=";
        private static String city = "%s";
        private static String key = "&units=metric&APPID=4b901fa3f006e19cb885d82755f8c695";

        private static String URL = OPEN_WEATHER_MAP_API + city + key;

        public static JSONObject getJSON( String city){
            try {

                URL url = new URL(String.format(URL, city));
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();



                if(HttpURLConnection.HTTP_OK == connection.getResponseCode()) {

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp="";
                    while((tmp=reader.readLine())!=null)
                        json.append(tmp).append("\n");
                    reader.close();

                    JSONObject data = new JSONObject(json.toString());

                    // This value will be 404 if the request was not
                    // successful
                    if (data.getInt("cod") != 200) {

                        return null;
                    }
                    Log.d(TAG,"json downloaded !");
                    return data;
                }

            }catch(IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
}
