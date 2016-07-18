package com.example.valverde.valverderunkeeper.running;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.running.processing_result.FinalizeRunActivity;
import com.example.valverde.valverderunkeeper.running.processing_result.Result;
import com.example.valverde.valverderunkeeper.settings.SettingsManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackerActivity extends AppCompatActivity {
    private static volatile TrackerActivity instance = null;
    private static final String TAG = "TrackerActivity";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String runningState = "init";
    private Timer timerThread;
    private Handler handler;
    private UpdateManager updateManager;
    private com.example.valverde.valverderunkeeper.settings.Settings settings;
    @BindView(R.id.accuracyProgressBar) ProgressBar accuracyProgressBar;
    @BindView(R.id.speedField) TextView speedField;
    @BindView(R.id.distanceField) TextView distanceField;
    @BindView(R.id.accuracyProgressBarField) TextView progressBarField;
    @BindView(R.id.timeField) TextView timerField;
    @BindView(R.id.stopButton) ImageButton stopButton;
    @BindView(R.id.startButton) ImageButton startButton;
    @BindView(R.id.trackerMainLayout) RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;
        handler = new Handler();
        settings = SettingsManager.getSettings(this);
        TrackerUtils.setSettings(settings);
        int progressBarColor = getResources().getColor(R.color.darkGreen);
        accuracyProgressBar.getProgressDrawable().setColorFilter(progressBarColor,
                android.graphics.PorterDuff.Mode.SRC_IN);
        updateManager = new UpdateManager(this, layout);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningState.equals("init") || runningState.equals("ready")) {
                    initTracker();
                    startTracker();
                } else if (runningState.equals("started")) {
                    pauseTracker();
                } else if (runningState.equals("paused")) {
                    startTracker();
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerThread != null) {
                    timerThread.setRunning(false);
                    TrackerUtils utils = TrackerUtils.getInstance();
                    utils.addLastEventToRoute();
                    double distance = utils.getOverallDistance();
                    long overallTime = timerThread.getOverallTime();
                    ArrayList<GPSEvent> route = utils.getRoute();
                    Result result = new Result(overallTime, distance, 0);
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
                    finish();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float signalAccuracy = location.getAccuracy();
                setAccuracyProgressBarStatus(signalAccuracy);
                if (runningState.equals("started")) {
                    TrackerUtils utils = TrackerUtils.getInstance();
                    GPSEvent gpsEvent = new GPSEvent(System.currentTimeMillis(), location.getLatitude(),
                            location.getLongitude(), location.getAccuracy());
                    double avgSpeed = utils.getAvgSpeedInKmH(gpsEvent);
                    double distance = utils.getOverallDistance();
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String avgSpeedInFormat = decimalFormat.format(avgSpeed) +
                            " " + getString(R.string.speedUnits);
                    String overallDistanceInFormat = decimalFormat.format(distance) +
                            " " + getString(R.string.distanceUnits);
                    speedField.setText(avgSpeedInFormat);
                    distanceField.setText(overallDistanceInFormat);
                    updateManager.notifyDistance(distance);

                    Log.d(TAG, "LAT: " + location.getLatitude() + "|  LNG: " + location.getLongitude() +
                            "  |  SPEED: " + avgSpeedInFormat + " km/h  |  ACCURACY: " + signalAccuracy);
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
        } else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                settings.getEventsRefreshTimeInSeconds() * 1000, 0, locationListener);
    }

    public void onScreenChangeState(String msg) {
        if (msg.equals("SCREEN_ON")) {
            if (runningState.equals("started")) {
                pauseTracker();
                updateManager.speak("Paused");
            }
        } else if (msg.equals("SCREEN_OFF")) {
            if (runningState.equals("init"))
                runningState = "ready";
            else {
                if (runningState.equals("ready"))
                    initTracker();
                if (runningState.equals("ready") || runningState.equals("paused")) {
                    startTracker();
                    updateManager.speak("Started");
                }
            }
        }
    }

    private void initTracker() {
        timerThread = new Timer(handler, timerField);
        timerThread.start();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GPSEvent event = new GPSEvent(System.currentTimeMillis(), location.getLatitude(),
                location.getLongitude(), location.getAccuracy());
        TrackerUtils.getInstance().addEvent(event);
    }

    private void pauseTracker() {
        timerThread.pause();
        runningState = "paused";
        startButton.setImageResource(R.drawable.play_black);
    }

    private void startTracker() {
        timerThread.unpause();
        runningState = "started";
        startButton.setImageResource(R.drawable.pause_black);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateManager.close();
    }

    public static TrackerActivity getInstance() {
        return instance;
    }
}