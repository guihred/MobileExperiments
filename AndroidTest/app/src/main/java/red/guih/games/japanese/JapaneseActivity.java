package red.guih.games.japanese;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

public class JapaneseActivity extends BaseActivity {

    public static final float HUNDRED = 100.0f;
    private Map<Integer, Long> pointsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JapaneseView.CHAPTER = getUserPreference(R.string.chapter, JapaneseView.CHAPTER);
        setContentView(R.layout.activity_japanese);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(R.string.japanese);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::doAction);
        FloatingActionButton secondButton = findViewById(R.id.secondButton);
        secondButton.setOnClickListener(e -> showTips());
        FloatingActionButton romajiButton = findViewById(R.id.showRomajiButton);
        romajiButton.setOnClickListener(e -> {
            JapaneseView.SHOW_ROMAJI = !JapaneseView.SHOW_ROMAJI;
            JapaneseView viewById = findViewById(R.id.japaneseView);
            viewById.postInvalidate();
        });

    }

    private void doAction(View view) {
        showConfig();
    }

    private void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.japanese_config_dialog);
        dialog.setTitle(R.string.config);
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(JapaneseView.CHAPTER);
        retrievePointsByDifficulty(seekBar);
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        viewById1.setChecked(JapaneseView.SHOW_ROMAJI);
        CheckBox nightMode = dialog.findViewById(R.id.nightMode);
        nightMode.setChecked(JapaneseView.NIGHT_MODE);
        seekBar.setFormatter(value -> String.format("%d - %.1f%%", value, (float) pointsMap.getOrDefault(value, 0L) / HUNDRED));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog));
        dialog.show();
    }

    public void retrievePointsByDifficulty(NumberPicker seekBar) {
        new Thread(() -> {
            try {
                List<UserRecord> records = db.userDao().getMaxRecords(UserRecord.JAPANESE);
                Log.i("RECORDS", records + "");
                pointsMap = records.stream().collect(Collectors.toMap(UserRecord::getDifficulty, UserRecord::getPoints));
                seekBar.refreshDrawableState();
            } catch (Exception ex) {
                Log.e("DATABASE", "DATABASE ERROR", ex);

            }
        }).start();
    }

    private void showTips() {
        JapaneseView viewById = findViewById(R.id.japaneseView);
        List<String> japaneseLessons = viewById.loadTips();
        String[] names = japaneseLessons.toArray(new String[0]);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.simple_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.tips);
        ListView lv = convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
        alertDialog.show();
    }

    private void onClickConfigButton(Dialog dialog) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        JapaneseView viewById = findViewById(R.id.japaneseView);
        viewById.setChapter(seekBar.getValue());
        viewById.loadLessons();
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        JapaneseView.SHOW_ROMAJI = viewById1.isChecked();
        CheckBox nightMode = dialog.findViewById(R.id.nightMode);
        JapaneseView.NIGHT_MODE = nightMode.isChecked();

        addUserPreference(R.string.chapter, JapaneseView.CHAPTER);
        dialog.dismiss();
        recreate();
    }


}

