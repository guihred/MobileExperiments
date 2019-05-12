package red.guih.games.sudoku;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SudokuSquare {

    public static final Paint BLACK = new Paint(Color.BLACK);
    public static final Paint RED = new Paint(Color.RED);
    public static final Paint WHITE = new Paint(Color.WHITE);
    public static final Paint GRAY = new Paint(Color.LTGRAY);

    public static int SQUARE_SIZE;
    public static float LAYOUT_Y;


    private int number = (0);
    private final int row;
    private final int col;
    private List<Integer> possibilities = new ArrayList<>();
    private boolean permanent = true;
    private boolean wrong = (false);
    private RectF rect;

    public SudokuSquare(int i, int j) {
        row = i;
        col = j;
//
    }

    public void draw(Canvas canvas) {
        float top = col * SQUARE_SIZE + LAYOUT_Y;
        if (permanent) {
            canvas.drawRect(row * SQUARE_SIZE, top, row * SQUARE_SIZE + SQUARE_SIZE,
                    top + SQUARE_SIZE, GRAY);
        } else {
            canvas.drawRect(row * SQUARE_SIZE, top, row * SQUARE_SIZE + SQUARE_SIZE,
                    top + SQUARE_SIZE, WHITE);
        }

        if (!isEmpty()) {
            BLACK.setStyle(Paint.Style.FILL);
            canvas.drawText(Integer.toString(number), row * SQUARE_SIZE + SQUARE_SIZE / 2,
                    top + SQUARE_SIZE / 2 + NumberButton.TEXT_SIZE * 2 / 5, wrong ? RED : BLACK);
        }
        BLACK.setStyle(Paint.Style.STROKE);
        canvas.drawRect(row * SQUARE_SIZE, top, row * SQUARE_SIZE + SQUARE_SIZE, top + SQUARE_SIZE,
                BLACK);
    }

    public boolean contains(float x, float y) {
        return getBounds().contains(x, y);
    }

    public RectF getBounds() {
        if (rect == null) {
            rect = new RectF(row * SQUARE_SIZE, col * SQUARE_SIZE + LAYOUT_Y,
                    row * SQUARE_SIZE + SQUARE_SIZE, col * SQUARE_SIZE + SQUARE_SIZE + LAYOUT_Y);
        }
        return rect;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;

    }

    public void setPossibilities(List<Integer> possibilities) {
        this.possibilities = (possibilities);
    }

    public List<Integer> getPossibilities() {
        return possibilities;
    }

    public void setNumber(int value) {
        number = (value);
    }

    public int setEmpty() {
        int k = number;
        number = (0);
        return k;
    }

    public boolean isInCol(int col1) {
        return col == col1;
    }

    public boolean isInArea(int row1, int col1) {
        return row / SudokuView.MAP_NUMBER == row1 / SudokuView.MAP_NUMBER
                && col / SudokuView.MAP_NUMBER == col1 / SudokuView.MAP_NUMBER;
    }

    public boolean isInRow(int row1) {
        return row == row1;
    }

    public boolean isInPosition(int row1, int col1) {
        return row == row1 && col1 == col;
    }

    public boolean isEmpty() {
        return number == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public int getNumber() {
        return number;

    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return true;
        }
        return ((SudokuSquare) obj).getRow() == getRow() &&
                ((SudokuSquare) obj).getCol() == getCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isWrong() {
        return wrong;
    }

    public void setWrong(boolean wrong) {
        this.wrong = (wrong);
    }

    public static void setSquareSize(int squareSize, float layoutY) {
        SQUARE_SIZE = squareSize;
        SudokuSquare.LAYOUT_Y = layoutY;
        BLACK.setStyle(Paint.Style.STROKE);
        BLACK.setColor(Color.BLACK);
        BLACK.setStrokeWidth(1);
        BLACK.setTextSize(NumberButton.TEXT_SIZE);
        BLACK.setTextAlign(Paint.Align.CENTER);
        RED.setStyle(Paint.Style.FILL);
        RED.setColor(Color.RED);
        RED.setTextSize(NumberButton.TEXT_SIZE);
        WHITE.setStyle(Paint.Style.FILL);
        WHITE.setColor(Color.WHITE);
        GRAY.setStyle(Paint.Style.FILL);
        GRAY.setColor(Color.GRAY);
    }
}
