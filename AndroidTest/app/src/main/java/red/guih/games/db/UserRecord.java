package red.guih.games.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class that will store user record data;
 * <p>
 * Created by guilherme.hmedeiros on 25/01/2018.
 */

@Entity()
public class UserRecord {
    public static final String DATABASE_NAME = "USER_RECORDS";
    public static final String MINESWEEPER = "Minesweeper";
    public static final String DOTS = "Dots";
    public static final String TETRIS = "Tetris";
    public static final String PUZZLE = "Puzzle";
    public static final String SLIDING_PUZZLE = "SlidingPuzzle";
    public static final String SQUARE_2048 = "Square2048";
    public static final String MAD_MAZE = "MadMaze";
//    public static final String SOLITAIRE = "Solitaire";

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "first_name")
    private String description;

    @ColumnInfo(name = "gameName")
    private String gameName;

    @ColumnInfo(name = "points")
    private Long points;
    @ColumnInfo(name = "difficulty")
    private Integer difficulty;
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
        return getPosition() + " - " + description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}