package odometer.hfad.com.joggingtracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class RunActivity extends AppCompatActivity{
    private TextView timerTV;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTV.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_run);
        TextView milesTV = (TextView) findViewById(R.id.miles_textview);
        timerTV = (TextView) findViewById(R.id.timer_textview);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        TextView speedTV = (TextView) findViewById(R.id.speed_textview);
        speedTV.setText(String.valueOf(0.0));
        milesTV.setText(String.valueOf(findMilesRun()));
    }

    public double findMilesRun() {
        return 0.0;
    }
}
