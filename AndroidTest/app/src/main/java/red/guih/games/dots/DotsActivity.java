package red.guih.games.dots;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.List;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;


public class DotsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dots);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.link_dots);
            a.setDisplayHomeAsUpEnabled(true);
        }
        setUserPreferences();
    }

    private void setUserPreferences() {
        DotsDrawingView
                .setDifficulty(getUserPreference(R.string.difficulty, DotsDrawingView.difficulty));
        DotsDrawingView.setMazeWidth(getUserPreference(R.string.size, DotsDrawingView.mazeWidth));
    }

    @Override
    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAllDesc(difficulty, gameName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.config:
                showConfig();
                return true;
            case R.id.records:
                showRecords(DotsDrawingView.mazeWidth, UserRecord.DOTS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        return expandMenu(menu,R.string.config);
    }


    private void showConfig() {
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.dots_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);

        spinner.setSelection(DotsDrawingView.difficulty);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog, spinner));

        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog, Spinner spinner) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        int progress = seekBar.getValue();
        int difficulty = spinner.getSelectedItemPosition() % 3;
        DotsDrawingView.setDifficulty(difficulty);
        addUserPreference(R.string.difficulty, difficulty);
        if (DotsDrawingView.mazeWidth != progress) {
            DotsDrawingView.setMazeWidth(progress);
            addUserPreference(R.string.size, progress);
            recreate();
        }

        dialog.dismiss();
    }
}
