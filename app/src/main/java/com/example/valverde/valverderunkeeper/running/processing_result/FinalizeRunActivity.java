package com.example.valverde.valverderunkeeper.running.processing_result;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.data.DatabaseGPSEventsTable;
import com.example.valverde.valverderunkeeper.data.DatabaseHelper;
import com.example.valverde.valverderunkeeper.data.DatabaseRunResultsTable;
import com.example.valverde.valverderunkeeper.main_menu.MainMenuActivity;
import com.example.valverde.valverderunkeeper.running.GPSEvent;
import com.example.valverde.valverderunkeeper.running.Timer;
import com.example.valverde.valverderunkeeper.statistics.ResultsSorter;
import com.example.valverde.valverderunkeeper.statistics.StatisticsUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FinalizeRunActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    private DatabaseHelper dbHelper;
    @BindView(R.id.avgSpeedRecordLabel) TextView speedRecordLabel;
    @BindView(R.id.timeRecordLabel) TextView timeRecordLabel;
    @BindView(R.id.distanceRecordLabel) TextView distanceRecordLabel;
    @BindView(R.id.caloriesRecordLabel) TextView caloriesRecordLabel;
    @BindView(R.id.timeField) TextView timeField;
    @BindView(R.id.avgSpeedField) TextView avgSpeedField;
    @BindView(R.id.distanceField) TextView distanceField;
    @BindView(R.id.caloriesField) TextView caloriesField;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.deleteButton) Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_run_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        dbHelper = new DatabaseHelper(this);
        final Result result = (Result) intent.getSerializableExtra("result");
        result.setDate(new Date().getTime());
        hideRecordTextViews();
        setResultParamsInTextViews(result);
        checkForRecords(result);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(i);
                dbHelper.insertResult(result);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FinalizeRunActivity.this);
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
                        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                        startActivity(i);
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

    private void hideRecordTextViews() {
        speedRecordLabel.setVisibility(View.INVISIBLE);
        timeRecordLabel.setVisibility(View.INVISIBLE);
        distanceRecordLabel.setVisibility(View.INVISIBLE);
        caloriesRecordLabel.setVisibility(View.INVISIBLE);
    }

    private void setResultParamsInTextViews(Result result) {
        DecimalFormat df = new DecimalFormat("#.##");
        String timeInFormat = Timer.getTimeInFormat(result.getTime());
        double distance = result.getDistance();
        String distanceInFormat = df.format(distance)+" "+getString(R.string.distanceUnits);
        double avgSpeed = result.getAvgSpeed();
        String avgSpeedInFormat = df.format(avgSpeed)+" "+getString(R.string.speedUnits);
        timeField.setText(timeInFormat);
        distanceField.setText(distanceInFormat);
        avgSpeedField.setText(avgSpeedInFormat);
    }

    public void checkForRecords(Result result) {
        try {
            ArrayList<Result> results = dbHelper.getAllResults();
            checkForDistanceRecord(results, result);
            checkForSpeedRecord(results, result);
            checkForTimeRecord(results, result);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "There isn't any previous results..");
        }
    }

    private void checkForDistanceRecord(ArrayList<Result> results, Result result) {
        Result highest = ResultsSorter.getHighestByDistance(results);
        if (result.getDistance() > highest.getDistance()) {
            distanceRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New distance record!");
        }
        else {
            double avgDistance = StatisticsUtils.getOverallAVGDistance(results);
            if (result.getDistance() > avgDistance) {
                distanceRecordLabel.setVisibility(View.VISIBLE);
                distanceRecordLabel.setText(getString(R.string.improvedLabel));
                distanceRecordLabel.setBackground(getDrawable(R.color.lime));
            }
        }
    }

    private void checkForSpeedRecord(ArrayList<Result> results, Result result) {
        Result highest = ResultsSorter.getHighestBySpeed(results);
        if (result.getAvgSpeed() > highest.getAvgSpeed()) {
            speedRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New speed record!");
        }
        else {
            double avgSpeed = StatisticsUtils.getOverallAVGSpeed(results);
            if (result.getAvgSpeed() > avgSpeed) {
                speedRecordLabel.setVisibility(View.VISIBLE);
                speedRecordLabel.setText(getString(R.string.improvedLabel));
                speedRecordLabel.setBackground(getDrawable(R.color.lime));
            }
        }
    }

    private void checkForTimeRecord(ArrayList<Result> results, Result result) {
        Result highest = ResultsSorter.getHighestByTime(results);
        if (result.getTime() > highest.getTime()) {
            timeRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New time record!");
        }
        else {
            double avgTime = StatisticsUtils.getOverallAVGTime(results);
            if (result.getTime() > avgTime) {
                timeRecordLabel.setVisibility(View.VISIBLE);
                timeRecordLabel.setText(getString(R.string.improvedLabel));
                timeRecordLabel.setBackground(getDrawable(R.color.lime));
            }
        }
    }
}