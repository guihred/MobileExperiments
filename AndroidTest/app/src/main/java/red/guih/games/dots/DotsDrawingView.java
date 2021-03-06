package red.guih.games.dots;


import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import red.guih.games.AutomaticListener;
import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

import static java.lang.Math.abs;
import static red.guih.games.dots.StreamHelp.comparing;
import static red.guih.games.dots.StreamHelp.distinct;
import static red.guih.games.dots.StreamHelp.filter;
import static red.guih.games.dots.StreamHelp.flatMap;
import static red.guih.games.dots.StreamHelp.groupBy;
import static red.guih.games.dots.StreamHelp.map;
import static red.guih.games.dots.StreamHelp.min;
import static red.guih.games.dots.StreamHelp.mins;
import static red.guih.games.dots.StreamHelp.toSet;

public class DotsDrawingView extends BaseView {
    public static final int LINE_ANIMATION_DURATION = 500;
    public static final int THIRD_STAR_THRESHOLD = 70;
    private static final String TAG = "DOTS";
    private static final String[] PLAYERS = {"EU", "TU"};
    static int mazeWidth = 8;
    static int difficulty = 2;
    private final Line line = new Line();
    private final List<Line> lines = Collections.synchronizedList(new ArrayList<>());
    private final List<Set<DotsSquare>> whites = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Set<Set<DotsSquare>>> points = new HashMap<>();
    private final Random random = new Random();
    private final Paint paint;
    private final Paint transparent = new Paint();
    private int mazeHeight = 8;
    private DotsSquare selected;
    private DotsSquare[][] maze;
    private int currentPlayer = 1;

