package com.example.valverde.valverderunkeeper.run_keeper;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.database.DatabaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final double INIT_LAT = 50.79829564, INIT_LNG = 16.25238182;
    private static final int GPS_ERROR_DIALOG_REQUEST = 9001;
    private static final int EVENTS_REFRESH_TIME_IN_SECONDS = 3;
    private static final float DEFAULT_ZOOM = 16;
    private TextView speedField, distanceField, progressBarField, timerField;
    private ProgressBar accuracyProgressBar;
    private Handler handler = new Handler();
    private Button startButton, stopButton;
    private DatabaseHelper databaseHelper;
    private String runningState = "init";
    private PolylineOptions polylineOptions = new PolylineOptions();
    private TimerThread timerThread;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
//        databaseHelper.onUpgrade(databaseHelper.getWritableDatabase(), 1, 1);

        polylineOptions.color(Color.BLUE);
        if (isServicesAvailable()) {
            Log.i("D", "Services are available");
            if (initMap()) {
                Log.i("D", "Map is ready to use");
                goToLocation(INIT_LAT, INIT_LNG, DEFAULT_ZOOM);
            }
            else
                Log.i("D", "Map is not available");
        }

        speedField = (TextView) findViewById(R.id.speedField);
        distanceField = (TextView) findViewById(R.id.distanceField);
        accuracyProgressBar = (ProgressBar) findViewById(R.id.accuracyProgressBar);
        progressBarField = (TextView) findViewById(R.id.accuracyProgressBarField);
        timerField = (TextView) findViewById(R.id.timeField);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningState.equals("init")) {
                    timerThread = new TimerThread(handler, timerField);
                    timerThread.start();
                    runningState = "started";
                    startButton.setText(getString(R.string.pauseButton));
                }
                else if (runningState.equals("started")) {
                    timerThread.pause();
                    runningState = "paused";
                    startButton.setText(getString(R.string.startButton));
                }
                else if (runningState.equals("paused")) {
                    timerThread.unpause();
                    runningState = "started";
                    startButton.setText(getString(R.string.pauseButton));
                }
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerThread != null) {
                    timerThread.setRunning(false);
                    timerThread = null;
                    runningState = "stopped";
                }
//                showAllEvents();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float signalAccuracy = location.getAccuracy();
                setAccuracyProgressBarStatus(signalAccuracy);


                if (runningState.equals("started")) {
                    TrackManager manager = TrackManager.getInstance();

                    GPSEvent gpsEvent = new GPSEvent(System.currentTimeMillis(), location.getLatitude(),
                            location.getLongitude(), location.getAccuracy());
                    double averangeSpeed = manager.getAverangeSpeedInKmH(gpsEvent);
                    double overallDistance = manager.getOverallDistance();
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String averangeSpeedInFormat = decimalFormat.format(averangeSpeed)+
                            " "+getString(R.string.speedUnits);
                    String overallDistanceInFormat = decimalFormat.format(overallDistance)+
                            " "+getString(R.string.distanceUnits);
                    speedField.setText(averangeSpeedInFormat);
                    distanceField.setText(overallDistanceInFormat);
                    polylineOptions.add(new LatLng(gpsEvent.getLat(), gpsEvent.getLng()));
                    map.addPolyline(polylineOptions);

                    /*** DEBUG ****/
                    Log.d("SPEED", "LAT: "+location.getLatitude()+"|  LNG: "+location.getLongitude()+
                            "  |  SPEED: "+averangeSpeedInFormat+" km/h  |  ACCURACY: "+signalAccuracy);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET}, 10);
            }
        }
        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            EVENTS_REFRESH_TIME_IN_SECONDS*1000, 0, locationListener);
    }


    private void setAccuracyProgressBarStatus(float signalAccuracy) {
        if (signalAccuracy <= 3.5) {
            accuracyProgressBar.setProgress(100 - ((int) signalAccuracy * 4));
            progressBarField.setText(getString(R.string.accuracyExcellentStatus));
        }
        else if (signalAccuracy <= 6.0) {
            accuracyProgressBar.setProgress(100 - ((int) signalAccuracy * 4));
            progressBarField.setText(getString(R.string.accuracyVeryGoodStatus));
        }
        else if (signalAccuracy <= 10.0) {
            accuracyProgressBar.setProgress(100 - ((int) signalAccuracy * 4));
            progressBarField.setText(getString(R.string.accuracyGoodStatus));
        }
        else if (signalAccuracy <= 20.0) {
            int accuracy = (int) signalAccuracy - 10;
            accuracyProgressBar.setProgress(60 - accuracy*3);
            progressBarField.setText(getString(R.string.accuracyFairStatus));
        }
        else if (signalAccuracy <= 30.0) {
            int accuracy = (int) signalAccuracy - 20;
            accuracyProgressBar.setProgress(30 - accuracy*2);
            progressBarField.setText(getString(R.string.accuracyBadStatus));
        }
        else {
            int accuracy = (int) signalAccuracy - 30;
            accuracyProgressBar.setProgress(10 - (int)(accuracy * 0.5));
            progressBarField.setText(getString(R.string.accuracyFatalStatus));
        }
    }


    private boolean isServicesAvailable() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS) return true;
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else Toast.makeText(this, "Can't connect to GooglePlayServices", Toast.LENGTH_SHORT).show();
        return false;
    }


    private boolean initMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            map = mapFragment.getMap();
        }
        return (map != null);
    }


    private void goToLocation(double latitude, double longtitude, float zoom) {
        LatLng ll = new LatLng(latitude, longtitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        map.moveCamera(update);
    }

    private void showAllEvents() {
        ArrayList<GPSEvent> events = databaseHelper.getAllEvents();
        TrackManager manager = TrackManager.getInstance();
        Log.d("events", events.size()+" events amount");
        PolylineOptions polylineOptions = new PolylineOptions();
        String averangeSpeedInFormat = "";
        String overallDistanceInFormat = "";
        double lastSpeed = 0.0;
        int i = 0;
        for (GPSEvent event : events) {
            if (i % EVENTS_REFRESH_TIME_IN_SECONDS == 0) {
                double averangeSpeed = manager.getAverangeSpeedInKmH(event);
                double overallDistance = manager.getOverallDistance();
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                averangeSpeedInFormat = decimalFormat.format(averangeSpeed)+
                                        " "+getString(R.string.speedUnits);
                overallDistanceInFormat = decimalFormat.format(overallDistance)+
                                        " "+getString(R.string.distanceUnits);
                speedField.setText(averangeSpeedInFormat);
                distanceField.setText(overallDistanceInFormat);
                if (lastSpeed == averangeSpeed)
                    map.addMarker(new MarkerOptions().position(new LatLng(event.getLat(), event.getLng())));

                polylineOptions.color(Color.BLUE);
                polylineOptions.add(new LatLng(event.getLat(), event.getLng()));
                lastSpeed = averangeSpeed;


                /*** DEBUG ****/
                Log.d("SPEED", "ID: "+i/EVENTS_REFRESH_TIME_IN_SECONDS+" | SPEED: "+
                        averangeSpeedInFormat+" km/h  |  ACCURACY: "+event.getAccuracy()+
                        " | DISTANCE: "+overallDistanceInFormat);
            }
            i++;
        }
        map.addPolyline(polylineOptions);
    }
}