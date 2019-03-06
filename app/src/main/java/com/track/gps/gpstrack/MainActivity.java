package com.track.gps.gpstrack;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvLongitude, tvLatitude, tvAltitude, tvInfo;
    TextView sat1,sat2,sat3,sat4,sat5,sat6,sat7,sat8,sat9,sat10,sat11,sat12,sat13,sat14,sat15,sat16,sat17,sat18,sat19,sat20,sat21,sat22;
    TextView sat23, sat24, sat25, sat26, sat27, sat28, sat29, sat30, sat31, sat32, tvNote;
    LinearLayout llSatList, llInfo;
    Button btnStart;
    ImageButton btnCollapsSat;
    boolean isTracking = false;
    boolean isHidden = false;
    LoactionService locationService;
    MyServiceReceiver myServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing UI components.
        initialize();

        //Checking if location service is already running or not.
        if(locationService.state.equals("STARTED")){
            isTracking = true;
            btnStart.setText("Stop Gps Tracking");
        }else{
            //Start location service if it is not already running.
            btnStart.setText("Start Gps Tracking");
            isTracking= false;
            Intent intent = new Intent(MainActivity.this, LoactionService.class);
            startService(intent);
        }

        //Starting broadcast reciever to listen for update of location.
        startReciever();

    }

    private void initialize() {
        tvInfo = (TextView) findViewById(R.id.tvInfo) ;
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvAltitude = (TextView) findViewById(R.id.tvAltitude);
        tvNote = (TextView) findViewById(R.id.tvNote);
        llSatList = (LinearLayout)  findViewById(R.id.llSatList);
        llInfo = (LinearLayout) findViewById(R.id.llInfo);
        btnCollapsSat = (ImageButton) findViewById(R.id.btnCollapsSat);
        btnCollapsSat.setTag(1);
        btnCollapsSat.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
        btnCollapsSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnCollapsSat.getTag().equals(1)){
                    llSatList.setVisibility(View.GONE);
                    btnCollapsSat.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    btnCollapsSat.setTag(2);
                }else{
                    llSatList.setVisibility(View.VISIBLE);
                    btnCollapsSat.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    btnCollapsSat.setTag(1);
                }
            }
        });
        btnStart = (Button) findViewById(R.id.btnStartGps);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTracking == false){
                    //Starting location service.
                    startGpsTracking();
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                    if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        // Call your Alert message
                        btnStart.setText("Stop Gps Tracking");
                        isTracking = true;
                    }else{
                        Toast.makeText(MainActivity.this,"Please turn on  Location Service",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Stoping location Service.
                    stopGpsTracking();
                    btnStart.setText("Start Gps Tracking");
                    isTracking = false;
                }

            }
        });
        initializeSalliteListTextViews();
    }

    private void initializeSalliteListTextViews() {
        sat1= (TextView) findViewById(R.id.tvSat1);
        sat2= (TextView) findViewById(R.id.tvSat2);
        sat3= (TextView) findViewById(R.id.tvSat3);
        sat4= (TextView) findViewById(R.id.tvSat4);
        sat5= (TextView) findViewById(R.id.tvSat5);
        sat6= (TextView) findViewById(R.id.tvSat6);
        sat7= (TextView) findViewById(R.id.tvSat7);
        sat8= (TextView) findViewById(R.id.tvSat8);
        sat9= (TextView) findViewById(R.id.tvSat9);
        sat10= (TextView) findViewById(R.id.tvSat10);
        sat11= (TextView) findViewById(R.id.tvSat11);
        sat12= (TextView) findViewById(R.id.tvSat12);
        sat13= (TextView) findViewById(R.id.tvSat13);
        sat14= (TextView) findViewById(R.id.tvSat14);
        sat15= (TextView) findViewById(R.id.tvSat15);
        sat16= (TextView) findViewById(R.id.tvSat16);
        sat17= (TextView) findViewById(R.id.tvSat17);
        sat18= (TextView) findViewById(R.id.tvSat18);
        sat19= (TextView) findViewById(R.id.tvSat19);
        sat20= (TextView) findViewById(R.id.tvSat20);
        sat21= (TextView) findViewById(R.id.tvSat21);
        sat22= (TextView) findViewById(R.id.tvSat22);
        sat23= (TextView) findViewById(R.id.tvSat23);
        sat24= (TextView) findViewById(R.id.tvSat24);
        sat25= (TextView) findViewById(R.id.tvSat25);
        sat26= (TextView) findViewById(R.id.tvSat26);
        sat27= (TextView) findViewById(R.id.tvSat27);
        sat28= (TextView) findViewById(R.id.tvSat28);
        sat29= (TextView) findViewById(R.id.tvSat29);
        sat30= (TextView) findViewById(R.id.tvSat30);
        sat31= (TextView) findViewById(R.id.tvSat31);
        sat32= (TextView) findViewById(R.id.tvSat32);

    }

    private void stopGpsTracking() {
        stopLocationServiceUpdates();
    }


    private void startGpsTracking() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        startLocationServiceUpdates();
    }

    private void startReciever(){
        //Brodcast Reciever for recieving location data from location service.
        myServiceReceiver = new MyServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(locationService.ACTION_UPDATE_POSITION);
        intentFilter.addAction(locationService.ACTION_UPDATE_NMEA);
        registerReceiver(myServiceReceiver, intentFilter);
    }

    private void startLocationServiceUpdates() {
        // Method for starting location service.
        Intent intent = new Intent();
        intent.setAction(locationService.ACTION_START_TRACKING);
        sendBroadcast(intent);

    }


    private void terminateLocationService(){
        Intent intent = new Intent();
        intent.setAction(locationService.ACTION_STOP_SERVICE);
        sendBroadcast(intent);
    }


    private void stopLocationServiceUpdates() {
        // Method for stoping location service.
        Intent intent = new Intent();
        intent.setAction(locationService.ACTION_STOP_TRACKING);
        sendBroadcast(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking for permission
        switch (requestCode){
            case 10:
                startGpsTracking();
                break;
            default:
                break;
        }
    }
    public class MyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals(locationService.ACTION_UPDATE_POSITION)){
                Log.i("nmea sentences:  ",intent.getStringExtra(locationService.KEY_LATITUDE_FROM_SERVICE)+" "+intent.getStringExtra(locationService.KEY_LONGITUDE_FROM_SERVICE));
                tvLatitude.setText(intent.getStringExtra(locationService.KEY_LATITUDE_FROM_SERVICE));
                tvLongitude.setText(intent.getStringExtra(locationService.KEY_LONGITUDE_FROM_SERVICE));
                String altitude = intent.getStringExtra(locationService.KEY_ALTITUDE_FROM_SERVICE);
                if(altitude!=null) {
                    tvAltitude.setText(intent.getStringExtra(locationService.KEY_ALTITUDE_FROM_SERVICE) + " m");
                }
            }else if(action.equals(locationService.ACTION_UPDATE_NMEA)){
                String rawNmeaMessage= intent.getStringExtra(locationService.KEY_NMEA_FROM_SERVICE);
                String[] splitNmeaMessage = rawNmeaMessage.split(",");
                displaySatelliteList(splitNmeaMessage);

            }
        }
    }


    private void displaySatelliteList(String[] nmeaMessage) {
        //Displaying Satellite Information is corresponding textView
        for(int i =1; i<=llSatList.getChildCount(); i++) {
            TextView tvSatellite = (TextView) llSatList.getChildAt(i);
            tvNote.setVisibility(View.GONE);
            llInfo.setVisibility(View.VISIBLE);
            if (nmeaMessage[4].equals(i+"")) {
                tvSatellite.setGravity(Gravity.CENTER_HORIZONTAL);
                tvSatellite.setVisibility(View.VISIBLE);
                tvSatellite.setText("PRN No : " + nmeaMessage[4] + ", Elevation: " + nmeaMessage[5] + ", Azimuth: " + nmeaMessage[6] + ", SNR: " + nmeaMessage[7]);
            } else if (nmeaMessage[8].equals(i+"")) {
                tvSatellite.setVisibility(View.VISIBLE);
                tvSatellite.setGravity(Gravity.CENTER_HORIZONTAL);
                tvSatellite.setText("PRN No : " + nmeaMessage[8] + ", Elevation: " + nmeaMessage[9] + ", Azimuth: " + nmeaMessage[10] + ", SNR: " + nmeaMessage[11]);
            } else if (nmeaMessage[12].equals(i+"")) {
                tvSatellite.setVisibility(View.VISIBLE);
                tvSatellite.setGravity(Gravity.CENTER_HORIZONTAL);
                tvSatellite.setText("PRN No : " + nmeaMessage[12] + ", Elevation: " + nmeaMessage[13] + ", Azimuth: " + nmeaMessage[14] + ", SNR: " + nmeaMessage[15]);
            } else if (nmeaMessage[16].equals(i+"")) {
                tvSatellite.setVisibility(View.VISIBLE);
                tvSatellite.setGravity(Gravity.CENTER_HORIZONTAL);
                String[] nmeaMessage19 = nmeaMessage[19].split("\\*");
                tvSatellite.setText("PRN No : " + nmeaMessage[16] + ", Elevation: " + nmeaMessage[17] + ", Azimuth: " + nmeaMessage[18] + ", SNR: " + nmeaMessage19[0]);
            }
        }

    }

    // Method for checking if a service is running or not.
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationService.state.equals("STOPPED")){
            terminateLocationService();
            isTracking=false;
        }
    }
}
