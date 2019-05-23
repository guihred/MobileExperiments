package red.guih.games;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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


    protected final UserRecordDatabase db = BaseActivity.getInstance(this);
    private Dialog dialog;

    public static UserRecordDatabase getInstance(Context context) {
        return Room.databaseBuilder(context,
                UserRecordDatabase.class, UserRecord.DATABASE_NAME)
                   .fallbackToDestructiveMigration()
                   .allowMainThreadQueries().build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void showRecords(int difficulty, String gameName) {
        dialog = new Dialog(this);
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

    protected boolean expandMenu(Menu menu, int reset) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.config);
        item.setTitle(reset);
        return true;
    }

    public void retrieveRecords(View recordListView, ArrayAdapter<UserRecord> adapter,
            int difficulty, String gameName) {
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

    protected int getUserPreference(int name, int defaultValue) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(name), defaultValue);
    }

    protected float getUserPreferenceFloat(int name, int defaultValue) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getFloat(getString(name), defaultValue);
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
