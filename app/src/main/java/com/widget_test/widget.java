package com.widget_test;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.Permissions;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static java.security.AccessController.getContext;

/**
 * Created by ivanmarroquin on 4/1/17.
 */

public class widget extends AppWidgetProvider {
    double latitude=0,longitude=0;
    float LOCATION_REFRESH_DISTANCE = 1;
    long LOCATION_REFRESH_TIME = 100;
    LocationManager mLocationManager;
    Context x;
    Context z;
    int wid=0;
    public double currentTemperature;
    public double change;
    public int extremeChange;
    public int weatherChange;
    public int currentWeather;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            wid=widgetId;
        }
        checkpermissions(context);
        z=context;

    }

    public void callapi() {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(z);
        Log.w("Weather Info2", "Latitude: " + latitude);
        Log.w("Weather Info2", "Longitude: " + longitude);
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&mode=xml&APPID=9d1155745f4c476fe4b71e23a478e4e9";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TextView forecastt = (TextView) findViewById(R.id.forecast);
                        //forecastt.setText("Response is: " + response);
                        try {
                            Log.w("about to process","o-o");
                            processData(response);
                            Log.w("Processed","o-o");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "It didnt work");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(z);
        //RemoteViews remoteViews = new RemoteViews(z.getPackageName(), R.layout.widget);
        //ComponentName thisWidget = new ComponentName(z, widget.class);
        //remoteViews.setTextViewText(R.id.text2, "Lat:"+permissions.lat);
        //appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        //remoteViews.setTextViewText(R.id.text3, "Long:"+permissions.longg);
        //appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        //updateviews(z, thisWidget, appWidgetManager);
    }

    public void processData(String data) throws IOException, XmlPullParserException {
        WeatherChanges wc = new WeatherChanges(data);

        currentTemperature = wc.getCurrentTemperature();
        change = wc.temperatureChange();
        extremeChange = wc.extremeTemperatureChange();
        weatherChange = wc.getWeatherChange();
        currentWeather = wc.getCurrentWeather();



        Log.w("Current weather",currentWeather+" ");
        Log.w("Weather Info2", "Current Temperature: " + currentTemperature);
        Log.w("Weather Info2", "Temperature change: " + change);
        Log.w("Weather Info2", "The extreme change is: " + extremeChange);
        Log.w("Weather Info2", "The weather change: " + weatherChange);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(z);
        //RemoteViews remoteViews = new RemoteViews(z.getPackageName(), R.layout.widget);
        ComponentName thisWidget = new ComponentName(z, widget.class);
        //remoteViews.setTextViewText(R.id.text2, "Lat:"+permissions.lat);
        //appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        //remoteViews.setTextViewText(R.id.text3, "Long:"+permissions.longg);
        //appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        updateviews(z, thisWidget, appWidgetManager);

    }

    public void updateviews(Context context,ComponentName widgetId, AppWidgetManager appWidgetManager){


        //Log.w("Weather Info", "Temperature change: " + change);
        //Log.w("Weather Info", "The extreme change is: " + extremeChange);
        //Log.w("Weather Info", "The weather change: " + weatherChange);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        String tempChange = "";
        if(change >= 0)
        {
            tempChange = "+ " + change+"ºC";
            remoteViews.setTextViewText(R.id.tempup, tempChange);
        }
        else
        {
            tempChange = "- " + (-1*change)+"ºC";
            remoteViews.setTextViewText(R.id.tempdown, tempChange);
        }
        String number = currentTemperature+"ºC ";

        remoteViews.setTextViewText(R.id.textView, number);
        switch(currentWeather){
            case 0:
                remoteViews.setImageViewResource(R.id.weather,R.drawable.sunny);

                remoteViews.setTextViewText(R.id.text2,"Sunny/Clear");
                break;
            case 1:
                remoteViews.setImageViewResource(R.id.weather,R.drawable.summerrain);
                remoteViews.setTextViewText(R.id.text2,"Rainy");
                break;
            case 2:
                remoteViews.setImageViewResource(R.id.weather,R.drawable.storm);
                remoteViews.setTextViewText(R.id.text2,"Stormy");
                break;
            case 3:
                remoteViews.setImageViewResource(R.id.weather,R.drawable.snowing);
                remoteViews.setTextViewText(R.id.text2,"Snowy");
                break;
        }
        remoteViews.setViewVisibility(R.id.hot, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.cold, View.INVISIBLE);
        if(change>4){
            remoteViews.setViewVisibility(R.id.hot, View.VISIBLE);
        }else if(change<-4){
            remoteViews.setViewVisibility(R.id.cold, View.VISIBLE);
        }

        if(weatherChange!=1 && weatherChange!=2 &&weatherChange!=3 ){
            remoteViews.setViewVisibility(R.id.rainy, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.thunder, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.snow, View.INVISIBLE);
            remoteViews.setImageViewResource(R.id.weather2,R.drawable.sunny);
            if(extremeChange==1){
                notification(context,appWidgetManager,wid,"Alerta! Habra un cambio importante de temperatura en las proximas horas (+"+change+" grados)");
            }else if(extremeChange==-1){
                notification(context,appWidgetManager,wid,"Alerta! Habra un cambio importante de temperatura en las proximas horas (-"+change+" grados)");
            }
        }else{
            switch (weatherChange){
                case 1:
                    remoteViews.setViewVisibility(R.id.rainy, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.thunder, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.snow, View.INVISIBLE);
                    remoteViews.setImageViewResource(R.id.weather2,R.drawable.summerrain);
                    notification(context,appWidgetManager,wid,"Alerta! Habra lluvia en las proximas horas.");
                    break;
                case 2:
                    remoteViews.setViewVisibility(R.id.rainy, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.thunder, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.snow, View.INVISIBLE);
                    remoteViews.setImageViewResource(R.id.weather2,R.drawable.storm);
                    //remoteViews.setInt(R.id.thunder, "setAlpha", 100);
                    notification(context,appWidgetManager,wid,"Alerta! Habra una tormenta electrica en las proximas horas.");
                    break;
                case 3:
                    remoteViews.setViewVisibility(R.id.rainy, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.thunder, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.snow, View.VISIBLE);
                    remoteViews.setImageViewResource(R.id.weather2,R.drawable.snowing);
                    //remoteViews.setInt(R.id.snow, "setAlpha", 100);
                    notification(context,appWidgetManager,wid,"Alerta! Habra nieve en las proximas horas.");
                    break;
            }
        }

        /*Intent intent = new Intent(context, widget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        //remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
    }

    public void checkpermissions(Context context){
        if(permissions.lat == 0) {
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w(":D", "sad bois");
                return;
            } else {
                Log.w(":D", "happi bois");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, mLocationListener);
            }
        }
    }
    public void notification(Context cnt,AppWidgetManager appwidgetmngr,int widgetId,String message){

        Uri sound = Uri.parse("android.resource://"
                + cnt.getPackageName() + "/" + R.raw.demonstrative);


        Notification.Builder notificactionBuilder = new Notification.Builder(cnt);
        notificactionBuilder.setStyle(new Notification.BigTextStyle(notificactionBuilder).bigText(message).setBigContentTitle("Weather Next").setSummaryText("Inf."))
                //coloca el icono de la notificacion
                .setSmallIcon(R.drawable.cloud)
                //coloca el titulo de la notificacion
                .setContentTitle("Weather Next")
                //coloca el sonido del objeto tipo uri creado anteriormente
                .setSound(sound)
                //se encarga de dar prioridad a las notificaciones, si es high
                //sale como un heads up notification
                .setPriority(Notification.PRIORITY_HIGH)
                //crea los patrones de vibracion
                .setVibrate(new long [] {0, 250, 100,50,10,20,40,60,100})
                //despliega mensaje
                .setContentText(message);
        int iNoticationId = 1;
        //crea un notification manager
        NotificationManager mNotiMgr =
                (NotificationManager) cnt.getSystemService(NOTIFICATION_SERVICE);
        //el notification manager manda a llamar a la variable notification builder
        mNotiMgr.notify(iNoticationId, notificactionBuilder.build());
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.w("Location","GET");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            permissions.lat = latitude;
            permissions.longg = longitude;

            callapi();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}