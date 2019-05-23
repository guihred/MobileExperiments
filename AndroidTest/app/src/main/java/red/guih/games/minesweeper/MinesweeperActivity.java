package red.guih.games.minesweeper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class MinesweeperActivity extends BaseActivity {


    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.minesweeper);
            a.setDisplayHomeAsUpEnabled(true);
        }
        setUserPreferences();
    }

    private void setUserPreferences() {
        int numberPref = getUserPreference(R.string.size, MinesweeperView.numberOfBombs);
        int numberOfBombs = numberPref / MinesweeperView.BOMBS_STEP - 1;
        MinesweeperView.setNumberOfBombs(numberOfBombs);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.config:
                showConfig();
                return true;
            case R.id.records:
                showRecords(MinesweeperView.numberOfBombs, UserRecord.MINESWEEPER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showConfig() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.minesweeper_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);

        spinner.setSelection(MinesweeperView.numberOfBombs / MinesweeperView.BOMBS_STEP - 1);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            MinesweeperView.setNumberOfBombs(spinner.getSelectedItemPosition());
            addUserPreference(R.string.size, MinesweeperView.numberOfBombs);
            recreate();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
