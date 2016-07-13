package com.example.valverde.valverderunkeeper.running;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.notification.RunningSpeaker;
import com.example.valverde.valverderunkeeper.notification.SpeakingManager;
import com.example.valverde.valverderunkeeper.running.processing_result.FinalizeRunActivity;
import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;
import com.example.valverde.valverderunkeeper.settings.SettingsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackerActivity extends AppCompatActivity {
    private static final double INIT_LAT = 50.79829564, INIT_LNG = 16.25238182;
    private static final int GPS_ERROR_DIALOG_REQUEST = 9001;
    private com.example.valverde.valverderunkeeper.settings.Settings settings;
    private PolylineOptions polylineOptions = new PolylineOptions();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SpeakingManager speakingManager;
    private String runningState = "init";
    private Timer timerThread;
    private Handler handler;
    private GoogleMap map;
    @BindView(R.id.accuracyProgressBar) ProgressBar accuracyProgressBar;
    @BindView(R.id.speedField) TextView speedField;
    @BindView(R.id.distanceField) TextView distanceField;
    @BindView(R.id.accuracyProgressBarField) TextView progressBarField;
    @BindView(R.id.timeField) TextView timerField;
    @BindView(R.id.stopButton) Button stopButton;
    @BindView(R.id.startButton) Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler();
        settings = SettingsManager.getSettings(this);
        TrackManager.setSettings(settings);
        int progressBarColor = getResources().getColor(R.color.darkGreen);
        accuracyProgressBar.getProgressDrawable().setColorFilter(progressBarColor,
                android.graphics.PorterDuff.Mode.SRC_IN);
        if (settings.getSoundNotifications()) {
            speakingManager = new RunningSpeaker(this);
            speakingManager.setDistanceNotifyInterval(settings.getSoundNotificationDistanceInterval());
        }


        polylineOptions.color(Color.BLUE);
        if (isServicesAvailable()) {
            Log.i("D", "Services are available");
            if (initMap()) {
                Log.i("D", "Map is ready to use");
                goToLocation(INIT_LAT, INIT_LNG, settings.getDefaultZoom());
            } else
                Log.i("D", "Map is not available");
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningState.equals("init")) {
                    timerThread = new Timer(handler, timerField);
                    timerThread.start();
                    runningState = "started";
                    startButton.setText(getString(R.string.pauseButton));
                } else if (runningState.equals("started")) {
                    timerThread.pause();
                    runningState = "paused";
                    startButton.setText(getString(R.string.startButton));
                } else if (runningState.equals("paused")) {
                    timerThread.unpause();
                    runningState = "started";
                    startButton.setText(getString(R.string.pauseButton));
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerThread != null) {
                    timerThread.setRunning(false);
                    TrackManager manager = TrackManager.getInstance();
                    manager.addLastEventToRoute();
                    double distance = manager.getOverallDistance();
                    long overallTime = timerThread.getOverallTime();
                    ArrayList<GPSEvent> route = manager.getRoute();
                    RunResult result = new RunResult(overallTime, distance, 0);
                    result.setRoute(route);
                    timerThread = null;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeUpdates(locationListener);
                    }
                    Intent intent = new Intent(getApplicationContext(), FinalizeRunActivity.class);
                    intent.putExtra("result", result);
                    startActivity(intent);
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float signalAccuracy = location.getAccuracy();
                setAccuracyProgressBarStatus(signalAccuracy);
                goToLocation(location.getLatitude(), location.getLongitude(), settings.getDefaultZoom());

                if (runningState.equals("started")) {
                    TrackManager manager = TrackManager.getInstance();
                    GPSEvent gpsEvent = new GPSEvent(System.currentTimeMillis(), location.getLatitude(),
                            location.getLongitude(), location.getAccuracy());
                    double averangeSpeed = manager.getAverangeSpeedInKmH(gpsEvent);
                    double distance = manager.getOverallDistance();
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String averangeSpeedInFormat = decimalFormat.format(averangeSpeed)+
                            " "+getString(R.string.speedUnits);
                    String overallDistanceInFormat = decimalFormat.format(distance)+
                            " "+getString(R.string.distanceUnits);
                    speedField.setText(averangeSpeedInFormat);
                    distanceField.setText(overallDistanceInFormat);
                    polylineOptions.add(new LatLng(gpsEvent.getLat(), gpsEvent.getLng()));
                    map.addPolyline(polylineOptions);
                    if (settings.getSoundNotifications()) {
                        speakingManager.notifyDistance(distance);
                    }

                    /*** DEBUG ****/
                    Log.d("TrackerActivity", "LAT: "+location.getLatitude()+"|  LNG: "+location.getLongitude()+
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
                            settings.getEventsRefreshTimeInSeconds()*1000, 0, locationListener);
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
        if (isAvailable == ConnectionResult.SUCCESS) return true;
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
}