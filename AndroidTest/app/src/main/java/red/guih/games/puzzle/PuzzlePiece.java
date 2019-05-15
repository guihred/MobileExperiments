package red.guih.games.puzzle;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

public class PuzzlePiece {
    static final float SQRT_0_5 = 0.7071067811865476F;// SQRT(0.5)
    private final Matrix translate = new Matrix();
    private final Path translated = new Path();
    private final Matrix mTranslationMatrix = new Matrix();
    private PuzzlePath down = PuzzlePath.STRAIGHT;
    private Bitmap image;
    private BitmapShader imagePattern;
    private PuzzlePath left = PuzzlePath.STRAIGHT;
    private Path path;
    private Paint paint;
    private Paint stroke;
    private PuzzlePath right = PuzzlePath.STRAIGHT;
    private PuzzlePath up = PuzzlePath.STRAIGHT;
    private float width;
    private float height;
    private int x;
    private int y;
    private float layoutX;
    private float layoutY;

    PuzzlePiece(int x, int y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "PuzzlePiece{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void move(Point2D subtract) {
        setLayoutX(getLayoutX() + subtract.getX());
        setLayoutY(getLayoutY() + subtract.getY());
    }

    public float getLayoutX() {
        return layoutX;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    void move(float x, float y) {
        setLayoutX(getLayoutX() + x);
        setLayoutY(getLayoutY() + y);
    }

    public PuzzlePath getLeft() {
        return left;
    }

    public void setLeft(PuzzlePath left) {
        this.left = left;
    }

    public PuzzlePath getRight() {
        return right;
    }

    public void setRight(PuzzlePath right) {
        this.right = right;
    }

    void setDown(PuzzlePath down) {
        this.down = down;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        getPaint().setShader(getImagePattern());
    }

    public Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);

        }
        return paint;
    }

    private BitmapShader getImagePattern() {
        if (imagePattern == null) {

            imagePattern = new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

            imagePattern.setLocalMatrix(mTranslationMatrix);
        }

        return imagePattern;
    }

    public void setUp(PuzzlePath up) {
        this.up = up;
    }

    public void draw(Canvas canvas) {

        Path translatedPath = getTranslatedPath();
        canvas.drawPath(translatedPath, getPaint());
        canvas.drawPath(translatedPath, getStroke());
    }

    Path getTranslatedPath() {
        mTranslationMatrix.setTranslate(getLayoutX() - x * width, getLayoutY() - y * height);
        imagePattern.setLocalMatrix(mTranslationMatrix);
        translate.setTranslate(layoutX, layoutY);
        getPath().transform(translate, translated);
        return translated;
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

    public Path getPath() {
        if (path == null) {
            path = new Path();
            path.moveTo(0, 0);
            up.getPath(width, 0, path);
            right.getPath(0, height, path);
            down.getPath(-width, 0, path);
            left.getPath(0, -height, path);
        }
        return path;
    }
}
