package com.example.valverde.valverderunkeeper.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Map;

public class SettingsManager {
    private static final String PREFERENCES_KEY = "com.example.valverde.valverderunkeeper.preferences";
    private final String TAG = getClass().getSimpleName();
    private Context context;

    public SettingsManager(Context context) {
        this.context = context;
    }

    public Settings getSettings() {
        SharedPreferences pref = getPreferences();
        Map<String, ?> prefMap = pref.getAll();
        Settings settings = new Settings();
        for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
            String methodName = "set"+entry.getKey().substring(0,1).toUpperCase()+
                    entry.getKey().substring(1);
            Class paramType = entry.getValue().getClass();
            try {
                Method method = settings.getClass().getMethod(methodName, paramType);
                method.invoke(settings, entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return settings;
    }

    public void setSettings(Settings settings) {
        SharedPreferences pref = getPreferences();
        SharedPreferences.Editor editor = pref.edit();
        Map<String, ?> prefMap = pref.getAll();

        for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
            String key = entry.getKey();
            String methodName = "get"+key.substring(0,1).toUpperCase()+
                    key.substring(1);
            try {
                Method method = settings.getClass().getMethod(methodName);
                Object result  = method.invoke(settings);
                if (result instanceof Integer) {
                    editor.putInt(key, (int) result);
                }
                else if (result instanceof Float) {
                    editor.putFloat(key, (float) result);
                }
                else if (result instanceof Boolean) {
                    editor.putBoolean(key, (boolean) result);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        editor.apply();
    }

    public void setDefaultSettings() {
        SharedPreferences preferences = getPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("defaultZoom", 16.0f);
        editor.putFloat("eventsRefreshTimeInSeconds", 3f);
        editor.putInt("amountOfEventsInAverangeSpeed", 4);
        editor.putFloat("maxUpperChangeBetweenEvents", 8f);
        editor.putFloat("maxLowerChangeBetweenEvents", 8f);
        editor.putFloat("maxChangeIncreasePerMeasure", 6f);
        editor.putFloat("gpsAccuracyLimit", 25.0f);
        editor.putBoolean("soundNotifications", true);
        editor.putFloat("soundNotificationDistanceInterval", 0.5f);
        editor.putBoolean("screenLockSupport", true);
        editor.putFloat("defaultPace", 11f);
        editor.apply();
    }

    public void resetSettings() {
        SharedPreferences preferences = getPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
}