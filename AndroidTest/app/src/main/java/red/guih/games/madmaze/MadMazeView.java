package red.guih.games.madmaze;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;


public class MadMazeView extends BaseView implements SensorEventListener {
    static final float SQR_ROOT_OF_3 = 1.7320508075688772f;
    public static int DIFFICULTY = 10;
    List<MadCell> allCells = new ArrayList<>();
    List<MadEdge> allEdges = new ArrayList<>();
    float ballx, bally;
    float triangleSide;
    float xSpeed, ySpeed;
    private static int madMazeOption;
    private final Paint paint = new Paint();
    private final Paint ballPaint = new Paint();
    private Thread gameLoopThread;
    private float speed;
    private float ballRadius;
    private List<MadTriangle> balls;
    private long startTime;
    private boolean displaying;

    public MadMazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        ballPaint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reset();
    }

    private void reset() {
        displaying = false;
        startTime = System.currentTimeMillis();
        reset(getWidth(), getHeight());
    }


    public void reset(float maxWidth, float maxHeight) {
        if (madMazeOption == 0) {
            createSquarePoints(maxWidth, maxHeight);
        } else if (madMazeOption == 1) {
            createTrianglesPoints(maxWidth, maxHeight);
        } else {
            createHexagonalPoints(maxWidth, maxHeight);
        }
        CreateMadMaze labyrinth = CreateMadMaze.createLabyrinth(allCells);
        allEdges = labyrinth.allEdges;
        MadPoint center = labyrinth.triangles.get(0).getCenter();


        Random random = new Random();

        balls = new ArrayList<>();
        List<MadTriangle> deadEnds = labyrinth.triangles.stream().filter(MadTriangle::isDeadEnd).collect(Collectors.toList());
        int size = deadEnds.size();
        for (int i = 0; !deadEnds.isEmpty() && i < Math.min(size, DIFFICULTY); i++) {
            balls.add(deadEnds.remove(random.nextInt(deadEnds.size())));
        }


        ballx = center.getX();
        bally = center.getY();
        continueGame();
    }

    private void createTrianglesPoints(float maxWidth, float maxHeight) {
        int sqrt = DIFFICULTY;
        triangleSide = maxWidth / sqrt;
        ballRadius = triangleSide / 5;
        speed = triangleSide / 50;
        int m = (int) (maxHeight / triangleSide / SQR_ROOT_OF_3 * 2) + 1;
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
    }

    private void createHexagonalPoints(float maxWidth, float maxHeight) {
        int sqrt = DIFFICULTY;
        triangleSide = maxWidth / sqrt;
        speed = triangleSide / 50;
        ballRadius = triangleSide / 7;
        int m = (int) (maxHeight / triangleSide / SQR_ROOT_OF_3 * 2) + 1;
        int size = sqrt * m;
        for (int i = 0; i < size; i++) {
            if (i % 3 == 0) {
                continue;
            }
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
    }

    private void createSquarePoints(float maxWidth, float maxHeight) {
        allCells.clear();
        int sqrt = DIFFICULTY;
        triangleSide = maxWidth / sqrt;
        speed = triangleSide / 50;
        ballRadius = triangleSide / 5;
        int m = (int) (maxHeight / triangleSide);
        int size = sqrt * m;
        for (int i = 0; i < size; i++) {
            MadCell cell = new MadCell(i);
            float x = i % sqrt * triangleSide + triangleSide * 1 / 2;
            int j = i / sqrt;
            float y = j * triangleSide + triangleSide * 1 / 2;
            cell.relocate(x, y);
            allCells.add(cell);
            if (i == 0) {
                ballx = x + triangleSide * 1 / 2;
                bally = y + triangleSide * 1 / 2;
            }
        }
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

    public int getMadMazeOption() {
        return madMazeOption;
    }

    public static void setMadMazeOption(int madMazeOption) {
        MadMazeView.madMazeOption = madMazeOption;
    }

    public static void setDifficulty(int mazeSize) {
        MadMazeView.DIFFICULTY =  mazeSize* 5;
    }


    boolean updateBall() {
        for (int i = 0; i < 5; i++) {
            ballx -= xSpeed * speed;
            if (checkCollision(allEdges)) {
                ballx += xSpeed * speed;
                break;
            }
        }
        for (int i = 0; i < 5; i++) {
            bally += ySpeed * speed;
            if (checkCollision(allEdges)) {
                bally -= ySpeed * speed;
                break;
            }
        }
        for (Iterator<MadTriangle> iterator = balls.iterator(); iterator.hasNext(); ) {
            MadTriangle e = iterator.next();
            float a = e.getCenter().getX() - ballx;
            float b = e.getCenter().getY() - bally;
            if (a * a + b * b <= ballRadius * ballRadius) {
                iterator.remove();
            }
        }
        postInvalidate();
        return !balls.isEmpty();


    }

    private void showDialogWinning() {
        displaying = true;
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        long emSegundos = (System.currentTimeMillis() - startTime) / 1000;
        String s = getResources().getString(R.string.time_format);
        String format = String.format(s, emSegundos / 60, emSegundos % 60);

        if (isRecordSuitable(emSegundos, UserRecord.MAD_MAZE, DIFFICULTY, true)) {
            createRecordIfSuitable(emSegundos, format, UserRecord.MAD_MAZE, DIFFICULTY, true);
            showRecords(DIFFICULTY, UserRecord.MAD_MAZE, this::reset);
            return;
        }
        text.setText(String.format(getResources().getString(R.string.you_win), format));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog


        dialogButton.setOnClickListener(v -> {
            this.reset();
            dialog.dismiss();

            invalidate();

        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xSpeed = sensorEvent.values[0];
        xSpeed = Math.signum(xSpeed) * Math.min(Math.abs(xSpeed), 10);

        ySpeed = sensorEvent.values[1];
        ySpeed = Math.signum(ySpeed) * Math.min(Math.abs(ySpeed), 10);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean checkCollision(Iterable<MadEdge> edges) {
        for (MadEdge p : edges) {
            if (p.checkCollisionBounds(ballx, bally) && distance(p) < ballRadius)
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
        for (int i = 0; i < balls.size(); i++) {
            MadTriangle e = balls.get(i);
            gc.drawCircle(e.getCenter().getX(), e.getCenter().getY(), ballRadius / 2, ballPaint);
        }
        gc.drawCircle(ballx, bally, ballRadius, paint);
        if (balls.isEmpty() && !displaying) {
            showDialogWinning();
        }
    }


}