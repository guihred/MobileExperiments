/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Note
 */
public class Square2048 {
    private static final List<Integer> COLORS =
            Arrays.asList(0, 0xFFff0000, 0xFFff8000, 0xFFffff00, 0xFF80ff00, 0xFF00ff00, 0xFF00ff80,
                    0xFF00ffff, 0xFF0080ff, 0xFF0000ff, 0xFF8000ff, 0xFFff00ff);
    private static float squareSize = 50;
    private static int padding;
    private final int i;
    private final int j;
    private final Paint paint = new Paint();
    private final Paint color = new Paint();
    private int number;
    private float layoutX;
    private float layoutY;

    Square2048(int i, int j) {
        this.i = i;
        this.j = j;
        paint.setColor(Color.BLACK);
        paint.setTextSize(squareSize / 4);
        paint.setStyle(Paint.Style.STROKE);
    }

    static void setSquareSize(float size) {
        Square2048.squareSize = size;
    }

    static void setPadding(int i) {
        padding = i;
    }

    void draw(Canvas canvas) {
        canvas.drawRoundRect(i * squareSize + 10, j * squareSize + 10 + padding,
                (i + 1) * squareSize - 10, (j + 1) * squareSize - 10 + padding, 20, 20, paint);
        if (number > 0) {
//            Color.
            String text = "" + number;
            canvas.drawRoundRect(i * squareSize + 10 + layoutX,
                    j * squareSize + 10 + padding + layoutY, (i + 1) * squareSize - 10 + layoutX,
                    (j + 1) * squareSize - 10 + padding + layoutY, 20, 20, color);
            canvas.drawText(text,
                    i * squareSize + squareSize / 2 - 20 * text.length() - 10 + layoutX,
                    j * squareSize + squareSize / 2 + layoutY + 10 + padding, paint);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !this.getClass().isInstance(o)) {
            return false;
        }
        Square2048 that = (Square2048) o;
        return i == that.i && j == that.j;
    }

    @Override
    public String toString() {
        return "Square2048{" +
                "number=" + number +
                ", i=" + i +
                ", j=" + j +
                '}';
    }

    public boolean isEmpty() {
        return number == 0;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int value) {

        number = value;

        if (number > 0) {
            int v = (int) (Math.log(number) / Math.log(2));
            Integer color1 = COLORS.get(v % COLORS.size());
            this.color.setColor(color1);
        }

    }

    public float getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    public float getX() {
        return i * squareSize;
    }

    public float getY() {
        return j * squareSize;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }
}