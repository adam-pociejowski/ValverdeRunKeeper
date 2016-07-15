package com.example.valverde.valverderunkeeper.notifications;

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
            int distanceInMeters = (int)((interval * (double) quotient) * 1000);
            int kilometersPart = distanceInMeters / 1000;
            int metersPart = distanceInMeters % 1000;
            String notifyMessage = "";
            if (kilometersPart > 0) {
                if (kilometersPart == 1)
                    notifyMessage = kilometersPart+" "+context.getString(R.string.fullNameOfDistanceKilometerUnits);
                else
                    notifyMessage = kilometersPart+" "+context.getString(R.string.fullNameOfDistanceKilometersUnits);
            }
            if (metersPart > 0)
                notifyMessage += " "+metersPart+" "+context.getString(R.string.fullNameOfDistanceMetersUnits);

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