package red.guih.games.labyrinth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class LabyrinthSquare extends View {
    public static int SQUARE_SIZE ;
    final int i, j;
    private List<RectF> walls = new ArrayList<>();
    private boolean visited = false;
    private boolean west = false;
    private boolean east = false;
    private boolean north = false;
    private boolean south = false;
    private Paint paint = new Paint(Color.GREEN);
    public LabyrinthSquare(Context c, int i, int j) {
        super(c);
        this.i = i;
        this.j = j;
        paint.setColor(Color.GREEN);
    }

    public static void setSquareSize(int squareSize) {
        LabyrinthSquare.SQUARE_SIZE = squareSize;
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


    private void drawWall(Canvas canvas, RectF rectF) {
        canvas.drawRect(rectF, paint);
    }

    public final boolean isVisited() {
        return visited;
    }

    public boolean isSouth() {
        return south;
    }

    public void setSouth(boolean south) {
        this.south = south;
        updateWalls();
    }

    public final boolean isWest() {
        return west;
    }

    public final void setWest(final boolean west) {
        this.west = west;
        updateWalls();
    }

    public final void setVisited() {
        this.visited = true;
        updateWalls();
    }

    public final boolean isEast() {
        return east;
    }


    public final void setEast(final boolean east) {
        this.east = east;
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
