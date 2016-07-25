package com.example.valverde.valverderunkeeper.statistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.data.DatabaseGPSEventsHelper;
import com.example.valverde.valverderunkeeper.data.DatabaseRunResultsHelper;
import com.example.valverde.valverderunkeeper.main_menu.MainMenuActivity;
import com.example.valverde.valverderunkeeper.running.GPSEvent;
import com.example.valverde.valverderunkeeper.running.Timer;
import com.example.valverde.valverderunkeeper.running.processing_result.Result;
import com.example.valverde.valverderunkeeper.settings.Settings;
import com.example.valverde.valverderunkeeper.settings.SettingsManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultPresentationActivity extends AppCompatActivity {
    @BindView(R.id.timeField) TextView timeField;
    @BindView(R.id.avgSpeedField) TextView avgSpeedField;
    @BindView(R.id.distanceField) TextView distanceField;
    @BindView(R.id.caloriesField) TextView caloriesField;
    @BindView(R.id.dateField) TextView dateField;
    @BindView(R.id.copperTestField) TextView copperTestField;
    @BindView(R.id.accuracyField) TextView accuracyField;
    @BindView(R.id.deleteButton) Button deleteButton;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_presentation);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        final DatabaseGPSEventsHelper dh = new DatabaseGPSEventsHelper(this);
        final Result result = (Result) intent.getSerializableExtra("result");
        final ArrayList<GPSEvent> events = dh.getRoute(result.getResultId());
        if (events == null)
            Log.e("ResultPresentation", "No route for result: "+result.getResultId());
        else if (events.size() > 1) {
            if (initMap()) {
                Log.i("D", "Map is ready to use");
                addMarkers(events);
                addRouteOnMap(events);
            } else
                Log.i("D", "Map is not available");

            Log.d("ResultPresentation", "Route events: "+events.size());
        }
        else
            Log.e("ResultPresentation", "Not enough events for result: "+result.getResultId());

        setResultParamsInTextViews(result, events);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultPresentationActivity.this);
                DecimalFormat df = new DecimalFormat("#.##");
                DateFormat datef = new SimpleDateFormat("dd-MM-yyyy");
                String alertMessage = "Date: "+datef.format(result.getDate())+
                        "\nDistance: "+df.format(result.getDistance())+" "
                        +getString(R.string.distanceUnits)+
                        "\nTime: "+Timer.getTimeInFormat(result.getTime());
                builder.setMessage(alertMessage)
                        .setTitle(getString(R.string.alertTitle));

                builder.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        long deletingResultId = result.getResultId();
                        DatabaseRunResultsHelper db = new DatabaseRunResultsHelper(getApplicationContext());
                        dh.removeRoute(deletingResultId);
                        db.removeResult(deletingResultId);
                        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

                builder.setNegativeButton(getString(R.string.cancelButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void addMarkers(ArrayList<GPSEvent> events) {
        double startLat = events.get(0).getLat();
        double startLng = events.get(0).getLng();
        double endLat = events.get(events.size() - 1).getLat();
        double endLng = events.get(events.size() - 1).getLng();
        SettingsManager settingsManager = new SettingsManager(this);
        Settings settings = settingsManager.getSettings();
        double centerLat = (startLat + endLat) / 2.0;
        double centerLng = (startLng + endLng) / 2.0;
        goToLocation(centerLat, centerLng, settings.getDefaultZoom());
        LatLng startLL = new LatLng(startLat, startLng);
        LatLng endLL = new LatLng(endLat, endLng);
        map.addMarker(new MarkerOptions()
                .position(startLL)
                .title("start"));
        map.addMarker(new MarkerOptions()
                .position(endLL)
                .title("finish"));
    }

    private void addRouteOnMap(ArrayList<GPSEvent> events) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        for (GPSEvent e : events) {
            polylineOptions.add(new LatLng(e.getLat(), e.getLng()));
        }
        map.addPolyline(polylineOptions);
    }

    private void setResultParamsInTextViews(Result result, ArrayList<GPSEvent> events) {
        DecimalFormat df = new DecimalFormat("#.##");
        String timeInFormat = Timer.getTimeInFormat(result.getTime());
        double distance = result.getDistance();
        String distanceInFormat = df.format(distance)+" "+getString(R.string.distanceUnits);
        double avgSpeed = result.getAvgSpeed();
        String avgSpeedInFormat = df.format(avgSpeed)+" "+getString(R.string.speedUnits);
        long date = result.getDate();
        Date d = new Date(date);
        String dateFormat = "dd-MM-yyyy";
        DateFormat datef = new SimpleDateFormat(dateFormat);
        String dateString = datef.format(d);
        int calories = result.getCalories();
        String caloriesInFormat = Integer.toString(calories);
        String copper = getCopperTestResult(result)+" "+getString(R.string.distanceUnits);
        String averangeAccuracy;
        if (events == null)
            averangeAccuracy = "unknown";
        else
            averangeAccuracy = getAverangeGPSAccuracy(events)+" "+getString(R.string.gpsAccuracyUnits);

        timeField.setText(timeInFormat);
        distanceField.setText(distanceInFormat);
        avgSpeedField.setText(avgSpeedInFormat);
        dateField.setText(dateString);
        caloriesField.setText(caloriesInFormat);
        copperTestField.setText(copper);
        accuracyField.setText(averangeAccuracy);
    }

    private String getAverangeGPSAccuracy(ArrayList<GPSEvent> events) {
        float sum = 0f;
        int eventsAmount = events.size();
        for (GPSEvent e : events) {
            sum += e.getAccuracy();
        }
        float averangeAccuracy = sum / (float) eventsAmount;
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(averangeAccuracy);
    }

    private String getCopperTestResult(Result result) {
        double distance = result.getDistance();
        long time = result.getTime();
        final long COPPER_TEST_TIME = 720000;
        double timeFactor = (double) COPPER_TEST_TIME / (double) time;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(timeFactor*distance);
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
