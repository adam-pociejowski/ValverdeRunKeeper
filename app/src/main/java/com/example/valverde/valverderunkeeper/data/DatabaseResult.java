package com.example.valverde.valverderunkeeper.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;

import java.util.ArrayList;

public class DatabaseResult extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "valverdeRunKeeper.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "results";
    private static final String ID_COL = "ID";
    private static final String TIME_COL = "TIME";
    private static final String DISTANCE_COL = "DISTANCE";
    private static final String CALORIES_COL = "CALORIES";
    private static final String DATE_COL = "DATE";
    public static final String SQL_CREATE_QUERY = "CREATE TABLE "+TABLE_NAME+
            " ("+ID_COL+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            TIME_COL+" INTEGER,"+
            DISTANCE_COL+" REAL,"+
            CALORIES_COL +" INTEGER, "+
            DATE_COL+" INTEGER)";
    public static final String SQL_DROP_QUERY = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseResult(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_QUERY);
        onCreate(sqLiteDatabase);
    }

    public boolean insertResult(RunResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME_COL, result.getTime());
        contentValues.put(DISTANCE_COL, result.getDistance());
        contentValues.put(CALORIES_COL, result.getCalories());
        contentValues.put(DATE_COL, result.getDate());
        long res = db.insert(TABLE_NAME, null, contentValues);
        if (res == -1) return false;
        else return true;
    }

    public ArrayList<RunResult> getAllResults() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { ID_COL, TIME_COL, DISTANCE_COL, CALORIES_COL, DATE_COL };
        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<RunResult> results = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                long time = c.getLong(1);
                double distance = c.getDouble(2);
                int calories = c.getInt(3);
                long date = c.getLong(4);
                RunResult result = new RunResult(time, distance, calories);
                result.setDate(date);
                results.add(result);
            } while (c.moveToNext());
        }
        return results;
    }
}
