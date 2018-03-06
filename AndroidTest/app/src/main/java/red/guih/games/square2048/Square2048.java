/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Note
 */
public class Square2048 {
    public static float SQUARE_SIZE = 50;
    private static int PADDING;
    private int number = 0;
    private final int i;
    private final int j;
    private Paint paint = new Paint();
    private Paint color = new Paint();
    private Paint opposite = new Paint();

    private static List<Integer> colors = Arrays.asList(0, 0xFFff0000, 0xFFff8000, 0xFFffff00, 0xFF80ff00, 0xFF00ff00, 0xFF00ff80, 0xFF00ffff, 0xFF0008ff, 0xFF0000ff, 0xFF8000ff, 0xFFff00ff);

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
        canvas.drawRoundRect(i * SQUARE_SIZE + 10, j * SQUARE_SIZE + 10 + PADDING, (i + 1) * SQUARE_SIZE - 10, (j + 1) * SQUARE_SIZE - 10 + PADDING, 20, 20, paint);
        if (number > 0) {
//            Color.
            String text = "" + number;
            canvas.drawRoundRect(i * SQUARE_SIZE + 10 + layoutX, j * SQUARE_SIZE + 10 + PADDING + layoutY, (i + 1) * SQUARE_SIZE - 10 + layoutX, (j + 1) * SQUARE_SIZE - 10 + PADDING + layoutY, 20, 20, color);
            canvas.drawText(text, i * SQUARE_SIZE + SQUARE_SIZE / 2 - 20 * text.length() - 10 + layoutX, j * SQUARE_SIZE + SQUARE_SIZE / 2 + layoutY + 10 + PADDING, paint);
        }
    }

    int opposite() {
//          1. Convert your colour to HSL.
        float[] hsv = new float[3];
        Color.colorToHSV(color.getColor(), hsv);
//            2. Change the Hue value to that of the Hue opposite (e.g., if your Hue is 50°, the opposite one will be at 230° on the wheel — 180° further around).

        Log.i("SQUARE2048", "" + hsv[0]);
//            4. Convert this new HSL value back to your original colour notation (RGB or whatever).
        hsv[0] = +180;
        hsv[0] %= 360;
        return Color.HSVToColor(hsv) & 0x80FFFFFF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square2048)) return false;
        Square2048 that = (Square2048) o;
        return i == that.i && j == that.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    public void setNumber(int value) {

        number = value;

        if (number > 0) {
            int v = (int) (Math.log(number) / Math.log(2));
            Integer color = colors.get(v % colors.size());
            this.color.setColor(color);
            opposite.setColor(opposite());
        }

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

    public static void setPadding(int i) {
        PADDING = i;
    }
}