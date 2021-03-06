package red.guih.games;

import android.app.Dialog;
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
    protected UserRecordDatabase db = BaseActivity
            .getInstance(this.getContext().getApplicationContext());
    boolean suitable;
    private Dialog dialog;

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void addUserPreference(int name, int value) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getResources().getString(name), value);
        editor.apply();
    }

    protected void addUserPreference(int name, String value) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getResources().getString(name), value);
        editor.apply();
    }

    protected void addUserPreference(String classname, int name, int value) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(classname, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getResources().getString(name), value);
        editor.apply();
    }

    protected int getUserPreference(int name, int defaultValue) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        return sharedPref.getInt(getResources().getString(name), defaultValue);
    }

    protected float getUserPreferenceFloat(int name, float defaultValue) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);

        Object o = sharedPref.getAll().get(getResources().getString(name));
        if (o instanceof Integer) {
            return ((Integer) o).floatValue();
        }

        return sharedPref.getFloat(getResources().getString(name), defaultValue);
    }

    protected String getUserPreference(int name, String defaultValue) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        return sharedPref.getString(getResources().getString(name), defaultValue);
    }

    protected void addUserPreference(int name, float value) {
        SharedPreferences sharedPref = getContext()
                .getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getResources().getString(name), value);
        editor.apply();
    }

    public void createRecordIfSuitable(long points, String description, String gameName,
            int difficulty, boolean asc) {
        new Thread(() -> createRecord(points, description, gameName, difficulty, asc)).start();
    }

    private void createRecord(long points, String description, String gameName, int difficulty,
            boolean asc) {
        int equals = db.userDao().getEqualRecords(points, difficulty, gameName);
        if (equals > 0) {
            return;
        }
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

    private void createUserRecord(long points, String description, String gameName,
            int difficulty) {
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

    public boolean isRecordSuitable(long points, String gameName, int difficulty, boolean asc) {
        if (points == 0) {
            return false;
        }

        Thread thread = new Thread(
                () -> suitable = BaseView.this.isSuitable(points, gameName, difficulty, asc));
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
        final Dialog dialog1 = getDialog();
        List<UserRecord> all = new ArrayList<>();
        dialog1.setContentView(R.layout.records_dialog);
        ListView recordListView = dialog1.findViewById(R.id.recordList);

        TextView title = dialog1.findViewById(R.id.recordTitle);
        title.setText(R.string.you_beaten_the_record);
        ArrayAdapter<UserRecord> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, all);

        new Thread(() -> retrieveRecords(recordListView, adapter, difficulty, gameName)).start();
        recordListView.setAdapter(adapter);
        dialog1.setTitle(R.string.records);
        // set the custom minesweeper_dialog components - text, image and button
        Button dialogButton = dialog1.findViewById(R.id.buttonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            onclick.run();
            dialog1.dismiss();
        });
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
    }

    protected Dialog getDialog() {
        if (dialog == null) {
            Context context = this.getContext();
            dialog = (context instanceof BaseActivity) ? ((BaseActivity) context).getDialog() :
                    new Dialog(context);
        }
        return dialog;
    }

    public void retrieveRecords(ListView recordListView, ArrayAdapter<UserRecord> adapter,
            int difficulty, String gameName) {
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

    protected void showDialogWinning(Runnable run) {
        String string = getResources().getString(R.string.game_over);
        showDialogWinning(string, run);
    }

    protected void showDialogWinning(String str, Runnable run) {
        final Dialog dialog1 = getDialog();
        dialog1.setContentView(R.layout.minesweeper_dialog);
        dialog1.setTitle(R.string.you_win);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog1.findViewById(R.id.textDialog);
        text.setText(str);
        Button dialogButton = dialog1.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            run.run();
            invalidate();
            dialog1.dismiss();
        });
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        invalidate();
    }

}
