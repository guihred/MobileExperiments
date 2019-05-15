package red.guih.games.labyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View for displaying the labyrinth and the ball;
 * * Created by guilherme.hmedeiros on 27/01/2018.
 */

public class LabyrinthView extends View implements SensorEventListener {

    public static final int MAZE_WIDTH = 15;
    static int mazeHeight = 5;
    private final List<RectF> walls = Collections.synchronizedList(new ArrayList<>());
    private Paint paint = new Paint();
    private float speed = LabyrinthSquare.squareSize / 4F;
    private float xSpeed;
    private float ySpeed;
    private RectF bounds = new RectF();
    private LabyrinthSquare[][] maze;
    private float ballX;
    private float ballY;
    private Thread gameLoopThread;

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (RectF w : walls) {
            canvas.drawRect(w, paint);
        }
        canvas.drawCircle(ballX, ballY, LabyrinthSquare.squareSize / 4F, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (maze == null) {
            reset();
        }
    }

    public void reset() {
        LabyrinthSquare.setSquareSize(getWidth() / MAZE_WIDTH);
        setMazeHeight(getHeight());
        maze = initializeMaze(getContext());
        speed = LabyrinthSquare.squareSize / 20F;
        ballX = LabyrinthSquare.squareSize / 2F;
        ballY = LabyrinthSquare.squareSize / 2F;

        CreateLabyrinth.createLabyrinth(maze);
        paint.setColor(Color.BLACK);

        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                walls.addAll(maze[i][j].updateWalls());
            }
        }

        continueGame();
        invalidate();
    }

    private static void setMazeHeight(int height) {
        mazeHeight = height / LabyrinthSquare.squareSize;
    }

    private static LabyrinthSquare[][] initializeMaze(Context c) {
        LabyrinthSquare[][] maze = new LabyrinthSquare[MAZE_WIDTH][mazeHeight];
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                maze[i][j] = new LabyrinthSquare(c, i, j);
                if (i == 0) {
                    maze[0][j].setNorth(false);
                }
                if (j == 0) {
                    maze[i][0].setWest(false);
                }
                if (mazeHeight - 1 == j) {
                    maze[i][mazeHeight - 1].setEast(false);
                }
                if (MAZE_WIDTH - 1 == i) {
                    maze[MAZE_WIDTH - 1][j].setSouth(false);
                }
            }
        }
        return maze;
    }

    void continueGame() {
        if (gameLoopThread == null || !gameLoopThread.isAlive()) {
            gameLoopThread = new Thread(this::gameLoop);
            gameLoopThread.start();
        }
    }

    private void gameLoop() {
        while (updateBall()) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("GAME LOOP", "ERROR IN GAME LOOP", e);
            }
        }
    }

    boolean updateBall() {
        for (int i = 0; i < 10; i++) {
            ballX -= xSpeed * speed;
            if (checkCollision(walls)) {
                ballX += xSpeed * speed;
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            ballY += ySpeed * speed;
            if (checkCollision(walls)) {
                ballY -= ySpeed * speed;
                break;
            }
        }
        postInvalidate();
        return true;
    }

    private boolean checkCollision(Iterable<RectF> observableList) {
        RectF boundSqr = getBounds();
        for (RectF p : observableList) {
            if (RectF.intersects(p, boundSqr)) {
                return true;
            }
        }
        return false;
    }

    RectF getBounds() {
        bounds.set(ballX - LabyrinthSquare.squareSize / 4F,
                ballY - LabyrinthSquare.squareSize / 4F,
                ballX + LabyrinthSquare.squareSize / 4F, ballY + LabyrinthSquare.squareSize / 4F);
        return bounds;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xSpeed = sensorEvent.values[0];
        ySpeed = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // DOES NOTHING
    }
}
