package odometer.hfad.com.joggingtracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class RunActivity extends AppCompatActivity implements LocationListener {
    private TextView timerTV;
    private TextView milesTV;
    private TextView speedTV;
    private long startTime = 0;
    private double kmDistance = 0;
    private Location previousLocation;
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
                speedTV.setText(String.format("%d:%02d", speedMins, speedSecs));
            }
        }
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
}
