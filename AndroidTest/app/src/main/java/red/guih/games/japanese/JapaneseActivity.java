package red.guih.games.japanese;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import red.guih.games.BaseActivity;
import red.guih.games.R;
import red.guih.games.db.JapaneseLesson;
import red.guih.games.db.UserRecord;

public class JapaneseActivity extends BaseActivity {

    private static final float HUNDRED = 100.0F;
    private SparseLongArray pointsMap = new SparseLongArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChapter(getUserPreference(R.string.chapter, JapaneseView.chapter));
        JapaneseView.setNightMode(getUserPreference(R.string.night_mode, 0) == 1);
        setContentView(R.layout.activity_japanese);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(R.string.japanese);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showConfig());
        FloatingActionButton secondButton = findViewById(R.id.secondButton);
        secondButton.setOnClickListener(e -> showTips());
        FloatingActionButton romajiButton = findViewById(R.id.showRomajiButton);
        romajiButton.setOnClickListener(e -> {
            JapaneseView.setShowRomaji(!JapaneseView.showRomaji);
            JapaneseView viewById = findViewById(R.id.japaneseView);
            viewById.postInvalidate();
        });
    }

    private static void setChapter(int chapter) {
        JapaneseView.chapter = chapter;
    }

    @SuppressLint("DefaultLocale")
    private void showConfig() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.japanese_config_dialog);
        dialog.setTitle(R.string.config);
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        seekBar.setValue(JapaneseView.chapter);
        retrievePointsByDifficulty(seekBar);
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        viewById1.setChecked(JapaneseView.showRomaji);
        CheckBox nightMode = dialog.findViewById(R.id.nightMode);
        nightMode.setChecked(JapaneseView.nightMode);
        seekBar.setFormatter(value -> String
                .format("%d - %.1f%%", value, (float) pointsMap.get(value, 0L) / HUNDRED));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> onClickConfigButton(dialog));
        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showTips() {
        JapaneseView viewById = findViewById(R.id.japaneseView);
        JapaneseLesson lessons = viewById.getLesson();
        String tip = lessons.getTip();
        if (tip != null) {
            Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
            return;
        }
        List<String> japaneseLessons = viewById.loadTips();
        String[] names = japaneseLessons.toArray(new String[0]);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.simple_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.tips);
        ListView lv = convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                names);
        lv.setAdapter(adapter);
        alertDialog.show();
    }


    private void retrievePointsByDifficulty(NumberPicker seekBar) {
        new Thread(() -> {
            try {
                List<UserRecord> records = db.userDao().getMaxRecords(UserRecord.JAPANESE);
                Log.i("RECORDS", records + "");
                Map<Integer, Long> collect = records.stream().collect(
                        Collectors.toMap(UserRecord::getDifficulty, UserRecord::getPoints));
                collect.forEach((a, b) -> pointsMap.put(a, b));
                seekBar.refreshDrawableState();
            } catch (Exception ex) {
                Log.e("DATABASE", "DATABASE ERROR", ex);
            }
        }).start();
    }

    private void onClickConfigButton(Dialog dialog) {
        NumberPicker seekBar = dialog.findViewById(R.id.number);
        JapaneseView viewById = findViewById(R.id.japaneseView);
        viewById.setChapter(seekBar.getValue());
        viewById.loadLessons();
        CheckBox viewById1 = dialog.findViewById(R.id.showRomaji);
        JapaneseView.setShowRomaji(viewById1.isChecked());
        CheckBox nightMode = dialog.findViewById(R.id.nightMode);
        JapaneseView.setNightMode(nightMode.isChecked());

        addUserPreference(R.string.night_mode, JapaneseView.nightMode ? 1 : 0);
        addUserPreference(R.string.chapter, JapaneseView.chapter);
        dialog.dismiss();
        recreate();
    }


}

