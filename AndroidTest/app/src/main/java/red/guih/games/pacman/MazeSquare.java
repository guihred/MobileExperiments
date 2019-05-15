package red.guih.games.pacman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MazeSquare {
    static int squareSize = 10;
    static Map<MazeSquare, Map<MazeSquare, MazeSquare>> paths; // <id,cell>
    final int i;
    final int j;
    private final List<RectF> walls = new ArrayList<>();
    private final Paint paint = new Paint(Color.GREEN);
    private List<MazeSquare> adjacents;
    private boolean visited;
    private boolean west;
    private boolean east;
    private boolean north;
    private boolean south;
    private List<RectF> bounds;

    MazeSquare(int i, int j) {
        this.i = i;
        this.j = j;
        paint.setColor(Color.GREEN);
    }

    @Override
    public String toString() {
        return "MazeSquare(" + i + "," + j + ')';
    }


    // <id,cell>

    protected void draw(Canvas canvas) {
        for (RectF w : walls) {
            drawWall(canvas, w);
        }
    }

    private void drawWall(Canvas canvas, RectF rectF) {
        canvas.drawRect(rectF, paint);
    }

    List<RectF> updateWalls() {
        walls.clear();
        float layoutX = (float) i * squareSize;
        float layoutX2 = PacmanView.MAZE_WIDTH * 2F * squareSize - i * squareSize - squareSize;
        float layoutY = (float) j * squareSize;
        float layoutY2 = PacmanView.mazeHeight * 2F * squareSize - j * squareSize - squareSize;
        if (this.isNotWest()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + squareSize, layoutY + 2));
            walls.add(new RectF(layoutX2, layoutY, layoutX2 + squareSize, layoutY + 2));
            walls.add(new RectF(layoutX, layoutY2 + squareSize, layoutX + squareSize,
                    layoutY2 + squareSize + 2));
            walls.add(new RectF(layoutX2, layoutY2 + squareSize, layoutX2 + squareSize,
                    layoutY2 + squareSize + 2));
        }
        if (this.isNotNorth()) {
            walls.add(new RectF(layoutX, layoutY, layoutX + 2, layoutY + squareSize));
            walls.add(new RectF(layoutX2 + squareSize, layoutY, layoutX2 + squareSize + 2,
                    layoutY + squareSize));
            walls.add(new RectF(layoutX, layoutY2, layoutX + 2, layoutY2 + squareSize));
            walls.add(new RectF(layoutX2 + squareSize, layoutY2, layoutX2 + squareSize + 2,
                    layoutY2 + squareSize));
        }
        if (this.isNotEast()) {
            walls.add(new RectF(layoutX, layoutY + squareSize, layoutX + squareSize,
                    layoutY + squareSize + 2));
            walls.add(new RectF(layoutX2, layoutY + squareSize, layoutX2 + squareSize,
                    layoutY + squareSize + 2));
            walls.add(new RectF(layoutX, layoutY2, layoutX + squareSize, layoutY2 + 2));
            walls.add(new RectF(layoutX2, layoutY2, layoutX2 + squareSize, layoutY2 + 2));
        }
        if (this.isNotSouth()) {
            walls.add(new RectF(layoutX + squareSize, layoutY, layoutX + squareSize + 2,
                    layoutY + squareSize));
            walls.add(new RectF(layoutX2, layoutY, layoutX2 + 2, layoutY + squareSize));
            walls.add(new RectF(layoutX + squareSize, layoutY2, layoutX + squareSize + 2,
                    layoutY2 + squareSize));
            walls.add(new RectF(layoutX2, layoutY2, layoutX2 + 2, layoutY2 + squareSize));
        }
        return walls;

    }

    final boolean isNotWest() {
        return !west;
    }

    final boolean isNotNorth() {
        return !north;
    }

    final boolean isNotEast() {
        return !east;
    }

    boolean isNotSouth() {
        return !south;
    }

    final void setWest(final boolean west) {
        this.west = west;
    }

    void setSouth() {
        this.south = true;
    }

    void setEast() {
        this.east = true;
    }

    final void setNorth(final boolean north) {
        this.north = north;
    }

    boolean isInBounds(float x, float y) {
        if (bounds == null) {

            float size = squareSize;
            float layoutX = i * size;
            float layoutX2 = PacmanView.MAZE_WIDTH * 2 * size - i * size - size;
            float layoutY = j * size;
            float layoutY2 = PacmanView.mazeHeight * 2 * size - j * size - size;
            List<RectF> arrayList = new ArrayList<>();
            arrayList.add(new RectF(layoutX, layoutY, layoutX + size, layoutY + size));
            arrayList.add(new RectF(layoutX, layoutY2, layoutX + size, layoutY2 + size));
            arrayList.add(new RectF(layoutX2, layoutY, layoutX2 + size, layoutY + size));
            arrayList.add(new RectF(layoutX2, layoutY2, layoutX2 + size, layoutY2 + size));

            bounds = arrayList;
        }
        return bounds.stream().anyMatch(e -> e.contains(x, y));

    }

    void dijkstra(final MazeSquare[][] map) {
        Map<MazeSquare, Integer> distance = new LinkedHashMap<>();
        Map<MazeSquare, Boolean> known = createDistanceMap(this, distance, map);
        while (known.entrySet().stream().anyMatch(e -> !e.getValue())) {
            Map.Entry<MazeSquare, Integer> orElse =
                    distance.entrySet().stream().filter(e -> !known.get(e.getKey()))
                            .min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
            if (orElse == null) {
                break;
            }

            MazeSquare v = orElse.getKey();
            known.put(v, true);
            for (MazeSquare w : v.adjacents(map)) {
                if (!known.get(w)) {
                    Integer cvw = 1;
                    if (distance.get(v) + cvw < distance.get(w)) {
                        distance.put(w, distance.get(v) + cvw);
                        setPath(w, this, v);
                    }
                }
            }
        }
    }

    private static Map<MazeSquare, Boolean> createDistanceMap(MazeSquare source,
            Map<MazeSquare, Integer> distance, MazeSquare[][] map) {
        Map<MazeSquare, Boolean> known = new HashMap<>();
        for (MazeSquare[] u : map) {
            for (MazeSquare v : u) {
                distance.put(v, Integer.MAX_VALUE);
                known.put(v, false);
            }
        }
        distance.put(source, 0);
        return known;
    }

    private List<MazeSquare> adjacents(MazeSquare[][] map) {
        if (adjacents == null) {
            adjacents = new ArrayList<>();
            MazeSquare el = map[i][j];
            if (el.east && j + 1 < PacmanView.mazeHeight) {
                adjacents.add(map[i][j + 1]);
            }
            if (el.west && j > 0) {
                MazeSquare e = map[i][j - 1];
                adjacents.add(e);

            }
            if (el.north && i > 0) {
                adjacents.add(map[i - 1][j]);

            }
            if (el.south && i + 1 < PacmanView.MAZE_WIDTH) {
                adjacents.add(map[i + 1][j]);
            }
            Log.i("MAZE_SQUARE", this + " ->" + adjacents);
        }

        return adjacents;
    }

    private static void setPath(MazeSquare from, MazeSquare to, MazeSquare by) {
        if (paths == null) {
            paths = new LinkedHashMap<>();
        }
        if (!paths.containsKey(from)) {
            paths.put(from, new LinkedHashMap<>());
        }
        paths.get(from).put(to, by);
    }

    final boolean isNotVisited() {
        return !visited;
    }

    final void setVisited() {
        this.visited = true;
    }


}
