package red.guih.games.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Class that will store user record data;
 * <p>
 * Created by guilherme.hmedeiros on 25/01/2018.
 */

@Entity(tableName = "JAPANESE_LESSON", primaryKeys = {"exercise", "lesson"})
public class JapaneseLesson {

    @ColumnInfo(name = "english")
    private String english;
    @ColumnInfo(name = "japanese")
    private String japanese;
    @ColumnInfo(name = "romaji")
    private String romaji;

    @ColumnInfo(name = "points")
    private Long points;

    @ColumnInfo(name = "exercise")
    @NonNull
    private Integer exercise;

    @NonNull
    @ColumnInfo(name = "lesson")
    private Integer lesson;

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getJapanese() {
        return japanese;
    }

    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

    public String getRomaji() {
        return romaji;
    }

    public void setRomaji(String romaji) {
        this.romaji = romaji;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Integer getExercise() {
        return exercise;
    }

    public void setExercise(Integer exercise) {
        this.exercise = exercise;
    }

    public Integer getLesson() {
        return lesson;
    }

    public void setLesson(Integer lesson) {
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "{" +
                "english='" + english + '\'' +
                ", japanese='" + japanese + '\'' +
                ", romaji='" + romaji + '\'' +
                '}';
    }
}