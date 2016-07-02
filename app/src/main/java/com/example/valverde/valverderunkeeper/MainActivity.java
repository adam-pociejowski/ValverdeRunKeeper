package com.example.valverde.valverderunkeeper;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView speedField, distanceField, progressBarField, timerField;
    private Button startButton, stopButton;
    private ProgressBar accuracyProgressBar;
    private String runningState = "init";
    private TimerThread timerThread;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    TrackManager manager = TrackManager.getInstance();
                    manager.setRunning(true);
                    runningState = "started";
                    startButton.setText(getString(R.string.pauseButton));
                }
                else if (runningState.equals("started")) {
                    timerThread.pause();
                    runningState = "paused";
                    TrackManager manager = TrackManager.getInstance();
                    manager.setRunning(false);
                    startButton.setText(getString(R.string.startButton));
                }
                else if (runningState.equals("paused")) {
                    timerThread.unpause();
                    runningState = "started";
                    TrackManager manager = TrackManager.getInstance();
                    manager.setRunning(true);
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
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TrackManager manager = TrackManager.getInstance();
                float signalAccuracy = location.getAccuracy();
                GPSEvent gpsEvent = new GPSEvent(System.currentTimeMillis(), location.getLatitude(),
                                        location.getLongitude(), location.getAccuracy());
                double averangeSpeed = manager.getAverangeSpeedInKmH(gpsEvent);
                double overallDistance = manager.getOverallDistance();
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                String averangeSpeedInFormat = decimalFormat.format(averangeSpeed)+
                                               " "+getString(R.string.speedUnits);
                String overallDistanceInFormat = decimalFormat.format(overallDistance)+
                                               " "+getString(R.string.distanceUnits);

                setAccuracyProgressBarStatus(signalAccuracy);
                speedField.setText(averangeSpeedInFormat);
                distanceField.setText(overallDistanceInFormat);

                /*** DEBUG ****/
                Log.d("SPEED", "LAT: "+location.getLatitude()+"|  LNG: "+location.getLongitude()+
                        "  |  SPEED: "+averangeSpeedInFormat+" km/h  |  ACCURACY: "+signalAccuracy);
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
        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
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
}