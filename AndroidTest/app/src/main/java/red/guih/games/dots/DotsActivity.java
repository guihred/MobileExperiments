package red.guih.games.dots;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import red.guih.games.R;


public class DotsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dots);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.link_dots);
            a.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater
                inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.config:
                showConfig();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dots_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);

        spinner.setSelection(DotsDrawingView.DIFFICULTY);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog, spinner));

        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog, Spinner spinner) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        int progress = seekBar.getValue();
        DotsDrawingView.DIFFICULTY = spinner.getSelectedItemPosition() % 3;
        if (DotsDrawingView.MAZE_WIDTH != progress) {
            DotsDrawingView.MAZE_WIDTH = progress;
            recreate();
        }

        dialog.dismiss();
    }
}
