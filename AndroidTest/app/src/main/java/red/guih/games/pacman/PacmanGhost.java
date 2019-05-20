package red.guih.games.pacman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import red.guih.games.R;

@SuppressLint("ViewConstructor")
public class PacmanGhost extends View {
    private static final int SPEED = 10;
    private static final Paint BLACK = new Paint(Color.BLACK);
    private Drawable drawable;
    private GhostDirection[] ghostDirections = GhostDirection.values();
    private int xPosition;
    private int yPosition;
    private RectF bounds = new RectF(xPosition, yPosition, (float) xPosition + getGhostWidth(),
            (float) yPosition + getGhostWidth());
    private MazeSquare ghostSquare;
    private GhostColor color;
    private GhostStatus status = GhostStatus.ALIVE;
    private GhostDirection direction = GhostDirection.NORTH;
    private float startX;
    private float startY;
    private Random random = new Random();
    private MazeSquare pacmanSquare;

    public PacmanGhost(GhostColor color, Context c) {
        super(c);
        this.color = color;
        drawable = getResources().getDrawable(color.getColor(), null);
        drawable.setBounds(0, 0, MazeSquare.squareSize, MazeSquare.squareSize);
        BLACK.setColor(color.getColor2());

    }

    private static boolean isMiddle(float y) {
        return y % (MazeSquare.squareSize / 4F) <= 5;
    }

    private static MazeSquare getSquareInBounds(MazeSquare[][] maze, float x, float y) {
        if (maze == null) {
            return null;
        }

        for (MazeSquare[] aMaze : maze) {
            for (MazeSquare anAMaze : aMaze) {
                boolean inBounds = anAMaze.isInBounds(x, y);
                if (inBounds) {
                    return anAMaze;
                }
            }
        }
        return null;
    }

    private static int adjustedX(float layoutX) {
        float paci = layoutX / MazeSquare.squareSize - 1;
        return (int) (paci > PacmanView.MAZE_WIDTH ? -paci + 2 * PacmanView.MAZE_WIDTH - 1
                : paci) % PacmanView.MAZE_WIDTH;
    }

    private static int adjustedY(float layoutX) {
        float paci = layoutX / MazeSquare.squareSize - 1;
        return (int) (paci > PacmanView.mazeHeight ? -paci - 1 + 2 * PacmanView.mazeHeight : paci)
                % PacmanView.mazeHeight;
    }

    private static MazeSquare getBestMaze(MazeSquare[][] maze, int hx, int hy, int hxg, int hyg) {
        if (MazeSquare.paths == null || maze == null) {
            return null;
        }
        if (hx < 0) {
            hx = 0;
        }
        if (hxg < 0) {
            hxg = 0;
        }
        if (hy < 0) {

            hy = 0;
        }
        if (hyg < 0) {
            hyg = 0;
        }

        Map<MazeSquare, MazeSquare> map = MazeSquare.paths.get(maze[hxg][hyg]);
        if (map == null) {
            return null;
        }

        return map.get(maze[hx][hy]);
    }

    private static GhostDirection changeDirection(float hx, float hy) {
        if (Math.abs(hx) < MazeSquare.squareSize / 4 &&
                Math.abs(hy) < MazeSquare.squareSize / 4) {
            if (hx > 0) {
                return hy < 0 ? GhostDirection.NORTHEAST : GhostDirection.SOUTHEAST;
            }
            return hy > 0 ? GhostDirection.SOUTHWEST : GhostDirection.NORTHWEST;
        }
        if (hx == 0 || Math.abs(hx) < Math.abs(hy)) {
            return hy < 0 ? GhostDirection.NORTH : GhostDirection.SOUTH;
        }
        return hx > 0 ? GhostDirection.WEST : GhostDirection.EAST;
    }

    boolean checkCollision(RectF boundsInParent, Collection<RectF> walls) {
        if (walls == null) {
            return false;
        }
        for (RectF b : walls) {
            if (RectF.intersects(b, boundsInParent)) {
                return true;
            }
        }
        return false;

    }

    public GhostDirection getDirection() {
        return direction;
    }

    public void setDirection(GhostDirection direction) {
        this.direction = direction;
    }

    public void move(long now, List<RectF> observableList, Pacman pacman, MazeSquare[][] maze) {
        if (status == GhostStatus.ALIVE) {
            shortestMovement(now, observableList, pacman, maze);
        } else if (status == GhostStatus.DEAD) {

            if (startX > xPosition) {
                xPosition += SPEED;
            } else if (startX < xPosition) {
                xPosition -= SPEED;
            }
            if (startY > yPosition) {
                yPosition += SPEED;
            } else if (startY < yPosition) {
                yPosition -= SPEED;
            }
            if (Math.abs(startX - xPosition) < 3 && Math.abs(startY - yPosition) < 3) {
                setStatus(GhostStatus.ALIVE);
            }
        } else {
            randomMovement(now, observableList);
        }
    }

