package com.example.valverde.valverderunkeeper.running;

import com.example.valverde.valverderunkeeper.settings.Settings;
import com.example.valverde.valverderunkeeper.settings.SettingsManager;

import java.util.ArrayList;

public class TrackManager {
    private double upperChangeFactor = 0.0, lowerChangeFactor = 0.0;
    private static final int EVENTS_PER_POINT_ON_ROUTE_MAP = 3;
    private static final double HOUR_FACTOR = 3600000.0;
    private static volatile TrackManager instance = null;
    private ArrayList<GPSEvent> route = new ArrayList<>();
    private ArrayList<GPSEvent> actualGPSEvents = new ArrayList<>();
    private static Settings settings;
    private double overallDistance = 0.0;
    private double lastKnownSpeed = 0.0;
    private int eventsCounter = 0;


    private TrackManager() {}

    public static void setSettings(Settings s) {
        settings = s;
    }

    public double getDistanceInKm(double lat1, double lng1, double lat2, double lng2) {
        double factor = Math.PI / 180.0;
        double dlng = (lng2 - lng1) * factor;
        double dlat = (lat2 - lat1) * factor;
        double a = Math.pow(Math.sin(dlat / 2.0), 2.0) + Math.cos(lat1 * factor) *
                Math.cos(lat2 * factor) * Math.pow(Math.sin(dlng / 2.0), 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return 6367 * c;
    }

    public double getAverangeSpeedInKmH(GPSEvent newEvent) {
        if (actualGPSEvents.size() <= 1) {
            actualGPSEvents.add(newEvent);
            return  0.0;
        }
        GPSEvent lastEvent = actualGPSEvents.get(actualGPSEvents.size() - 1);
        double distanceBetweenEvents =  getDistanceInKm(lastEvent.getLat(), lastEvent.getLng(),
                newEvent.getLat(), newEvent.getLng());
        long timeBetweenEvents = newEvent.getTime() - lastEvent.getTime();
        double speedBetweenEvents = getSpeedBetweenEvents(distanceBetweenEvents, timeBetweenEvents);
        if (iSSpeedMeasureGood(speedBetweenEvents) && newEvent.getAccuracy() <= settings.getGpsAccuracyLimit()) {
            if (actualGPSEvents.size() >= settings.getAmountOfEventsInAverangeSpeed())
                actualGPSEvents.remove(0);

            actualGPSEvents.add(newEvent);
            if (eventsCounter++ % EVENTS_PER_POINT_ON_ROUTE_MAP == 0)
                route.add(newEvent);
            double travelDistance = 0.0;
            for (int i = 0; i < actualGPSEvents.size() - 1; i++) {
                GPSEvent earlierEvent = actualGPSEvents.get(i);
                GPSEvent nextEvent = actualGPSEvents.get(i + 1);
                distanceBetweenEvents = getDistanceInKm(earlierEvent.getLat(), earlierEvent.getLng(),
                        nextEvent.getLat(), nextEvent.getLng());
                travelDistance += distanceBetweenEvents;
                if (i == actualGPSEvents.size() - 2 )
                    overallDistance += distanceBetweenEvents;
            }
            long firstEventTimeInMillis = actualGPSEvents.get(0).getTime();
            long lastEventTimeInMillis = actualGPSEvents.get(actualGPSEvents.size() - 1).getTime();
            long travelTimeInMillis = lastEventTimeInMillis - firstEventTimeInMillis;
            lastKnownSpeed = getSpeedBetweenEvents(travelDistance, travelTimeInMillis);
            return lastKnownSpeed;
        }
        else
            return lastKnownSpeed;
    }

    public void addLastEventToRoute() {
        if (actualGPSEvents.size() > 0 && route.size() > 0) {
            GPSEvent lastEvent = actualGPSEvents.get(actualGPSEvents.size() - 1);
            GPSEvent lastEventInRoute = route.get(route.size() - 1);
            if (lastEvent.getTime() != lastEventInRoute.getTime()) {
                route.add(lastEvent);
            }
        }
    }

    private boolean iSSpeedMeasureGood(double speedBetweenTwoEvents) {
        if (lastKnownSpeed == 0.0)
            return true;
        if (speedBetweenTwoEvents > lastKnownSpeed) { /* Much faster than before */
            if (speedBetweenTwoEvents - lastKnownSpeed <=
                        settings.getMaxUpperChangeBetweenEvents() + upperChangeFactor) {
                upperChangeFactor = 0.0;
                lowerChangeFactor = 0.0;
                return true;
            }
        }
        else { /* Much slower than before */
            if ((lastKnownSpeed - speedBetweenTwoEvents <=
                        settings.getMaxLowerChangeBetweenEvents() + lowerChangeFactor)) {
                upperChangeFactor = 0.0;
                lowerChangeFactor = 0.0;
                return true;
            }
        }
        upperChangeFactor += settings.getMaxChangeIncreasePerMeasure();
        lowerChangeFactor += settings.getMaxChangeIncreasePerMeasure();
        return false;
    }

    public ArrayList<GPSEvent> getRoute() {
        return route;
    }

    private double getSpeedBetweenEvents(double distanceBetween, long timeBetween) {
        return distanceBetween / ((double) timeBetween / HOUR_FACTOR);
    }

    public double getOverallDistance() {
        return overallDistance;
    }

    public static TrackManager getInstance() {
        if (instance == null)
            instance = new TrackManager();
        return instance;
    }
}