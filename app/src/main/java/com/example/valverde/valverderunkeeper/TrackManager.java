package com.example.valverde.valverderunkeeper;

import android.util.Log;
import java.util.ArrayList;

public class TrackManager {
    private static final int AMOUNT_OF_EVENT_IN_AVERANGE_SPEED = 5;
    private static final double HOUR_FACTOR = 3600000.0;
    private static volatile TrackManager instance = null;
    private ArrayList<GPSEvent> gpsEvents = new ArrayList<>();
    private double overallDistance = 0.0;
    private boolean running = false;


    private TrackManager() {}


    public double getDistanceInKm(double lat1, double lng1, double lat2, double lng2) {
        double factor = Math.PI / 180.0;
        double dlng = (lng2 - lng1) * factor;
        double dlat = (lat2 - lat1) * factor;
        double a = Math.pow(Math.sin(dlat / 2.0), 2.0) + Math.cos(lat1 * factor) *
                Math.cos(lat2 * factor) * Math.pow(Math.sin(dlng / 2.0), 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return 6367 * c;
    }


    public double getAverangeSpeedInKmH(GPSEvent event) {
        gpsEvents.add(event);
        double travelDistance = 0.0;
        int startIndex;
        if (gpsEvents.size() <= 1)
            return 0.0;
        else if (gpsEvents.size() <= AMOUNT_OF_EVENT_IN_AVERANGE_SPEED)
            startIndex = 0;
        else
            startIndex = gpsEvents.size() - AMOUNT_OF_EVENT_IN_AVERANGE_SPEED;

        for (int i = startIndex; i < gpsEvents.size() - 1; i++) {
            GPSEvent ealierEvent = gpsEvents.get(i);
            GPSEvent nextEvent = gpsEvents.get(i + 1);
            double distanceBetweenEvents = getDistanceInKm(ealierEvent.getLat(), ealierEvent.getLng(),
                                            nextEvent.getLat(), nextEvent.getLng());
            travelDistance += distanceBetweenEvents;
            if (i == gpsEvents.size() - 2 && running)
                overallDistance += distanceBetweenEvents;
        }
        long firstEventTimeInMillis = gpsEvents.get(startIndex).getTime();
        long lastEventTimeInMillis = gpsEvents.get(gpsEvents.size() - 1).getTime();
        long travelTimeInMillis = lastEventTimeInMillis - firstEventTimeInMillis;
        return travelDistance / ((double) travelTimeInMillis / HOUR_FACTOR);
    }


    public double getOverallDistance() {
        return overallDistance;
    }


    public void setRunning(boolean running) {
        this.running = running;
    }


    public static TrackManager getInstance() {
        if (instance == null)
            instance = new TrackManager();
        return instance;
    }
}