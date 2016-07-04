package com.example.valverde.valverderunkeeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.valverde.valverderunkeeper.run_keeper.GPSEvent;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "valverdeRunKeeper.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "runningEvents";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TIME";
    private static final String COL_3 = "LAT";
    private static final String COL_4 = "LNG";
    private static final String COL_5 = "ACCURACY";
    public static final String SQL_CREATE_QUERY = "CREATE TABLE "+TABLE_NAME+
            " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_2+" INTEGER,"+
            COL_3+" REAL,"+
            COL_4+" REAL,"+
            COL_5 +" REAL )";
    public static final String SQL_DROP_QUERY = "DROP TABLE IF EXISTS "+TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
//        SQLiteDatabase db = this.getWritableDatabase();
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

    public boolean insertData(GPSEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, event.getTime());
        contentValues.put(COL_3, event.getLat());
        contentValues.put(COL_4, event.getLng());
        contentValues.put(COL_5, event.getAccuracy());
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }


    public ArrayList<GPSEvent> getAllEvents() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COL_1, COL_2, COL_3, COL_4, COL_5 };
        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<GPSEvent> events = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                long time = c.getLong(1);
                double lat = c.getDouble(2);
                double lng = c.getDouble(3);
                float accuracy = c.getFloat(4);
                GPSEvent event = new GPSEvent(time, lat, lng, accuracy);
                events.add(event);
            } while (c.moveToNext());
        }
        return events;
    }
}