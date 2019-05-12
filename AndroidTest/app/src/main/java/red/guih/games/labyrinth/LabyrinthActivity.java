package red.guih.games.labyrinth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import red.guih.games.R;

/**
 * Activity that displays the labyrinth.
 *
 * Created by guilherme.hmedeiros on 27/01/2018.
 */

public class LabyrinthActivity extends AppCompatActivity {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private LabyrinthView labyrinthView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labyrinth);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.labyrinth);
            a.setDisplayHomeAsUpEnabled(true);
        }

        labyrinthView = findViewById(R.id.labyrinth_view);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(labyrinthView, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(labyrinthView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(labyrinthView, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
