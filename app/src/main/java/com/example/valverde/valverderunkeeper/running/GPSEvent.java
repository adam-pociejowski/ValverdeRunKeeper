package com.example.valverde.valverderunkeeper.running;

import java.io.Serializable;

public class GPSEvent implements Serializable {
    private long id;
    private long time;
    private double lat;
    private double lng;
    private float accuracy;

    public GPSEvent(long time, double lat, double lng, float accuracy) {
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public float getAccuracy() {
        return accuracy;
    }
}