package red.guih.games.pacman;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.List;
@SuppressWarnings("unused")
public class Pacman {

    private static final float PACMAN_RATIO = 0.5F;
    private final ObjectAnimator eatingAnimation;
    private final RectF bounds = new RectF();
    Paint paint = new Paint(Color.YELLOW);
    private float startAngle;
    private float length;
    private float x;
    private float y;
    private PacmanDirection direction = PacmanDirection.RIGHT;

    Pacman(PacmanView pacmanView) {
        paint.setColor(Color.YELLOW);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder
                .ofKeyframe("startAngle", Keyframe.ofFloat(0, 0),
                        Keyframe.ofFloat(1, PacmanView.START_ANGLE));
        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder
                .ofKeyframe("length", Keyframe.ofFloat(0, 360),
                        Keyframe.ofFloat(1, PacmanView.SWEEP_ANGLE));
        eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation, pvhRotation2);
        eatingAnimation.setDuration(500);
        eatingAnimation.addUpdateListener(animation -> pacmanView.invalidate());
        eatingAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        eatingAnimation.setRepeatMode(ValueAnimator.REVERSE);
        eatingAnimation.start();

    }

    public void draw(Canvas canvas) {
        canvas.drawArc(x, y, x + getPacmanWidth(), y + getPacmanWidth(),
                (direction == null ? 0 : direction.angle) + getStartAngle(), getLength(), true,
                paint);
    }

    float getPacmanWidth() {
        return MazeSquare.squareSize * PACMAN_RATIO;
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

    void move(List<RectF> walls) {
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

    private boolean checkCollision(List<RectF> observableList) {
        for (int i = 0; i < observableList.size(); i++) {
            RectF p = observableList.get(i);
            if (RectF.intersects(p, getBounds())) {
                return true;
            }
        }
        return false;
    }

    public RectF getBounds() {
        bounds.set(x, y, x + getPacmanWidth(), y + getPacmanWidth());
        return bounds;
    }

    void die() {
        if (eatingAnimation.isRunning()) {
            turn(null);
        }
    }

    void turn(PacmanDirection direction) {
        Log.i("PACMAN", "TURNED " + direction);

        this.direction = direction;
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

}
