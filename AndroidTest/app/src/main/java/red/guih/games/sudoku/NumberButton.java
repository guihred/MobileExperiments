package red.guih.games.sudoku;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public final class NumberButton {

    public static final int TEXT_SIZE = 60;
    private final int number;
    private boolean over;

    private final Paint paint = new Paint();
    private final Paint black = new Paint();

    public NumberButton(int i) {
        this.number = i;
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setTextSize(TEXT_SIZE);
    }

    RectF bounds = new RectF();

    public void draw(Canvas canvas, float x, float y) {
        paint.setColor(over ? Color.LTGRAY : Color.WHITE);
        int n = number - 1;
        float left = number == 0 ? SudokuSquare.SQUARE_SIZE * SudokuView.MAP_NUMBER :
                n % SudokuView.MAP_NUMBER * SudokuSquare.SQUARE_SIZE;
        float top = number == 0 ? 0 : n / SudokuView.MAP_NUMBER * SudokuSquare.SQUARE_SIZE;

        bounds.set(x + left, y + top, x + left + SudokuSquare.SQUARE_SIZE,
                y + top + SudokuSquare.SQUARE_SIZE);

        canvas.drawRoundRect(bounds, 5, 5, paint);
        black.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(bounds, 5, 5, black);
        black.setStyle(Paint.Style.FILL);
        if (number == 0) {
            canvas.drawText("X", bounds.centerX(), bounds.centerY(), black);
        } else {
            canvas.drawText(Integer.toString(number), bounds.centerX(), bounds.centerY(), black);
        }
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public int getNumber() {
        return number;
    }
}
