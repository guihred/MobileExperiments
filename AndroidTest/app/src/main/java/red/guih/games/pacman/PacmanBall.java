package red.guih.games.pacman;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PacmanBall {

    private static final int DURATION = 250;
    private float radius = 10;
    private float x;
    private float y;
    private ObjectAnimator objectAnimator = new ObjectAnimator();
    private Paint paint = new Paint();
    private RectF bounds = new RectF(-radius + x, -radius + y, radius + x, radius + y);
    private Boolean special = false;

    PacmanBall(Double x, Double y) {
        this.x = x.floatValue();
        this.y = y.floatValue();
        paint.setColor(Color.BLUE);

    }

    protected void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    final boolean isSpecial() {
        return special;
    }

    final void setSpecial() {

        this.special = true;
        objectAnimator.setDuration(DURATION);
        PropertyValuesHolder radius1 = PropertyValuesHolder
                .ofKeyframe("radius", Keyframe.ofFloat(0, 10),
                        Keyframe.ofFloat(1, MazeSquare.squareSize / 4F));
        objectAnimator.setValues(radius1);
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setTarget(this);
        objectAnimator.start();


    }

    RectF getBounds() {
        bounds.set(-radius + x, -radius + y, radius + x, radius + y);

        return bounds;
    }

    @Override
    public String toString() {
        return String
                .format("PacmanBall{special=%s, radius=%s, x=%s, y=%s, bounds=%s}", special, radius,
                        x, y, bounds);
    }

    @SuppressWarnings("unused")
    public float getRadius() {
        return radius;
    }

    @SuppressWarnings("unused")
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
