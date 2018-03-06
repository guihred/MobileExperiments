package red.guih.games;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import red.guih.games.db.UserRecord;
import red.guih.games.db.UserRecordDatabase;

/**
 * Main activity which will invoke other games.
 * <p>
 * Created by guilherme.hmedeiros on 18/01/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected final UserRecordDatabase db = Room.databaseBuilder(this,
            UserRecordDatabase.class, UserRecord.DATABASE_NAME).build();

    public void retrieveRecords(ListView recordListView, ArrayAdapter<UserRecord> adapter, int difficulty, String gameName) {
        List<UserRecord> records = getAll(difficulty, gameName);
        for (int i = 0; i < records.size(); i++) {
            records.get(i).setPosition(i + 1);
        }
        adapter.addAll(records);
        recordListView.refreshDrawableState();
    }

    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAll(difficulty, gameName);
    }

    public void showRecords(int difficulty, String gameName) {
        final Dialog dialog = new Dialog(this);
        List<UserRecord> all = new ArrayList<>();
        dialog.setContentView(R.layout.records_dialog);
        ListView recordListView = dialog.findViewById(R.id.recordList);
        ArrayAdapter<UserRecord> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, all);

        new Thread(() -> retrieveRecords(recordListView, adapter, difficulty, gameName)).start();
        recordListView.setAdapter(adapter);
        dialog.setTitle(R.string.records);
        // set the custom minesweeper_dialog components - text, image and button
        Button dialogButton = dialog.findViewById(R.id.buttonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    protected int getUserPreference(int name, int defaultValue) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(name), defaultValue);
    }
    protected String getUserPreference(int name, String defaultValue) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(getString(name), defaultValue);
    }

    protected void addUserPreference(int name, int value) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(name), value);
        editor.apply();
    }

    protected void addUserPreference(int name, String value) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(name), value);
        editor.apply();
    }
}
