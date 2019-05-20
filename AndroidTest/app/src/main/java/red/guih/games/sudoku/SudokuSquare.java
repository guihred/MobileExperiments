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
    private static final Paint RED = new Paint(Color.RED);
    private static final Paint WHITE = new Paint(Color.WHITE);
    private static final Paint GRAY = new Paint(Color.LTGRAY);
    static float layoutY;
    private static int squareSize;
    private final int row;
    private final int col;
    private int number;
    private List<Integer> possibilities = new ArrayList<>();
    private boolean permanent = true;
    private boolean wrong;
    private RectF rect;

    SudokuSquare(int i, int j) {
        row = i;
        col = j;
    }

    public static int getSquareSize() {
        return squareSize;
    }

    static void setSquareSize(int squareSize, float layoutY) {
        SudokuSquare.squareSize = squareSize;
        SudokuSquare.layoutY = layoutY;
        BLACK.setStyle(Paint.Style.STROKE);
        BLACK.setColor(Color.BLACK);
        BLACK.setStrokeWidth(1);
        BLACK.setTextSize(NumberButton.TEXT_SIZE);
        BLACK.setTextAlign(Paint.Align.CENTER);
        RED.setStyle(Paint.Style.FILL);
        RED.setColor(Color.RED);
        RED.setStrokeWidth(1);
        RED.setTextSize(NumberButton.TEXT_SIZE);
        RED.setTextAlign(Paint.Align.CENTER);
        WHITE.setStyle(Paint.Style.FILL);
        WHITE.setColor(Color.WHITE);
        GRAY.setStyle(Paint.Style.FILL);
        GRAY.setColor(Color.GRAY);
    }

    public void draw(Canvas canvas,boolean showN) {
        float size = SudokuSquare.squareSize;
        float top = col * size + layoutY;
        if (permanent) {
            canvas.drawRect(row * size, top, row * size + size,
                    top + size, GRAY);
        } else {
            canvas.drawRect(row * size, top, row * size + size,
                    top + size, WHITE);
        }

        if (!isEmpty()&&showN) {
            BLACK.setStyle(Paint.Style.FILL);
            canvas.drawText(Integer.toString(number), row * size + size / 2F,
                    top + size / 2F + NumberButton.TEXT_SIZE * 2F / 5, wrong ? RED : BLACK);
        }
        BLACK.setStyle(Paint.Style.STROKE);
        canvas.drawRect(row * size, top, row * size + size, top + size,
                BLACK);
    }

    public boolean isEmpty() {
        return number == 0;
    }

    public boolean contains(float x, float y) {
        return getBounds().contains(x, y);
    }

    public RectF getBounds() {
        if (rect == null) {
            float size = SudokuSquare.squareSize;
            rect = new RectF(row * size, col * size + layoutY,
                    row * size + size, col * size + size + layoutY);
        }
        return rect;
    }

    boolean isNotPermanent() {
        return !permanent;
    }

    void setPermanent(boolean permanent) {
        this.permanent = permanent;

    }

    List<Integer> getPossibilities() {
        return possibilities;
    }

    void setPossibilities(List<Integer> possibilities) {
        this.possibilities = (possibilities);
    }

    int setEmpty() {
        int k = number;
        number = (0);
        return k;
    }

    boolean isInCol(int col1) {
        return col == col1;
    }

    boolean isInArea(int row1, int col1) {
        return row / SudokuView.MAP_NUMBER == row1 / SudokuView.MAP_NUMBER
                && col / SudokuView.MAP_NUMBER == col1 / SudokuView.MAP_NUMBER;
    }

    boolean isInRow(int row1) {
        return row == row1;
    }

    boolean isNotInPosition(int row1, int col1) {
        return row != row1 || col1 != col;
    }

    boolean isNotEmpty() {
        return !isEmpty();
    }

    public int getNumber() {
        return number;

    }

    public void setNumber(int value) {
        number = (value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj == null || !this.getClass().isInstance(obj)) {
            return false;
        }
        return ((SudokuSquare) obj).getRow() == getRow() &&
                ((SudokuSquare) obj).getCol() == getCol();
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    boolean isWrong() {
        return wrong;
    }

    void setWrong(boolean wrong) {
        this.wrong = (wrong);
    }
}
