package com.example.valverde.valverderunkeeper.settings;

public class Settings {
    private float defaultZoom;
    private int eventsRefreshTimeInSeconds;
    private int amountOfEventsInAverangeSpeed;
    private double maxUpperChangeBetweenEvents;
    private double maxLowerChangeBetweenEvents;
    private double maxChangeIncreasePerMeasure;
    private float gpsAccuracyLimit;
    private boolean soundNotifications;
    private double soundNotificationDistanceInterval;

    public boolean getSoundNotifications() {
        return soundNotifications;
    }

    public void setSoundNotifications(boolean soundNotifications) {
        this.soundNotifications = soundNotifications;
    }

    public double getSoundNotificationDistanceInterval() {
        return soundNotificationDistanceInterval;
    }

    public void setSoundNotificationDistanceInterval(double soundNotificationDistanceInterval) {
        this.soundNotificationDistanceInterval = soundNotificationDistanceInterval;
    }

    public float getDefaultZoom() {
        return defaultZoom;
    }

    public void setDefaultZoom(float defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    public int getEventsRefreshTimeInSeconds() {
        return eventsRefreshTimeInSeconds;
    }

    public void setEventsRefreshTimeInSeconds(int eventsRefreshTimeInSeconds) {
        this.eventsRefreshTimeInSeconds = eventsRefreshTimeInSeconds;
    }

    public int getAmountOfEventsInAverangeSpeed() {
        return amountOfEventsInAverangeSpeed;
    }

    public void setAmountOfEventsInAverangeSpeed(int amountOfEventsInAverangeSpeed) {
        this.amountOfEventsInAverangeSpeed = amountOfEventsInAverangeSpeed;
    }

    public double getMaxUpperChangeBetweenEvents() {
        return maxUpperChangeBetweenEvents;
    }

    public void setMaxUpperChangeBetweenEvents(double maxUpperChangeBetweenEvents) {
        this.maxUpperChangeBetweenEvents = maxUpperChangeBetweenEvents;
    }

    public double getMaxLowerChangeBetweenEvents() {
        return maxLowerChangeBetweenEvents;
    }

    public void setMaxLowerChangeBetweenEvents(double maxLowerChangeBetweenEvents) {
        this.maxLowerChangeBetweenEvents = maxLowerChangeBetweenEvents;
    }

    public double getMaxChangeIncreasePerMeasure() {
        return maxChangeIncreasePerMeasure;
    }

    public void setMaxChangeIncreasePerMeasure(double maxChangeIncreasePerMeasure) {
        this.maxChangeIncreasePerMeasure = maxChangeIncreasePerMeasure;
    }

    public float getGpsAccuracyLimit() {
        return gpsAccuracyLimit;
    }

    public void setGpsAccuracyLimit(float gpsAccuracyLimit) {
        this.gpsAccuracyLimit = gpsAccuracyLimit;
    }
}