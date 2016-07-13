package com.example.valverde.valverderunkeeper.running.processing_result;

import com.example.valverde.valverderunkeeper.running.GPSEvent;

import java.io.Serializable;
import java.util.ArrayList;

public class RunResult implements Serializable {
    private long time;
    private double distance;
    private int calories;
    private long date;
    private ArrayList<GPSEvent> route;

    public RunResult(long time, double distance, int calories) {
        this.time = time;
        this.distance = distance;
        this.calories = calories;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public long getTime() {
        return time;
    }

    public int getCalories() {
        return calories;
    }

    public double getAvgSpeed() {
        return distance / (time / 3600000.0);
    }

    public ArrayList<GPSEvent> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<GPSEvent> route) {
        this.route = route;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
