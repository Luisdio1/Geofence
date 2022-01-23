package gr.hua.dit.android.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("TAG","Inside broadcast");
        Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //Check for errors
        if (geofencingEvent.hasError()) {
            Log.d(TAG,"on receive: Error receiving geofencing event");
            return;
        }

        //This is the location
        Location location = geofencingEvent.getTriggeringLocation();

        //This is the action
        int transitionType = geofencingEvent.getGeofenceTransition();

        //This is the current timestamp
        long time = System.currentTimeMillis();

        //Add what we want to the database in a thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (transitionType) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        Point enterPoint = new Point(location.getLatitude(), location.getLongitude(),"Enter", time, context);
                        try {
                            DBHelper helper = new DBHelper(context);
                            SQLiteDatabase database = helper.getReadableDatabase();
                            Cursor cursor = null;
                            cursor = database.query(DbContract.TABLE_NAME, null, null, null, null, null, null);
                            String[] columnNames = cursor.getColumnNames();
                            Log.d("TAG","Column name" + columnNames[1]);
                            enterPoint.persist();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        Point exitPoint = new Point(location.getLatitude(), location.getLongitude(),"Exit", time, context);
                        try {
                            exitPoint.persist();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        t.start();
    }
}