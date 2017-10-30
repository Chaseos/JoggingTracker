package com.joggingtracker;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joggingtracker.data.JogContract;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.joggingtracker.JogActivity.JOG_DATE;

public class SingleJogActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<LatLng> mapPoints;
    private GoogleMap mMap;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_finished);
        long jogDate = getIntent().getLongExtra(JOG_DATE, 0);

        cursor = getContentResolver().query(JogContract.JogEntry.buildJogUriWithDate(jogDate),
                null, null, null, null);
        cursor.moveToFirst();

        EditText titleEdit = (EditText) findViewById(R.id.title_edit_text);
        TextView paceTV = (TextView) findViewById(R.id.pace_textview);
        TextView durationTV = (TextView) findViewById(R.id.duration_textview);
        TextView milesTV = (TextView) findViewById(R.id.miles_textview);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.finish_map_fragment);
        mapFragment.getMapAsync(this);

        long jogDateMillis = cursor.getLong(
                (cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_DATE_TIME)));
        Date date = new Date(jogDateMillis);
        String jogDateString = DateFormat.getDateInstance().format(date);

        titleEdit.setText(jogDateString);
        paceTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_PACE)));
        durationTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_TIME_LENGTH)));
        milesTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_MILES_LENGTH)));

        String pathJsonString = cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_PATH_JSON));
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        mapPoints = new Gson().fromJson(pathJsonString, type);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        Polyline route = mMap.addPolyline(new PolylineOptions().clickable(true));
        route.setPoints(mapPoints);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : mapPoints) {
            builder.include(point);
        }
        final LatLngBounds bounds = builder.build();
        final int padding = 50;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SingleJogActivity.this, PreviousJogsActivity.class);
        startActivity(intent);
    }
}
