package red.guih.games.minesweeper.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by guilherme.hmedeiros on 25/01/2018.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName ORDER BY points")
    List<UserRecord> getAll(Integer difficulty,String gameName);

    @Insert
    void insertAll(UserRecord... userRecords);

    @Update
    void updateUsers(UserRecord... userRecords);

    @Delete
    void delete(UserRecord userRecord);
}
