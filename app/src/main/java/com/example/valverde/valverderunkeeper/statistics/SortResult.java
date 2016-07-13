package com.example.valverde.valverderunkeeper.statistics;

import android.util.Log;

import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;
import java.util.ArrayList;

public class SortResult {
    public static ArrayList<RunResult> sortByDistance(ArrayList<RunResult> results) {
        RunResult key;
        int j;
        for (int i = 1; i < results.size(); i++) {
            j = i - 1;
            key = results.get(i);
            while (j >= 0 && results.get(j).getDistance() < key.getDistance()) {
                results.set(j + 1, results.get(j));
                j--;
            }
            results.set(j + 1, key);
        }
        return results;
    }

    public static ArrayList<RunResult> sortByAvgSpeed(ArrayList<RunResult> results) {
        RunResult key;
        int j;
        for (int i = 1; i < results.size(); i++) {
            j = i - 1;
            key = results.get(i);
            while (j >= 0 && results.get(j).getAvgSpeed() < key.getAvgSpeed()) {
                results.set(j + 1, results.get(j));
                j--;
            }
            results.set(j + 1, key);
        }
        return results;
    }

    public static ArrayList<RunResult> sortByTime(ArrayList<RunResult> results) {
        RunResult key;
        int j;
        for (int i = 1; i < results.size(); i++) {
            j = i - 1;
            key = results.get(i);
            while (j >= 0 && results.get(j).getTime() < key.getTime()) {
                results.set(j + 1, results.get(j));
                j--;
            }
            results.set(j + 1, key);
        }
        return results;
    }

    public static ArrayList<RunResult> sortByDate(ArrayList<RunResult> results) {
        RunResult key;
        int j;
        for (int i = 1; i < results.size(); i++) {
            j = i - 1;
            key = results.get(i);
            while (j >= 0 && results.get(j).getDate() < key.getDate()) {
                results.set(j + 1, results.get(j));
                j--;
            }
            results.set(j + 1, key);
        }
        return results;
    }

    public static RunResult getHighestByDistance(ArrayList<RunResult> results) {
        RunResult highest = null;
        for (RunResult r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getDistance() < r.getDistance())
                highest = r;
        }
        return highest;
    }

    public static RunResult getHighestBySpeed(ArrayList<RunResult> results) {
        RunResult highest = null;
        for (RunResult r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getAvgSpeed() < r.getAvgSpeed())
                highest = r;
        }
        return highest;
    }

    public static RunResult getHighestByTime(ArrayList<RunResult> results) {
        RunResult highest = null;
        for (RunResult r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getTime() < r.getTime())
                highest = r;
        }
        return highest;
    }
}
