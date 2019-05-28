
package red.guih.games.pacman;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import red.guih.games.BaseView;
import red.guih.games.R;

import static java.util.stream.Stream.of;


public class PacmanView extends BaseView {

    public static final int MAZE_WIDTH = 5;
    public static final int GHOST_AFRAID_TIME = 200;
    public static final int SWEEP_ANGLE = 270;
    public static final int START_ANGLE = 45;
    public static final float INITIAL_POSITION = MazeSquare.squareSize / 4F;
    static int mazeHeight = 5;
    private final Pacman pacman;
    private final List<PacmanGhost> ghosts;
    boolean gamePaused;
    Random random = new Random();
    private int lifeCount = 3;
    private float startX;
    private float startY;
    private List<RectF> walls = new ArrayList<>();
    private Thread gameLoopThread;
    private List<PacmanBall> balls = new ArrayList<>();
    private Integer points = 0;
    private long time;
    private MazeSquare[][] maze;

    public PacmanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pacman = new Pacman(this);
        ghosts =
                of(GhostColor.RED, GhostColor.BLUE, GhostColor.ORANGE, GhostColor.GREEN)
                        .map((GhostColor color) -> new PacmanGhost(color, context))
                        .collect(Collectors.toList());
        createBalls();
    }

    private void createBalls() {
        float middle = MazeSquare.squareSize / 2F;
        balls = DoubleStream
                .iterate(middle, d -> d + MazeSquare.squareSize)
                .limit(MAZE_WIDTH * 2L)
                .boxed()
                .flatMap(
                        d -> DoubleStream.iterate(middle,
                                e -> e + MazeSquare.squareSize)
                                         .limit(mazeHeight * 2L)
                                         .mapToObj((double e) -> new PacmanBall(d, e)))
                .collect(Collectors.toList());
    }

    private static MazeSquare[][] initializeMaze() {
        MazeSquare[][] maze = new MazeSquare[MAZE_WIDTH][mazeHeight];
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                maze[i][j] = new MazeSquare(i, j);
                if (i == 0) {
                    maze[0][j].setNorth(false);
                }
                if (j == 0) {
                    maze[i][0].setWest(false);
                }
                if (mazeHeight - 1 == j && i % 3 == 0) {
                    maze[i][mazeHeight - 1].setEast();
                }
                if (MAZE_WIDTH - 1 == i && j % 3 == 0) {
                    maze[MAZE_WIDTH - 1][j].setSouth();
                }
            }
        }
        return maze;
    }

    private static void adjustDimensions(int width, int height) {
        MazeSquare.squareSize = width / MAZE_WIDTH / 2;
        mazeHeight = height / MazeSquare.squareSize / 2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
            startY = event.getY();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            setDirection(event);
            return true;
        }
        return false;
    }

    private void setDirection(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (Math.abs(startX - x) > Math.abs(startY - y)) {
            if (startX - x > 0) {
                pacman.turn(PacmanDirection.LEFT);
            } else {
                pacman.turn(PacmanDirection.RIGHT);
            }
        } else {
            if (startY - y > 0) {
                pacman.turn(PacmanDirection.UP);
            } else {
                pacman.turn(PacmanDirection.DOWN);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                maze[i][j].draw(canvas);
            }
        }
        for (int i = 0; i < balls.size(); i++) {
            PacmanBall g = balls.get(i);
            g.draw(canvas);
        }
        for (PacmanGhost g : ghosts) {
            g.draw(canvas);
        }
        pacman.draw(canvas);
        drawLives(canvas);


    }

    private void drawLives(Canvas canvas) {
        float y = mazeHeight * 2 * MazeSquare.squareSize + MazeSquare.squareSize / 8F;
        for (int i = 0; i < lifeCount; i++) {
            float x = MAZE_WIDTH * 3F / 5 * MazeSquare.squareSize + i * MazeSquare.squareSize;
            canvas.drawArc(x, y, x + pacman.getPacmanWidth() * 2 / 3,
                    y + pacman.getPacmanWidth() * 2 / 3, START_ANGLE, SWEEP_ANGLE, true,
                    pacman.paint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reset();
    }

    public void reset() {
        adjustDimensions(getWidth(), getHeight());
        maze = initializeMaze();

        CreateMazeHandler eventHandler = new CreateMazeHandler(maze);
        eventHandler.handle();


        walls = new ArrayList<>();
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                walls.addAll(maze[i][j].updateWalls());
                maze[i][j].dijkstra(maze);
            }
        }
        createBalls();

        for (int i = 0; i < 5; i++) {
            int nextInt = random.nextInt(balls.size());
            PacmanBall pacmanBall = balls.get(nextInt);
            pacmanBall.setSpecial();
        }
        pacman.turn(PacmanDirection.RIGHT);
        pacman.setY(INITIAL_POSITION);
        pacman.setX(INITIAL_POSITION);
        for (int i = 0; i < ghosts.size(); i++) {
            PacmanGhost ghost = ghosts.get(i);
            ghost.setStatus(GhostStatus.ALIVE);
            if (i == 0) {
                ghost.setStartPosition(MazeSquare.squareSize * (MAZE_WIDTH - 1),
                        MazeSquare.squareSize * (mazeHeight - 1));
            } else {
                ghost.setStartPosition(i % 2 * MazeSquare.squareSize * (2 * MAZE_WIDTH - 1),
                        i / 2 * MazeSquare.squareSize * (2 * mazeHeight - 1));
            }
        }
        continueGame();
        lifeCount = 3;
    }

    void continueGame() {
        gamePaused = false;
        if (gameLoopThread == null || !gameLoopThread.isAlive()) {
            gameLoopThread = new Thread(() -> {
                if (MazeSquare.squareSize > 0) {
                    while (gameLoop(System.currentTimeMillis())) {
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {
                            Log.e("GAME LOOP", "ERROR IN GAME LOOP", e);
                        }
                    }
                }

            });
            gameLoopThread.start();
        }
    }

    private void showDialog() {

        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(balls.isEmpty() ? R.string.you_win : R.string.you_lose);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(balls.isEmpty() ? R.string.you_win : R.string.you_lose);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener((View v) -> {
            PacmanView.this.reset();
            dialog.dismiss();
            invalidate();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void pause() {
        gamePaused = true;
    }

    private boolean gameLoop(long now) {
        if (gamePaused) {
            return false;
        }
        postInvalidate();
        if (balls.isEmpty()) {
            post(this::showDialog);
            return false;
        }
        ghosts.forEach(g -> g.move(now, walls, pacman, maze));
        pacman.move(walls);
        List<PacmanBall> bal =
                balls.stream().filter(b -> RectF.intersects(b.getBounds(), pacman.getBounds()))
                     .collect(Collectors.toList());
        if (!bal.isEmpty()) {

            setPoints(getPoints() + bal.size());
            balls.removeAll(bal);
            if (bal.stream().anyMatch(PacmanBall::isSpecial)) {
                ghosts.stream().filter(g -> g.getStatus() == GhostStatus.ALIVE)
                      .forEach(g -> g.setStatus(GhostStatus.AFRAID));
                time = GHOST_AFRAID_TIME;
            }
        }
        List<PacmanGhost> gh =
                ghosts.stream().filter(b -> RectF.intersects(b.getBounds(), pacman.getBounds()))
                      .collect(Collectors.toList());
        if (!gh.isEmpty()) {
            if (gh.stream().anyMatch(g -> g.getStatus() == GhostStatus.ALIVE)) {
                if (lifeCount > 0) {
                    lifeCount--;
                    pacman.turn(PacmanDirection.RIGHT);
                    pacman.setY(INITIAL_POSITION);
                    pacman.setX(INITIAL_POSITION);
                    return true;
                }

                pacman.die();
                post(this::showDialog);
                return false;
            } else {
                gh.forEach(g -> g.setStatus(GhostStatus.DEAD));
            }
        }
//
        if (time > 0) {
            time--;
            if (time == 0) {
                ghosts.stream().filter(g -> g.getStatus() == GhostStatus.AFRAID)
                      .forEach(g -> g.setStatus(GhostStatus.ALIVE));
            }
        }

        return true;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
