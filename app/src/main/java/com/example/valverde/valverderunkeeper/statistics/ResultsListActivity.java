package com.example.valverde.valverderunkeeper.statistics;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.data.DatabaseRunResultsHelper;
import com.example.valverde.valverderunkeeper.running.GPSEvent;
import com.example.valverde.valverderunkeeper.running.Timer;
import com.example.valverde.valverderunkeeper.running.TrackUtils;
import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsListActivity extends AppCompatActivity {
    @BindView(R.id.resultsPresentationListView) ListView listView;
    @BindView(R.id.avgDistanceLabel) TextView avgDistanceLabel;
    @BindView(R.id.avgTimeLabel) TextView avgTimeLabel;
    @BindView(R.id.avgSpeedLabel) TextView avgSpeedLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_presentation_layout);
        ButterKnife.bind(this);
        final List<RunResult> results = getResultsFromDatabase();
        MyListViewAdapter myAdapter = new MyListViewAdapter(this.getApplicationContext());
        myAdapter.setResults(results);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RunResult result = results.get(i);
                Intent intent = new Intent(getApplicationContext(), ResultPresentationActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });
        setAverangeStatisticsInFields(results);
    }

    private void setAverangeStatisticsInFields(List<RunResult> results) {
        if (results != null) {
            double overallDistance = getOverallDistance(results);
            long overallTime = getOverallTime(results);
            int resultsAmount = results.size();

            double avgDistance = overallDistance / (double) resultsAmount;
            long avgTime = overallTime / resultsAmount;
            double avgSpeed = TrackUtils.getSpeedBetweenEvents(overallDistance, overallTime);


            DecimalFormat df = new DecimalFormat("#.##");
            String avgDistanceString = df.format(avgDistance)+" "+getString(R.string.distanceUnits);
            String avgTimeString = Timer.getTimeInFormat(avgTime);
            String avgSpeedString = df.format(avgSpeed)+" "+getString(R.string.speedUnits);

            avgDistanceLabel.setText(avgDistanceString);
            avgSpeedLabel.setText(avgSpeedString);
            avgTimeLabel.setText(avgTimeString);
        }
    }

    private double getOverallDistance(List<RunResult> results) {
        double distance = 0.0;
        for (RunResult result : results) {
            distance += result.getDistance();
        }
        return distance;
    }

    private long getOverallTime(List<RunResult> results) {
        long overalTime = 0;
        for (RunResult result : results) {
            overalTime += result.getTime();
        }
        return overalTime;
    }

    private List<RunResult> getResultsFromDatabase() {
        DatabaseRunResultsHelper db = new DatabaseRunResultsHelper(getApplicationContext());
        return db.getAllResults();
    }


    class MyListViewAdapter extends BaseAdapter {
        private Context context;
        private List<RunResult> results;

        public MyListViewAdapter(Context context) { this.context = context; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView  = layoutInflater.inflate(R.layout.presentation_listview_item, parent, false);
            RunResult result = results.get(position);
            TextView numberField = (TextView) customView.findViewById(R.id.presentationListViewItemNumber);
            TextView dateField = (TextView) customView.findViewById(R.id.presentationListViewItemDate);
            TextView distanceField = (TextView) customView.findViewById(R.id.presentationListViewItemDistance);
            TextView timeField = (TextView) customView.findViewById(R.id.presentationListViewItemTime);
            TextView speedField = (TextView) customView.findViewById(R.id.presentationListViewItemSpeed);

            String number = Integer.toString(position + 1);
            numberField.setText(number);

            DecimalFormat df = new DecimalFormat("#.##");
            String distance = df.format(result.getDistance())+" "+getString(R.string.distanceUnits);
            distanceField.setText(distance);

            long dateInLong = result.getDate();
            Date date = new Date(dateInLong);
            String dateFormat = "dd-MM-yyyy";
            DateFormat datef = new SimpleDateFormat(dateFormat);
            dateField.setText(datef.format(date));

            long timeInMillis = result.getTime();
            String timeInFormat = Timer.getTimeInFormat(timeInMillis);
            timeField.setText(timeInFormat);

            String speed = df.format(result.getAvgSpeed())+" "+getString(R.string.speedUnits);
            speedField.setText(speed);

            return customView;
        }

        public void setResults(List<RunResult> results) {
            this.results = results;
        }

        @Override
        public int getCount() { return results.size(); }

        @Override
        public Object getItem(int position) { return results.get(position); }

        @Override
        public long getItemId(int position) { return position; }
    }
}