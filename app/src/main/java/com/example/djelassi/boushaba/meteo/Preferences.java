package com.example.djelassi.boushaba.meteo;

/**
 * Created by oualid on 10/12/2016.
 */
import android.app.Activity;
import android.content.SharedPreferences;

public class Preferences {

    SharedPreferences prefs;

    public Preferences(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Paris, FR");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}