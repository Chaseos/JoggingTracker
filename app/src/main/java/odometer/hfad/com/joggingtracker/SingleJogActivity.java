package odometer.hfad.com.joggingtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SingleJogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_finished);

        //TODO: Get ID from Intent then query with that ID to get the info and fill views
    }
}
