package red.guih.games.japanese;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.DatabaseMigration;
import red.guih.games.db.JapaneseLesson;
import red.guih.games.db.UserRecord;

/**
 * View for displaying the japanese game
 * <p>
 * Created by guilherme.hmedeiros on 16/03/2018.
 */

public class JapaneseView extends BaseView {

    public static final int LIGHT_RED = 0x88FF0000;
    public static final int MAX_CHAPTERS = 148;
    public static final int LIGHT_GREEN = 0x8800FF00;
    static boolean showRomaji;
    static boolean nightMode;
    static int chapter = 1;
    private final List<Letter> answer = new ArrayList<>();
    private final List<Letter> letters = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Paint greenPaint = new Paint();
    private final Paint redPaint = new Paint();
    private final RectF okButton = new RectF();
    private final List<JapaneseLesson> lessons = new ArrayList<>();
    private final List<String> tips = new ArrayList<>();
    private int characterSize = 50;
    private float points;
    private float currentScore;
    private int currentLesson;
    private DynamicLayout englishLayout;
    private DynamicLayout romajiLayout;
    private DynamicLayout answerLayout;

    public JapaneseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setTextSize(characterSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);

        greenPaint.setColor(LIGHT_GREEN);
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setTextAlign(Paint.Align.CENTER);
        redPaint.setColor(LIGHT_RED);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setTextAlign(Paint.Align.CENTER);
        loadLessons();
    }

    public void loadLessons() {
        new Thread(this::migrateLessons).start();
    }

    private void migrateLessons() {
        Log.e("JAPANESE VIEW", "LOADING LESSONS");
        lessons.clear();
        List<JapaneseLesson> all = db.japaneseLessonDao().getAll(chapter);
        Log.e("JAPANESE VIEW", db.toString());
        lessons.addAll(all);
        if (!lessons.isEmpty()) {
            points = getUserPreferenceFloat(R.string.punctuation, 0);
            currentLesson = getUserPreference(R.string.lesson, 0);
            configureCurrentLesson();
        }
        Log.e("JAPANESE VIEW", "NO LESSONS");
        String nao = getUserPreference(R.string.executed, "NAO");
        if ("NAO".equals(nao)) {
            new Thread(() -> {
                Log.e("DATABASE", "INITIALIZING DATABASE");
                DatabaseMigration.createDatabase(this.getContext().getApplicationContext(), db);
                addUserPreference(R.string.executed, "SIM");
            }).start();
        }

        postInvalidate();
        loadTips();
    }

    private void configureCurrentLesson() {
        if (lessons.isEmpty()) {
            return;
        }

        JapaneseLesson japaneseLesson = getLesson();
        letters.clear();
        answer.clear();
        List<String> split = Objects.toString(japaneseLesson.getJapanese(), "").chars()
                                    .mapToObj(value -> (char) value).map(Object::toString)
                                    .collect(Collectors.toList());
        Collections.shuffle(split);
        int h = getLettersLayout();
        int w = getWidth();

        for (int i = 0; i < split.size(); i++) {
            Letter e = new Letter(new RectF(), split.get(i));
            adjust(h, w, i, e);
            letters.add(e);
        }


        TextPaint tp = new TextPaint();
        tp.setColor(nightMode ? Color.WHITE : Color.BLACK);
        tp.setTextSize(characterSize);
        tp.setTextAlign(Paint.Align.CENTER);
        tp.setAntiAlias(true);
        englishLayout = new DynamicLayout(
                getContext().getString(R.string.english, japaneseLesson.getEnglish()), tp,
                getWidth() * 7 / 8, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        englishLayout.getOffsetToRightOf(characterSize);
        String romaji = japaneseLesson.getRomaji();


        romajiLayout = new DynamicLayout(getContext().getString(R.string.romaji,
                Objects.toString(romaji, "").replaceAll("\\(.+\\)", "")), tp,
                getWidth() * 7 / 8, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        romajiLayout.getOffsetToRightOf(characterSize);
        answerLayout = new DynamicLayout(getContext().getString(R.string.answer,
                Objects.toString(japaneseLesson.getJapanese(), "").replaceAll("\\(.+\\)", "")), tp,
                getWidth() * 7 / 8, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        answerLayout.getOffsetToRightOf(characterSize);
        if (romaji != null && romaji.contains("(") && romaji.contains(")")) {
            post(() -> {
                String tip = romaji.replaceAll(".+\\((.+)\\)", "$1");
                Toast.makeText(this.getContext(), tip, Toast.LENGTH_LONG).show();
            });
        }

    }

    public JapaneseLesson getLesson() {
        return lessons.get(currentLesson % lessons.size());
    }

    public List<String> loadTips() {
        tips.clear();
        tips.addAll(lessons.stream().filter(e -> e != null && e.getRomaji() != null)
                           .filter(e -> e.getRomaji().contains("(") && e.getRomaji().contains(")"))
                           .map(e -> e.getRomaji().replaceAll(".+\\((.+)\\)", "$1"))
                           .flatMap(e -> Stream.of(e.split("\\).*\\(")))
                           .collect(Collectors.toList())
        );
        postInvalidate();

        return tips;
    }

    private int getLettersLayout() {
        return getHeight() * 4 / 6;
    }

    private void adjust(int h, int w2, int j, Letter remove) {
        int left = w2 / 12 * (j % 10) + w2 / 12;
        int top = 2 * characterSize * (j / 10) + h;
        remove.bound.left = left;
        remove.bound.top = top;
        remove.bound.right = left + characterSize * 2F;
        remove.bound.bottom = top + characterSize * 2F;
    }

    static void setShowRomaji(boolean value) {
        JapaneseView.showRomaji = value;
    }

    static void setNightMode(boolean nightMode) {
        JapaneseView.nightMode = nightMode;
    }

    private static void setStaticChapter(int seekBar) {
        JapaneseView.chapter = seekBar;
    }

    public void setChapter(int seekBar) {
        if (chapter != seekBar) {
            addUserPreference(R.string.punctuation, (float) 0);
            addUserPreference(R.string.lesson, 0);
        }
        setStaticChapter(seekBar);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (lessons.isEmpty()) {
                loadLessons();
                return true;
            }
            JapaneseLesson japaneseLesson = getLesson();
            if (letters.isEmpty() && okButton.contains(event.getX(), event.getY())) {
                currentScore = CompareAnswers.compare(japaneseLesson.getJapanese(),
                        answer.stream().map(e -> e.character)
                              .collect(Collectors.joining())) * 100;
                points += currentScore;
                currentLesson = (currentLesson + 1) % lessons.size();
                if (currentLesson == 0) {

                    showDialogWinning();
                    return true;
                }
                addUserPreference(R.string.punctuation, points);
                addUserPreference(R.string.lesson, currentLesson);
                configureCurrentLesson();
                invalidate();
            }

            if (exchangeLetter(event, getLettersLayout(), letters, getAnswerLayout(), answer)) {
                if (letters.isEmpty()) {
                    currentScore = CompareAnswers.compare(japaneseLesson.getJapanese(),
                            answer.stream().map(e -> e.character).collect(Collectors.joining())) *
                            100;
                }
                return true;
            }

            if (!letters.isEmpty() && exchangeLetter(event, getAnswerLayout(), this.answer,
                    getLettersLayout(), this.letters)) {
                return true;
            }
        }
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (nightMode) {
            paint.setColor(Color.WHITE);
            canvas.drawColor(Color.BLACK);
        } else {
            paint.setColor(Color.BLACK);
            canvas.drawColor(Color.WHITE);
        }


        String chapterFormat = getContext()
                .getString(R.string.chapter_format, chapter, currentLesson, lessons.size());
        canvas.drawText(chapterFormat, getWidth() / 3F, characterSize, paint);
        String punctuationFormat = getContext().getString(R.string.punctuation, getCurrentScore());
        canvas.drawText(punctuationFormat, getWidth() * 2 / 3F, characterSize, paint);

        drawTextLayout(canvas, getWidth() / 2, getHeight() / 12, this.englishLayout);
        if (showRomaji || letters.isEmpty()) {
            drawTextLayout(canvas, getWidth() / 2, getHeight() * 3 / 6, this.romajiLayout);
        }
        if (letters.isEmpty()) {
            drawTextLayout(canvas, getWidth() / 2, getHeight() * 4 / 6, this.answerLayout);
            canvas.drawRoundRect(okButton, 10, 10, greenPaint);
            String singleScore = getContext().getString(R.string.percent, currentScore);
            canvas.drawText(singleScore, okButton.centerX(), okButton.centerY(), paint);
            canvas.drawText("Ok", okButton.centerX(), okButton.centerY() + characterSize, paint);
            canvas.drawRoundRect(okButton, 10, 10, paint);
        }
        drawLetters(canvas, letters);
        drawLetters(canvas, answer);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int letterFraction = 24;
        characterSize = getWidth() / letterFraction;
        int h = getHeight() * 3 / 4;
        int w = getWidth();
        okButton.set(w / 3F, h, w * 2F / 3, h + characterSize * 4F);

        configureCurrentLesson();
    }

    private double getCurrentScore() {
        if (currentLesson == 0) {
            if (lessons.isEmpty()) {
                return 0;
            }
            return points / lessons.size();
        }
        return points / currentLesson;
    }

    private static void drawTextLayout(Canvas canvas, int dx, int dy, DynamicLayout englishLayout) {
        if (englishLayout != null) {
            canvas.save();
            canvas.translate(dx, dy);
            englishLayout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawLetters(Canvas canvas, List<Letter> l) {
        if (lessons.isEmpty()) {
            return;
        }
        if (currentLesson >= lessons.size()) {
            currentLesson = 0;
        }


        JapaneseLesson japaneseLesson = lessons.get(currentLesson);
        for (int i = 0; i < l.size(); i++) {
            Letter rc = l.get(i);

            if (letters.isEmpty() && Objects.toString(japaneseLesson.getJapanese(), "")
                                            .length() > i) {
                Paint p = Objects.equals(Character.toString(japaneseLesson.getJapanese().charAt(i)),
                        rc.character) ? greenPaint : redPaint;

                canvas.drawRoundRect(rc.bound, 10, 10, p);
            }
            canvas.drawRoundRect(rc.bound, 10, 10, paint);
            canvas.drawText(rc.character, rc.bound.left + rc.bound.width() / 2,
                    rc.bound.top + characterSize * 5F / 4, this.paint);
        }
    }

    private void showDialogWinning() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);

        double score = getCurrentScore();
        String description = getContext().getString(R.string.punctuation, score);
        currentLesson = 0;
        if (isRecordSuitable((long) score * 100, UserRecord.JAPANESE, chapter, false)) {
            createRecordIfSuitable((long) score * 100, description, UserRecord.JAPANESE, chapter,
                    false);
            showRecords(chapter, UserRecord.JAPANESE, this::nextChapter);
            return;
        }
        text.setText(String.format(getResources().getString(R.string.you_win), description));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(v -> nextChapter());
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void nextChapter() {
        setChapter((chapter + 1) % MAX_CHAPTERS);
        addUserPreference("japanese." + JapaneseActivity.class.getSimpleName(), R.string.chapter,
                JapaneseView.chapter);
        loadLessons();
    }

    private int getAnswerLayout() {
        return getHeight() / 3;
    }

    private boolean exchangeLetter(MotionEvent event, int sourceOffset, List<Letter> source,
            int targetOffset, List<Letter> target) {
        int w2 = getWidth();
        for (int i = 0; i < source.size(); i++) {
            Letter rc = source.get(i);
            if (rc.bound.contains(event.getX(), event.getY())) {
                int j = target.size();
                Letter remove = source.remove(i);
                adjust(targetOffset, w2, j, remove);
                target.add(remove);
                for (int k = 0; k < source.size(); k++) {
                    adjust(sourceOffset, w2, k, source.get(k));
                }
                invalidate();
                return true;
            }
        }
        return false;
    }
}

