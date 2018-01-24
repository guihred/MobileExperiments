package red.guih.games.minesweeper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;

import red.guih.games.R;

public class MinesweeperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);

        ActionBar a = getSupportActionBar();
        if(a!=null){
            a.setTitle(R.string.campo_minado);
            a.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.config:
                showConfig();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.minesweeper_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button
        Spinner spinner = dialog.findViewById(R.id.spinner1);

        spinner.setSelection(MinesweeperView.NUMBER_OF_BOMBS / 15 - 1);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            MinesweeperView.NUMBER_OF_BOMBS = (spinner.getSelectedItemPosition() + 1) * 15;
            recreate();

            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
