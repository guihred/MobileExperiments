package red.guih.games.madmaze;

import java.util.Objects;

public class MadPoint {
    private float x;
    private float y;
    private MadCell cell;

    MadPoint(float x, float y, MadCell c) {
        this.x = x;
        this.y = y;
        cell = c;
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

    MadCell getCell() {
        return cell;
    }

    public MadPoint add(MadPoint vector) {
        return new MadPoint(x + vector.x, y + vector.y, cell);
    }

    float cross(MadPoint vector) {
        return y * vector.x - x * vector.y;
    }

    float dot(MadPoint vector) {
        return x * vector.x + y * vector.y;
    }

    @Override
    public boolean equals(Object obj) {

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    MadPoint multiply(float scalar) {
        return new MadPoint(x * scalar, y * scalar, cell);
    }

    MadPoint sub(MadPoint vector) {
        return new MadPoint(x - vector.x, y - vector.y, cell);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
