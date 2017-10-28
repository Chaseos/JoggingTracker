package odometer.hfad.com.joggingtracker;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class JogActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 2000;
    private boolean mLocationPermissionGranted;
    public static final int DEFAULT_ZOOM = 17;
    private Location mLastKnownLocation;
    private ArrayList<LatLng> mapPoints;
    private ImageView accuracyIndicator;
    private Location previousLocation;
    private double kmDistance = 0;
    private long startTime = 0;
    private TextView timerTV;
    private TextView milesTV;
    private TextView speedTV;
    private GoogleMap mMap;
    private int minutes;
    private int seconds;
    private long millis;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerTV.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_run);
        milesTV = (TextView) findViewById(R.id.miles_textview);
        milesTV.setText("0.00");
        timerTV = (TextView) findViewById(R.id.timer_textview);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        speedTV = (TextView) findViewById(R.id.speed_textview);
        speedTV.setText("00:00");

        accuracyIndicator = (ImageView) findViewById(R.id.accuracy_indicator);
        accuracyIndicator.setImageResource(R.drawable.red_circle);

        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.background_map_layout);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.background_map_fragment);
        mapFragment.getMapAsync(this);

        final ImageView gpsImage = (ImageView) findViewById(R.id.gps_image);
        gpsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularReveal(constraintLayout);
            }
        });

        final ImageView closeImage = (ImageView) findViewById(R.id.black_x_circle);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularClose(constraintLayout);
            }
        });

        final ImageView pauseButton = (ImageView) findViewById(R.id.pause_button);
        final ImageView stopButton = (ImageView) findViewById(R.id.stop_button);
        final ImageView playButton = (ImageView) findViewById(R.id.play_button);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.vibrate(1000);
                pauseButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Convert and insert data into database then place id in intent and send to RunFinished
            }
        });

        mapPoints = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        if (previousLocation == null) {
            previousLocation = location;
        } else {
            kmDistance += previousLocation.distanceTo(location);
            double milesDistance = (kmDistance / 1000) * .62137;
            milesTV.setText(String.format("%.2f", milesDistance));
            previousLocation = location;

            if (milesDistance > 0.009) {
                double totaltime = (millis / 1000) / milesDistance;
                int speedMins = (int) (totaltime / 60);
                int speedSecs = (int) (totaltime % 60);

                if (speedMins > 99) {
                    speedTV.setText("99+'");
                } else {
                    speedTV.setText(String.format("%d:%02d", speedMins, speedSecs));
                }
            }
        }

        Log.d("blahblah", String.valueOf(location.getAccuracy()));

        if (location.getAccuracy() <= 6) {
            accuracyIndicator.setImageResource(R.drawable.green_circle);
        } else if (location.getAccuracy() > 6 && location.getAccuracy() <= 8) {
            accuracyIndicator.setImageResource(R.drawable.orange_circle);
        } else {
            accuracyIndicator.setImageResource(R.drawable.red_circle);
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
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

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void circularReveal(View viewTo) {
        // get the center for the clipping circle
        int cx = viewTo.getWidth();
        int cy = viewTo.getHeight();

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils
                .createCircularReveal(viewTo, 0,
                        0, 0, finalRadius);

        anim.setDuration(500);
        // make the view visible and start the animation
        viewTo.setVisibility(View.VISIBLE);

        anim.start();
    }

    private void circularClose(final View viewTo) {
        // get the center for the clipping circle
        int cx = viewTo.getWidth();
        int cy = viewTo.getHeight();

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(viewTo,
                        0, 0, initialRadius, 0);

        anim.setDuration(500);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewTo.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}