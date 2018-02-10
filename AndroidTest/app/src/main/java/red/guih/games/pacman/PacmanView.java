
package red.guih.games.pacman;

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
    public static int MAZE_HEIGHT = 5;
    private final Pacman pacman;
    float startX, startY;
    List<RectF> walls;
    Thread gameLoopThread;
    boolean gameOver = false;
    private List<PacmanBall> balls = new ArrayList<>();
    private List<PacmanGhost> ghosts;
    private Integer points = 0;
    private long time;
    private MazeSquare[][] maze;

    public PacmanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pacman = new Pacman(this);
        ghosts =
                of(PacmanGhost.GhostColor.RED, PacmanGhost.GhostColor.BLUE, PacmanGhost.GhostColor.ORANGE, PacmanGhost.GhostColor.GREEN)
                        .map((PacmanGhost.GhostColor color) -> new PacmanGhost(color, context))
                        .collect(Collectors.toList());
    }

    private static MazeSquare[][] initializeMaze(Context c) {
        MazeSquare[][] maze = new MazeSquare[MAZE_WIDTH][MAZE_HEIGHT];
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                maze[i][j] = new MazeSquare(i, j);
                if (i == 0) {
                    maze[0][j].setNorth(false);
                }
                if (j == 0) {
                    maze[i][0].setWest(false);
                }
                if (MAZE_HEIGHT - 1 == j && i % 3 == 0) {
                    maze[i][MAZE_HEIGHT - 1].setEast();
                }
                if (MAZE_WIDTH - 1 == i && j % 3 == 0) {
                    maze[MAZE_WIDTH - 1][j].setSouth();
                }
            }
        }
        return maze;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                return true;
            case MotionEvent.ACTION_MOVE:
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
                return true;
            default:
                break;
        }


        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
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
        if (gameOver) {
            showDialog();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reset();
    }

    public void reset() {
        MazeSquare.SQUARE_SIZE = getWidth() / MAZE_WIDTH / 2;
        MAZE_HEIGHT = getHeight() / MazeSquare.SQUARE_SIZE / 2;
        maze = initializeMaze(getContext());
        CreateMazeHandler eventHandler = new CreateMazeHandler(maze);
        eventHandler.handle();

        walls = new ArrayList<>();
        for (int i = 0; i < MAZE_WIDTH; i++) {
            for (int j = 0; j < MAZE_HEIGHT; j++) {
                walls.addAll(maze[i][j].updateWalls());
            }
        }
        balls = DoubleStream
                .iterate(MazeSquare.SQUARE_SIZE / 2, d -> d + MazeSquare.SQUARE_SIZE)
                .limit(MAZE_WIDTH * 2)
                .boxed()
                .flatMap(
                        d -> DoubleStream.iterate(MazeSquare.SQUARE_SIZE / 2, e -> e + MazeSquare.SQUARE_SIZE)
                                .limit(MAZE_HEIGHT * 2)
                                .mapToObj((double e) -> new PacmanBall(d, e)))
                .collect(Collectors.toList());

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int nextInt = random.nextInt(balls.size());
            PacmanBall pacmanBall = balls.get(nextInt);
            pacmanBall.setSpecial();
        }
        pacman.turn(PacmanDirection.RIGHT);
        pacman.setY(MazeSquare.SQUARE_SIZE / 4);
        pacman.setX(MazeSquare.SQUARE_SIZE / 4);
        for (int i = 0; i < ghosts.size(); i++) {
            PacmanGhost ghost = ghosts.get(i);
            ghost.setStatus(PacmanGhost.GhostStatus.ALIVE);
            if (i == 0) {
                ghost.setStartPosition(MazeSquare.SQUARE_SIZE * (MAZE_WIDTH - 1), MazeSquare.SQUARE_SIZE * (MAZE_HEIGHT - 1));
            } else {
                ghost.setStartPosition(i % 2 * MazeSquare.SQUARE_SIZE * (2 * MAZE_WIDTH - 1),
                        i / 2 * MazeSquare.SQUARE_SIZE * (2 * MAZE_HEIGHT - 1));
            }
        }
        continueGame();
        gameOver = false;

    }

    void continueGame() {
        if (gameLoopThread == null || !gameLoopThread.isAlive()) {
            gameLoopThread = new Thread(() -> {
                while (gameLoop(System.currentTimeMillis())) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        Log.e("GAME LOOP", "ERRO DE GAME LOOP", e);
                    }
                }
                gameOver = true;
            });
            gameLoopThread.start();
        }
    }

    private void showDialog() {

        gameOver = false;
        final Dialog dialog = new Dialog(getContext());
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

    private boolean gameLoop(long now) {
        postInvalidate();
        if (balls.isEmpty()) {
            return false;
        }
        ghosts.forEach(g -> g.move(now, walls, pacman));
        pacman.move(walls);
        List<PacmanBall> bal = balls.stream().filter(b -> RectF.intersects(b.getBounds(), pacman.getBounds())).collect(Collectors.toList());
        if (!bal.isEmpty()) {

            setPoints(getPoints() + bal.size());
            balls.removeAll(bal);
            if (bal.stream().anyMatch(PacmanBall::isSpecial)) {
                ghosts.stream().filter(g -> g.getStatus() == PacmanGhost.GhostStatus.ALIVE)
                        .forEach(g -> g.setStatus(PacmanGhost.GhostStatus.AFRAID));
                time = GHOST_AFRAID_TIME;
            }
        }
        List<PacmanGhost> gh = ghosts.stream().filter(b -> RectF.intersects(b.getBounds(), pacman.getBounds()))
                .collect(Collectors.toList());
        if (!gh.isEmpty()) {
            if (gh.stream().anyMatch(g -> g.getStatus() == PacmanGhost.GhostStatus.ALIVE)) {
                pacman.die();
                return false;
            } else {
                gh.forEach(g -> g.setStatus(PacmanGhost.GhostStatus.DEAD));
            }
        }
//
        if (time > 0) {
            time--;
            if (time == 0) {
                ghosts.stream().filter(g -> g.getStatus() == PacmanGhost.GhostStatus.AFRAID)
                        .forEach(g -> g.setStatus(PacmanGhost.GhostStatus.ALIVE));
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
