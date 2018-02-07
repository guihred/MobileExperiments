package red.guih.games.puzzle;

/**
 * Class for keeping 2d coordinates
 *
 * Created by guilherme.hmedeiros on 06/02/2018.
 */

public class Point2D {
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

    public Point2D subtract(Point2D intersectedPoint) {
        x -= intersectedPoint.x;
        y -= intersectedPoint.y;
        return this;

    }

}
