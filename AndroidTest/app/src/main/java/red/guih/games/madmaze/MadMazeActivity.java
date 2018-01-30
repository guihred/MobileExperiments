package red.guih.games.madmaze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import red.guih.games.R;

public class MadMazeActivity extends AppCompatActivity {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private MadMazeView labyrinthView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madmaze);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.labyrinth);
            a.setDisplayHomeAsUpEnabled(true);
        }

        labyrinthView = findViewById(R.id.madTopology);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(labyrinthView, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(labyrinthView);
    }


    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(labyrinthView, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}