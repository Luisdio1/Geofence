package gr.hua.dit.android.geofenceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

//Class representing the data to be stored in the database
public class Point {


    private double lat;
    private double lon;
    private String action;
    private long timestamp;
    private DBHelper helper;

    public Point(double lat, double lon, String action, long timestamp, Context context) {
        this.lat = lat;
        this.lon = lon;
        this.action = action;
        this.timestamp = timestamp;
        helper = new DBHelper(context);
    }

    public Point(double lat, double lon, String action, long timestamp) {
        this.lat = lat;
        this.lon = lon;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Point(Context context) {
        helper = new DBHelper(context);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    //insert a point in the database
    public long persist() throws Exception {
        ContentValues values = new ContentValues();
        values.put(DbContract.FIELD_1, this.lat);
        values.put(DbContract.FIELD_2, this.lon);
        values.put(DbContract.FIELD_3, this.action);
        values.put(DbContract.FIELD_4, this.timestamp);
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.insert(DbContract.TABLE_NAME,null,values);
        db.close();
        if (result == -1) {
            throw  new Exception("Insert Failed!");
        }
        return result;
    }

    //Get all the points
    public static ArrayList<Point> getAllPoints(Context context) {
        DBHelper helper = new DBHelper(context);
        ArrayList<Point> points = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor results = db.query(DbContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (results.moveToFirst()) {
            do{
                Point point = new Point(results.getDouble(0), results.getDouble(1), results.getString(2),results.getLong(3));
                points.add(point);
            } while(results.moveToNext());
        }

        db.close();
        return points;
    }

}
