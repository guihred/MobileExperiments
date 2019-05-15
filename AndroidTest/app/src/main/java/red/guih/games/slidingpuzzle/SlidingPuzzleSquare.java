package red.guih.games.slidingpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

public final class SlidingPuzzleSquare {


    private final int number;
    private Paint paint = new Paint();
    private Paint stroke;
    private Bitmap image;
    private BitmapShader imagePattern;
    private Rect src = new Rect();
    private RectF dst = new RectF();
    private Matrix mTranslationMatrix = new Matrix();

    SlidingPuzzleSquare(int number, int i, int j, int squareSize) {
        this.number = number;
        paint.setTextSize(squareSize / 2F);
        src.set(j * squareSize, i * squareSize, (j + 1) * squareSize, (i + 1) * squareSize);
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == SlidingPuzzleSquare.class &&
                ((SlidingPuzzleSquare) obj).number == number;

    }

    public void draw(Canvas canvas, int i, int j, int squareSize) {
        float size = (float) squareSize;
        if (image == null) {
            drawSquare(canvas, i, j, size);
            return;
        }

        dst.set(j * size, i * size, (j + 1) * size, (i + 1) * size);
        if (!isEmpty()) {
            canvas.drawBitmap(image, src, dst, getPaint());
        }
        canvas.drawRect(dst, getStroke());
    }

    void drawSquare(Canvas canvas, int i, int j, float squareSize) {
        canvas.drawRect(j * squareSize, i * squareSize, (j + 1) * squareSize, (i + 1) * squareSize,
                getStroke());
        if (!this.isEmpty()) {
            String text = "" + this.getNumber();
            canvas.drawText(text, j * squareSize + squareSize / 3 / text.length(),
                    i * squareSize + squareSize * 2 / 3, getPaint());
        }
    }

    public boolean isEmpty() {
        return number == SlidingPuzzleView.mapWidth * SlidingPuzzleView.mapHeight;
    }

    public Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);

        }
        return paint;
    }

    public Paint getStroke() {
        if (stroke == null) {
            stroke = new Paint();
            stroke.setColor(Color.BLACK);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeWidth(1);

        }
        return stroke;
    }

    public Integer getNumber() {
        return number;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        if (image != null) {
            getPaint().setShader(getImagePattern());
        }
    }

    private BitmapShader getImagePattern() {
        if (imagePattern == null) {

            imagePattern = new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

            imagePattern.setLocalMatrix(mTranslationMatrix);
        }

        return imagePattern;
    }


}
