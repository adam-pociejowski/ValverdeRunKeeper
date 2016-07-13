package com.example.valverde.valverderunkeeper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.valverde.valverderunkeeper.running.GPSEvent;
import java.util.ArrayList;

public class DatabaseGPSEventsHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "valverdeRunKeeper.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "runningEvents";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "RUN_ID";
    private static final String COL_3 = "TIME";
    private static final String COL_4 = "LAT";
    private static final String COL_5 = "LNG";
    private static final String COL_6 = "ACCURACY";
    public static final String SQL_CREATE_QUERY = "CREATE TABLE "+TABLE_NAME+
            " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_2+" INTEGER,"+
            COL_3+" INTEGER,"+
            COL_4+" REAL,"+
            COL_5+" REAL,"+
            COL_6 +" REAL )";
    public static final String SQL_DROP_QUERY = "DROP TABLE IF EXISTS "+TABLE_NAME;


    public DatabaseGPSEventsHelper(Context context) {
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

    public void insertData(ArrayList<GPSEvent> events) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (GPSEvent event : events) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, event.getId());
            contentValues.put(COL_3, event.getTime());
            contentValues.put(COL_4, event.getLat());
            contentValues.put(COL_5, event.getLng());
            contentValues.put(COL_6, event.getAccuracy());
            db.insert(TABLE_NAME, null, contentValues);
        }
    }


    public ArrayList<GPSEvent> getAllEvents() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COL_1, COL_2, COL_3, COL_4, COL_5, COL_6 };
        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<GPSEvent> events = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(1);
                long time = c.getLong(2);
                double lat = c.getDouble(3);
                double lng = c.getDouble(4);
                float accuracy = c.getFloat(5);
                GPSEvent event = new GPSEvent(time, lat, lng, accuracy);
                event.setId(id);
                events.add(event);
            } while (c.moveToNext());
        }
        return events;
    }
}