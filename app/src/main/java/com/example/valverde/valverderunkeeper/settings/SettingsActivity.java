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

import java.text.DecimalFormat;

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
    @BindView(R.id.gpsScreenLockSupportCheckBox) CheckBox screenLockSupportBox;
    @BindView(R.id.gpsDefaultPaceLabel) TextView defaultPaceLabel;
    @BindView(R.id.gpsDefaultPaceField) EditText defaultPaceField;
    @BindView(R.id.gpsDefaultPaceFieldUnits) TextView defaultPaceUnits;
    private final String TAG = getClass().getSimpleName();
    private SettingsManager preferencesManager;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        preferencesManager = new SettingsManager(this);
        settings = preferencesManager.getSettings();
        setSettingsInFields();
        setActiveStatusSoundNotificationFields();

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
                setActiveStatusSoundNotificationFields();
            }
        });
    }

    private void setActiveStatusSoundNotificationFields() {
        boolean soundNotification = settings.getSoundNotifications();
        soundNotificationsDistanceIntervalField.setEnabled(soundNotification);
        distIntervalLabel.setEnabled(soundNotification);
        distIntervalUnits.setEnabled(soundNotification);
        defaultPaceLabel.setEnabled(soundNotification);
        defaultPaceField.setEnabled(soundNotification);
        defaultPaceUnits.setEnabled(soundNotification);
    }

    private void saveSettings() {
        try {
            Settings settings = new Settings();
            settings.setGpsAccuracyLimit(Float.parseFloat(gpsAccuracyField.getText().toString()));
            settings.setEventsRefreshTimeInSeconds(Float.parseFloat(gpsEventsRefreshField.getText().toString()));
            settings.setDefaultZoom(Float.parseFloat(gpsDefaultMapZoomField.getText().toString()));
            settings.setAmountOfEventsInAverangeSpeed(Integer.parseInt(
                    gpsAmountOfEventsInAvgSpeedField.getText().toString()));
            settings.setMaxChangeIncreasePerMeasure(Float.parseFloat(
                    gpsChangeIncreasePerMeasureField.getText().toString()));
            settings.setMaxUpperChangeBetweenEvents(Float.parseFloat(
                    gpsMaxUpperChangeBetweenEventsField.getText().toString()));
            settings.setMaxLowerChangeBetweenEvents(Float.parseFloat(
                    gpsMaxLowerChangeBetweenEventsField.getText().toString()));
            settings.setSoundNotifications(soundNotificationsCheckBox.isChecked());
            settings.setSoundNotificationDistanceInterval(
                    Float.parseFloat(soundNotificationsDistanceIntervalField.getText().toString()));
            settings.setScreenLockSupport(screenLockSupportBox.isChecked());
            settings.setDefaultPace(Float.parseFloat(defaultPaceField.getText().toString()));

            preferencesManager.setSettings(settings);
        }
        catch (NumberFormatException e) {
            String message = "Wrong format of saving data";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Wrong settings data to save");
        }
    }

    private void setSettingsInFields() {
        try {
            String accuracy = Float.toString(settings.getGpsAccuracyLimit());
            String refresh = Float.toString(settings.getEventsRefreshTimeInSeconds());
            String defaultZoom = Float.toString(settings.getDefaultZoom());
            String amountOFEventsInAvSpeed = Integer.toString(settings.getAmountOfEventsInAverangeSpeed());
            String maxUpperChange = Float.toString(settings.getMaxUpperChangeBetweenEvents());
            String maxLowerChange = Float.toString(settings.getMaxLowerChangeBetweenEvents());
            String changePerMeasure = Float.toString(settings.getMaxChangeIncreasePerMeasure());
            boolean soundNotifications = settings.getSoundNotifications();
            boolean screenLockSupport = settings.getScreenLockSupport();
            DecimalFormat df = new DecimalFormat("#.##");
            String soundNotificationsDistInterval = df.format(settings.getSoundNotificationDistanceInterval());
            String pace = df.format(settings.getDefaultPace());

            gpsAccuracyField.setText(accuracy);
            gpsEventsRefreshField.setText(refresh);
            gpsDefaultMapZoomField.setText(defaultZoom);
            gpsAmountOfEventsInAvgSpeedField.setText(amountOFEventsInAvSpeed);
            gpsMaxUpperChangeBetweenEventsField.setText(maxUpperChange);
            gpsMaxLowerChangeBetweenEventsField.setText(maxLowerChange);
            gpsChangeIncreasePerMeasureField.setText(changePerMeasure);
            soundNotificationsCheckBox.setChecked(soundNotifications);
            soundNotificationsDistanceIntervalField.setText(soundNotificationsDistInterval);
            screenLockSupportBox.setChecked(screenLockSupport);
            defaultPaceField.setText(pace);
        }
        catch (Exception e) {
            Log.e(TAG, "No data in preferences. Loading default values.");
            preferencesManager.setDefaultSettings();
            settings = preferencesManager.getSettings();
            setSettingsInFields();
        }
    }
}