    public DotsDrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);

        paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);

        transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparent.setColor(Color.TRANSPARENT);
        transparent.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        setLayerType(View.LAYER_TYPE_SOFTWARE, new Paint(Color.WHITE));
        points.put("EU", new HashSet<>());
        points.put("TU", new HashSet<>());

    }


    public static int getMazeWidth() {
        return mazeWidth;
    }

    public static void setMazeWidth(int mazeWidth) {
        DotsDrawingView.mazeWidth = mazeWidth;
    }

    public static void setDifficulty(int difficulty) {
        DotsDrawingView.difficulty = difficulty;
    }

    private static Float getDistance(DotsSquare e, float x, float y) {
        float[] center = e.getCenter();
        float abs = abs(center[0] - x);
        float abs2 = abs(center[1] - y);
        return abs * abs + abs2 * abs2;
    }

    private static DotsSquare getMin(DotsSquare a1, DotsSquare b1, boolean condition1,
            boolean condition2) {
        if (condition1 || condition2) {
            return a1;
        }
        return b1;
    }

    private List<Pair> getPossibilities() {
        List<Pair> possibilities = new ArrayList<>();
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                if (i < mazeWidth - 1 && !maze[i][j].contains(maze[i + 1][j])) {
                    possibilities.add(new Pair(maze[i][j], maze[i + 1][j]));
                }
                if (j < mazeHeight - 1 && !maze[i][j].contains(maze[i][j + 1])) {
                    possibilities.add(new Pair(maze[i][j], maze[i][j + 1]));
                }
            }
        }
        Collections.shuffle(possibilities);
        return possibilities;
    }

    private List<Pair> getBestPossibilities(Collection<Pair> possibilities) {
        Log.i(TAG, "BEST POSSIBILITY 1");
        Set<Pair> best2 = createPairs();
        List<Pair> best = new ArrayList<>(best2);
        if (difficulty > 0) {
            if (best.size() == 2) {
                Set<Pair> pairs1 = getPairs(best.get(0).getKey(), best.get(0).getValue());
                int countPair = pairs1.size();
                Set<Pair> pairs2 = getPairs(best.get(1).getKey(), best.get(1).getValue());
                int countPair2 = pairs2.size();
                if (countPair < countPair2) {
                    return Collections.singletonList(best.get(0));
                }
                if (countPair > countPair2) {
                    return Collections.singletonList(best.get(1));
                }
                if (difficulty > 1 && countPair == 3 && pairs1.equals(pairs2)) {
                    List<Pair> distinct = distinct(possibilities);
                    distinct.removeAll(pairs1);
                    final Map<Integer, List<Pair>> collect = groupBy(distinct,
                            e -> getCountPair(e.getKey(), e.getValue()));
                    final int minimum = min(collect.keySet(), 0);
                    if (minimum > 2) {
                        return distinct(pairs1).subList(1, 2);
                    }
                }
            }
            if (best.size() == 1) {
                Set<Pair> pairs = getPairs(best.get(0).getKey(), best.get(0).getValue());
                int countPair = pairs.size();
                List<Pair> distinct = distinct(possibilities);
                distinct.removeAll(pairs);
                final Map<Integer, List<Pair>> collect = groupBy(distinct,
                        e -> getCountPair(e.getKey(), e.getValue()));
                final int minimum = min(collect.keySet(), 0);

                if (countPair == 2 && minimum > 2) {
                    Set<Pair> setCountMap = getPairs(best.get(0).getKey(), best.get(0).getValue());
                    setCountMap.remove(best.get(0));
                    return distinct(setCountMap);
                }
            }
        }
        return best;
    }

    private Set<Pair> createPairs() {
        Set<Pair> best2 = new HashSet<>();

        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                final List<DotsSquare> bestCheck = maze[i][j].almostSquare();
                final DotsSquare maze1 = maze[i][j];
                final List<Pair> collect = map(bestCheck, e -> new Pair(maze1, e));
                best2.addAll(collect);
            }
        }
        return best2;
    }

    private void reset() {
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                maze[i][j].clear();
            }
        }
        line.reset();
        points.get("EU").clear();
        points.get("TU").clear();
        invalidate();


    }

    @Override
    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAllDesc(difficulty, gameName);
    }

    private void showDialog() {
        invalidate();
        float userPoints = this.points.get("TU").size();
        int percentage = (int) (userPoints / (mazeWidth - 1) / (mazeHeight - 1) * 100);
        boolean userWon = points.get("TU").size() > points.get("EU").size();
        if (userWon && isRecordSuitable(percentage, UserRecord.DOTS, DotsDrawingView.mazeWidth,
                false)) {
            createRecordIfSuitable(percentage, percentage + "%", UserRecord.DOTS,
                    DotsDrawingView.mazeWidth, false);
            showRecords(DotsDrawingView.mazeWidth, UserRecord.DOTS, DotsDrawingView.this::reset);
            return;
        }

        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.dots_dialog);
        dialog.setTitle(R.string.game_over);
        String string = getResources().getString(R.string.you_win);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.text);

        if (userWon) {
            String format = String.format(string, percentage + "%");
            text.setText(format);
            ImageView image = dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.smile);
            if (percentage >= 60) {
                ImageView image2 = dialog.findViewById(R.id.image2);
                image2.setImageResource(R.drawable.smile);
            }
            if (percentage >= THIRD_STAR_THRESHOLD) {
                ImageView image2 = dialog.findViewById(R.id.image3);
                image2.setImageResource(R.drawable.smile);
            }

        } else {
            text.setText(R.string.you_lose);
        }

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            DotsDrawingView.this.reset();
            dialog.dismiss();
        });

        dialog.show();
    }

    private List<Pair> getBestPossibilities2(final List<Pair> possibilities) {
        Log.i(TAG, "BEST Possibility 2");
        return filter(possibilities, entry -> {
            final boolean check = entry.getKey().checkMelhor(entry.getValue());
            if (!check) {
                return false;
            }
            final boolean check2 = entry.getValue().checkMelhor(entry.getKey());
            if (!check2) {
                return false;
            }
            entry.getKey().addAdj(entry.getValue());
            final boolean almostSquare = hasAnyAlmostSquare();
            entry.getKey().removeAdj(entry.getValue());
            return !almostSquare;
        }, 5);
    }

    boolean hasAnyAlmostSquare() {
        for (DotsSquare[] s : maze) {
            for (DotsSquare d : s) {
                if (d.hasAlmostSquare()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (currentPlayer == 1 && whites.isEmpty()) {
            if (action == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                DotsSquare min = getCloserDotsSquare(x, y);
                float[] center = min.getCenter();
                line.startX = center[0];
                line.startY = center[1];
                line.endX = x;
                line.endY = y;
                selected = min;
                this.invalidate();
            } else if (action == MotionEvent.ACTION_MOVE) {
                float x = event.getX();
                float y = event.getY();
                line.endX = x;
                line.endY = y;
                this.invalidate();
            } else if (action == MotionEvent.ACTION_UP) {
                synchronized (this) {
                    onRelease(event);
                }
            }
        }

        return true;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        drawClosedSquares(canvas);
        drawWhiteSquares(canvas);
        drawCirclesAndConnectedLines(canvas);

        points.get(PLAYERS[currentPlayer]);
//Draw current User line
        canvas.drawLine(line.startX, line.startY, line.endX, line.endY, this.paint);
//Draw computer player  lines
        for (Line l : lines) {
            canvas.drawLine(l.startX, l.startY, l.endX, l.endY, this.transparent);
        }
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                DotsSquare dotsSquare = maze[i][j];
                float[] center = dotsSquare.getCenter();
                canvas.drawCircle(center[0], center[1], 5, paint);
            }
        }
    }

    private void drawClosedSquares(Canvas canvas) {
        for (Map.Entry<String, Set<Set<DotsSquare>>> entry : points.entrySet()) {
            Set<Set<DotsSquare>> value = entry.getValue();
            for (Set<DotsSquare> d : value) {
                DotsSquare min = min(d, comparing((DotsSquare e) -> e.i * mazeWidth + e.j));
                float[] center = min.getCenter();
                float left = center[0];
                float top = center[1]; // basically (X1, Y1)
                float right = left + DotsSquare.squareSize; // width (distance from X1 to X2)
                float bottom = top + DotsSquare.squareSize; // height (distance from Y1 to Y2)

                if ("EU".equals(entry.getKey())) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLUE);
                }
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    private void drawWhiteSquares(Canvas canvas) {
        for (Set<DotsSquare> d : whites) {
            DotsSquare min = min(d, comparing((DotsSquare e) -> e.i * mazeWidth + e.j));
            float[] center = min.getCenter();
            float left = center[0];
            float top = center[1]; // basically (X1, Y1)
            float right = left + DotsSquare.squareSize; // width (distance from X1 to X2)
            float bottom = top + DotsSquare.squareSize; // height (distance from Y1 to Y2)
            canvas.drawRect(left, top, right, bottom, transparent);
        }
    }

    private void drawCirclesAndConnectedLines(Canvas canvas) {
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                DotsSquare dotsSquare = maze[i][j];
                float[] center = dotsSquare.getCenter();
                canvas.drawCircle(center[0], center[1], 5, paint);
                for (DotsSquare a : dotsSquare.getAdjacencies()) {
                    float[] center1 = a.getCenter();
                    canvas.drawLine(center[0], center[1], center1[0], center1[1], this.paint);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = this.getWidth();
        setSquareSize(width / mazeWidth);
        mazeHeight = this.getHeight() / DotsSquare.squareSize;
        if (maze == null) {
            maze = new DotsSquare[mazeWidth][mazeHeight];
            for (int i = 0; i < mazeWidth; i++) {
                for (int j = 0; j < mazeHeight; j++) {
                    maze[i][j] = new DotsSquare(i, j);
                }
            }
        }
    }

    static void setSquareSize(int squareSize) {
        DotsSquare.squareSize = squareSize;
    }

    private void onRelease(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        DotsSquare over = getCloserDotsSquare(x, y);

        if (isValidMove(over)) {
            over.addAdj(selected);
            //Checks whether the user closed a square
            Set<Set<DotsSquare>> check = over.check();
            Set<Set<DotsSquare>> collect = toSet(flatMap(points.values(), a -> a));
            Set<Set<DotsSquare>> collect1 = filter(check, s -> !collect.contains(s));
            if (!collect1.isEmpty()) {
                points.get("TU").addAll(collect1);
            } else {
                currentPlayer = (currentPlayer + 1) % PLAYERS.length;
                for (int nPlayed = 0; currentPlayer == 0; nPlayed++) {
                    List<Pair> possibilities = choosePossiblePlays();
                    if (possibilities.isEmpty()) {
                        currentPlayer = 1;
                        break;
                    }
                    // AI chooses the best choice among the possibilities
                    final Pair get = possibilities.get(random.nextInt(possibilities.size()));
                    get.getKey().addAdj(get.getValue());

                    Set<Set<DotsSquare>> check2 = get.getKey().check();
                    final Set<Set<DotsSquare>> collect2 = toSet(flatMap(points.values(), a -> a));
                    final Set<Set<DotsSquare>> squaresWon = Collections
                            .synchronizedSet(filter(check2, s -> !collect2.contains(s)));
                    if (squaresWon.isEmpty()) {
                        currentPlayer = 1;
                    } else {
                        points.get("EU").addAll(squaresWon);
                        whites.addAll(squaresWon);
                    }
                    createAnimation(nPlayed, get, squaresWon);
                }
            }
        }
        selected = null;
        line.reset();

        verifyEndOfGame();
        this.invalidate();

    }

    private void verifyEndOfGame() {
        int total = points.get("EU").size() + points.get("TU").size();
        if (total == (mazeWidth - 1) * (mazeHeight - 1) && whites.isEmpty()) {
            showDialog();// END OF GAME
        }
    }

    private List<Pair> choosePossiblePlays() {
        final List<Pair> allPossibilities = getPossibilities();
        // VERIFY IF THE USER LEFT ANY SQUARE TO BE CLOSED
        List<Pair> possibilities = getBestPossibilities(allPossibilities);
        // VERIFY IF THERE ARE ANY OPTIONS THAT DON'T GIVE POINTS TO
        // THE USER
        possibilities = possibilities.isEmpty() ? getBestPossibilities2(allPossibilities)
                : possibilities;
        // VERIFY AMONG THE OPTIONS WHICH GIVES LESS POINTS
        possibilities = possibilities.isEmpty() ? getBestPossibilities3(allPossibilities)
                : possibilities;
        Log.i(TAG, "BEST POSSIBILITIES FOUND");
        possibilities = possibilities.isEmpty() ? allPossibilities : possibilities;
        return possibilities;
    }

    private void createAnimation(int nPlayed, Pair get, Set<Set<DotsSquare>> squaresWon) {
        final float[] center = get.getKey().getCenter();
        final float[] center2 = get.getValue().getCenter();
        Keyframe kf0 = Keyframe.ofFloat(0, center2[0]);
        Keyframe kf1 = Keyframe.ofFloat(1, center[0]);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder
                .ofKeyframe("endX", kf0, kf1);
        Keyframe kf2 = Keyframe.ofFloat(0, center2[1]);
        Keyframe kf3 = Keyframe.ofFloat(1, center[1]);

        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder
                .ofKeyframe("endY", kf2, kf3);
        Line line2 = new Line(center, center2);
        lines.add(line2);
        ObjectAnimator lineAnim = ObjectAnimator
                .ofPropertyValuesHolder(line2, pvhRotation,
                        pvhRotation2);
        lineAnim.setDuration(LINE_ANIMATION_DURATION);
        long startDelay = (long) LINE_ANIMATION_DURATION * nPlayed;
        lineAnim.setStartDelay(startDelay);
        lineAnim.start();
        lineAnim.addUpdateListener(a -> invalidate());
        lineAnim.addListener(new AutomaticListener(() -> {
            lines.remove(line2);
            whites.removeAll(squaresWon);
            if (points.get("EU").size() + points.get("TU")
                                                .size() == (mazeWidth - 1) * (mazeHeight - 1) &&
                    whites.isEmpty()) {
                showDialog();// END OF GAME
            }
        }));
    }

    private boolean isValidMove(DotsSquare over) {
        return selected != null && over != null && !Objects.equals(selected, over) && abs(
                over.i - selected.i) + abs(over.j - selected.j) == 1
                && !over.contains(selected);
    }

    private DotsSquare getCloserDotsSquare(float x, float y) {
        List<DotsSquare> dotsSquares = flatMap(Arrays.asList(maze), Arrays::asList);
        return min(dotsSquares, comparing((DotsSquare e) -> getDistance(e, x, y)));
    }

    private List<Pair> getBestPossibilities3(final Iterable<Pair> possibilities) {
        Log.i(TAG, "BEST Possibilities 3");
        return mins(possibilities, comparing(e -> getCountPair(e.getKey(), e.getValue())));
    }

    private int getCountPair(DotsSquare a1, DotsSquare b1) {
        Set<Pair> setCountMap = getSetCountMap(a1, b1);
        for (Pair p : setCountMap) {
            p.a.removeAdj(p.b);
        }

        return setCountMap.size();
    }

    private Set<Pair> getPairs(DotsSquare a1, DotsSquare b1) {
        Set<Pair> setCountMap = getSetCountMap(a1, b1);
        for (Pair p : setCountMap) {
            p.a.removeAdj(p.b);
        }
        return setCountMap;
    }

    private Set<Pair> getSetCountMap(DotsSquare a1, DotsSquare b1) {
        DotsSquare a = getMin(a1, b1, a1.i < b1.i, a1.j < b1.j);
        DotsSquare b = getMin(b1, a1, b1.i > a1.i, b1.j > a1.j);
        if (Objects.equals(a, b)) {
            Log.e("DOTS DRAWING VIEW", "ERROR THIS SHOULD NOT HAPPEN");
            return Collections.emptySet();
        }

        a.addAdj(b);
        Set<Pair> pairs = new LinkedHashSet<>();
        pairs.add(new Pair(a, b));
        int i = a.i < b.i ? a.i : b.i;
        int j = a.j < b.j ? a.j : b.j;

        if (a.i == b.i) {
            if (i > 0) {
                DotsSquare c = maze[i - 1][j];
                DotsSquare d = maze[i - 1][j + 1];
                pairs.addAll(getPairPoint(a, b, c, d));

            }
            if (i < mazeWidth - 1) {
                DotsSquare c = maze[i + 1][j];
                DotsSquare d = maze[i + 1][j + 1];
                pairs.addAll(getPairPoint(a, b, c, d));
            }
        } else if (a.j == b.j) {
            if (j > 0) {
                DotsSquare c = maze[i][j - 1];
                DotsSquare d = maze[i + 1][j - 1];
                pairs.addAll(getPairPoint(a, b, c, d));
            }
            if (j < mazeHeight - 1) {
                DotsSquare c = maze[i][j + 1];
                DotsSquare d = maze[i + 1][j + 1];
                pairs.addAll(getPairPoint(a, b, c, d));
            }
        }
        return pairs;
    }

    private Set<Pair> getPairPoint(DotsSquare a, DotsSquare b, DotsSquare c, DotsSquare d) {

        if (a.contains(b) && b.contains(d) && d.contains(c) && !c.contains(a)) {
            Pair pair = new Pair(a, c);
            Set<Pair> setCountMap = getSetCountMap(a, c);
            setCountMap.add(pair);
            return setCountMap;
        }
        if (a.contains(b) && !b.contains(d) && d.contains(c) && c.contains(a)) {
            Pair pair = new Pair(d, b);
            Set<Pair> setCountMap = getSetCountMap(d, b);
            setCountMap.add(pair);
            return setCountMap;
        }
        if (a.contains(b) && b.contains(d) && !d.contains(c) && c.contains(a)) {
            Pair pair = new Pair(d, c);
            Set<Pair> setCountMap = getSetCountMap(d, c);
            setCountMap.add(pair);
            return setCountMap;
        }
        return Collections.emptySet();
    }
}

