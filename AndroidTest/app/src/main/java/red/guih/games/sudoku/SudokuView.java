package red.guih.games.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import red.guih.games.BaseView;
import red.guih.games.R;

public class SudokuView extends BaseView {
    public static final int MAP_NUMBER = 3;

    public static final int MAP_N_SQUARED = MAP_NUMBER * MAP_NUMBER;
    private List<NumberButton> numberOptions = new ArrayList<>();
    private List<SudokuSquare> sudokuSquares = new ArrayList<>();
    private Random random = new Random();

    private SudokuSquare pressedSquare;
    private Paint black;

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleMousePressed(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMouseMoved(event);
                break;
            case MotionEvent.ACTION_UP:
                handleMouseReleased(event);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int squareSize = getWidth() / MAP_N_SQUARED;
        float layoutY = (getHeight() - squareSize * MAP_N_SQUARED) / 2;
        SudokuSquare.setSquareSize(squareSize, layoutY);
        initialize();
        invalidate();
    }

    private void initialize() {
//        numberBoard.setVisible(false);
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                sudokuSquares.add(new SudokuSquare(i, j));
            }
        }
        for (int i = 0; i < MAP_NUMBER; i++) {
            for (int j = 0; j < MAP_NUMBER; j++) {
                NumberButton child = new NumberButton(i * MAP_NUMBER + j + 1);
                numberOptions.add(child);
//                numberBoard.add(child, j, i);
            }
        }
        NumberButton child = new NumberButton(0);
        numberOptions.add(child);
//        numberBoard.add(child, 3, 0);
        reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (SudokuSquare sq : sudokuSquares) {
            sq.draw(canvas);
        }
        paintDarkerSquares(canvas);

    }

    private void paintDarkerSquares(Canvas canvas) {
        if (black == null) {
            black = new Paint();
            black.setStyle(Paint.Style.STROKE);
            black.setColor(Color.BLACK);
            black.setStrokeWidth(4);
            black.setTextSize(30);
            black.setTextAlign(Paint.Align.CENTER);
        }

        for (int i = 0; i < MAP_NUMBER; i++) {
            for (int j = 0; j < MAP_NUMBER; j++) {
                int sqSize = SudokuSquare.SQUARE_SIZE * MAP_NUMBER;
                canvas.drawRect(i * sqSize, j * sqSize + SudokuSquare.LAYOUT_Y, i * sqSize + sqSize, j * sqSize + sqSize + SudokuSquare.LAYOUT_Y, black);
            }
        }
    }

    private void createRandomNumbers() {
        List<Integer> numbers = IntStream.rangeClosed(1, MAP_N_SQUARED).boxed().collect(Collectors.toList());
        int nTries = 0;
        for (int i = 0; i < MAP_N_SQUARED; i++) {
            for (int j = 0; j < MAP_N_SQUARED; j++) {
                int row = i;
                int col = j;
                Collections.shuffle(numbers);
                Optional<Integer> fitNumbers = numbers.stream().filter(n -> isNumberFit(n, row, col)).findFirst();
                getMapAt(i, j).setPermanent(true);
                if (fitNumbers.isPresent()) {
                    getMapAt(i, j).setNumber(fitNumbers.get());
                    continue;
                }
                nTries++;
                j = -1;
                sudokuSquares.stream().filter(e -> e.isInRow(row)).forEach(SudokuSquare::setEmpty);
                if (nTries > 100) {
                    i = -1;
                    nTries = 0;
                    sudokuSquares.forEach(SudokuSquare::setEmpty);
                    break;
                }
            }
        }
    }


    private boolean isNumberFit(int n, int row, int col) {
        return sudokuSquares.stream().filter(e -> !e.isInPosition(row, col)).filter(s -> s.isInRow(row))
                .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> !e.isInPosition(row, col)).filter(s -> s.isInArea(row, col))
                .noneMatch(s -> s.getNumber() == n)
                && sudokuSquares.stream().filter(e -> !e.isInPosition(row, col)).filter(s -> s.isInCol(col))
                .noneMatch(s -> s.getNumber() == n);
    }


    public SudokuSquare getMapAt(int i, int j) {
        return sudokuSquares.get(i * MAP_N_SQUARED + j);
    }


