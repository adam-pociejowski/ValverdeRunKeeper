package com.example.valverde.valverderunkeeper.statistics;

import com.example.valverde.valverderunkeeper.running.processing_result.Result;
import java.util.ArrayList;

public class ResultsSorter {
    public static ArrayList<Result> sortByDistance(ArrayList<Result> results) {
        Result key;
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

    public static ArrayList<Result> sortByAvgSpeed(ArrayList<Result> results) {
        Result key;
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

    public static ArrayList<Result> sortByTime(ArrayList<Result> results) {
        Result key;
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

    public static ArrayList<Result> sortByDate(ArrayList<Result> results) {
        Result key;
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

    public static Result getHighestByDistance(ArrayList<Result> results) {
        Result highest = null;
        for (Result r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getDistance() < r.getDistance())
                highest = r;
        }
        return highest;
    }

    public static Result getHighestBySpeed(ArrayList<Result> results) {
        Result highest = null;
        for (Result r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getAvgSpeed() < r.getAvgSpeed())
                highest = r;
        }
        return highest;
    }

    public static Result getHighestByTime(ArrayList<Result> results) {
        Result highest = null;
        for (Result r : results) {
            if (highest == null)
                highest = r;
            else if (highest.getTime() < r.getTime())
                highest = r;
        }
        return highest;
    }
}
