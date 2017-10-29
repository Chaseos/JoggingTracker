package com.joggingtracker;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joggingtracker.data.JogContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.joggingtracker.JogActivity.JOG_DATE;

public class SingleJogActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "blahblah";
    private boolean mLocationPermissionGranted;
    private final int DEFAULT_ZOOM = 17;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_finished);
        long jogDate = getIntent().getLongExtra(JOG_DATE, 0);

        cursor = getContentResolver().query(JogContract.JogEntry.buildJogUriWithDate(jogDate),
                null, null, null, null);

        EditText titleEdit = (EditText) findViewById(R.id.title_edit_text);
        TextView paceTV = (TextView) findViewById(R.id.pace_textview);
        TextView durationTV = (TextView) findViewById(R.id.duration_textview);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.finish_map_fragment);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        long jogDateMillis = Long.valueOf(cursor.getString
                (cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_DATE_TIME)));
        Date date = new Date(jogDateMillis);
        String jogDateString = DateFormat.getDateInstance().format(date);

        titleEdit.setText(jogDateString);
        paceTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_PACE)));
        durationTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_TIME_LENGTH)));
        //TODO: Get ID from Intent then query with that ID to get the info and fill views
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
