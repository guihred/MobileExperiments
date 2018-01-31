package red.guih.games.madmaze;

public class MadEdge implements Comparable<MadEdge> {

    private MadCell source;
    private MadCell target;
    float a, b, c, sqrt;
    //    float[] bounds;
    float[] xBounds;
    float[] yBounds;

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
            float left = (source.getX());
            float top = (source.getY());
            float right = (target.getX());
            float bottom = (target.getY());
            float frac = 2;
            xBounds = new float[]{left + getA() / frac, left - getA() / frac, right - getA() / frac, right + getA() / frac};
            yBounds = new float[]{top + getB() / frac, top - getB() / frac, bottom - getB() / frac, bottom + getB() / frac};
        }
//        if (bounds != null)
//            return bounds;
//
//        return bounds = new float[]{xBounds[0], yBounds[0], xBounds[1], yBounds[1], xBounds[1], yBounds[1], xBounds[2], yBounds[2], xBounds[2], yBounds[2], xBounds[3], yBounds[3], xBounds[3], yBounds[3], xBounds[0], yBounds[0]};

    }

    boolean checkCollisionBounds(float testx, float testy) {
        getBounds();
        return checkInBounds(xBounds, yBounds, testx, testy);
    }


    boolean checkInBounds(float[] vertx, float[] verty, float testx, float testy) {
        int i, j;
        boolean c = false;
        for (i = 0, j = 3; i < 4; j = i++) {
            if (((verty[i] > testy) != (verty[j] > testy)) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                c = !c;
        }
        return c;
    }


    @Override
    public int compareTo(MadEdge o) {
        return 0;
    }

    public float distance(float ballx, float bally) {
        return Math.abs(getA() * ballx + getB() * bally + getC()) / getSqrt();
    }

    private float getSqrt() {
        if (sqrt != 0)
            return sqrt;


        float a = getA();
        float b = getB();
        return sqrt = (float) Math.sqrt(a * a + b * b);
    }

    private float getC() {
        if (c != 0)
            return c;
        return c = -this.getTarget().getX() * this.getSource().getY() + this.getTarget().getY() * this.getSource().getX();
    }

    private float getB() {
        if (b != 0)
            return b;


        return b = this.getTarget().getX() - this.getSource().getX();
    }

    private float getA() {
        if (a != 0)
            return a;
        return a = this.getSource().getY() - this.getTarget().getY();
    }


}
