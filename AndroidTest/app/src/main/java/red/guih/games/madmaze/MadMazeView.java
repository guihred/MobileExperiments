package red.guih.games.madmaze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MadMazeView extends View implements SensorEventListener {
    static final float SQR_ROOT_OF_3 = 1.7320508075688772f;
    List<MadCell> allCells = new ArrayList<>();
    List<MadEdge> allEdges = new ArrayList<>();
    private Paint paint = new Paint();
    private Thread gameLoopThread;
    private float speed;
    float ballx, bally;
    float triangleSide;

    float xSpeed, ySpeed;

    public MadMazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        execute(getWidth(), getHeight());
    }


    public void execute(float maxWidth, float maxHeight) {
        int sqrt = 10;
        triangleSide = maxWidth / sqrt;
        int m = (int) (maxHeight / triangleSide / SQR_ROOT_OF_3 * 2) + 1;
        speed = triangleSide / 50;
        int size = sqrt * m;
        for (int i = 0; i < size; i++) {
            MadCell cell = new MadCell(i);
            float x = i % sqrt * triangleSide + (i / sqrt % 2 == 0 ? 0 : -triangleSide / 2) + triangleSide * 3 / 4;
            int j = i / sqrt;
            float k = j * triangleSide;
            float y = k * SQR_ROOT_OF_3 / 2 + triangleSide / 2;
            cell.relocate(x, y);
            allCells.add(cell);
            if (i == 0) {
                ballx = x;
                bally = y + triangleSide * 3 / 4;
            }


        }
        allEdges = CreateMadMaze.createLabyrinth(allCells);
        continueGame();
    }

    void continueGame() {
        if (gameLoopThread == null || !gameLoopThread.isAlive()) {
            gameLoopThread = new Thread(() -> {

                while (updateBall()) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        Log.e("GAME LOOP", "ERRO DE GAME LOOP", e);
                    }
                }

            });
            gameLoopThread.start();
        }
    }


    boolean updateBall() {
//        for (int i = 0; i < 10; i++) {
        ballx -= xSpeed * speed;
        if (checkCollision(allEdges)) {
            ballx += xSpeed * speed;

        }
//        }
//        for (int i = 0; i < 10; i++) {
        bally += ySpeed * speed;
        if (checkCollision(allEdges)) {
            bally -= ySpeed * speed;

        }
//        }
        postInvalidate();
        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xSpeed = sensorEvent.values[0];
        xSpeed = Math.abs(xSpeed) > 10 ? Math.signum(xSpeed) * 10 : xSpeed;

        ySpeed = sensorEvent.values[1];
        ySpeed = Math.abs(ySpeed) > 10 ? Math.signum(ySpeed) * 10 : ySpeed;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean checkCollision(List<MadEdge> observableList) {
        for (MadEdge p : observableList) {
            if (p.checkCollisionBounds(ballx, bally) && distance(p) < triangleSide / 5 - 1)
                return true;
        }
        return false;
    }

    float distance(MadEdge edge) {
        return edge.distance(ballx, bally);
    }


    @Override
    protected void onDraw(Canvas gc) {
        for (MadEdge c : allEdges) {
            gc.drawLine(c.getSource().x, c.getSource().y, c.getTarget().x, c.getTarget().y, paint);
        }
        gc.drawCircle(ballx, bally, triangleSide / 5, paint);
    }


}