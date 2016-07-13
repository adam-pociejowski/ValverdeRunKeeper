package com.example.valverde.valverderunkeeper.notification;

public interface SpeakingManager {
    void notifyDistance(double distance);
    void setDistanceNotifyInterval(double interval);
    void close();
}
