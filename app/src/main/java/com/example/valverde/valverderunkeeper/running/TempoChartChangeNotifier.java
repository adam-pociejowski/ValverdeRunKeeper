package com.example.valverde.valverderunkeeper.running;

import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;

public class TempoChartChangeNotifier {
    private TempoChart tempoChart;
    private int nodesAmount = 0;
    private int lastEventIndex = 0;
    private double distanceInterval = 0.1;
    private TrackUtils manager;

    public TempoChartChangeNotifier(Context context) {
        tempoChart = new TempoChart(context);
        manager = TrackUtils.getInstance();
    }

    public void notifyDistance(double distance) {
        int quotient = (int) (distance / distanceInterval);
        if (nodesAmount < quotient) {
            nodesAmount++;
            ArrayList<GPSEvent> events = manager.getRoute();
            double tempo = getTempo(events, quotient);
            tempoChart.addEntry((float) tempo);
        }
    }

    private double getTempo(ArrayList<GPSEvent> allEvents, int nodeNumber) {
        double dist = 0.0;
        double endDistance = (double) nodeNumber*distanceInterval;
        GPSEvent firstEvent = allEvents.get(lastEventIndex);
        long startTime = firstEvent.getTime();
        for (int i = lastEventIndex + 1; i < allEvents.size(); i++) {
            GPSEvent secondEvent = allEvents.get(i);
            dist += manager.getDistanceInKm(firstEvent, secondEvent);
            firstEvent = secondEvent;
            if (dist > endDistance) {
                lastEventIndex = i;
                break;
            }
        }
        long endTime = firstEvent.getTime();
        long timeElapsed = endTime - startTime;
        return manager.getSpeedBetweenEvents(dist, timeElapsed);
    }

    public LineChart getChart() {
        return tempoChart.getChart();
    }
}