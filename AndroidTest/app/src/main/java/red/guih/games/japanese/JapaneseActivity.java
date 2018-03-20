package red.guih.games.japanese;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.DatabaseHelper;

public class JapaneseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JapaneseView.CHAPTER = getUserPreference(R.string.chapter, JapaneseView.CHAPTER);
        setContentView(R.layout.activity_japanese);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::doAction);
        new Thread(this::executeDatabase).start();
        if (toolbar != null) {
            toolbar.setTitle(R.string.japanese);
        }
    }

    private void executeDatabase() {
        DatabaseHelper myDbHelper;
        SQLiteDatabase myDb = null;

        myDbHelper = new DatabaseHelper(this);
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */

        myDbHelper.initializeDataBase();
        try {
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
        /*
         * Place code to use database here.
         */
        } catch (Exception ex) {
            Log.e("JP", "DATABASE ERROR", ex);

        } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                Log.e("JP", "DATABASE ERROR", ex);
            } finally {
                if (myDb != null) {
                    myDb.close();
                }
            }
        }
    }

    private void doAction(View view) {
        showConfig();

    }

    private void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.japanese_config_dialog);
        dialog.setTitle(R.string.config);
        // set the custom minesweeper_dialog components - text, image and button

        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(JapaneseView.CHAPTER);
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        viewById1.setChecked(JapaneseView.SHOW_ROMAJI);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog));
        dialog.show();
    }

    private void onClickConfigButton(Dialog dialog) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        JapaneseView viewById = findViewById(R.id.japaneseView);
        viewById.setChapter(seekBar.getValue());
        viewById.loadLessons();
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        JapaneseView.SHOW_ROMAJI = viewById1.isChecked();

        addUserPreference(R.string.chapter, JapaneseView.CHAPTER);
        dialog.dismiss();
        recreate();
    }


}

