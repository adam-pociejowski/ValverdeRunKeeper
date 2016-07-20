package com.example.valverde.valverderunkeeper.notifications;

import android.content.Context;
import com.example.valverde.valverderunkeeper.R;

public class RunningSpeaker implements SpeakingManager {
    private int alreadyNotifiedTimes = 0;
    private double startDistance = 0.0;
    private double interval = 0.0;
    private PaceMaker pacemaker = null;
    private Speaker speaker;
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
    public void notify(double distance, long timeElapsed) {
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
            if (pacemaker != null) {
                long expectedTime = pacemaker.getExpectedTimeInPoint(distance);
                String pacemakerMsg = pacemaker.getDifferenceInSeconds(timeElapsed, expectedTime);
                speaker.speak(pacemakerMsg);
            }
            alreadyNotifiedTimes++;
        }
    }

    @Override
    public void speak(String text) {
        speaker.speak(text);
    }

    @Override
    public void setDistanceNotifyInterval(double interval) {
        this.interval = interval;
    }

    @Override
    public void close() {
        speaker.close();
    }

    @Override
    public void setPacemaker(PaceMaker pacemaker) {
        this.pacemaker = pacemaker;
    }
}