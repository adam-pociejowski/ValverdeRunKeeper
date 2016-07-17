package com.example.valverde.valverderunkeeper.statistics;

import com.example.valverde.valverderunkeeper.running.TrackerUtils;
import com.example.valverde.valverderunkeeper.running.processing_result.Result;

import java.util.List;

public class StatisticsUtils {

    public static double getOverallDistance(List<Result> results) {
        double distance = 0.0;
        for (Result result : results) {
            distance += result.getDistance();
        }
        return distance;
    }

    public static long getOverallTime(List<Result> results) {
        long overallTime = 0;
        for (Result result : results) {
            overallTime += result.getTime();
        }
        return overallTime;
    }

    public static double getOverallAVGSpeed(List<Result> results) {
        long overallTime = getOverallTime(results);
        double overallDistance = getOverallDistance(results);
        return TrackerUtils.getSpeedBetweenEvents(overallDistance, overallTime);
    }

    public static long getOverallAVGTime(List<Result> results) {
        long overallTime = getOverallTime(results);
        return overallTime / results.size();
    }

    public static double getOverallAVGDistance(List<Result> results) {
        double overallDistance = getOverallDistance(results);
        return overallDistance / (double) results.size();
    }
}
