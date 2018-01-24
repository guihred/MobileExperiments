package red.guih.games.pacman;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.List;

public class Pacman extends View {

    float startAngle = 0;
    float length = 0;
    private Paint paint = new Paint(Color.YELLOW);

    private float x, y;

    public enum PacmanDirection {
        DOWN(90),
        LEFT(180),
        RIGHT(0),
        UP(270);

        private final int angle;

        PacmanDirection(int angle) {
            this.angle = angle;
        }

        public int getAngle() {
            return angle;
        }

    }

    private PacmanDirection direction = PacmanDirection.RIGHT;
    ObjectAnimator eatingAnimation;

    public Pacman(Context c, PacmanView pacmanView) {
        super(c);
        paint.setColor(Color.YELLOW);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("startAngle", Keyframe.ofFloat(0, 45), Keyframe.ofFloat(1, 0));
        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("length", Keyframe.ofFloat(0, 270), Keyframe.ofFloat(1, 360));
        eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation, pvhRotation2);
        eatingAnimation.setDuration(1000);
        eatingAnimation.addUpdateListener(animation -> pacmanView.invalidate());
        eatingAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        eatingAnimation.setRepeatMode(ValueAnimator.REVERSE);
        eatingAnimation.start();

    }

    public void onDraw(Canvas canvas) {
        canvas.drawArc(x, y, x + MazeSquare.SQUARE_SIZE * 0.5f, y + MazeSquare.SQUARE_SIZE * 0.5f, (direction == null ? 0 : direction.angle) + startAngle, length, true, paint);
    }

    private RectF bounds = new RectF();

    public RectF getBounds() {
        bounds.set(x, y, x + MazeSquare.SQUARE_SIZE * 0.5f, y + MazeSquare.SQUARE_SIZE * 0.5f);
        return bounds;
    }


    private boolean checkCollision(List<RectF> observableList) {
        return observableList.parallelStream()
                .anyMatch(p -> RectF.intersects(p, getBounds()));
    }

    public void move(List<RectF> walls) {
        if (direction == null) {
            return;
        }

        int step = 10;
        switch (direction) {
            case RIGHT:
                x += step;
                if (checkCollision(walls)) {
                    x -= step;
                }
                break;
            case UP:
                y -= step;

                if (checkCollision(walls)) {
                    y += step;

                }
                break;
            case DOWN:
                y += step;
                if (checkCollision(walls)) {
                    y -= step;
                }
                break;
            case LEFT:
                x -= step;
                if (checkCollision(walls)) {
                    x += step;
                }
                break;
            default:
                break;
        }
    }

    public void turn(PacmanDirection direction) {
        if (eatingAnimation.isRunning()) {
            this.direction = direction;
        }
    }

    public void die() {
        if (eatingAnimation.isRunning()) {
            turn(null);

        }

    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }
}
