package red.guih.games;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import red.guih.games.db.UserRecord;
import red.guih.games.db.UserRecordDatabase;

/**
 * Main activity which will invoke other games.
 * <p>
 * Created by guilherme.hmedeiros on 18/01/2018.
 */

public abstract class BaseView extends View {

    public static final int MAX_RECORDS = 5;
    protected UserRecordDatabase db = Room.databaseBuilder(this.getContext(), UserRecordDatabase.class, UserRecord.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()

            .build();

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void createUserRecord(long points, String description, String gameName, int difficulty) {
        try {
            UserRecord userRecord = new UserRecord();
            userRecord.setDescription(description);
            userRecord.setPoints(points);
            userRecord.setGameName(gameName);
            userRecord.setDifficulty(difficulty);
            db.userDao().insertAll(userRecord);
        } catch (Exception e) {
            Log.e("BASEVIEW", "ERROR WHEN CREATING USER RECORD", e);
        }
    }

    protected void addUserPreference(int name, int value) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getResources().getString(name), value);
        editor.apply();
    }
    protected void addUserPreference(String classname,int name, int value) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(classname, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getResources().getString(name), value);
        editor.apply();
    }
    protected int getUserPreference(int name, int defaultValue) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getClass().getSimpleName(),Context.MODE_PRIVATE);
        return sharedPref.getInt(getResources().getString(name), defaultValue);
    }
    protected float getUserPreferenceFloat(int name, float defaultValue) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getClass().getSimpleName(),Context.MODE_PRIVATE);

        Object o = sharedPref.getAll().get(getResources().getString(name));
        if(o instanceof Integer){
            return ((Integer) o).floatValue();
        }

        return sharedPref.getFloat(getResources().getString(name), defaultValue);
    }
    protected String getUserPreference(int name, String defaultValue) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getClass().getSimpleName(),Context.MODE_PRIVATE);
        return sharedPref.getString(getResources().getString(name), defaultValue);
    }

    protected void addUserPreference(int name, float value) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getResources().getString(name), value);
        editor.apply();
    }

    public void createRecordIfSuitable(long points, String description, String gameName, int difficulty, boolean asc) {
        new Thread(() -> createRecord(points, description, gameName, difficulty, asc)).start();
    }

    private void createRecord(long points, String description, String gameName, int difficulty, boolean asc) {
        int equals = db.userDao().getEqualRecords(points, difficulty, gameName);
        if (equals > 0)
            return;
        int count = db.userDao().getCountRecords(difficulty, gameName);
        if (count < MAX_RECORDS) {
            createUserRecord(points, description, gameName, difficulty);
            return;
        }
        UserRecord maxPoints = db.userDao().getMaxPoints(difficulty, gameName);
        UserRecord minPoints = db.userDao().getMinPoints(difficulty, gameName);
        if (points < maxPoints.getPoints() && asc || points > minPoints.getPoints() && !asc) {
            createUserRecord(points, description, gameName, difficulty);
            UserRecord updated = asc ? maxPoints : minPoints;
            db.userDao().delete(updated);
        }
    }


    boolean suitable;

    public boolean isRecordSuitable(long points, String gameName, int difficulty, boolean asc) {
        if (points == 0) {
            return false;
        }

        Thread thread = new Thread(() -> suitable = BaseView.this.isSuitable(points, gameName, difficulty, asc));
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            Log.e("BASEVIEW", "CONCURRENCY ERROR", e);
            return false;
        }
        return suitable;
    }

    private boolean isSuitable(long points, String gameName, int difficulty, boolean asc) {
        int equals = db.userDao().getEqualRecords(points, difficulty, gameName);
        if (equals > 0) {
            return false;
        }
        int count = db.userDao().getCountRecords(difficulty, gameName);
        if (count < MAX_RECORDS) {
            return true;
        }
        UserRecord maxPoints = db.userDao().getMaxPoints(difficulty, gameName);
        UserRecord minPoints = db.userDao().getMinPoints(difficulty, gameName);
        return points < maxPoints.getPoints() && asc || points > minPoints.getPoints() && !asc;
    }

    public void showRecords(int difficulty, String gameName, Runnable onclick) {
        final Dialog dialog = new Dialog(this.getContext());
        List<UserRecord> all = new ArrayList<>();
        dialog.setContentView(R.layout.records_dialog);
        ListView recordListView = dialog.findViewById(R.id.recordList);

        TextView title = dialog.findViewById(R.id.recordTitle);
        title.setText(R.string.you_beaten_the_record);
        ArrayAdapter<UserRecord> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, all);

        new Thread(() -> retrieveRecords(recordListView, adapter, difficulty, gameName)).start();
        recordListView.setAdapter(adapter);
        dialog.setTitle(R.string.records);
        // set the custom minesweeper_dialog components - text, image and button
        Button dialogButton = dialog.findViewById(R.id.buttonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            onclick.run();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void retrieveRecords(ListView recordListView, ArrayAdapter<UserRecord> adapter, int difficulty, String gameName) {
        List<UserRecord> records = getAll(difficulty, gameName);
        int max = 0;
        long maxId = 0;
        for (int i = 0; i < records.size(); i++) {
            UserRecord userRecord = records.get(i);
            if (maxId < userRecord.getUid()) {
                maxId = userRecord.getUid();
                max = i;
            }
            userRecord.setPosition(i + 1);
        }

        adapter.addAll(records);
        recordListView.setSelection(max);
        recordListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        recordListView.refreshDrawableState();
    }

    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAll(difficulty, gameName);
    }

}
