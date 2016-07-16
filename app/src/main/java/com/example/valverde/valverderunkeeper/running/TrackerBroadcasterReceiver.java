package com.example.valverde.valverderunkeeper.running;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrackerBroadcasterReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        TrackerActivity activity = TrackerActivity.getInstance();
        if (activity == null) {
            Log.i(TAG, "No instance of TrackActivity");
        }
        else {
            String msg = "";
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, "Screen went OFF");
                msg = "SCREEN_OFF";
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i(TAG, "Screen went ON");
                msg = "SCREEN_ON";
            }
            activity.onScreenChangeState(msg);
        }
    }
}
