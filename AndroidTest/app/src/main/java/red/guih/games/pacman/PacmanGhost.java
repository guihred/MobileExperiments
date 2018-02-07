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
import java.util.Random;

import red.guih.games.R;

@SuppressLint("ViewConstructor")
public class PacmanGhost extends View {
    final int speed = 5;
    int x, y;
    Drawable drawable;
    Paint black = new Paint(Color.BLACK);
    GhostDirection[] ghostDirections = GhostDirection.values();
    RectF bounds = new RectF(x, y, x + MazeSquare.SQUARE_SIZE * 3 / 4, y + MazeSquare.SQUARE_SIZE * 3 / 4);
    private GhostColor color;
    private GhostStatus status = GhostStatus.ALIVE;
    private GhostDirection direction = GhostDirection.NORTH;
    private Eye leftEye = new Eye();
    private Eye rightEye = new Eye();
    private double startX;
    private double startY;
    private Random random = new Random();

    public PacmanGhost(GhostColor color, Context c) {
        super(c);
        this.color = color;
        drawable = getResources().getDrawable(color.getColor(), null);
        drawable.setBounds(0, 0, MazeSquare.SQUARE_SIZE, MazeSquare.SQUARE_SIZE);
        black.setColor(Color.BLACK);
//        a.intersect();
//		polygon.fillProperty()
//				.bind(Bindings.when(status.isEqualTo(GhostStatus.ALIVE)).then(color.color)
//						.otherwise(Bindings.when(status.isEqualTo(GhostStatus.AFRAID)).then(Color.BLUEVIOLET)
//								.otherwise(Color.TRANSPARENT)));
//		polygon.getPoints().addAll(-12D, 0D, -12D, 20D, -8D, 10D, -4D, 20D, 0D, 10D, 4D, 20D, 8D, 10D, 12D, 20D, 12D,
//				0D);
//
//		Ellipse ellipse = new Ellipse(4, 6);
//		ellipse.setFill(Color.WHITE);
//		ellipse.setLayoutX(-5);
//		Ellipse ellipse2 = new Ellipse(4, 6);
//		ellipse2.setFill(Color.WHITE);
//		ellipse2.setLayoutX(5);
//		ellipse.fillProperty().bind(
//				Bindings.when(status.isEqualTo(GhostStatus.AFRAID)).then(Color.TRANSPARENT).otherwise(Color.WHITE));
//		ellipse2.fillProperty().bind(
//				Bindings.when(status.isEqualTo(GhostStatus.AFRAID)).then(Color.TRANSPARENT).otherwise(Color.WHITE));
//
//		rightEye.setLayoutX(5);
//		rightEye.setLayoutY(2);
//		leftEye.setLayoutX(-5);
//		leftEye.setLayoutY(2);
//
//		getChildren().add(polygon);
//		getChildren().add(ellipse);
//		getChildren().add(ellipse2);
//		getChildren().add(rightEye);
//		getChildren().add(leftEye);
    }

    boolean checkColision(RectF boundsInParent, Collection<RectF> walls) {

        return walls.parallelStream().anyMatch(b -> RectF.intersects(b, boundsInParent));

    }

    public GhostDirection getDirection() {
        return direction;
    }

    public void setDirection(GhostDirection direction) {
        adjustEyes(-1);
        this.direction = direction;
        adjustEyes(1);
    }

    public void move(long now, List<RectF> observableList, Pacman pacman) {
        if (status == GhostStatus.ALIVE) {
            shortestMovement(now, observableList, pacman);
        } else if (status == GhostStatus.DEAD) {

            if (startX > x) {
                x += speed;
            } else if (startX < x) {
                x -= speed;
            }
            if (startY > y) {
                y += speed;
            } else if (startY < y) {
                y -= speed;
            }
            if (Math.abs(startX - x) < 3 && Math.abs(startY - y) < 3) {
                setStatus(GhostStatus.ALIVE);
            }
        } else {
            randomMovement(now, observableList);
        }
    }

