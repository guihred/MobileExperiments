/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author Note
 */
public class Square2048 {
    public static float SQUARE_SIZE = 50;
    private int number = 0;
    private final int i;
    private final int j;
    private Paint paint = new Paint();

    private float layoutX, layoutY;

    public Square2048(int i, int j) {
        this.i = i;
        this.j = j;
        paint.setColor(Color.BLACK);
        paint.setTextSize(SQUARE_SIZE / 4);
        paint.setStyle(Paint.Style.STROKE);
    }

    public static void setSquareSize(float size) {
        Square2048.SQUARE_SIZE = size;
    }

    void draw(Canvas canvas) {
        canvas.drawRoundRect(i * SQUARE_SIZE + 10, j * SQUARE_SIZE + 10, (i + 1) * SQUARE_SIZE - 10, (j + 1) * SQUARE_SIZE - 10, 20, 20, paint);
        if (number > 0) {
            canvas.drawText("" + number, i * SQUARE_SIZE + SQUARE_SIZE / 2 - 10 + layoutX, j * SQUARE_SIZE + SQUARE_SIZE / 2 - 10 + layoutY, paint);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square2048)) return false;

        Square2048 that = (Square2048) o;

        if (i != that.i) return false;
        return j == that.j;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        return result;
    }

    public void setNumber(int value) {
        number = value;
    }

    public boolean isEmpty() {
        return number == 0;
    }

    public int getNumber() {
        return number;
    }

    public float getLayoutX() {
        return layoutX;
    }

    public float getX() {
        return i * SQUARE_SIZE;
    }

    public float getY() {
        return j * SQUARE_SIZE;
    }

    @Override
    public String toString() {
        return "Square2048{" +
                "number=" + number +
                ", i=" + i +
                ", j=" + j +
                '}';
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }
}