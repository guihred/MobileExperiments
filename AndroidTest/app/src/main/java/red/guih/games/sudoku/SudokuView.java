package red.guih.games.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import red.guih.games.BaseView;
import red.guih.games.R;

public class SudokuView extends BaseView {
    public static final int MAP_NUMBER = 3;

    public static final int MAP_N_SQUARED = MAP_NUMBER * MAP_NUMBER;
    private final List<NumberButton> numberOptions = new ArrayList<>();
    private final List<SudokuSquare> sudokuSquares = new ArrayList<>();

    private SudokuSquare pressedSquare;
    private Paint black;
    private boolean resetStarted;
    private Drawable spinButton;

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        spinButton = getResources().getDrawable(R.drawable.return_button, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (resetStarted) {
            return true;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            handleMousePressed(event);
        } else if (action == MotionEvent.ACTION_MOVE) {
            handleMouseMoved(event);
        } else if (action == MotionEvent.ACTION_UP) {
            handleMouseReleased(event);
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (SudokuSquare sq : sudokuSquares) {
            sq.draw(canvas, !resetStarted);
        }
        paintDarkerSquares(canvas);
        if (pressedSquare != null) {
            RectF boundsInParent = pressedSquare.getBounds();
            int halfTheSize = MAP_N_SQUARED / 2;
            float maxY = pressedSquare.getCol() > halfTheSize ?
                    boundsInParent.top - SudokuSquare.getSquareSize() * MAP_NUMBER
                    : boundsInParent.bottom;
            float maxX = pressedSquare.getRow() > halfTheSize ?
                    boundsInParent.left - SudokuSquare.getSquareSize() * MAP_NUMBER
                    : boundsInParent.right;
            numberOptions.forEach(e -> e.draw(canvas, maxX, maxY));
        }

    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int squareSize = getWidth() / MAP_N_SQUARED;
        float layoutY = (getHeight() - squareSize * MAP_N_SQUARED) / 2F;
        SudokuSquare.setSquareSize(squareSize, layoutY);
        blank();
        spinButton.setBounds(0, 0, getWidth(), getHeight());
        invalidate();
        new Thread(() -> {
            initialize();
            postInvalidate();
        }).start();
    }

    private void paintDarkerSquares(Canvas canvas) {
        if (black == null) {
            black = new Paint();
            black.setStyle(Paint.Style.STROKE);
            black.setColor(Color.BLACK);
            black.setStrokeWidth(4);
            black.setTextSize(NumberButton.TEXT_SIZE);
            black.setTextAlign(Paint.Align.CENTER);
        }

        for (int i = 0; i < MAP_NUMBER; i++) {
            for (int j = 0; j < MAP_NUMBER; j++) {
                float sqSize = (float) SudokuSquare.getSquareSize() * MAP_NUMBER;
                canvas.drawRect(i * sqSize, j * sqSize + SudokuSquare.layoutY, i * sqSize + sqSize,
                        j * sqSize + sqSize + SudokuSquare.layoutY, black);
            }
        }
    }

    private void initialize() {
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                sudokuSquares.add(new SudokuSquare(i, j));
            }
        }
        for (int i = 0; i < MAP_NUMBER; i++) {
            for (int j = 0; j < MAP_NUMBER; j++) {
                NumberButton child = new NumberButton(i * MAP_NUMBER + j + 1);
                numberOptions.add(child);
            }
        }
        NumberButton child = new NumberButton(0);
        numberOptions.add(child);
        reset();
    }

    private void createRandomNumbers(List<Integer> numbers) {
        int nTries = 0;
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                if (tryToSetNumbers(i, j, numbers)) {
                    j = -1;
                    nTries++;
                    if (nTries > 100) {
                        sudokuSquares.forEach(SudokuSquare::setEmpty);
                        i = -1;
                        nTries = 0;
                        break;
                    }
                }
            }
        }
    }

    boolean tryToSetNumbers(int i, int j, List<Integer> numbers) {
        Collections.shuffle(numbers);
        Optional<Integer> fitNumbers =
                numbers.stream().filter(n -> isNumberFit(n, i, j)).findFirst();
        getMapAt(i, j).setPermanent(true);
        if (fitNumbers.isPresent()) {
            getMapAt(i, j).setNumber(fitNumbers.get());
            return false;
        }
        sudokuSquares.stream().filter(e -> e.isInRow(i)).forEach(SudokuSquare::setEmpty);
        return true;
    }


    private boolean isNumberFit(int n, int row, int col) {
        return sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                            .filter(s -> s.isInRow(row))
                            .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                                .filter(s -> s.isInArea(row, col))
                                .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                                .filter(s -> s.isInCol(col))
                                .noneMatch(s -> s.getNumber() == n);
    }

    private boolean isNumberFitStrictly(int n, int row, int col) {
        return sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                            .filter(s -> s.isInRow(row))
                            .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                                .filter(s -> s.isInArea(row, col))
                                .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> e.isNotInPosition(row, col))
                                .filter(s -> s.isInCol(col))
                                .noneMatch(s -> s.getNumber() == n);
    }


    public SudokuSquare getMapAt(int i, int j) {
        return sudokuSquares.get(i * MAP_N_SQUARED + j);
    }

    public void reset() {
        resetStarted = true;
        Log.i("Reset", " RESETTING");
        List<Integer> numbers =
                IntStream.rangeClosed(1, MAP_N_SQUARED).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
        createRandomNumbers(numbers);
        List<SudokuSquare> all = sudokuSquares.stream().filter(SudokuSquare::isNotEmpty)
                                              .collect(Collectors.toList());
        Collections.shuffle(all);

        for (SudokuSquare sudokuSquare : all) {
            int previousN = sudokuSquare.setEmpty();
            updatePossibilities();
            solve();
            if (isFullyFilled()) {
                sudokuSquare.setPermanent(false);
            } else {
                sudokuSquare.setNumber(previousN);
            }
            postInvalidate();

            sudokuSquares.parallelStream().filter(SudokuSquare::isNotPermanent)
                         .forEach(SudokuSquare::setEmpty);
        }
        resetStarted = false;
    }

    private void updatePossibilities() {
        for (SudokuSquare sq : sudokuSquares) {
            sq.setPossibilities(IntStream
                    .rangeClosed(1, MAP_N_SQUARED)
                    .filter(n -> isNumberFitStrictly(n, sq.getRow(), sq.getCol())).boxed()
                    .collect(Collectors.toList()));
            sq.setWrong(!sq.isEmpty() && !sq.getPossibilities().contains(sq.getNumber()));
        }

        removeDuplicatedPossibilities();
    }

    private boolean isFullyFilled() {
        return sudokuSquares.stream().allMatch(e -> !e.isEmpty() && !e.isWrong());
    }

    private void oneSolution(int number, List<SudokuSquare> squares) {
        SudokuSquare sq = squares.get(0);
        sq.setNumber(number);
        updatePossibilities();
    }

    public void solve() {
        updatePossibilities();
        sudokuSquares.stream().filter(SudokuSquare::isNotEmpty).forEach(e -> e.setPermanent(true));
        boolean changed = true;
        while (changed) {
            changed = false;
            setSquareWithOnePossibility();
            for (int i = 0; i < MAP_N_SQUARED; i++) {
                for (int number = 1; number <= MAP_N_SQUARED; number++) {
                    List<SudokuSquare> area = getArea(i, number);
                    if (area.size() == 1) {
                        oneSolution(number, area);
                        changed = true;
                    }
                    List<SudokuSquare> row = getRow(i, number);
                    if (row.size() == 1) {
                        oneSolution(number, row);
                        changed = true;
                    }
                    List<SudokuSquare> col = getCol(i, number);
                    if (col.size() == 1) {
                        oneSolution(number, col);
                        changed = true;
                    }
                }
            }
            setSquareWithOnePossibility();
        }
    }

    public void blank() {
        for (SudokuSquare sq : sudokuSquares) {
            sq.setPermanent(false);
            sq.setEmpty();
        }
        invalidate();
    }

    private void setSquareWithOnePossibility() {
        while (sudokuSquares.stream()
                            .anyMatch(e -> e.isEmpty() && e.getPossibilities().size() == 1)) {
            sudokuSquares.stream().filter(e -> e.isEmpty() && e.getPossibilities().size() == 1)
                         .forEach(sq -> {
                             Integer number = sq.getPossibilities().get(0);
                             sq.setNumber(number);
                         });
            updatePossibilities();
        }
    }

    private List<SudokuSquare> getArea(int row) {
        return sudokuSquares.stream()
                            .filter(e -> e.isEmpty() && e.isInArea(row % MAP_NUMBER * MAP_NUMBER,
                                    row / MAP_NUMBER * MAP_NUMBER))
                            .collect(Collectors.toList());
    }

    private List<SudokuSquare> getArea(int i, int number) {
        return sudokuSquares.stream()
                            .filter(e1 -> e1.isEmpty() && e1.isInArea(i % MAP_NUMBER * MAP_NUMBER,
                                    i / MAP_NUMBER * MAP_NUMBER))
                            .filter(e -> e.getPossibilities().contains(number))
                            .collect(Collectors.toList());
    }

    private List<SudokuSquare> getCol(int row) {
        return sudokuSquares.stream().filter(e -> e.isEmpty() && e.isInCol(row))
                            .collect(Collectors.toList());
    }

    private List<SudokuSquare> getCol(int i, int number) {
        return sudokuSquares.stream().filter(e1 -> e1.isEmpty() && e1.isInCol(i))
                            .filter(e -> e.getPossibilities().contains(number))
                            .collect(Collectors.toList());
    }

    private List<SudokuSquare> getRow(int row) {
        return sudokuSquares.stream().filter(e -> e.isEmpty() && e.isInRow(row))
                            .collect(Collectors.toList());
    }

    private List<SudokuSquare> getRow(int i, int number) {
        return sudokuSquares.stream().filter(e1 -> e1.isEmpty() && e1.isInRow(i))
                            .filter(e -> e.getPossibilities().contains(number))
                            .collect(Collectors.toList());
    }

    public void handleMousePressed(MotionEvent ev) {
        Optional<SudokuSquare> pressed = sudokuSquares.stream()
                                                      .filter(SudokuSquare::isNotPermanent)
                                                      .filter(s -> s.contains(ev.getX(), ev.getY()))
                                                      .findFirst();
        if (!pressed.isPresent()) {
            pressedSquare = null;
            return;
        }
        pressedSquare = pressed.get();
        handleMouseMoved(ev);
    }

    public void handleMouseMoved(MotionEvent s) {
        numberOptions.forEach(e -> e.setOver(e.contains(s.getX(), s.getY())));
    }

    public void handleMouseReleased(MotionEvent s) {
        Optional<NumberButton> findFirst = numberOptions.stream()
                                                        .filter(e -> e.contains(s.getX(), s.getY()))
                                                        .findFirst();
        if (pressedSquare != null && findFirst.isPresent()) {
            NumberButton node = findFirst.get();
            pressedSquare.setNumber(node.getNumber());
            updatePossibilities();
        }
        pressedSquare = null;

        if (isFullyFilled()) {
            showDialogWinning(this::reset);
        }

    }

    private void clearPossibilities(List<SudokuSquare> squares) {
        for (int l = 0; l < squares.size(); l++) {
            SudokuSquare sq = squares.get(l);
            for (int k = l + 1; k < squares.size() && sq.getPossibilities().size() == 2; k++) {
                SudokuSquare sq2 = squares.get(k);
                if (Objects.equals(sq.getPossibilities(), sq2.getPossibilities())) {
                    removeFromCol(sq, sq2);
                    removeFromRow(sq, sq2);
                    removeFromArea(sq, sq2);
                }
            }
        }
    }

    private void removeDuplicatedPossibilities() {
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            for (int number = 1; number <= MAP_N_SQUARED; number++) {
                List<SudokuSquare> squares = getArea(i, number);
                if (squares.size() == 2) {
                    SudokuSquare sq0 = squares.get(0);
                    SudokuSquare sq1 = squares.get(1);
                    removeNumber(number, sq0, sq1);
                }
            }
        }
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            clearPossibilities(getRow(i));
            clearPossibilities(getCol(i));
            clearPossibilities(getArea(i));
        }
    }

    private void removeFromArea(SudokuSquare sq, SudokuSquare sq2) {
        if (sq.getRow() / MAP_NUMBER == sq2.getRow() / MAP_NUMBER
                && sq.getCol() / MAP_NUMBER == sq2.getCol() / MAP_NUMBER) {
            int row = sq.getRow() / MAP_NUMBER;
            int col = sq.getCol() / MAP_NUMBER;
            for (int i = 0; i < MAP_NUMBER; i++) {
                for (int j = 0; j < MAP_NUMBER; j++) {
                    SudokuSquare mapAt = getMapAt(row * MAP_NUMBER + i, col * MAP_NUMBER + j);
                    if (!mapAt.equals(sq) && !mapAt.equals(sq2)) {
                        mapAt.getPossibilities().removeAll(sq.getPossibilities());
                    }
                }
            }
        }
    }

    private void removeFromCol(SudokuSquare sq, SudokuSquare sq2) {
        if (sq.getRow() == sq2.getRow()) {
            int row = sq.getRow();
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                SudokuSquare mapAt = getMapAt(row, j);
                if (!mapAt.equals(sq) && !mapAt.equals(sq2)) {
                    mapAt.getPossibilities().removeAll(sq.getPossibilities());
                }
            }
        }
    }

    private void removeFromRow(SudokuSquare sq, SudokuSquare sq2) {
        if (sq.getCol() == sq2.getCol()) {
            int col = sq.getCol();
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                SudokuSquare mapAt = getMapAt(j, col);
                if (!mapAt.equals(sq) && !mapAt.equals(sq2)) {
                    mapAt.getPossibilities().removeAll(sq.getPossibilities());
                }
            }
        }
    }

    private void removeNumber(int number, SudokuSquare sq0, SudokuSquare sq1) {
        boolean sameCol = sq0.getCol() == sq1.getCol();
        boolean sameRow = sq0.getRow() == sq1.getRow();
        if (sameCol || sameRow) {
            for (int l = 0; l < MAP_N_SQUARED; l++) {
                int row = !sameRow ? l : sq0.getRow();
                int col = !sameCol ? l : sq0.getCol();
                SudokuSquare mapAt = getMapAt(row, col);
                if (!mapAt.isInArea(sq0.getRow(), sq0.getCol())) {
                    mapAt.getPossibilities().remove(Integer.valueOf(number));
                }

            }
        }
    }


}

