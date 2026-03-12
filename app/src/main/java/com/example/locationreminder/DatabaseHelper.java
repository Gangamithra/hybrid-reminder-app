package com.example.locationreminder;

import android.content.ContentValues;
import android.content.Context;import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReminderDB";

    // 1. CHANGE THIS TO 2 to trigger an update
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 2. Updated Table Structure with date and time
        db.execSQL("CREATE TABLE reminders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "lat REAL, " +
                "lon REAL, " +
                "radius REAL, " +
                "date TEXT, " +
                "time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This deletes the old table and creates the new one when version changes
        db.execSQL("DROP TABLE IF EXISTS reminders");
        onCreate(db);
    }

    // 3. Updated Insert Method
    public void insertData(String title, double lat, double lon, float radius, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("radius", radius);
        values.put("date", date);
        values.put("time", time);
        db.insert("reminders", null, values);
    }

    public List<ReminderModel> getAllReminders() {
        List<ReminderModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reminders", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new ReminderModel(
                        cursor.getString(1), // title
                        cursor.getDouble(2), // lat
                        cursor.getDouble(3), // lon
                        cursor.getFloat(4),  // radius
                        cursor.getString(5), // date (Added this)
                        cursor.getString(6)  // time (Added this)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    // Inside DatabaseHelper.java
    public void deleteReminder(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminders", "title = ?", new String[]{title});
        db.close();
    }
}