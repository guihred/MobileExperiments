package red.guih.games.pacman;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Collection;

public class Pacman  {

    public static final float PACMAN_RATIO = 0.5f;
    private float startAngle = 0;
    private float length = 0;
    private ObjectAnimator eatingAnimation;
    private Paint paint = new Paint(Color.YELLOW);
    private float x, y;
    private PacmanDirection direction = PacmanDirection.RIGHT;
    private RectF bounds = new RectF();

    public Pacman(PacmanView pacmanView) {
        paint.setColor(Color.YELLOW);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("startAngle", Keyframe.ofFloat(0, 0), Keyframe.ofFloat(1, 45));
        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("length", Keyframe.ofFloat(0, 360), Keyframe.ofFloat(1, 270));
        eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation, pvhRotation2);
        eatingAnimation.setDuration(1000);
        eatingAnimation.addUpdateListener(animation -> pacmanView.invalidate());
        eatingAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        eatingAnimation.setRepeatMode(ValueAnimator.REVERSE);
        eatingAnimation.start();

    }

    public void draw(Canvas canvas) {
        canvas.drawArc(x, y, x + getPacmanWidth(), y + getPacmanWidth(), (direction == null ? 0 : direction.angle) + getStartAngle(), getLength(), true, paint);
    }

    private float getPacmanWidth() {
        return MazeSquare.SQUARE_SIZE * PACMAN_RATIO;
    }

    public RectF getBounds() {
        bounds.set(x, y, x + getPacmanWidth(), y + getPacmanWidth());
        return bounds;
    }

    private boolean checkCollision(Collection<RectF> observableList) {
        return observableList.parallelStream()
                .anyMatch(p -> RectF.intersects(p, getBounds()));
    }

    public void move(Collection<RectF> walls) {
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
        this.direction = direction;
    }

    public void die() {
        if (eatingAnimation.isRunning()) {
            turn(null);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

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

    public enum PacmanDirection {
        DOWN(90),
        LEFT(180),
        RIGHT(0),
        UP(270);

        private final int angle;

        PacmanDirection(int angle) {
            this.angle = angle;
        }
    }
}
