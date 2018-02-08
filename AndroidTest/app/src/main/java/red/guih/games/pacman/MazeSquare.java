package red.guih.games.pacman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class MazeSquare  {
    public static int SQUARE_SIZE = 20;
    private List<RectF> walls = new ArrayList<>();
    private boolean visited = false;
    private boolean west = false;
    private boolean east = false;
    private boolean north = false;
    private boolean south = false;
    private Paint paint = new Paint(Color.GREEN);
    final int i, j;


    public MazeSquare(int i, int j) {
        this.i = i;
        this.j = j;
        paint.setColor(Color.GREEN);
//        setStyle("-fx-background-color:green;");
//        styleProperty().bind(Bindings.when(visited).then("-fx-background-color:green;").otherwise("-fx-background-color:gray;"));
//        setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
//        final Line line = new Line(0, 0, 0, SQUARE_SIZE);
//        line.visibleProperty().bind(east.not());
//        setRight(line);
//        final Line line2 = new Line(0, 0, SQUARE_SIZE, 0);
//        line2.visibleProperty().bind(north.not());
//        setTop(line2);
//        final Line line3 = new Line(0, 0, 0, SQUARE_SIZE);
//        line3.visibleProperty().bind(west.not());
//        setLeft(line3);
//        final Line line4 = new Line(0, 0, SQUARE_SIZE, 0);
//        line4.visibleProperty().bind(south.not());
//        setBottom(line4);
    }

    protected void draw(Canvas canvas) {
        for (RectF w : walls) {
            drawWall(canvas, w);
        }
    }

    public List<RectF> updateWalls() {
        walls.clear();
        float layoutX = i * SQUARE_SIZE;
        float layoutX2 = PacmanView.MAZE_WIDTH * 2 * SQUARE_SIZE - i * SQUARE_SIZE - SQUARE_SIZE;
        float layoutY = j * SQUARE_SIZE;
        float layoutY2 = PacmanView.MAZE_HEIGHT * 2 * SQUARE_SIZE - j * SQUARE_SIZE - SQUARE_SIZE;
        if (!this.isWest()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + SQUARE_SIZE, layoutY + 2));
            walls.add(new RectF(layoutX2, layoutY, layoutX2 + SQUARE_SIZE, layoutY + 2));
            walls.add(new RectF(layoutX, layoutY2 + SQUARE_SIZE, layoutX + SQUARE_SIZE, layoutY2 + SQUARE_SIZE + 2));
            walls.add(new RectF(layoutX2, layoutY2 + SQUARE_SIZE, layoutX2 + SQUARE_SIZE, layoutY2 + SQUARE_SIZE + 2));
        }
        if (!this.isNorth()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + 2, layoutY + SQUARE_SIZE));
            walls.add(new RectF(layoutX2 + SQUARE_SIZE, layoutY, layoutX2 + SQUARE_SIZE + 2, layoutY + SQUARE_SIZE));
            walls.add(new RectF(layoutX, layoutY2, layoutX + 2, layoutY2 + SQUARE_SIZE));
            walls.add(new RectF(layoutX2 + SQUARE_SIZE, layoutY2, layoutX2 + SQUARE_SIZE + 2, layoutY2 + SQUARE_SIZE));
        }
        if (!this.isEast()) {
            walls.add(new RectF(layoutX, layoutY + SQUARE_SIZE, layoutX + SQUARE_SIZE, layoutY + SQUARE_SIZE + 2));
            walls.add(new RectF(layoutX2, layoutY + SQUARE_SIZE, layoutX2 + SQUARE_SIZE, layoutY + SQUARE_SIZE + 2));
            walls.add(new RectF(layoutX, layoutY2, layoutX + SQUARE_SIZE, layoutY2 + 2));
            walls.add(new RectF(layoutX2, layoutY2, layoutX2 + SQUARE_SIZE, layoutY2 + 2));
        }
        if (!this.isSouth()) {
            walls.add(new RectF(layoutX + SQUARE_SIZE, layoutY, layoutX + SQUARE_SIZE + 2, layoutY + SQUARE_SIZE));
            walls.add(new RectF(layoutX2, layoutY, layoutX2 + 2, layoutY + SQUARE_SIZE));
            walls.add(new RectF(layoutX + SQUARE_SIZE, layoutY2, layoutX + SQUARE_SIZE + 2, layoutY2 + SQUARE_SIZE));
            walls.add(new RectF(layoutX2, layoutY2, layoutX2 + 2, layoutY2 + SQUARE_SIZE));
        }
        return walls;

    }

    private void drawWall(Canvas canvas, RectF rectF) {
        canvas.drawRect(rectF, paint);
    }

    public void  setSouth() {
        this.south = true;
        updateWalls();
    }

    public final boolean isVisited() {
        return visited;
    }

    public boolean isSouth() {
        return south;
    }

    public final boolean isWest() {
        return west;
    }

    public final void setVisited() {
        this.visited = true;
        updateWalls();
    }

    public final void setWest(final boolean west) {
        this.west = west;
        updateWalls();
    }

    public final boolean isEast() {
        return east;
    }


    public final void setEast() {
        this.east = true;
        updateWalls();
    }

    public final boolean isNorth() {
        return north;
    }


    public final void setNorth(final boolean north) {
        this.north = north;
        updateWalls();
    }


}
