package com.find.wifitool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 30/12/16.
 */

public class InternalDataBase extends SQLiteOpenHelper {

    // Private variables
    private static final String DATABASE_NAME = "WifiDB";
    private static final String TABLE_NAME = "Wifi_db";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "WIFINAME";
    private static final String COLUMN_GROUP = "WIFIGROUP";
    private static final String COLUMN_USER = "WIFIUSER";
    private static final int DATABASE_VERSION = 1;

    public InternalDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_GROUP + " VARCHAR, "
                + COLUMN_USER + " VARCHAR);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS" + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new event
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getWifiName());
        values.put(COLUMN_GROUP, event.getWifiGroup());
        values.put(COLUMN_USER, event.getWifiUser());

        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting all records
    public List<Event> getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<Event> eventList = new ArrayList<Event>();

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(Integer.parseInt(cursor.getString(0)));
                event.setWifiName(cursor.getString(1));
                event.setWifiGroup(cursor.getString(2));
                event.setWifiUser(cursor.getString(3));
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    // Deleting all records
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
