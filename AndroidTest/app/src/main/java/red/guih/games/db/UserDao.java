package red.guih.games.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data Access Object to define common queries for local database access.l
 * * Created by guilherme.hmedeiros on 25/01/2018.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName ORDER BY points LIMIT 5")
    List<UserRecord> getAll(Integer difficulty, String gameName);

    @Query("SELECT * FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName ORDER BY points desc LIMIT 5")
    List<UserRecord> getAllDesc(Integer difficulty, String gameName);

    @Query("SELECT COUNT(points) FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName ")
    int getCountRecords(Integer difficulty, String gameName);

    @Query("SELECT COUNT(points) FROM UserRecord WHERE points=:points AND difficulty=:difficulty AND gameName=:gameName ")
    int getEqualRecords(Long points, Integer difficulty, String gameName);

    @Query("SELECT * FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName GROUP BY difficulty,gameName HAVING points=MIN(points)")
    UserRecord getMinPoints(Integer difficulty, String gameName);

    @Query("SELECT * FROM UserRecord WHERE difficulty=:difficulty AND gameName=:gameName GROUP BY difficulty,gameName HAVING points=MAX(points)")
    UserRecord getMaxPoints(Integer difficulty, String gameName);

    @Insert
    void insertAll(UserRecord... userRecords);

    @Update
    void updateUsers(UserRecord... userRecords);

    @Delete
    void delete(UserRecord userRecord);
}
