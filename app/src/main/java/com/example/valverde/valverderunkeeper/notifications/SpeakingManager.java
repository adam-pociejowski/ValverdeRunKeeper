package com.example.valverde.valverderunkeeper.notifications;

public interface SpeakingManager {
    void notify(double distance, long timeElapsed);
    void speak(String text);
    void setDistanceNotifyInterval(double interval);
    void close();
    void setPacemaker(PaceMaker pacemaker);
}
