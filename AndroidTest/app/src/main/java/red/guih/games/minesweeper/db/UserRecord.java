package red.guih.games.minesweeper.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class that will store user record data;
 * <p>
 * Created by guilherme.hmedeiros on 25/01/2018.
 */

@Entity
public class UserRecord {
    public static final String DATABASE_NAME = "USER_RECORDS";

    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "first_name")
    private String description;

    @ColumnInfo(name = "points")
    private Long points;
    @Ignore
    private Integer position;



    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return position+" - "+description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}


/*UserRecordDatabase db = Room.databaseBuilder(getApplicationContext(),
            UserRecordDatabase.class, "database-name").build();*/



