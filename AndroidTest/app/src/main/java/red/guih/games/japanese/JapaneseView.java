package red.guih.games.japanese;

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
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.JapaneseLesson;

/**
 * View for displaying the japanese game
 * <p>
 * Created by guilherme.hmedeiros on 16/03/2018.
 */

public class JapaneseView extends BaseView {

    public int characterSize = 50;
    public static int LESSON = 1;
    private List<JapaneseLesson> lessons = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Paint greenPaint = new Paint();
    private int currentLesson;
    final List<Letter> answer = new ArrayList<>();
    final List<Letter> letters = new ArrayList<>();
    private DynamicLayout englishLayout;
    private final RectF okButton = new RectF();
    private DynamicLayout romajiLayout;
    private DynamicLayout answerLayout;


    public JapaneseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setTextSize(characterSize);
        paint.setStyle(Paint.Style.STROKE);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStyle(Paint.Style.FILL);
        loadLessons();
    }

    private void loadLessons() {
        new Thread(() -> {
            lessons = db.japaneseLessonDao().getAll(LESSON);
            if (!lessons.isEmpty()) {
                points = getUserPreferenceFloat(R.string.punctuation, 0);
                currentLesson = getUserPreference(R.string.lesson, 0);


                configureCurrentLesson();
            }
            postInvalidate();
        }).start();
    }

    private void configureCurrentLesson() {
        if (lessons.isEmpty())
            return;

        JapaneseLesson japaneseLesson = lessons.get(currentLesson % lessons.size());
        letters.clear();
        answer.clear();
        List<String> split = Objects.toString(japaneseLesson.getJapanese(), "").chars().mapToObj(value -> (char) value).map(Object::toString).collect(Collectors.toList());
        Collections.shuffle(split);
        int h = getLettersLayout();
        int w = getWidth();

        for (int i = 0; i < split.size(); i++) {
            Letter e = new Letter(new RectF(), split.get(i));
            adjust(h, w, i, e);
            letters.add(e);
        }


        TextPaint tp = new TextPaint();
        tp.setColor(Color.BLACK);
        tp.setTextSize(characterSize);
        tp.setTextAlign(Paint.Align.CENTER);
        tp.setAntiAlias(true);
        englishLayout = new DynamicLayout("English: " + japaneseLesson.getEnglish(), tp,
                getWidth() * 3 / 4, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        englishLayout.getOffsetToRightOf(characterSize);
        romajiLayout = new DynamicLayout("Romaji: " + Objects.toString(japaneseLesson.getRomaji(), "").replaceAll("\\(.+\\)", ""), tp,
                getWidth() * 3 / 4, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        romajiLayout.getOffsetToRightOf(characterSize);
        answerLayout = new DynamicLayout("Answer: " + Objects.toString(japaneseLesson.getJapanese(), "").replaceAll("\\(.+\\)", ""), tp,
                getWidth() * 3 / 4, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        answerLayout.getOffsetToRightOf(characterSize);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        characterSize = getWidth() / 24;
        int h = getHeight() * 5 / 6;
        int w = getWidth();
        okButton.set(w / 3, h, w / 3 + characterSize * 6, h + characterSize * 4);

        configureCurrentLesson();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawText("Lesson " + LESSON + " : " + currentLesson + "/" + lessons.size(), characterSize, characterSize, paint);
        canvas.drawText(getContext().getString(R.string.punctuation, getScore()), getWidth() / 2, characterSize, paint);

        drawTextLayout(canvas, getWidth() / 2, getHeight() / 6, this.englishLayout);
        drawTextLayout(canvas, getWidth() / 2, getHeight() * 2 / 6, this.romajiLayout);
        if (letters.isEmpty()) {
            drawTextLayout(canvas, getWidth() / 2, getHeight() * 4 / 6, this.answerLayout);
            canvas.drawRoundRect(okButton, 10, 10, greenPaint);
            canvas.drawText("Ok", okButton.centerX(), okButton.centerY(), paint);
            canvas.drawRoundRect(okButton, 10, 10, paint);
        }
        drawLetters(canvas, this.letters);
        drawLetters(canvas, this.answer);

    }

    private double getScore() {
        if (currentLesson == 0)
            return 0;

        return points / currentLesson;
    }

    private void drawTextLayout(Canvas canvas, int dx, int dy, DynamicLayout englishLayout) {
        if (englishLayout != null) {

            canvas.save();
            canvas.translate(dx, dy);
            englishLayout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawLetters(Canvas canvas, List<Letter> l) {
        for (Letter rc : l) {
            canvas.drawRoundRect(rc.bound, 10, 10, paint);
            canvas.drawText(rc.character, rc.bound.left + characterSize / 2, rc.bound.top + characterSize * 5 / 4, paint);
        }
    }

    float points;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (letters.isEmpty() && okButton.contains(event.getX(), event.getY())) {
                    if (lessons.isEmpty()) {
                        loadLessons();
                        return true;
                    }


                    JapaneseLesson japaneseLesson = lessons.get(currentLesson % lessons.size());
                    float compare = CompareAnswers.compare(japaneseLesson.getJapanese(), answer.stream().map(e -> e.character).collect(Collectors.joining()));
                    points += compare * 100;
                    currentLesson = (currentLesson + 1) % lessons.size();
                    addUserPreference(R.string.punctuation, points);
                    addUserPreference(R.string.lesson, currentLesson);


                    configureCurrentLesson();
                    invalidate();
                }

                if (exchangeLetter(event, getLettersLayout(), this.letters, getAnswerLayout(), this.answer)) {
                    return true;
                }

                if (exchangeLetter(event, getAnswerLayout(), this.answer, getLettersLayout(), this.letters)) {


                    return true;
                }

                break;
            default:
                break;
        }
        invalidate();

        return true;
    }

    private int getAnswerLayout() {
        return getHeight() * 3 / 6;
    }

    private int getLettersLayout() {
        return getHeight() * 4 / 6;
    }

    private boolean exchangeLetter(MotionEvent event, int sourceOffset, List<Letter> source, int targetOffset, List<Letter> target) {
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

    private void adjust(int h, int w2, int j, Letter remove) {
        int left = w2 / 12 * (j % 10) + w2 / 12;
        int top = 2 * characterSize * (j / 10) + h;
        remove.bound.left = left;
        remove.bound.top = top;
        remove.bound.right = left + characterSize * 2;
        remove.bound.bottom = top + characterSize * 2;
    }
}

