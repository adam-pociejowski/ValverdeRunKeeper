package com.example.valverde.valverderunkeeper;

class GPSEvent {
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