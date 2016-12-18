package com.example.djelassi.boushaba.meteo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oualid on 10/12/2016.
 */

public class MeteoFragment extends Fragment {
    Typeface weatherFont;
    public static String info_sms;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    public MeteoFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meteo, container, false);
        cityField = (TextView)rootView.findViewById(R.id.ville_field);
        updatedField = (TextView)rootView.findViewById(R.id.maj);
        detailsField = (TextView)rootView.findViewById(R.id.infos_supplementaires);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.temperature);
        weatherIcon = (TextView)rootView.findViewById(R.id.image_temps);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new Preferences(getActivity()).getCity());
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = ApiDonnees.getJSON(city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.ville_non_trouve),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidité: " + main.getString("humidity") + "%" +
                            "\n" + "Pression: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.1f", main.getDouble("temp"))+ " ℃");


            info_sms ="Salut, voilà le temps qu'il fait à " + json.getString("name")+","+"\n\n"+ "température: "+String.format("%.1f", main.getDouble("temp"))+ " ℃" +
                    "\n" + "Humidité: " + main.getString("humidity") + "%" + "\n" + "Pression: " + main.getString("pressure") + " hPa";

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Dernière mise à jour: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "certaines données n'ont pas été trouvées");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.soleil);
            } else {
                icon = getActivity().getString(R.string.nuit);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.tonnerre);
                    break;
                case 3 : icon = getActivity().getString(R.string.precipitations);
                    break;
                case 7 : icon = getActivity().getString(R.string.brume);
                    break;
                case 8 : icon = getActivity().getString(R.string.nuageu);
                    break;
                case 6 : icon = getActivity().getString(R.string.neige);
                    break;
                case 5 : icon = getActivity().getString(R.string.pluie);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }
}