package com.example.valverde.valverderunkeeper.settings;

public class Settings {
    private Float defaultZoom;
    private Float eventsRefreshTimeInSeconds;
    private Integer amountOfEventsInAverangeSpeed;
    private Float maxUpperChangeBetweenEvents;
    private Float maxLowerChangeBetweenEvents;
    private Float maxChangeIncreasePerMeasure;
    private Float gpsAccuracyLimit;
    private Boolean soundNotifications;
    private Float soundNotificationDistanceInterval;
    private Boolean screenLockSupport;
    private Float defaultPace;

    public Float getDefaultZoom() {
        return defaultZoom;
    }

    public void setDefaultZoom(Float defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    public Float getEventsRefreshTimeInSeconds() {
        return eventsRefreshTimeInSeconds;
    }

    public void setEventsRefreshTimeInSeconds(Float eventsRefreshTimeInSeconds) {
        this.eventsRefreshTimeInSeconds = eventsRefreshTimeInSeconds;
    }

    public Integer getAmountOfEventsInAverangeSpeed() {
        return amountOfEventsInAverangeSpeed;
    }

    public void setAmountOfEventsInAverangeSpeed(Integer amountOfEventsInAverangeSpeed) {
        this.amountOfEventsInAverangeSpeed = amountOfEventsInAverangeSpeed;
    }

    public Float getMaxLowerChangeBetweenEvents() {
        return maxLowerChangeBetweenEvents;
    }

    public void setMaxLowerChangeBetweenEvents(Float maxLowerChangeBetweenEvents) {
        this.maxLowerChangeBetweenEvents = maxLowerChangeBetweenEvents;
    }

    public Float getMaxUpperChangeBetweenEvents() {
        return maxUpperChangeBetweenEvents;
    }

    public void setMaxUpperChangeBetweenEvents(Float maxUpperChangeBetweenEvents) {
        this.maxUpperChangeBetweenEvents = maxUpperChangeBetweenEvents;
    }

    public Float getMaxChangeIncreasePerMeasure() {
        return maxChangeIncreasePerMeasure;
    }

    public void setMaxChangeIncreasePerMeasure(Float maxChangeIncreasePerMeasure) {
        this.maxChangeIncreasePerMeasure = maxChangeIncreasePerMeasure;
    }

    public Float getGpsAccuracyLimit() {
        return gpsAccuracyLimit;
    }

    public void setGpsAccuracyLimit(Float gpsAccuracyLimit) {
        this.gpsAccuracyLimit = gpsAccuracyLimit;
    }

    public Boolean getSoundNotifications() {
        return soundNotifications;
    }

    public void setSoundNotifications(Boolean soundNotifications) {
        this.soundNotifications = soundNotifications;
    }

    public Boolean getScreenLockSupport() {
        return screenLockSupport;
    }

    public void setScreenLockSupport(Boolean screenLockSupport) {
        this.screenLockSupport = screenLockSupport;
    }

    public Float getSoundNotificationDistanceInterval() {
        return soundNotificationDistanceInterval;
    }

    public void setSoundNotificationDistanceInterval(Float soundNotificationDistanceInterval) {
        this.soundNotificationDistanceInterval = soundNotificationDistanceInterval;
    }

    public Float getDefaultPace() {
        return defaultPace;
    }

    public void setDefaultPace(Float defaultPace) {
        this.defaultPace = defaultPace;
    }
}