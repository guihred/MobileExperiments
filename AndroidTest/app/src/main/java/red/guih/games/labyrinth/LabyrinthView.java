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
    public static int MAZE_HEIGHT = 5;
    Paint paint = new Paint();
    float speed = LabyrinthSquare.SQUARE_SIZE / 4;
    float xSpeed, ySpeed;
    RectF bounds = new RectF();
    private LabyrinthSquare[][] maze;
    private List<RectF> walls = Collections.synchronizedList(new ArrayList<RectF>());
    private float ballx, bally;
    private Thread gameLoopThread;

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    private  LabyrinthSquare[][] initializeMaze(Context c) {
        LabyrinthSquare[][] maze = new LabyrinthSquare[MAZE_WIDTH][MAZE_HEIGHT];
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                maze[i][j] = new LabyrinthSquare(c, i, j);
                if (i == 0) {
                    maze[0][j].setNorth(false);
                }
                if (j == 0) {
                    maze[i][0].setWest(false);
                }
                if (MAZE_HEIGHT - 1 == j) {
                    maze[i][MAZE_HEIGHT - 1].setEast(false);
                }
                if (MAZE_WIDTH - 1 == i) {
                    maze[MAZE_WIDTH - 1][j].setSouth(false);
                }
            }
        }
        return maze;
    }

    public void reset() {
        LabyrinthSquare.SQUARE_SIZE = getWidth() / MAZE_WIDTH;
        MAZE_HEIGHT = getHeight() / LabyrinthSquare.SQUARE_SIZE;
        maze = initializeMaze(getContext());
        speed = LabyrinthSquare.SQUARE_SIZE / 20;
        ballx = LabyrinthSquare.SQUARE_SIZE / 2;
        bally = LabyrinthSquare.SQUARE_SIZE / 2;

        CreateLabyrinth.createLabyrinth(maze);
        paint.setColor(Color.BLACK);

        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                walls.addAll(maze[i][j].updateWalls());
            }
        }

        continueGame();
        invalidate();
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
                Log.e("GAME LOOP", "ERRO DE GAME LOOP", e);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (RectF w : walls) {
            canvas.drawRect(w, paint);
        }
        canvas.drawCircle(ballx, bally, LabyrinthSquare.SQUARE_SIZE / 4, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (maze == null) {
            reset();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xSpeed = sensorEvent.values[0];
        ySpeed = sensorEvent.values[1];
    }

    boolean updateBall() {
        for (int i = 0; i < 10; i++) {
            ballx -= xSpeed * speed;
            if (checkCollision(walls)) {
                ballx += xSpeed * speed;
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            bally += ySpeed * speed;
            if (checkCollision(walls)) {
                bally -= ySpeed * speed;
                break;
            }
        }
        postInvalidate();
        return true;
    }

    private boolean checkCollision(Iterable<RectF> observableList) {
        RectF bounds = getBounds();
        for (RectF p : observableList) {
            if (RectF.intersects(p, bounds)) {
                return true;
            }
        }
        return false;
    }

    RectF getBounds() {
        bounds.set(ballx - LabyrinthSquare.SQUARE_SIZE / 4, bally - LabyrinthSquare.SQUARE_SIZE / 4, ballx + LabyrinthSquare.SQUARE_SIZE / 4, bally + LabyrinthSquare.SQUARE_SIZE / 4);
        return bounds;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
