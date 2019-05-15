package red.guih.games.madmaze;

import java.util.Arrays;

public class MadTriangle {

    private final MadPoint a;

    private final MadPoint b;

    private final MadPoint c;
    private boolean visited;
    private MadPoint center;
    private boolean deadEnd;

    MadTriangle(MadPoint a, MadPoint b, MadPoint c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public boolean contains(MadPoint point) {
        float pab = point.sub(a).cross(b.sub(a));
        float pbc = point.sub(b).cross(c.sub(b));
        if (!hasSameSign(pab, pbc)) {
            return false;
        }
        float pca = point.sub(c).cross(a.sub(c));
        return hasSameSign(pab, pca);
    }

    MadEdgeDistance findNearestEdge(MadPoint point) {
        MadEdgeDistance[] edges = new MadEdgeDistance[3];

        edges[0] = new MadEdgeDistance(new MadLine(a, b),
                computeClosestPoint(new MadLine(a, b), point).sub(point).mag());
        edges[1] = new MadEdgeDistance(new MadLine(b, c),
                computeClosestPoint(new MadLine(b, c), point).sub(point).mag());
        edges[2] = new MadEdgeDistance(new MadLine(c, a),
                computeClosestPoint(new MadLine(c, a), point).sub(point).mag());

        Arrays.sort(edges);
        return edges[0];
    }

    public MadPoint getA() {
        return a;
    }

    public MadPoint getB() {
        return b;
    }

    @Override
    public String toString() {
        return "MadTriangle [a=" + a + ", b=" + b + ", c=" + c + ", visited=" + visited + "]";
    }

    public MadPoint getCenter() {
        if (center == null) {
            center = a.add(b).add(c).multiply(1.0F / 3);
        }
        return center;
    }


    public MadPoint getC() {
        return c;
    }

    MadPoint getNoneEdgeVertex(MadLine edge) {
        if (a != edge.a && a != edge.b) {
            return a;
        } else if (b != edge.a && b != edge.b) {
            return b;
        } else if (c != edge.a && c != edge.b) {
            return c;
        }
        return null;
    }

    boolean hasVertex(MadPoint vertex) {
        return a == vertex || b == vertex || c == vertex;
    }

    boolean isNeighbour(MadLine edge) {

        return isEqual(edge.a, a, b, c) && isEqual(edge.b, a, b, c);
    }

    private static boolean isEqual(MadPoint p, MadPoint... arr) {
        for (MadPoint madPoint : arr) {
            if (madPoint == p) {
                return true;
            }
        }
        return false;
    }


    private boolean isOrientedCCW() {
        float a11 = a.getX() - c.getX();
        float a21 = b.getX() - c.getX();

        float a12 = a.getY() - c.getY();
        float a22 = b.getY() - c.getY();

        float det = a11 * a22 - a12 * a21;

        return det > 0.0D;
    }

    boolean isPointInCircumcircle(MadPoint point) {
        float a11 = a.getX() - point.getX();
        float a21 = b.getX() - point.getX();
        float a31 = c.getX() - point.getX();

        float a12 = a.getY() - point.getY();
        float a22 = b.getY() - point.getY();
        float a32 = c.getY() - point.getY();

        float a13 = (a.getX() - point.getX()) * (a.getX() - point.getX()) +
                (a.getY() - point.getY()) * (a.getY() - point.getY());
        float a23 = (b.getX() - point.getX()) * (b.getX() - point.getX()) +
                (b.getY() - point.getY()) * (b.getY() - point.getY());
        float a33 = (c.getX() - point.getX()) * (c.getX() - point.getX()) +
                (c.getY() - point.getY()) * (c.getY() - point.getY());

        float det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 -
                a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0D;
        }

        return det < 0.0D;
    }

    private static MadPoint computeClosestPoint(MadLine edge, MadPoint point) {
        MadPoint ab = edge.b.sub(edge.a);
        float t = point.sub(edge.a).dot(ab) / ab.dot(ab);

        if (t < 0.0F) {
            t = 0.0F;
        } else if (t > 1.0D) {
            t = 1.0F;
        }

        return edge.a.add(ab.multiply(t));
    }

    private static boolean hasSameSign(float a1, float b1) {
        return Math.signum(a1) == Math.signum(b1);
    }

    boolean isNotVisited() {
        return !visited;
    }

    void setVisited() {
        this.visited = true;
    }

    void setDeadEnd() {
        deadEnd = true;
    }

    boolean isDeadEnd() {
        return deadEnd;
    }
}
