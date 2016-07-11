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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FinalizeRunActivity extends Activity {
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
//        db.onUpgrade(db.getWritableDatabase(), 1, 1);
        DatabaseGPSEventsHelper dh = new DatabaseGPSEventsHelper(this);

        final RunResult result = (RunResult) intent.getSerializableExtra("result");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String timeInFormat = Timer.getTimeInFormat(result.getTime());
        double distance = result.getDistance();
        String distanceInFormat = decimalFormat.format(distance)+" "+getString(R.string.distanceUnits);
        double avgSpeed = result.getAvgSpeed();
        String avgSpeedInFormat = decimalFormat.format(avgSpeed)+" "+getString(R.string.speedUnits);

        timeField.setText(timeInFormat);
        distanceField.setText(distanceInFormat);
        avgSpeedField.setText(avgSpeedInFormat);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                result.setDate(date.getTime());
//                db.insertResult(result);
                showAllEvents(result.getRoute());
                showAllResults(db);
                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(i);
            }
        });
    }

    private void showAllResults(DatabaseRunResultsHelper db) {
        ArrayList<RunResult> results = db.getAllResults();
        for (RunResult r : results) {
            Log.d("RunResults", "Time: "+r.getTime()+" | AVG: "+r.getAvgSpeed()+" | Distance: "+r.getDistance());
        }
    }


    private void showAllEvents(ArrayList<GPSEvent> events) {
        for (GPSEvent e : events) {
            Log.d("SPEED", "ID: "+e.getId()+" |  ACCURACY: "+e.getAccuracy()+
                    " | LAT: "+e.getLat()+" | LNG: "+e.getLng());
        }
    }
}