package red.guih.games.puzzle;


import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import red.guih.games.R;
import red.guih.games.db.UserRecord;
import red.guih.games.db.UserRecordDatabase;

public class PuzzleActivity extends AppCompatActivity {

    UserRecordDatabase db = Room.databaseBuilder(this,
            UserRecordDatabase.class, UserRecord.DATABASE_NAME).build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setTitle(R.string.puzzle);
            a.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater
                inflater = getMenuInflater();

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
                showRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showRecords() {
        final Dialog dialog = new Dialog(this);
        List<UserRecord> all = new ArrayList<>();
        dialog.setContentView(R.layout.records_dialog);
        ListView recordListView = dialog.findViewById(R.id.recordList);

        ArrayAdapter<UserRecord> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, all);
        new Thread(() -> retrieveRecords(recordListView, adapter)).start();


        recordListView.setAdapter(adapter);
        dialog.setTitle(R.string.records);
        // set the custom minesweeper_dialog components - text, image and button

        Button dialogButton = dialog.findViewById(R.id.buttonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    private void retrieveRecords(ListView recordListView, ArrayAdapter<UserRecord> adapter) {
        List<UserRecord> records = db.userDao().getAll(PuzzleView.PUZZLE_WIDTH, UserRecord.PUZZLE);
        for (int i = 0; i < records.size(); i++) {
            records.get(i).setPosition(i + 1);
        }
        adapter.addAll(records);
        recordListView.refreshDrawableState();
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
//        PuzzleView.PUZZLE_WIDTH = spinner.getSelectedItemPosition() * 2;
        if (PuzzleView.PUZZLE_WIDTH != progress * 2) {
            PuzzleView.setPuzzleDimensions(progress);
            recreate();
        }

        dialog.dismiss();
    }


}
