package com.example.valverde.valverderunkeeper.notifications;

public interface SpeakingManager {
    void notifyDistance(double distance);
    void speak(String text);
    void setDistanceNotifyInterval(double interval);
    void close();
}
