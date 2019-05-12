package red.guih.games.madmaze;

import android.support.annotation.NonNull;

public class MadEdge implements Comparable<MadEdge> {

    private final MadCell source;
    private final MadCell target;
    private float a;
    private float b;
    private float c;
    private float sqrt;
    private float[] xBounds;
    private float[] yBounds;

    public MadEdge(MadCell source, MadCell target) {
        this.source = source;
        this.target = target;
    }

    public MadCell getSource() {
        return source;
    }

    public MadCell getTarget() {
        return target;
    }

    public void getBounds() {
        if (xBounds == null || yBounds == null) {
            float left = source.getX();
            float top = source.getY();
            float right = target.getX();
            float bottom = target.getY();
            float frac = 2;
            xBounds = new float[]{left + getA() / frac, left - getA() / frac,
                    right - getA() / frac, right + getA() / frac};
            yBounds = new float[]{top + getB() / frac, top - getB() / frac,
                    bottom - getB() / frac, bottom + getB() / frac};
        }

    }

    boolean checkCollisionBounds(float testx, float testy) {
        getBounds();
        return checkInBounds(xBounds, yBounds, testx, testy);
    }
    boolean checkInBounds(float[] vertx, float[] verty, float testx, float testy) {
        int i, j;
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
    @Override
    public int compareTo(@NonNull MadEdge o) {
        return 0;
    }

    public float distance(float ballX, float ballY) {
        return Math.abs(getA() * ballX + getB() * ballY + getC()) / getSqrt();
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

    private float getC() {
        if (c == 0) {
            c = -this.getTarget().getX() * this.getSource().getY() +
                    this.getTarget().getY() * this.getSource().getX();
        }
        return c;
    }

    private float getB() {
        if (b == 0) {
            b = this.getTarget().getX() - this.getSource().getX();
        }
        return b;
    }

    private float getA() {
        if (a == 0) {
            a = this.getSource().getY() - this.getTarget().getY();
        }
        return a;
    }
}
