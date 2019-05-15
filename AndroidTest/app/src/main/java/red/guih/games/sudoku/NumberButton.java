package red.guih.games.sudoku;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public final class NumberButton {

    static final int TEXT_SIZE = 60;
    private final int number;
    private final Paint paint = new Paint();
    private final Paint black = new Paint();
    private RectF bounds = new RectF();
    private boolean over;

    NumberButton(int i) {
        this.number = i;
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setTextSize(TEXT_SIZE);
    }

    public void draw(Canvas canvas, float x, float y) {
        paint.setColor(over ? Color.LTGRAY : Color.WHITE);
        float n = number - 1F;
        float left = number == 0 ? SudokuSquare.getSquareSize() * SudokuView.MAP_NUMBER :
                n % SudokuView.MAP_NUMBER * SudokuSquare.getSquareSize();
        float top = number == 0 ? 0 :
                (float) Math.floor(n / SudokuView.MAP_NUMBER) * SudokuSquare.getSquareSize();

        bounds.set(x + left, y + top, x + left + SudokuSquare.getSquareSize(),
                y + top + SudokuSquare.getSquareSize());

        canvas.drawRoundRect(bounds, 5, 5, paint);
        black.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(bounds, 5, 5, black);
        black.setStyle(Paint.Style.FILL);
        float v = bounds.width() / 3;
        float x1 = bounds.left + v;
        float y1 = bounds.top + bounds.width() * 2 / 3;
        if (number == 0) {
            canvas.drawText("X", x1, y1, black);
        } else {
            canvas.drawText(Integer.toString(number), x1, y1, black);
        }
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    void setOver(boolean over) {
        this.over = over;
    }

    public int getNumber() {
        return number;
    }
}
