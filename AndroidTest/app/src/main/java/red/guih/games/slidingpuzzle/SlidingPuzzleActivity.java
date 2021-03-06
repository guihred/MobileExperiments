/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.slidingpuzzle;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;


public class SlidingPuzzleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingpuzzle);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.slidingpuzzle);
            a.setDisplayHomeAsUpEnabled(true);
        }
        setUserPreferences();
    }

    private void setUserPreferences() {
        SlidingPuzzleView
                .setPuzzleDimensions(getUserPreference(R.string.size, SlidingPuzzleView.mapHeight));
        SlidingPuzzleView
                .setPuzzleImage(getUserPreference(R.string.image, SlidingPuzzleView.puzzleImage));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.records:
                showRecords(SlidingPuzzleView.mapWidth, UserRecord.SLIDING_PUZZLE);
                return true;
            case R.id.config:
                showConfig();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showConfig() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.puzzle_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);
        String[] testArray = getResources().getStringArray(R.array.slidingpuzzle_images);
        spinner.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, testArray));
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(SlidingPuzzleView.mapHeight);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog, spinner));
        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog, Spinner spinner) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        int progress = seekBar.getValue();
        int selectedItemPosition = spinner.getSelectedItemPosition();
        int image = getImage(selectedItemPosition);
        SlidingPuzzleView.setPuzzleImage(image);
        SlidingPuzzleView.setPuzzleDimensions(progress);
        addUserPreference(R.string.size, progress);
        addUserPreference(R.string.image, SlidingPuzzleView.puzzleImage);
        recreate();
        dialog.dismiss();
    }

    private static int getImage(int selectedItemPosition) {
        if (selectedItemPosition == 0) {
            return 0;
        } else if (selectedItemPosition == 1) {
            return R.drawable.mona_lisa;
        } else {
            return R.drawable.the_horse_in_motion;
        }
    }


}