    void shortestMovement(long now, List<RectF> otherNodes, Pacman pacman) {

        if (pacman == null) {
            randomMovement(now, otherNodes);
            return;
        }
        double hx = x - pacman.getX();
        double hy = y - pacman.getY();

        addTranslate(speed);
        if (checkColision(getBounds(), otherNodes)
                || now % 200 == 0
                ) {
            addTranslate(-speed);
            setDirection(changeDirection(hx, hy));
            addTranslate(speed);
            if (checkColision(getBounds(), otherNodes)) {
                addTranslate(-speed);
                randomMovement(now,otherNodes);
            }

        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = (int) x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = (int) y;
    }

    public void setStartPosition(int startX, int startY) {
        x = startX;
        y = startY;
        this.startX = startX;
        this.startY = startY;
    }

    private GhostDirection changeDirection(double hx, double hy) {
        if (Math.abs(Math.abs(hx) - Math.abs(hy)) < 30) {
            if (hx > 0) {
                return hy < 0 ? GhostDirection.NORTHWEST : GhostDirection.SOUTHWEST;
            }
            return hy > 0 ? GhostDirection.SOUTHEAST : GhostDirection.NORTHEAST;
        }
        if (Math.abs(hx) > Math.abs(hy)) {
            return hx > 0 ? GhostDirection.EAST : GhostDirection.WEST;
        }
        return hy < 0 ? GhostDirection.SOUTH : GhostDirection.NORTH;
    }

    private void addTranslate(final int step) {
        if (getDirection() != null) {
            y += getDirection().y * step;
            x += getDirection().x * step;
        }
    }

    private void randomMovement(long now,
                                List<RectF> walls) {

        addTranslate(speed);
        if (checkColision(getBounds(), walls)) {
            addTranslate(-speed);
            setDirection(ghostDirections[random.nextInt(ghostDirections.length)]);
        }


        if (now % 500 == 0) {
            setDirection(ghostDirections[random.nextInt(ghostDirections.length)]);
        }
    }

    private void adjustEyes(int mul) {
        rightEye.x += mul * direction.x;
        rightEye.y += mul * direction.y;
        leftEye.y += mul * direction.y;
        leftEye.x += mul * direction.x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawable.setBounds(x, y, x + MazeSquare.SQUARE_SIZE * 3 / 4, y + MazeSquare.SQUARE_SIZE * 3 / 4);
        drawable.draw(canvas);
    }

    public RectF getBounds() {
        bounds.set(x, y, x + MazeSquare.SQUARE_SIZE * 3 / 4, y + MazeSquare.SQUARE_SIZE * 3 / 4);

        return bounds;
    }

    public final GhostStatus getStatus() {
        return status;
    }

    public final void setStatus(final GhostStatus status) {
        if(status==GhostStatus.AFRAID){
            drawable = getResources().getDrawable(R.drawable.afraid_ghost, null);
        } else  if(status==GhostStatus.DEAD){
            drawable = getResources().getDrawable(R.drawable.dead_ghost, null);
        }else{
            drawable = getResources().getDrawable(color.getColor(), null);
        }

        this.status = status;
    }

    public enum GhostColor {
        RED(R.drawable.red_ghost),
        BLUE(R.drawable.blue_ghost),
        ORANGE(R.drawable.orange_ghost),
        GREEN(R.drawable.green_ghost);

        private final int color;

        GhostColor(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    public enum GhostDirection {
        EAST(-1, 0),
        NORTH(0, 2),
        SOUTH(0, -2),
        WEST(1, 0),
        NORTHEAST(-1, 1),
        SOUTHEAST(-1, -1),
        NORTHWEST(1, 1),
        SOUTHWEST(1, -1);
        private final int x;
        private final int y;

        GhostDirection(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    public enum GhostStatus {
        ALIVE,
        AFRAID,
        DEAD
    }

    class Eye {
        float x, y;
    }

}
