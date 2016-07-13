package com.example.valverde.valverderunkeeper.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.main_menu.MainMenuActivity;

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
    @BindView(R.id.gpsSoundNotificationsDistanceIntervalField) EditText soundNotificationsDistanceIntervalField;
    @BindView(R.id.gpsSoundNotificationsDistanceIntervalLabel) TextView distIntervalLabel;
    @BindView(R.id.gpsSoundNotificationsDistanceIntervalUnitsField) TextView distIntervalUnits;
    @BindView(R.id.gpsNotificationCheckBox) CheckBox soundNotificationsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        final Settings settings = SettingsManager.getSettings(getApplicationContext());
        setSettingsInFields(settings);
        setActiveStatusSoundNotificationFields(settings);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
            }
        });

        soundNotificationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setSoundNotifications(soundNotificationsCheckBox.isChecked());
                setActiveStatusSoundNotificationFields(settings);
            }
        });
    }

    private void setActiveStatusSoundNotificationFields(Settings settings) {
        boolean soundNotification = settings.getSoundNotifications();
        soundNotificationsDistanceIntervalField.setEnabled(soundNotification);
        distIntervalLabel.setEnabled(soundNotification);
        distIntervalUnits.setEnabled(soundNotification);
    }

    private void saveSettings() {
        try {
            Settings settings = new Settings();
            settings.setGpsAccuracyLimit(Float.parseFloat(gpsAccuracyField.getText().toString()));
            settings.setEventsRefreshTimeInSeconds(Integer.parseInt(gpsEventsRefreshField.getText().toString()));
            settings.setDefaultZoom(Float.parseFloat(gpsDefaultMapZoomField.getText().toString()));
            settings.setAmountOfEventsInAverangeSpeed(Integer.parseInt(
                    gpsAmountOfEventsInAvgSpeedField.getText().toString()));
            settings.setMaxChangeIncreasePerMeasure(Double.parseDouble(
                    gpsChangeIncreasePerMeasureField.getText().toString()));
            settings.setMaxUpperChangeBetweenEvents(Double.parseDouble(
                    gpsMaxUpperChangeBetweenEventsField.getText().toString()));
            settings.setMaxLowerChangeBetweenEvents(Double.parseDouble(
                    gpsMaxLowerChangeBetweenEventsField.getText().toString()));
            settings.setSoundNotifications(soundNotificationsCheckBox.isChecked());
            settings.setSoundNotificationDistanceInterval(
                    Double.parseDouble(soundNotificationsDistanceIntervalField.getText().toString()));
            SettingsManager.setSettings(settings, getApplicationContext());
        }
        catch (NumberFormatException e) {
            String message = "Wrong format of saving data";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.d("Settings Activity", "Wrong settings data to save");
        }
    }

    private void setSettingsInFields(Settings settings) {
        String accuracy = Float.toString(settings.getGpsAccuracyLimit());
        String refresh = Integer.toString(settings.getEventsRefreshTimeInSeconds());
        String defaultZoom = Float.toString(settings.getDefaultZoom());
        String amountOFEventsInAvSpeed = Integer.toString(settings.getAmountOfEventsInAverangeSpeed());
        String maxUpperChange = Double.toString(settings.getMaxUpperChangeBetweenEvents());
        String maxLowerChange = Double.toString(settings.getMaxLowerChangeBetweenEvents());
        String changePerMeasure = Double.toString(settings.getMaxChangeIncreasePerMeasure());
        boolean soundNotifications = settings.getSoundNotifications();
        String soundNotificationsDistInterval = Double.toString(settings.getSoundNotificationDistanceInterval());

        gpsAccuracyField.setText(accuracy);
        gpsEventsRefreshField.setText(refresh);
        gpsDefaultMapZoomField.setText(defaultZoom);
        gpsAmountOfEventsInAvgSpeedField.setText(amountOFEventsInAvSpeed);
        gpsMaxUpperChangeBetweenEventsField.setText(maxUpperChange);
        gpsMaxLowerChangeBetweenEventsField.setText(maxLowerChange);
        gpsChangeIncreasePerMeasureField.setText(changePerMeasure);
        Log.d("LOG", soundNotifications+" sound");
        soundNotificationsCheckBox.setChecked(soundNotifications);
        soundNotificationsDistanceIntervalField.setText(soundNotificationsDistInterval);
    }
}
