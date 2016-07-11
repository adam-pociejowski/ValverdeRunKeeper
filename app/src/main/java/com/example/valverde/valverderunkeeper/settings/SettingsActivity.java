package com.example.valverde.valverderunkeeper.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.valverde.valverderunkeeper.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.settingsSaveButton) Button saveButton;
    @BindView(R.id.gpsAccuracyField) EditText gpsAccuracyField;
    @BindView(R.id.gpsEventsRefreshField) EditText gpsEventsRefreshField;
    @BindView(R.id.gpsDefaultMapZoomField) EditText gpsDefaultMapZoomField;
    @BindView(R.id.gpsAmountOfEventsInAvgSpeedField) EditText gpsAmountOfEventsInAvgSpeedField;
    @BindView(R.id.gpsMaxUpperChangeBetweenEventsField) EditText gpsMaxUpperChangeBetweenEventsField;
    @BindView(R.id.gpsMaxLowerChangeBetweenEventsField) EditText gpsMaxLowerChangeBetweenEventsField;
    @BindView(R.id.gpsChangeIncreasePerMeasureField) EditText gpsChangeIncreasePerMeasureField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Settings settings = SettingsManager.getSettings(getApplicationContext());
        setSettingsInFields(settings);

    }

    private void setSettingsInFields(Settings settings) {
        String accuracy = Float.toString(settings.getGpsAccuracyLimit());
        String refresh = Integer.toString(settings.getEventsRefreshTimeInSeconds());
        String defaultZoom = Float.toString(settings.getDefaultZoom());
        String amountOFEventsInAvSpeed = Integer.toString(settings.getAmountOfEventsInAverangeSpeed());
        String maxUpperChange = Double.toString(settings.getMaxUpperChangeBetweenEvents());
        String maxLowerChange = Double.toString(settings.getMaxLowerChangeBetweenEvents());
        String changePerMeasure = Double.toString(settings.getMaxChangeIncreasePerMeasure());

        gpsAccuracyField.setText(accuracy);
        gpsEventsRefreshField.setText(refresh);
        gpsDefaultMapZoomField.setText(defaultZoom);
        gpsAmountOfEventsInAvgSpeedField.setText(amountOFEventsInAvSpeed);
        gpsMaxUpperChangeBetweenEventsField.setText(maxUpperChange);
        gpsMaxLowerChangeBetweenEventsField.setText(maxLowerChange);
        gpsChangeIncreasePerMeasureField.setText(changePerMeasure);
    }
}
