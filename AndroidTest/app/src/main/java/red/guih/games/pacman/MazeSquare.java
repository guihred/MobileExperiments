package red.guih.games.pacman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MazeSquare {
    public static int SQUARE_SIZE = 20;
    public static Map<MazeSquare, Map<MazeSquare, MazeSquare>> paths; // <id,cell>
    final int i, j;
    List<MazeSquare> adjacents;
    private List<RectF> walls = new ArrayList<>();
    private boolean visited = false;
    private boolean west = false;
    private boolean east = false;
    private boolean north = false;
    private boolean south = false;
    private Paint paint = new Paint(Color.GREEN);
    private List<RectF> bounds;

    public MazeSquare(int i, int j) {
        this.i = i;
        this.j = j;
        paint.setColor(Color.GREEN);
    }

    public static void setPath(MazeSquare from, MazeSquare to, MazeSquare by) {
        if (paths == null) {
            paths = new LinkedHashMap<>();
        }
        if (!paths.containsKey(from)) {
            paths.put(from, new LinkedHashMap<>());
        }
        paths.get(from).put(to, by);
    }


    // <id,cell>

    @Override
    public String toString() {
        return "MazeSquare(" + i + "," + j + ')';
    }

    private Map<MazeSquare, Boolean> createDistanceMap(MazeSquare source, Map<MazeSquare, Integer> distance, MazeSquare[][] map) {
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

    public List<MazeSquare> adjacents(MazeSquare[][] map) {
        if (adjacents == null) {
            adjacents = new ArrayList<>();
            MazeSquare el = map[i][j];
            if (el.east && j + 1 < PacmanView.MAZE_HEIGHT) {
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
            System.out.println(this + " ->" + adjacents);
        }

        return adjacents;
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

    public boolean isInBounds(float x, float y) {
        if (bounds == null) {

            float layoutX = i * SQUARE_SIZE;
            float layoutX2 = PacmanView.MAZE_WIDTH * 2 * SQUARE_SIZE - i * SQUARE_SIZE
                    - SQUARE_SIZE;
            float layoutY = j * SQUARE_SIZE;
            float layoutY2 = PacmanView.MAZE_HEIGHT * 2 * SQUARE_SIZE - j * SQUARE_SIZE
                    - SQUARE_SIZE;
            List<RectF> arrayList = new ArrayList<>();
            arrayList.add(new RectF(layoutX, layoutY, layoutX+SQUARE_SIZE, layoutY+SQUARE_SIZE));
            arrayList.add(new RectF(layoutX, layoutY2, layoutX+SQUARE_SIZE, layoutY2+MazeSquare.SQUARE_SIZE));
            arrayList.add(new RectF(layoutX2, layoutY, layoutX2+SQUARE_SIZE, layoutY+SQUARE_SIZE));
            arrayList.add(new RectF(layoutX2, layoutY2, layoutX2+SQUARE_SIZE, layoutY2+SQUARE_SIZE));

            bounds = arrayList;
        }
        return bounds.stream().anyMatch(e -> e.contains(x, y));

    }

    public Map<MazeSquare, Integer> dijkstra(final MazeSquare[][] map) {
        Map<MazeSquare, Integer> distance = new LinkedHashMap<>();
        Map<MazeSquare, Boolean> known = createDistanceMap(this, distance, map);
        while (known.entrySet().stream().anyMatch(e -> !e.getValue())) {
            Map.Entry<MazeSquare, Integer> orElse = distance.entrySet().stream().filter(e -> !known.get(e.getKey()))
                    .min(Comparator.comparing(Map.Entry<MazeSquare, Integer>::getValue)).orElse(null);
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
        return distance;
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

    public void setSouth(boolean b) {
        this.south = b;
    }

    public final boolean isWest() {
        return west;
    }

    public final void setWest(final boolean west) {
        this.west = west;
    }

    public final void setVisited() {
        this.visited = true;
    }

    public final boolean isEast() {
        return east;
    }


    public void setEast(boolean b) {
        this.east = b;
    }

    public final boolean isNorth() {
        return north;
    }


    public final void setNorth(final boolean north) {
        this.north = north;
    }


}
