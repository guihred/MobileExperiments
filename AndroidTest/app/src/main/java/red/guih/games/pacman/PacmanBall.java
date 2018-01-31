package red.guih.games.pacman;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

@SuppressLint("ViewConstructor")
public class PacmanBall extends View {

    float radius = 10;
    float x, y;
    ObjectAnimator objectAnimator = new ObjectAnimator();
    Paint paint = new Paint();
    RectF bounds = new RectF(-radius + x, -radius + y, radius + x, radius + y);
    private Boolean special = (false);

    public PacmanBall(Double x, Double y, Context c) {
        super(c);
        this.x = x.floatValue();
        this.y = y.floatValue();
        paint.setColor(Color.BLUE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public final boolean isSpecial() {
        return special;
    }

    public final void setSpecial() {

        this.special = true;
        if (true) {
            objectAnimator.setDuration(250);
            objectAnimator.setValues(PropertyValuesHolder.ofKeyframe("radius", Keyframe.ofFloat(0, 10), Keyframe.ofFloat(1, MazeSquare.SQUARE_SIZE / 4)));
            objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
            objectAnimator.setTarget(this);
            objectAnimator.start();
        } else {
            radius = 10;
            objectAnimator.pause();
        }


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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
