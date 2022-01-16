package gr.hua.dit.android.geofenceapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import gr.hua.dit.android.geofenceapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private static int ACCESS_FINE_LOCATION_PERMISION_CODE = 1;
    private static int ACCESS_BACKGROUND_LOCATION_PERMISION_CODE = 2;
    private static int RADIUS = 200;
    private String GEOFENCE_ID = "UNIQUE ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize the geofencing client
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {

        //If permission is Granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //enable user location
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission
            }
        }

        if (requestCode == ACCESS_BACKGROUND_LOCATION_PERMISION_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "We can add geofence", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "background location access denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                addingGeofence(latLng);
            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},ACCESS_BACKGROUND_LOCATION_PERMISION_CODE );
                } else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},ACCESS_BACKGROUND_LOCATION_PERMISION_CODE );
                }
            }
        }else {
            addingGeofence(latLng);
        }
    }

    private void addingGeofence(LatLng latLng) {
        //We only want one marker so
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, RADIUS);
        addGeofence(latLng, RADIUS);
    }

    private void addMarker(LatLng latLng) {
        //Create the marker
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        //Add the marker to the map
        mMap.addMarker(markerOptions);
    }

    private void addGeofence(LatLng latLng, float radius) {
        //Using the helper class to add a geofence
        //Create a geofence that triggers when we enter or exit
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        //Create the geofencing request
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        //Create the pending intent
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        //Add the geofence
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "on success: Geofence Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getError(e);
                        Log.d(TAG, "on failure: " + errorMessage);
                    }
                });
    }

    private void addCircle(LatLng latLng, float radius) {
        //Create the circle
        CircleOptions circleOptions = new CircleOptions();
        //Pass the center of the circle
        circleOptions.center(latLng);
        //Pass the radius
        circleOptions.radius(radius);
        //Make the circle red
        circleOptions.strokeColor(Color.argb(225, 225, 0, 0) );
        circleOptions.fillColor(Color.argb(64, 225, 0, 0) );
        //Add the circle to the map
        mMap.addCircle(circleOptions);
    }
}