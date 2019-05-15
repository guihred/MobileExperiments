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
    static int squareSize;
    final int i;
    final int j;
    private List<RectF> walls = new ArrayList<>();
    private boolean visited;
    private boolean west;
    private boolean east;
    private boolean north;
    private boolean south;
    private Paint paint = new Paint(Color.GREEN);

    public LabyrinthSquare(Context c, int i, int j) {
        super(c);
        this.i = i;
        this.j = j;
        paint.setColor(Color.GREEN);
    }

    public static void setSquareSize(int squareSize) {
        LabyrinthSquare.squareSize = squareSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (RectF w : walls) {
            drawWall(canvas, w);
        }
    }

    private void drawWall(Canvas canvas, RectF rectF) {
        canvas.drawRect(rectF, paint);
    }

    public final boolean isNotVisited() {
        return !visited;
    }

    public final void setVisited() {
        this.visited = true;
        updateWalls();
    }

    public List<RectF> updateWalls() {
        walls.clear();
        float layoutX = (float) i * squareSize;
        float layoutY = (float) j * squareSize;
        if (!this.isWest()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + squareSize, layoutY + 2));

        }
        if (!this.isNorth()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + 2, layoutY + squareSize));

        }
        if (!this.isEast()) {
            walls.add(new RectF(layoutX, layoutY + squareSize, layoutX + squareSize,
                    layoutY + squareSize + 2));

        }
        if (!this.isSouth()) {
            walls.add(new RectF(layoutX + squareSize, layoutY, layoutX + squareSize + 2,
                    layoutY + squareSize));

        }
        return walls;

    }

    public final boolean isWest() {
        return west;
    }

    public final void setWest(final boolean west) {
        this.west = west;
        updateWalls();
    }

    public final boolean isNorth() {
        return north;
    }

    public final boolean isEast() {
        return east;
    }

    public boolean isSouth() {
        return south;
    }

    public void setSouth(boolean south) {
        this.south = south;
        updateWalls();
    }

    public final void setEast(final boolean east) {
        this.east = east;
        updateWalls();
    }

    public final void setNorth(final boolean north) {
        this.north = north;
        updateWalls();
    }


}
