package com.example.valverde.valverderunkeeper.data;

import android.content.Context;
import com.example.valverde.valverderunkeeper.running.GPSEvent;
import com.example.valverde.valverderunkeeper.running.processing_result.Result;
import java.util.ArrayList;

public class DatabaseHelper {
    private DatabaseRunResultsTable resultsDB;
    private DatabaseGPSEventsTable routeDB;

    public DatabaseHelper(Context context) {
        resultsDB = new DatabaseRunResultsTable(context);
        routeDB = new DatabaseGPSEventsTable(context);
    }

    public void insertResult(Result result) {
        long lastResultId = resultsDB.getMaxResultId();
        long id = lastResultId + 1;
        result.setResultId(id);
        resultsDB.insertResult(result);
        if (result.getRoute() != null) {
            for (GPSEvent e : result.getRoute()) {
                e.setId(id);
            }
            routeDB.insertData(result.getRoute());
        }
    }

    public ArrayList<GPSEvent> getAllRoutes() {
        return routeDB.getAllEvents();
    }

    public ArrayList<GPSEvent> getRoute(long id) {
        return routeDB.getRoute(id);
    }

    public void removeResult(long id) {
        resultsDB.removeResult(id);
    }

    public void removeRoute(long id) {
        routeDB.removeRoute(id);
    }

    public ArrayList<Result> getAllResults() {
        return resultsDB.getAllResults();
    }
}