    void shortestMovement(long now, List<RectF> otherNodes, Pacman pacman, MazeSquare[][] maze) {
        if (pacman == null) {
            randomMovement(now, otherNodes);
            return;
        }
        float hx = -getX() - MazeSquare.squareSize / 2F +
                (ghostSquare != null ? readjustedX(ghostSquare.i) : pacman.getX());
        float hy = -getY() - MazeSquare.squareSize / 2F +
                (ghostSquare != null ? readjustedY(ghostSquare.j) : pacman.getY());
        if (pacmanSquare != null) {
            hx = (-getX() - MazeSquare.squareSize / 2F + readjustedX(pacmanSquare.i));
            hy = (-getY() - MazeSquare.squareSize / 2F + readjustedY(pacmanSquare.j));
        }
        if (isMiddle(getY()) || isMiddle(getX())) {
            extracted(pacman, maze);
            if (pacmanSquare != null) {
                hx = (-getX() - getGhostWidth() / 2F + readjustedX(pacmanSquare.i));
                hy = (-getY() - getGhostWidth() / 2F + readjustedY(pacmanSquare.j));
                GhostDirection changeDirection = changeDirection(hx, hy);
                setDirection(changeDirection);
            }
        }
        addTranslate(SPEED);
        if (checkCollision(getBounds(), otherNodes)) {
            addTranslate(-SPEED);
            setDirection(changeDirection(hx, hy));
            addTranslate(SPEED);
            if (checkCollision(getBounds(), otherNodes)) {
                addTranslate(-SPEED);
                randomMovement(now, otherNodes);
            }

        }

    }

    private float readjustedX(int i) {

        return MazeSquare.squareSize / 2F +
                (getX() > MazeSquare.squareSize * PacmanView.MAZE_WIDTH ?
                        PacmanView.MAZE_WIDTH * 2 - i - 1 : i) * MazeSquare.squareSize;
    }

    private float readjustedY(int i) {

        return MazeSquare.squareSize / 2F +
                (getY() > MazeSquare.squareSize * PacmanView.mazeHeight ?
                        PacmanView.mazeHeight * 2 - i - 1 : i) * MazeSquare.squareSize;
    }

    private void extracted(Pacman pacman, MazeSquare[][] maze) {
        int hxg = adjustedX(getX());
        int hyg = adjustedY(getY());
        ghostSquare =
                getSquareInBounds(maze, getX() + getGhostWidth() / 2F,
                        getY() + getGhostWidth() / 2F);
        if (ghostSquare != null) {
            hxg = ghostSquare.i;
            hyg = ghostSquare.j;

        }

        int hx = adjustedX(pacman.getX());
        int hy = adjustedY(pacman.getY());
        MazeSquare pacmanSquare1 =
                getSquareInBounds(maze, pacman.getX() + pacman.getPacmanWidth() / 2,
                        pacman.getY() + pacman.getPacmanWidth() / 2);
        if (pacmanSquare1 != null) {
            hx = pacmanSquare1.i;
            hy = pacmanSquare1.j;
            if ((hx == 0 || hx == PacmanView.MAZE_WIDTH - 1) &&
                    (hy == 0 || hy == PacmanView.mazeHeight - 1)) {
                hx = PacmanView.MAZE_WIDTH - 1;
                hy = PacmanView.mazeHeight - 1;
            }
        }

        this.pacmanSquare = getBestMaze(maze, hx, hy, hxg, hyg);
    }

    @Override
    public float getX() {
        return xPosition;
    }

    @Override
    public void setX(float x) {
        this.xPosition = (int) x;
    }

    @Override
    public float getY() {
        return yPosition;
    }

    @Override
    public void setY(float y) {
        this.yPosition = (int) y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawable.setBounds(xPosition, yPosition, xPosition + getGhostWidth(),
                yPosition + getGhostWidth());
        drawable.draw(canvas);
    }

    private static int getGhostWidth() {
        return MazeSquare.squareSize * 3 / 4;
    }

    public void setStartPosition(int startX, int startY) {
        xPosition = startX;
        yPosition = startY;
        this.startX = startX;
        this.startY = startY;
    }

    private void addTranslate(final int step) {
        if (getDirection() != null) {
            yPosition += getDirection().y * step;
            xPosition += getDirection().x * step;
        }
    }

    private void randomMovement(long now,
            List<RectF> walls) {

        addTranslate(SPEED);
        if (checkCollision(getBounds(), walls)) {
            addTranslate(-SPEED);
            setDirection(ghostDirections[random.nextInt(ghostDirections.length)]);
            addTranslate(SPEED);
            if (checkCollision(getBounds(), walls)) {
                addTranslate(-SPEED);
            }
        }
        if (now % 500 == 0) {
            setDirection(ghostDirections[random.nextInt(ghostDirections.length)]);
        }
    }

    public RectF getBounds() {
        bounds.set(xPosition, yPosition, (float) xPosition + getGhostWidth(),
                (float) yPosition + getGhostWidth());

        return bounds;
    }

    public final GhostStatus getStatus() {
        return status;
    }

    public final void setStatus(final GhostStatus status) {
        if (status == GhostStatus.AFRAID) {
            drawable = getResources().getDrawable(R.drawable.afraid_ghost, null);
        } else if (status == GhostStatus.DEAD) {
            drawable = getResources().getDrawable(R.drawable.dead_ghost, null);
        } else {
            drawable = getResources().getDrawable(color.getColor(), null);
        }

        this.status = status;
    }

}
