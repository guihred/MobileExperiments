package red.guih.games.madmaze;

import java.util.Arrays;

public class MadTriangle {

    private MadPoint a;

    private MadPoint b;

    private MadPoint c;
    private boolean visited;
    private MadPoint center;

    public MadTriangle(MadPoint a, MadPoint b, MadPoint c) {
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

    public MadEdgeDistance findNearestEdge(MadPoint point) {
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
            center = a.add(b).add(c).multiply(1.0f / 3);
        }
        return center;
    }


    public MadPoint getC() {
        return c;
    }

    public MadPoint getNoneEdgeVertex(MadLine edge) {
        if (a != edge.a && a != edge.b) {
            return a;
        } else if (b != edge.a && b != edge.b) {
            return b;
        } else if (c != edge.a && c != edge.b) {
            return c;
        }
        return null;
    }

    public boolean hasVertex(MadPoint vertex) {
        return a == vertex || b == vertex || c == vertex;
    }

    public boolean isNeighbour(MadLine edge) {

        return (a == edge.a || b == edge.a || c == edge.a) && (a == edge.b || b == edge.b || c == edge.b);
    }

    public boolean isOrientedCCW() {
        float a11 = a.x - c.x;
        float a21 = b.x - c.x;

        float a12 = a.y - c.y;
        float a22 = b.y - c.y;

        float det = a11 * a22 - a12 * a21;

        return det > 0.0D;
    }

    public boolean isPointInCircumcircle(MadPoint point) {
        float a11 = a.x - point.x;
        float a21 = b.x - point.x;
        float a31 = c.x - point.x;

        float a12 = a.y - point.y;
        float a22 = b.y - point.y;
        float a32 = c.y - point.y;

        float a13 = (a.x - point.x) * (a.x - point.x) + (a.y - point.y) * (a.y - point.y);
        float a23 = (b.x - point.x) * (b.x - point.x) + (b.y - point.y) * (b.y - point.y);
        float a33 = (c.x - point.x) * (c.x - point.x) + (c.y - point.y) * (c.y - point.y);

        float det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0D;
        }

        return det < 0.0D;
    }

    private static MadPoint computeClosestPoint(MadLine edge, MadPoint point) {
        MadPoint ab = edge.b.sub(edge.a);
        float t = point.sub(edge.a).dot(ab) / ab.dot(ab);

        if (t < 0.0f) {
            t = 0.0f;
        } else if (t > 1.0D) {
            t = 1.0f;
        }

        return edge.a.add(ab.multiply(t));
    }

    private static boolean hasSameSign(float a1, float b1) {
        return Math.signum(a1) == Math.signum(b1);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        this.visited = true;
    }

}
