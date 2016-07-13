package com.example.valverde.valverderunkeeper.notification;

import android.content.Context;
import com.example.valverde.valverderunkeeper.R;

public class RunningSpeaker implements SpeakingManager {
    private Speaker speaker;
    private int alreadyNotifiedTimes = 0;
    private double startDistance = 0.0;
    private double interval = 0.0;
    private Context context;

    public RunningSpeaker(Context context) {
        this.context = context;
        speaker = new Speaker(context);
    }

    public RunningSpeaker(Context context, double startDistance) {
        this.context = context;
        speaker = new Speaker(context);
        this.startDistance = startDistance;
    }

    @Override
    public void notifyDistance(double distance) {
        int quotient = (int) ((distance - startDistance) / interval);
        if (quotient > alreadyNotifiedTimes) {
            int distanceToNotify = (int)interval * quotient;
            String notifyMessage = distanceToNotify+" "+context.getString(R.string.fullNameOfDistanceUnits);
            speaker.speak(notifyMessage);
            alreadyNotifiedTimes++;
        }
    }

    @Override
    public void setDistanceNotifyInterval(double interval) {
        this.interval = interval;
    }

    @Override
    public void close() {
        speaker.close();
    }
}