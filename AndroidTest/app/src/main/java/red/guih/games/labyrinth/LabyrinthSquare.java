package red.guih.games.labyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LabyrinthSquare extends View {
    public static int SQUARE_SIZE = 20;
    private List<RectF> walls = new ArrayList<>();
    private boolean visited = (false);
    private boolean west = (false);
    private boolean east = (false);
    private boolean north = (false);
    private boolean south = (false);
    private Paint paint = new Paint(Color.GREEN);
    final int i, j;


    public LabyrinthSquare(Context c, int i, int j) {
        super(c);
        this.i = (i);
        this.j = (j);
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

    @Override
    protected void onDraw(Canvas canvas) {
        for (RectF w : walls) {
            drawWall(canvas, w);
        }
    }


    public List<RectF> updateWalls() {
        walls.clear();
        float layoutX = i * SQUARE_SIZE;
        float layoutY = j * SQUARE_SIZE;
        if (!this.isWest()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + SQUARE_SIZE, layoutY + 2));

        }
        if (!this.isNorth()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + 2, layoutY + SQUARE_SIZE));

        }
        if (!this.isEast()) {
            walls.add(new RectF(layoutX, layoutY + SQUARE_SIZE, layoutX + SQUARE_SIZE, layoutY + SQUARE_SIZE + 2));

        }
        if (!this.isSouth()) {
            walls.add(new RectF(layoutX + SQUARE_SIZE, layoutY, layoutX + SQUARE_SIZE + 2, layoutY + SQUARE_SIZE));

        }
        return walls;

    }


    public List<RectF> getBounds() {
        return walls;
    }

    private void drawWall(Canvas canvas, RectF rectF) {
        canvas.drawRect(rectF, paint);
    }

    public void setSouth(boolean south) {
        this.south = (south);
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

    public final void setVisited(final boolean visited) {
        this.visited = (visited);
        updateWalls();
    }

    public final void setWest(final boolean west) {
        this.west = (west);
        updateWalls();
    }

    public final boolean isEast() {
        return east;
    }


    public final void setEast(final boolean east) {
        this.east = (east);
        updateWalls();
    }

    public final boolean isNorth() {
        return north;
    }


    public final void setNorth(final boolean north) {
        this.north = (north);
        updateWalls();
    }


}
