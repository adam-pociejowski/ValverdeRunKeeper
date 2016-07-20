package com.example.valverde.valverderunkeeper.notifications;

import android.util.Log;

public class PaceMaker {
    private final String TAG = getClass().getSimpleName();
    private static final double HOUR_FACTOR = 3600000.0;
    private double paceSpeed;

    public PaceMaker(double paceSpeed) {
        this.paceSpeed = paceSpeed;
        Log.d(TAG, "Pacemaker created! Pace: "+paceSpeed);
    }

    public long getExpectedTimeInPoint(double distance) {
        double timeInDouble = distance / paceSpeed;
        double expectedTime = Math.round(timeInDouble*HOUR_FACTOR);
        return (long) expectedTime;
    }

    public String getDifferenceInSeconds(long actualTime, long expectedTime) {
        long differenceInTime = actualTime - expectedTime;
        long secondsPart = differenceInTime / 1000;
        long msPart = differenceInTime % 1000;
        boolean addition = false;
        String differenceString;
        if (Math.abs(msPart) > 500)
            addition = true;

        if (differenceInTime < 0) {
            if (addition)
                secondsPart -= 1;
            differenceString = Math.abs(secondsPart)+" seconds advantage";
        }
        else if ((differenceInTime > 0) || (differenceInTime == 0 && addition)) {
            if (addition)
                secondsPart += 1;
            differenceString = Math.abs(secondsPart)+" seconds waste";
        }
        else
            differenceString = "pacemaker time";

        return differenceString;
    }
}
