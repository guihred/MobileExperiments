package red.guih.games.puzzle;

import android.view.MotionEvent;

/**
 * Class for keeping 2d coordinates
 * <p>
 * Created by guilherme.hmedeiros on 06/02/2018.
 */

public class Point2D {
    private static Point2D instance;
    float x, y;

    public static Point2D getInstance() {
        if (instance == null)
            instance = new Point2D();
        return instance;
    }
    private Point2D(){}
    public static Point2D getIntersectedPoint(MotionEvent e) {
        Point2D point3D = Point2D.getInstance();
        point3D.x = e.getX();
        point3D.y = e.getY();
        return point3D;
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

    public Point2D subtract(MotionEvent intersectedPoint) {
        x = intersectedPoint.getX()-x;
        y = intersectedPoint.getY()-y;
        return this;

    }

}
