package com.track.gps.gpstrack;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by ADITYA on 1/25/2018.
 */


public class LoactionService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    MyServiceReceiver myServiceReceiver;
    static String state = "STOPPED";

    //from MainActivity to LocationService
    final static String ACTION_START_TRACKING = "ACTION_START_TRACKING";
    final static String ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING";
    final static String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    /**/


    //from LocationService to MainActivity
    final static String ACTION_UPDATE_POSITION = "ACTION_UPDATE_POSITION";
    final static String ACTION_UPDATE_NMEA = "ACTION_UPDATE_NMEA";
    final static String KEY_LATITUDE_FROM_SERVICE = "KEY_LATITUDE_FROM_SERVICE";
    final static String KEY_LONGITUDE_FROM_SERVICE = "KEY_LONGITUDE_FROM_SERVICE";
    final static String KEY_ALTITUDE_FROM_SERVICE = "KEY_ALTITUDE_FROM_SERVICE";
    final static String KEY_NMEA_FROM_SERVICE = "KEY_NMEA_FROM_SERVICE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startReciever();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getCurrentPosition();

        //Starting Location Listener to get current position.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //Getting NMEA Sentences
                getNmea();
                final String lat = String.valueOf(location.getLatitude());
                final String lon = String.valueOf(location.getLongitude());
                final String alt = String.valueOf(location.getAltitude());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Send broadcast after every 2s = 2000ms
                        Intent intent = new Intent();
                        intent.setAction(ACTION_UPDATE_POSITION);
                        intent.putExtra(KEY_LATITUDE_FROM_SERVICE, lat);
                        intent.putExtra(KEY_LONGITUDE_FROM_SERVICE, lon);
                        intent.putExtra(KEY_ALTITUDE_FROM_SERVICE, alt);
                        sendBroadcast(intent);

                    }
                }, 2000);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                //opening settings if gps is disabled.
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        return START_STICKY;

    }

    private void getCurrentPosition() {
        //getting last known position and broadcasting it for MainActivity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!= null) {
                double latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                double longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                double altitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAltitude();
                Intent intent = new Intent();
                intent.setAction(ACTION_UPDATE_POSITION);
                intent.putExtra(KEY_LATITUDE_FROM_SERVICE, String.valueOf(latitude));
                intent.putExtra(KEY_LONGITUDE_FROM_SERVICE, String.valueOf(longitude));
                intent.putExtra(KEY_ALTITUDE_FROM_SERVICE, String.valueOf(altitude));
                sendBroadcast(intent);
            }
        }
    }


    private void startReciever() {
        //Starting Reciever for this service.
        myServiceReceiver = new MyServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_TRACKING);
        intentFilter.addAction(ACTION_START_TRACKING);
        intentFilter.addAction(ACTION_STOP_SERVICE);
        registerReceiver(myServiceReceiver, intentFilter);
    }


    private void startGpsTracking(){
        //starting gps tracking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,50,0,locationListener);
        state ="STARTED";
    }

    private void stopGpsTracking() {
        //stopping gps tracking
        locationManager.removeUpdates(locationListener);
        state="STOPPED";

    }
    private void getNmea() {
        //scanning through available NMEA sentences and getting required sentences.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.addNmeaListener(new OnNmeaMessageListener() {
                @Override
                public void onNmeaMessage(final String message, long timeStamp) {
                    String[] splitNmeaMessage;
                    splitNmeaMessage = message.split(",");
                    if(splitNmeaMessage[0].equals("$GPGSV") && splitNmeaMessage.length==20){

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Send broadcast after every 2s = 2000ms
                                Intent intent = new Intent();
                                intent.setAction(ACTION_UPDATE_NMEA);
                                intent.putExtra(KEY_NMEA_FROM_SERVICE, message);
                                sendBroadcast(intent);
                            }
                        }, 2000);

                    }
                }
            });
        }
    }

    public class MyServiceReceiver extends BroadcastReceiver {
        //Reciever Class
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals(ACTION_START_TRACKING)){
                startGpsTracking();
            }else if(action.equals(ACTION_STOP_TRACKING)){
                stopGpsTracking();
            }else if(action.equals(ACTION_STOP_SERVICE)){
                stopSelf();
            }

        }
    }

}
