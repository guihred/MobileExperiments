package red.guih.games.puzzle;

/**
 * Created by guilherme.hmedeiros on 06/02/2018.
 */

public class Point3D {
     float x,y;

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

    public Point3D subtract(Point3D intersectedPoint) {
        x=x-intersectedPoint.x;
        y=y-intersectedPoint.y;
        return this;

    }

    public float magnitude() {
        return (float) Math.sqrt(x*x+y*y);
    }
}
