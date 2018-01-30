package red.guih.games.madmaze;

public class MadCell {

    int id;
    float x, y;

    public MadCell(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void relocate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "[" + id + "," + x + "," + y + "]";
    }

    public void setY(float y) {
        this.y = y;
    }

}
