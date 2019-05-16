package red.guih.games.madmaze;

import android.support.annotation.NonNull;

import java.util.Objects;

public class MadEdge implements Comparable<MadEdge> {

    private final MadCell source;
    private final MadCell target;
    private float a;
    private float b;
    private float c;
    private float sqrt;
    private float[] xBounds;
    private float[] yBounds;

    MadEdge(MadCell source, MadCell target) {
        this.source = source;
        this.target = target;
    }

    boolean checkCollisionBounds(float testX, float testY) {
        getBounds();
        return checkInBounds(xBounds, yBounds, testX, testY);
    }

    public void getBounds() {
        if (xBounds == null || yBounds == null) {
            float left = source.getX();
            float top = source.getY();
            float right = target.getX();
            float bottom = target.getY();
            float fraction = 2;
            xBounds = new float[]{left + getA() / fraction, left - getA() / fraction,
                    right - getA() / fraction, right + getA() / fraction};
            yBounds = new float[]{top + getB() / fraction, top - getB() / fraction,
                    bottom - getB() / fraction, bottom + getB() / fraction};
        }

    }

    private static boolean checkInBounds(float[] vertx, float[] verty, float testx, float testy) {
        int i;
        int j;
        boolean inBound = false;
        for (i = 0, j = 3; i < 4; j = i++) {
            if (verty[i] > testy != verty[j] > testy
                    && testx <
                    (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]) {
                inBound = !inBound;
            }
        }
        return inBound;
    }

    private float getA() {
        if (a == 0) {
            a = this.getSource().getY() - this.getTarget().getY();
        }
        return a;
    }

    private float getB() {
        if (b == 0) {
            b = this.getTarget().getX() - this.getSource().getX();
        }
        return b;
    }

    MadCell getSource() {
        return source;
    }

    MadCell getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().isInstance(obj)) {
            return false;
        }
        final MadEdge other = (MadEdge) obj;
        return source == other.source && target == other.target;
    }

    @Override
    public int compareTo(@NonNull MadEdge o) {
        return 0;
    }

    float distance(float ballX, float ballY) {
        return Math.abs(getA() * ballX + getB() * ballY + getC()) / getSqrt();
    }

    private float getC() {
        if (c == 0) {
            c = -this.getTarget().getX() * this.getSource().getY() +
                    this.getTarget().getY() * this.getSource().getX();
        }
        return c;
    }

    private float getSqrt() {
        if (sqrt != 0) {
            return sqrt;
        }
        float a1 = getA();
        float b1 = getB();
        sqrt = (float) Math.sqrt(a1 * a1 + b1 * b1);
        return sqrt;
    }
}
