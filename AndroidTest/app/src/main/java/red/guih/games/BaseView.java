package red.guih.games;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import red.guih.games.db.UserRecord;
import red.guih.games.db.UserRecordDatabase;

/**
 * Main activity which will invoke other games.
 * <p>
 * Created by guilherme.hmedeiros on 18/01/2018.
 */

public abstract class BaseView extends View {

    UserRecordDatabase db = Room.databaseBuilder(this.getContext(),UserRecordDatabase.class, UserRecord.DATABASE_NAME).build();

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
    public void createUserRecordThread(long points, String description, String gameName, int difficulty){
        new Thread(() -> createUserRecord(points, description, gameName, difficulty)).start();
    }

}
