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
    public static final float SQRT_2 = (float) Math.sqrt(0.5);
    private PuzzlePath down = PuzzlePath.STRAIGHT;
    private Bitmap image;
    private BitmapShader imagePattern;
    private PuzzlePath left = PuzzlePath.STRAIGHT;
    private Path path;
    private Paint paint;
    private Paint stroke;
    private PuzzlePath right = PuzzlePath.STRAIGHT;
    private PuzzlePath up = PuzzlePath.STRAIGHT;
    private float width = 50, height = 50;
    private int x = 0, y = 0;
    private float layoutX = 0, layoutY = 0;
    private Matrix translate = new Matrix();
    private Path translated = new Path();
    private Matrix mTranslationMatrix = new Matrix();

    public PuzzlePiece(int x, int y, float width, float height) {
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

    public void move(Point3D subtract) {
        setLayoutX(getLayoutX() + subtract.getX());
        setLayoutY(getLayoutY() + subtract.getY());
    }

    public void move(float x, float y) {
        setLayoutX(getLayoutX() + x);
        setLayoutY(getLayoutY() + y);
    }


    public BitmapShader getImagePattern() {
        if (imagePattern == null) {

            imagePattern = new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

            imagePattern.setLocalMatrix(mTranslationMatrix);
        }

        return imagePattern;
    }

    public Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
            //Assign the 'fillBMPshader' to this paint
        }
        return paint;
    }

    public Paint getStroke() {
        if (stroke == null) {
            stroke = new Paint();
            stroke.setColor(Color.BLACK);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeWidth(1);
            //Assign the 'fillBMPshader' to this paint
        }
        return stroke;
    }


    public PuzzlePath getDown() {
        return down;
    }

    public PuzzlePath getLeft() {
        return left;
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

    public PuzzlePath getRight() {
        return right;
    }

    public PuzzlePath getUp() {
        return up;
    }

    public void setDown(PuzzlePath down) {
        this.down = down;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setLeft(PuzzlePath left) {
        this.left = left;
    }

    public void setRight(PuzzlePath right) {
        this.right = right;
    }

    public void setUp(PuzzlePath up) {
        this.up = up;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    public float getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    public void setScale(float scalex) {
        getPaint().setShader(getImagePattern());
    }


    public void draw(Canvas canvas) {

        Path translatedPath = getTranslatedPath();
        canvas.drawPath(translatedPath, getPaint());
        canvas.drawPath(translatedPath, getStroke());
    }

    public Path getTranslatedPath() {
        mTranslationMatrix.setTranslate(getLayoutX()-x * width, getLayoutY()-y * height);
        imagePattern.setLocalMatrix(mTranslationMatrix);
        translate.setTranslate(layoutX, layoutY);
        getPath().transform(translate, translated);
        return translated;
    }
}
