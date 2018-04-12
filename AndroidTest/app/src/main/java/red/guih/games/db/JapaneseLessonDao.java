package red.guih.games.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Object to define common queries for local database access.l
 * * Created by guilherme.hmedeiros on 25/01/2018.
 */
@Dao
public interface JapaneseLessonDao {

    @Query("SELECT l.* FROM JAPANESE_LESSON l WHERE lesson=:lesson ORDER BY lesson, exercise LIMIT 200")
    List<JapaneseLesson> getAll(Integer lesson);

}
