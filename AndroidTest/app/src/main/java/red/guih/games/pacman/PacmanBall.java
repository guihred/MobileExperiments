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

    float radius = 10;
    float x, y;
    ObjectAnimator objectAnimator = new ObjectAnimator();
    Paint paint = new Paint();
    RectF bounds = new RectF(-radius + x, -radius + y, radius + x, radius + y);
    private Boolean special = false;

    public PacmanBall(Double x, Double y) {
        this.x = x.floatValue();
        this.y = y.floatValue();
        paint.setColor(Color.BLUE);

    }

    protected void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public final boolean isSpecial() {
        return special;
    }

    public final void setSpecial() {

        this.special = true;
        objectAnimator.setDuration(250);
        PropertyValuesHolder radius1 = PropertyValuesHolder
                .ofKeyframe("radius", Keyframe.ofFloat(0, 10),
                        Keyframe.ofFloat(1, MazeSquare.SQUARE_SIZE / 4));
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
        return "PacmanBall{" +
                "special=" + special +
                ", radius=" + radius +
                ", x=" + x +
                ", y=" + y +
                ", bounds=" + bounds +
                '}';
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
