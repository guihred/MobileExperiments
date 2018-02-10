package red.guih.games.dots;


import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
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
import java.util.Random;
import java.util.Set;

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
    private static final String TAG = "DOTS";
    public static final int LINE_ANIMATION_DURATION = 500;
    public static int MAZE_WIDTH = 8;
    public static int DIFFICULTY = 2;

    private int MAZE_HEIGHT = 8;
    private DotsSquare selected;
    private DotsSquare[][] maze;
    private Line line = new Line();
    private List<Line> lines = Collections.synchronizedList(new ArrayList<>());
    private List<Set<DotsSquare>> whites = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Set<Set<DotsSquare>>> points = new HashMap<>();
    private int currentPlayer = 1;
    private String[] players = {"EU", "TU"};

    private Random random = new Random();

    private Paint paint;
    private Paint transparent = new Paint();

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


    @Override
    protected void onDraw(final Canvas canvas) {
        drawClosedSquares(canvas);
        drawWhiteSquares(canvas);
        drawCirclesAndConnectedLines(canvas);

        points.get(players[currentPlayer]);
//Draw current User line
        canvas.drawLine(line.startX, line.startY, line.endX, line.endY, this.paint);
//Draw computer player  lines
        for (Line l : lines) {
            canvas.drawLine(l.startX, l.startY, l.endX, l.endY, this.transparent);
        }
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                DotsSquare dotsSquare = maze[i][j];
                float[] center = dotsSquare.getCenter();
                canvas.drawCircle(center[0], center[1], 5, paint);
            }
        }
    }

    private void drawCirclesAndConnectedLines(Canvas canvas) {
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
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

    private void drawWhiteSquares(Canvas canvas) {
        for (Set<DotsSquare> d : whites) {
            DotsSquare min = min(d, comparing((DotsSquare e) -> e.i * MAZE_WIDTH + e.j));
            float[] center = min.getCenter();
            float left = center[0], top = center[1]; // basically (X1, Y1)
            float right = left + DotsSquare.SQUARE_SIZE; // width (distance from X1 to X2)
            float bottom = top + DotsSquare.SQUARE_SIZE; // height (distance from Y1 to Y2)
            canvas.drawRect(left, top, right, bottom, transparent);
        }
    }

    private void drawClosedSquares(Canvas canvas) {
        for (Map.Entry<String, Set<Set<DotsSquare>>> entry : points.entrySet()) {
            Set<Set<DotsSquare>> value = entry.getValue();
            for (Set<DotsSquare> d : value) {
                DotsSquare min = min(d, comparing((DotsSquare e) -> e.i * MAZE_WIDTH + e.j));
                float[] center = min.getCenter();
                float left = center[0], top = center[1]; // basically (X1, Y1)
                float right = left + DotsSquare.SQUARE_SIZE; // width (distance from X1 to X2)
                float bottom = top + DotsSquare.SQUARE_SIZE; // height (distance from Y1 to Y2)

                if (entry.getKey().equals("EU")) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLUE);
                }
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = this.getWidth();
        DotsSquare.setSquareSize(width / MAZE_WIDTH);
        MAZE_HEIGHT = this.getHeight() / DotsSquare.SQUARE_SIZE;
        if (maze == null) {
            maze = new DotsSquare[MAZE_WIDTH][MAZE_HEIGHT];
            for (int i = 0; i < MAZE_WIDTH; i++) {
                for (int j = 0; j < MAZE_HEIGHT; j++) {
                    maze[i][j] = new DotsSquare(i, j);
                }
            }
        }
    }


    private List<Pair> getPossibilidades() {
        List<Pair> possibilidades = new ArrayList<>();
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                if (i < MAZE_WIDTH - 1 && !maze[i][j].contains(maze[i + 1][j])) {
                    possibilidades.add(new Pair(maze[i][j], maze[i + 1][j]));
                }
                if (j < MAZE_HEIGHT - 1 && !maze[i][j].contains(maze[i][j + 1])) {
                    possibilidades.add(new Pair(maze[i][j], maze[i][j + 1]));
                }
            }
        }
        Collections.shuffle(possibilidades);
        return possibilidades;
    }

    private List<Pair> getBestPossibilities(Collection<Pair> possibilities) {
        Log.i(TAG, "Melhor Possibilidade 1");
        Set<Pair> best2 = new HashSet<>();

        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                final List<DotsSquare> bestCheck = maze[i][j].almostSquare();
                final DotsSquare maze1 = maze[i][j];
                final List<Pair> collect = map(bestCheck, e -> new Pair(maze1, e));
                best2.addAll(collect);
            }
        }
        List<Pair> best = new ArrayList<>(best2);
        if (DIFFICULTY > 0) {
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
                if (DIFFICULTY > 1)
                    if (countPair == 3 && pairs1.equals(pairs2)) {
                        List<Pair> distinct = distinct(possibilities);
                        distinct.removeAll(pairs1);
                        final Map<Integer, List<Pair>> collect = groupBy(distinct, e -> getCountPair(e.getKey(), e.getValue()));
                        final int minimum = min(collect.keySet(), 0);
                        if (minimum > 2)
                            return distinct(pairs1).subList(1, 2);
                    }


            }
            if (best.size() == 1) {
                Set<Pair> pairs = getPairs(best.get(0).getKey(), best.get(0).getValue());
                int countPair = pairs.size();
                List<Pair> distinct = distinct(possibilities);
                distinct.removeAll(pairs);
                final Map<Integer, List<Pair>> collect = groupBy(distinct, e -> getCountPair(e.getKey(), e.getValue()));
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

    private void reset() {
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                maze[i][j].clear();
            }
        }
        line.reset();
        points.get("EU").clear();
        points.get("TU").clear();
        invalidate();


    }

    protected List<UserRecord> getAll(int difficulty, String gameName) {
        return db.userDao().getAllDesc(difficulty, gameName);
    }
    private void showDialog() {
        invalidate();
        float userPoints = this.points.get("TU").size();
        int percentage = (int) (userPoints / (MAZE_WIDTH - 1) / (MAZE_HEIGHT - 1) * 100);
        boolean userWon = points.get("TU").size() > points.get("EU").size();
        if (userWon&&isRecordSuitable(percentage, UserRecord.DOTS, DotsDrawingView.MAZE_WIDTH, false)) {
            createRecordIfSuitable(percentage, percentage + "%", UserRecord.DOTS, DotsDrawingView.MAZE_WIDTH, false);
            showRecords(DotsDrawingView.MAZE_WIDTH, UserRecord.DOTS, () -> DotsDrawingView.this.reset());
            return;
        }

        final Dialog dialog = new Dialog(getContext());
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
            if (percentage >= 70) {
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

    private List<Pair> getBestPossibilities2(final List<Pair> possibilidades) {
        Log.i(TAG, "Melhor Possibilidade 2");
        return filter(possibilidades, entry -> {
            final boolean checkMelhor = entry.getKey().checkMelhor(entry.getValue());
            if (!checkMelhor) {
                return false;
            }

            final boolean checkMelhor1 = entry.getValue().checkMelhor(entry.getKey());
            if (!checkMelhor1) {
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
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (currentPlayer == 1 && whites.isEmpty()) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
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
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX();
                    float y = event.getY();
                    line.endX = x;
                    line.endY = y;
                    this.invalidate();
                }
                break;
                case MotionEvent.ACTION_UP:
                    synchronized (this) {

                        float x = event.getX();
                        float y = event.getY();
                        DotsSquare over = getCloserDotsSquare(x, y);

                        if (selected != null && over != null && selected != over && abs(over.i - selected.i) + abs(over.j - selected.j) == 1
                                && !over.contains(selected)) {
                            over.addAdj(selected);
                            //Checks whether the user closed a square
                            Set<Set<DotsSquare>> check = over.check();

                            Set<Set<DotsSquare>> collect = toSet(flatMap(points.values(), a -> a));

                            Set<Set<DotsSquare>> collect1 = filter(check, s -> !collect.contains(s));
                            if (!collect1.isEmpty()) {
                                points.get("TU").addAll(collect1);
                            } else {
                                currentPlayer = (currentPlayer + 1) % players.length;

                                int nplayed = 0;
                                while (currentPlayer == 0) {
                                    final List<Pair> todas = getPossibilidades();
                                    // VERIFY IF THE USER LEFT ANY SQUARE TO BE CLOSED
                                    List<Pair> possibilities = getBestPossibilities(todas);

                                    // VERIFY IF THERE ARE ANY OPTIONS THAT DON'T GIVE POINTS TO THE USER
                                    possibilities = possibilities.isEmpty() ? getBestPossibilities2(todas) : possibilities;

                                    // VERIFY AMONG THE OPTIONS WHICH GIVES LESS POINTS
                                    possibilities = possibilities.isEmpty() ? getBestPossibilities3(todas) : possibilities;
                                    Log.i(TAG, "BEST POSSIBILITIES FOUND");
                                    possibilities = possibilities.isEmpty() ? todas : possibilities;

                                    if (possibilities.isEmpty()) {
                                        currentPlayer = 1;
                                        break;
                                    }
                                    // AI chooses the best choice among the possibilities
                                    final Pair get = possibilities.get(random.nextInt(possibilities.size()));
                                    get.getKey().addAdj(get.getValue());

                                    Set<Set<DotsSquare>> check2 = get.getKey().check();
                                    final Set<Set<DotsSquare>> collect2 = toSet(flatMap(points.values(), a -> a));
                                    final Set<Set<DotsSquare>> squaresWon = Collections.synchronizedSet(filter(check2, s -> !collect2.contains(s)));
                                    final boolean empty = squaresWon.isEmpty();
                                    if (empty) {
                                        currentPlayer = 1;
                                    } else {
                                        points.get("EU").addAll(squaresWon);
                                        whites.addAll(squaresWon);
                                    }

                                    final float[] center = get.getKey().getCenter();
                                    final float[] center2 = get.getValue().getCenter();
                                    Keyframe kf0 = Keyframe.ofFloat(0, center2[0]);
                                    Keyframe kf1 = Keyframe.ofFloat(1, center[0]);

                                    PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("endX", kf0, kf1);
                                    Keyframe kf2 = Keyframe.ofFloat(0, center2[1]);
                                    Keyframe kf3 = Keyframe.ofFloat(1, center[1]);

                                    PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("endY", kf2, kf3);
                                    Line line2 = new Line(center, center2);
                                    lines.add(line2);
                                    ObjectAnimator lineAnim = ObjectAnimator.ofPropertyValuesHolder(line2, pvhRotation, pvhRotation2);
                                    lineAnim.setDuration(LINE_ANIMATION_DURATION);
                                    lineAnim.setStartDelay(LINE_ANIMATION_DURATION * nplayed);
                                    lineAnim.start();
                                    nplayed++;

                                    lineAnim.addUpdateListener(a -> invalidate());
                                    lineAnim.addListener(new LineAnimatorListener(line2, squaresWon)
                                    );


                                }


                            }

                        }
                        selected = null;
                        line.reset();

                        if (points.get("EU").size() + points.get("TU").size() == (MAZE_WIDTH - 1) * (MAZE_HEIGHT - 1) && whites.isEmpty()) {
                            showDialog();// END OF GAME
                        }
                        this.invalidate();
                    }
                    break;

                default:
            }
        }

        return true;
    }

    private DotsSquare getCloserDotsSquare(float x, float y) {
        List<DotsSquare> dotsSquares = flatMap(Arrays.asList(maze), Arrays::asList);
        return min(dotsSquares, comparing((DotsSquare e) -> getDistance(e, x, y)));
    }

    @NonNull
    private Float getDistance(DotsSquare e, float x, float y) {
        float[] center = e.getCenter();
        float abs = abs(center[0] - x);
        float abs2 = abs(center[1] - y);
        return abs * abs + abs2 * abs2;
    }

    private List<Pair> getBestPossibilities3(final Iterable<Pair> possibilidades) {
        Log.i(TAG, "Melhor Possibilidade 3");
        return mins(possibilidades, comparing(e -> getCountPair(e.getKey(), e.getValue())));
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
        DotsSquare a = a1.i < b1.i ? a1 : a1.j < b1.j ? a1 : b1;
        DotsSquare b = b1.i > a1.i ? b1 : b1.j > a1.j ? b1 : a1;
        if (a == b) {
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
            if (i < MAZE_WIDTH - 1) {
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
            if (j < MAZE_HEIGHT - 1) {
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

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    private class LineAnimatorListener implements Animator.AnimatorListener {
        private final Line line2;
        private final Set<Set<DotsSquare>> squaresWon;

        public LineAnimatorListener(Line line2, Set<Set<DotsSquare>> collect3) {
            this.line2 = line2;
            this.squaresWon = collect3;
        }

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            lines.remove(line2);
            whites.removeAll(squaresWon);
            if (points.get("EU").size() + points.get("TU").size() == (MAZE_WIDTH - 1) * (MAZE_HEIGHT - 1) && whites.isEmpty()) {
                showDialog();// END OF GAME
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }
}

