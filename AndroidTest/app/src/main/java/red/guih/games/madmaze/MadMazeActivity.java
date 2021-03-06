package red.guih.games.madmaze;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class MadMazeActivity extends BaseActivity {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.records) {
            showRecords(MadMazeView.difficulty, UserRecord.MAD_MAZE);
            return true;
        } else if (i == R.id.config) {
            showConfig();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfig() {

        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.madmaze_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.mazeMode);

        spinner.setSelection(labyrinthView.getMadMazeOption());
        NumberPicker mazeSize = dialog.findViewById(R.id.mazeSize);

        mazeSize.setValue(MadMazeView.difficulty / 5);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            MadMazeView.setMadMazeOption(spinner.getSelectedItemPosition());
            MadMazeView.setDifficulty(mazeSize.getValue());
            recreate();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}