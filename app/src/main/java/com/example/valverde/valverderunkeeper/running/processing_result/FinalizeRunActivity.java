package com.example.valverde.valverderunkeeper.running.processing_result;

import android.app.Activity;
import android.content.Intent;
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
import com.example.valverde.valverderunkeeper.statistics.ResultsSorter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FinalizeRunActivity extends Activity {
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
        final DatabaseRunResultsHelper db = new DatabaseRunResultsHelper(this);
        final DatabaseGPSEventsHelper dh = new DatabaseGPSEventsHelper(this);
        final RunResult result = (RunResult) intent.getSerializableExtra("result");
        hideRecordTextViews();
        setResultParamsInTextViews(result);
        checkForRecords(result, db);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                result.setDate(date.getTime());
                long id = db.getMaxResultId()  +1;
                result.setResultId(id);
                for (GPSEvent e : result.getRoute()) {
                    e.setId(id);
                }
                db.insertResult(result);
                dh.insertData(result.getRoute());
                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(i);
            }
        });
    }

    private void hideRecordTextViews() {
        speedRecordLabel.setVisibility(View.INVISIBLE);
        timeRecordLabel.setVisibility(View.INVISIBLE);
        distanceRecordLabel.setVisibility(View.INVISIBLE);
        caloriesRecordLabel.setVisibility(View.INVISIBLE);
    }

    private void setResultParamsInTextViews(RunResult result) {
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

    public void checkForRecords(RunResult result, DatabaseRunResultsHelper db) {
        try {
            ArrayList<RunResult> results = db.getAllResults();
            checkIfIsDistanceRecord(results, result);
            checkIfIsSpeedRecord(results, result);
            checkIfIsTimeRecord(results, result);
        }
        catch (IndexOutOfBoundsException e) {
            Log.e(this.getClass().getName(), "There isn't any previous results..");
        }
    }

    private void checkIfIsDistanceRecord(ArrayList<RunResult> results, RunResult result) {
        RunResult highest = ResultsSorter.getHighestByDistance(results);
        if (result.getDistance() > highest.getDistance()) {
            distanceRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New distance record!");
        }
    }

    private void checkIfIsSpeedRecord(ArrayList<RunResult> results, RunResult result) {
        RunResult highest = ResultsSorter.getHighestBySpeed(results);
        if (result.getAvgSpeed() > highest.getAvgSpeed()) {
            speedRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New speed record!");
        }
    }

    private void checkIfIsTimeRecord(ArrayList<RunResult> results, RunResult result) {
        RunResult highest = ResultsSorter.getHighestByTime(results);
        if (result.getTime() > highest.getTime()) {
            timeRecordLabel.setVisibility(View.VISIBLE);
            Log.d(getClass().getName(), "New time record!");
        }
    }
}