package red.guih.games.minesweeper.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Class user to create and retrive the userDAO
 *
 * Created by guilherme.hmedeiros on 25/01/2018.
 */
@Database(entities = {UserRecord.class}, version = 1)
public abstract class UserRecordDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
