package gr.hua.dit.android.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //Check for errors
        if (geofencingEvent.hasError()) {
            Log.d(TAG,"on receive: Error receiving geofencing event");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        //This is the location
        Location location = geofencingEvent.getTriggeringLocation();

        //This is the action
        int transitionType = geofencingEvent.getGeofenceTransition();

        //This is the current timestamp
        long time = System.currentTimeMillis();

    }
}