package red.guih.games.tetris;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import red.guih.games.R;

public class TetrisView extends View {

    public static final int UPDATE_DELAY_MILLIS = 750;
    public int mapHeight = 20;
    public int mapWidth = 10;
    boolean movedLeftRight = false;
    private int currentI, currentJ;
    private TetrisDirection direction = TetrisDirection.UP;
    private TetrisSquare[][] map = new TetrisSquare[mapWidth][mapHeight];
    private TetrisPiece piece = TetrisPiece.L;
    private Map<TetrisPiece, Map<TetrisDirection, int[][]>> pieceDirection = new EnumMap<>(TetrisPiece.class);
    private Paint paint = new Paint();
    private Random random = new Random();
    private float startX, startY;
    private int squareSize;
    private Thread gameLoopThread;
    private boolean gameOver;

    public TetrisView(Context c, AttributeSet a) {
        super(c, a);
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                map[i][j] = new TetrisSquare();
            }
        }

        for (TetrisPiece value : TetrisPiece.values()) {
            pieceDirection.put(value, new HashMap<>());
            pieceDirection.get(value).put(TetrisDirection.UP, value.getMap());

            int[][] right = rotateMap(value.getMap());
            pieceDirection.get(value).put(TetrisDirection.RIGHT, right);
            int[][] down = rotateMap(right);
            pieceDirection.get(value).put(TetrisDirection.DOWN, down);
            int[][] left = rotateMap(down);
            pieceDirection.get(value).put(TetrisDirection.LEFT, left);
        }

    }

    public TetrisDirection getDirection(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (Math.abs(startX - x) > Math.abs(startY - y)) {
            if ((startX - x) > 0) {
                return (TetrisDirection.LEFT);
            } else {
                return (TetrisDirection.RIGHT);
            }
        } else {
            if ((startY - y) > 0) {
                return (TetrisDirection.UP);
            } else {
                return TetrisDirection.DOWN;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        squareSize = getWidth() / mapWidth;
        mapHeight = getHeight() / squareSize;

        reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                TetrisPieceState state = map[i][j].getState();
                switch (state) {

                    case EMPTY:
                        paint.setColor(Color.BLACK);
                        break;
                    case TRANSITION:
                        paint.setColor(piece.getColor());
                        break;
                    case SETTLED:
                        paint.setColor(map[i][j].getColor());
                        break;
                }
                canvas.drawRect(i * squareSize, j * squareSize, (i + 1) * squareSize, (j + 1) * squareSize, paint);
            }
        }
        if (gameOver) {
            showDialog();
        }
    }

    private void showDialog() {

        gameOver = false;
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.you_lose);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(R.string.you_lose);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener((View v) -> {
            TetrisView.this.reset();
            dialog.dismiss();
            invalidate();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                User started to press the screen
               startX = e.getX();
                startY = e.getY();
                movedLeftRight = false;
                return true;
            case MotionEvent.ACTION_UP:
//                User ceased to press the screen
                TetrisDirection direction = getDirection(e);
                if (direction == TetrisDirection.UP && !movedLeftRight)
                    this.changeDirection();
                if (direction == TetrisDirection.DOWN && !movedLeftRight)
                    while (!this.checkCollision(this.getCurrentI(), this.getCurrentJ() + 1)) {
                        this.setCurrentJ(this.getCurrentJ() + 1);
                        this.clearMovingPiece();
                        this.drawPiece();
                    }
                break;
            case MotionEvent.ACTION_MOVE:
//                User moved to the left or right the screen
                TetrisDirection code = getDirection(e);
                int dx = Math.abs((int) ((startX - e.getX()) / squareSize));
                switch (code) {
                    case LEFT:

                        for (int i = 0; i < dx; i++)
                            if (!this.checkCollision(this.getCurrentI() - 1, this.getCurrentJ())) {
                                this.setCurrentI(this.getCurrentI() - 1);
                                startX -= squareSize;
                                this.clearMovingPiece();
                                this.drawPiece();
                                movedLeftRight = true;
                                invalidate();
                            }

                        break;
                    case RIGHT:
                        for (int i = 0; i < dx; i++)
                            if (!this.checkCollision(this.getCurrentI() + 1, this.getCurrentJ())) {
                                this.setCurrentI(this.getCurrentI() + 1);
                                startX += squareSize;
                                this.clearMovingPiece();
                                this.drawPiece();
                                movedLeftRight = true;
                                invalidate();
                            }
                        break;
                    default:
                }
        }
        return true;
    }

    void changeDirection() {
        TetrisDirection previousDirection = direction;
        direction = direction.next();
        int previous = getCurrentI();
        boolean changed = false;
        for (int i = 0; i < 3; i++) {
            int i1 = i == 2 ? -1 : i;
            if (getCurrentI() + i1 >= 0 && getCurrentI() + i1 < mapWidth)
                setCurrentI(previous + i1);
            if (!checkCollision(getCurrentI(), getCurrentJ())) {
                clearMovingPiece();
                drawPiece();
                changed = true;
                break;
            }
        }
        if (!changed) {
            setCurrentI(previous);
            direction = previousDirection;
        }

        invalidate();
    }

    boolean checkCollision(int nextI, int nextJ) {
        final int[][] get = pieceDirection.get(piece).get(direction);
        for (int i = 0; i < get.length; i++) {
            for (int j = 0; j < get[i].length; j++) {
                if (get[i][j] == 1) {
                    if (nextI + i >= mapWidth || nextI < 0) {
                        return true;
                    }
                    if (nextJ + j >= mapHeight) {
                        return true;
                    }
                    if (map[nextI + i][nextJ + j].getState() == TetrisPieceState.SETTLED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void continueGame() {
        if (gameLoopThread == null || !gameLoopThread.isAlive()) {
            gameLoopThread = new Thread(this::gameLoop);
            gameLoopThread.start();
        }
    }

    private void gameLoop() {
        while (movePiecesTimeline()) {
            try {
                postInvalidate();
                Thread.sleep(UPDATE_DELAY_MILLIS);
            } catch (Exception e) {
                Log.e("GAME LOOP", "ERRO DE GAME LOOP", e);
            }
        }
        gameOver = true;
        postInvalidate();
    }

    void clearMovingPiece() {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (map[i][j].getState() == TetrisPieceState.TRANSITION) {
                    map[i][j].setState(TetrisPieceState.EMPTY);
                }
            }
        }

    }


    void drawPiece() {
        drawPiece(TetrisPieceState.TRANSITION);
    }


    void drawPiece(TetrisPieceState state) {
        final int[][] get = pieceDirection.get(piece).get(direction);
        for (int i = 0; i < get.length; i++) {
            for (int j = 0; j < get[i].length; j++) {
                if (get[i][j] == 1) {
                    map[getCurrentI() + i][getCurrentJ() + j].setColor(piece.getColor());
                    map[getCurrentI() + i][getCurrentJ() + j].setState(state);
                }
            }
        }
    }

    public int getCurrentI() {
        return currentI;
    }

    public void setCurrentI(int currentI) {
        this.currentI = currentI;
    }

    public int getCurrentJ() {
        return currentJ;
    }

    public void setCurrentJ(int currentJ) {
        this.currentJ = currentJ;
    }

    public boolean movePiecesTimeline() {
        clearMovingPiece();
        if (!checkCollision(getCurrentI(), getCurrentJ() + 1)) {
            drawPiece();
            setCurrentJ(getCurrentJ() + 1);
            return true;
        } else {
            drawPiece(TetrisPieceState.SETTLED);
            final TetrisPiece[] values = TetrisPiece.values();
            piece = values[random.nextInt(values.length)];
            TetrisDirection[] directions = TetrisDirection.values();
            direction = directions[random.nextInt(directions.length)];
            setCurrentJ(0);
            setCurrentI(mapWidth / 2);
            if (checkCollision(getCurrentI(), getCurrentJ())) {
                return false;
            }
            for (int i = 0; i < mapHeight; i++) {
                boolean clearLine = isLineClear(i);
                if (clearLine) {
                    removeLine(i);
                }
            }

        }

        return true;
    }

    private boolean isLineClear(int i) {
        boolean clearLine = true;
        for (int j = 0; j < mapWidth; j++) {
            if (map[j][i].getState() != TetrisPieceState.SETTLED) {
                clearLine = false;
            }
        }
        return clearLine;
    }

    private void removeLine(int i) {
        for (int k = i; k >= 0; k--) {
            for (int j = 0; j < mapWidth; j++) {
                if (k <= 0) {
                    map[j][k].setState(TetrisPieceState.EMPTY);
                } else {
                    map[j][k].setState(map[j][k - 1].getState());
                }
            }
        }
    }

    void reset() {
        map = new TetrisSquare[mapWidth][mapHeight];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                map[i][j] = new TetrisSquare();
                map[i][j].setState(TetrisPieceState.EMPTY);
            }
        }
        continueGame();
    }

    final int[][] rotateMap(int[][] pieceMap) {
        int width = pieceMap.length;
        int height = pieceMap[0].length;
        int[][] left = new int[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                left[j][i] = pieceMap[i][height - j - 1];
            }
        }
        return left;
    }

}
