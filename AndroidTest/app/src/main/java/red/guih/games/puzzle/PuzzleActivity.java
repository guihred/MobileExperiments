package red.guih.games.puzzle;


import android.app.Dialog;
import android.os.Bundle;
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

public class PuzzleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.puzzle);
            a.setDisplayHomeAsUpEnabled(true);
        }
        setUserPreferences();
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
            case R.id.config:
                showConfig();
                return true;
            case R.id.records:
                showRecords(PuzzleView.PUZZLE_WIDTH, UserRecord.PUZZLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUserPreferences() {
        PuzzleView.setPuzzleDimensions(getUserPreference(R.string.size, PuzzleView.PUZZLE_WIDTH));
        PuzzleView.setImage(getUserPreference(R.string.image, R.drawable.mona_lisa));
    }




    private void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.puzzle_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(PuzzleView.PUZZLE_WIDTH);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog, spinner));
        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog, Spinner spinner) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        int progress = seekBar.getValue();
        int selectedItemPosition = spinner.getSelectedItemPosition();
        PuzzleView.setImage(selectedItemPosition == 0 ? R.drawable.mona_lisa : R.drawable.the_horse_in_motion);
        PuzzleView.setPuzzleDimensions(progress);
        addUserPreference(R.string.size, progress);
        addUserPreference(R.string.image, PuzzleView.PUZZLE_IMAGE);

        recreate();
        dialog.dismiss();
    }


}