//    public Region getNumberBoard() {
//        return numberBoard;
//    }


    private void removeRandomNumbers() {
        List<SudokuSquare> sudokuSquares2 = sudokuSquares.stream().filter(SudokuSquare::isNotEmpty)
                .collect(Collectors.toList());
        SudokuSquare sudokuSquare = sudokuSquares2.get(random.nextInt(sudokuSquares2.size()));
        int previousN = sudokuSquare.setEmpty();
        List<Integer> possibleNumbers = IntStream.rangeClosed(1, MAP_N_SQUARED)
                .filter(n -> isNumberFit(sudokuSquare, n)).boxed().collect(Collectors.toList());
        if (possibleNumbers.size() == 1) {
            updatePossibilities();
            sudokuSquare.setPermanent(false);
            return;
        }
        sudokuSquare.setNumber(previousN);
    }

    private void updatePossibilities() {
        sudokuSquares.stream().forEach(sq -> sq.setPossibilities(IntStream
                .rangeClosed(1, MAP_N_SQUARED).filter(n -> isNumberFit(sq, n)).boxed().collect(Collectors.toList())));
        sudokuSquares.stream()
                .forEach(sq -> sq.setWrong(!sq.isEmpty() && !sq.getPossibilities().contains(sq.getNumber())));
    }

    public void handleMousePressed(MotionEvent ev) {
        Optional<SudokuSquare> pressed = sudokuSquares.stream()
                .filter(e -> !e.isPermanent())
                .filter(s -> s.contains(ev.getX(), ev.getY())).findFirst();
        if (!pressed.isPresent()) {
            pressedSquare = null;
            return;
        }
        pressedSquare = pressed.get();
        RectF boundsInParent = pressedSquare.getBounds();
        int halfTheSize = MAP_N_SQUARED / 2;
        double maxY = pressedSquare.getCol() > halfTheSize ? boundsInParent.top - SudokuSquare.SQUARE_SIZE * MAP_NUMBER
                : boundsInParent.bottom;
        double maxX = pressedSquare.getRow() > halfTheSize ? boundsInParent.left - SudokuSquare.SQUARE_SIZE * MAP_NUMBER
                : boundsInParent.right;
//        numberBoard.setPadding(new Insets(maxY, 0, 0, maxX));
//        numberBoard.setVisible(true);
        handleMouseMoved(ev);
    }

    public void handleMouseMoved(MotionEvent s) {
//        numberOptions.forEach(e -> e.setOver(e.contains(s.getX(), s.getY())));
    }

    public void handleMouseReleased(MotionEvent s) {
//        Optional<NumberButton> findFirst = numberOptions.stream()
//                .filter(e -> e.getBoundsInParent().contains(s.getX(), s.getY())).findFirst();
//        if (pressedSquare != null && findFirst.isPresent()) {
//            NumberButton node = findFirst.get();
//            pressedSquare.setNumber(node.getNumber());
//            updatePossibilities();
//            pressedSquare = null;
//        }
//        numberBoard.setVisible(false);
//        if (sudokuSquares.stream().allMatch(e -> !e.isEmpty() && !e.isWrong())) {
//            showDialogWinning();
//        }

    }

    private void showDialogWinning() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.you_win);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(R.string.game_over);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            this.reset();
            invalidate();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        invalidate();
    }

    private void reset() {
        createRandomNumbers();
        for (int i = 0; i < MAP_N_SQUARED * MAP_N_SQUARED; i++) {
            removeRandomNumbers();
        }
    }


    private boolean isNumberFit(SudokuSquare sudokuSquare, int n) {
        return isNumberFit(n, sudokuSquare.getRow(), sudokuSquare.getCol());
    }

}

final class NumberButton {

    private final int number;
    private boolean over = (false);

    public NumberButton(int i) {
        this.number = i;
//        styleProperty().bind(Bindings.when(over).then("-fx-background-color: white;")
//                .otherwise("-fx-background-color: lightgray;"));
//        setEffect(new InnerShadow());
//        Text text = new Text(i == 0 ? "X" : Integer.toString(i));
//        text.wrappingWidthProperty().bind(widthProperty());
//        text.setTextOrigin(VPos.CENTER);
//        text.layoutYProperty().bind(heightProperty().divide(2));
//        text.setTextAlignment(TextAlignment.CENTER);
//        getChildren().add(text);
//        setPrefSize(30, 30);

    }


    public void setOver(boolean over) {
        this.over = (over);
    }

    public int getNumber() {
        return number;
    }
}