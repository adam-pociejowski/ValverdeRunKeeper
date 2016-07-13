package com.example.valverde.valverderunkeeper.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREFERENCES_KEY = "com.example.valverde.valverderunkeeper.preferences";

    public static Settings getSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        Settings settings = new Settings();
        settings.setDefaultZoom(preferences.getFloat("defaultZoom", 0f));
        settings.setEventsRefreshTimeInSeconds(preferences.getInt("eventsRefreshTimeInSeconds", 0));
        settings.setAmountOfEventsInAverangeSpeed(preferences.getInt("amountOfEventsInAverangeSpeed", 0));
        settings.setMaxUpperChangeBetweenEvents((double) preferences.getInt("maxUpperChangeBetweenEvents", 0));
        settings.setMaxLowerChangeBetweenEvents((double) preferences.getInt("maxLowerChangeBetweenEvents", 0));
        settings.setMaxChangeIncreasePerMeasure((double) preferences.getInt("maxChangeIncreasePerMeasure", 0));
        settings.setGpsAccuracyLimit(preferences.getFloat("gpsAccuracyLimit", 0f));
        settings.setSoundNotifications(preferences.getBoolean("soundNotifications", false));
        settings.setSoundNotificationDistanceInterval((double) preferences.getInt("soundNotificationDistanceInterval", 0));
        return settings;
    }

    public static void setSettings(Settings settings, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("defaultZoom", settings.getDefaultZoom());
        editor.putInt("eventsRefreshTimeInSeconds", settings.getEventsRefreshTimeInSeconds());
        editor.putInt("amountOfEventsInAverangeSpeed", settings.getAmountOfEventsInAverangeSpeed());
        editor.putInt("maxUpperChangeBetweenEvents", (int) settings.getMaxUpperChangeBetweenEvents());
        editor.putInt("maxLowerChangeBetweenEvents", (int) settings.getMaxLowerChangeBetweenEvents());
        editor.putInt("maxChangeIncreasePerMeasure", (int) settings.getMaxChangeIncreasePerMeasure());
        editor.putFloat("gpsAccuracyLimit", settings.getGpsAccuracyLimit());
        editor.putBoolean("soundNotifications", settings.getSoundNotifications());
        editor.putInt("soundNotificationDistanceInterval", (int) settings.getSoundNotificationDistanceInterval());
        editor.apply();
    }
}