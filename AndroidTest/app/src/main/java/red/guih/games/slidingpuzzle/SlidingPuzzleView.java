/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.slidingpuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

/**
 * @author Note
 */
public class SlidingPuzzleView extends BaseView {

    public static int MAP_WIDTH = 4;
    public static int MAP_HEIGHT = 4;


    private SlidingPuzzleSquare[][] map;
    private int moves;
    private int squareSize;
    private Paint paint = new Paint();

    public SlidingPuzzleView(Context c, AttributeSet attrs) {
        super(c, attrs);

        reset();
        paint.setStyle(Paint.Style.STROKE);

    }

//    final EventHandler<MouseEvent> createMouseClickedEvento(SlidingPuzzleSquare mem) {
//        return e -> slideIfPossible(mem);
//    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        squareSize = getWidth() / MAP_HEIGHT;
        MAP_WIDTH = getHeight() / squareSize;
        paint.setTextSize(squareSize / 2);

        reset();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int i = (int) (event.getY() / squareSize);
                int j = (int) (event.getX() / squareSize);
                if (i < MAP_WIDTH && j < MAP_HEIGHT) {
                    slideIfPossible(map[i][j]);
                }

            }
        }


        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                canvas.drawRect(j * squareSize, i * squareSize, (j + 1) * squareSize, (i + 1) * squareSize, paint);
                if (!map[i][j].isEmpty()) {
                    String text = "" + map[i][j].getNumber();
                    canvas.drawText(text, j * squareSize + squareSize / 3 / text.length(), i * squareSize + squareSize * 2 / 3, paint);
                }

            }
        }
    }

    private void slideIfPossible(SlidingPuzzleSquare mem) {
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                if (map[i][j] == mem && (
                        isNeighborEmpty(i, j, 0, -1)
                                || isNeighborEmpty(i, j, 0, 1)
                                || isNeighborEmpty(i, j, 1, 0)
                                || isNeighborEmpty(i, j, -1, 0))) {
                    swapEmptyNeighbor(i, j);
                    moves++;
                    invalidate();
                    if (verifyEnd()) {
                        showDialogWinning();
                    }
                    return;
                }
            }
        }
    }

    private void showDialogWinning() {
        invalidate();

        String s = getResources().getString(R.string.you_win);
        String format = String.format(s, moves + " moves");
        if (isRecordSuitable(moves, UserRecord.SLIDING_PUZZLE, MAP_WIDTH, true)) {
            createRecordIfSuitable(moves, format, UserRecord.SLIDING_PUZZLE, MAP_WIDTH, true);
            showRecords(MAP_WIDTH, UserRecord.SLIDING_PUZZLE, this::reset);
            return;
        }


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(format);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            this.reset();

            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    boolean isNeighborEmpty(int i, int j, int h, int v) {
        return isWithinRange(i, j, h, v) && map[i + h][j + v].isEmpty();
    }

    private boolean isWithinRange(int i, int j, int h, int v) {
        return i + h >= 0 && i + h < MAP_WIDTH && j + v >= 0 && j + v < MAP_HEIGHT;
    }

    public static void setPuzzleDimensions(int progress) {
        MAP_HEIGHT = progress;

    }

    final void reset() {
        map = new SlidingPuzzleSquare[MAP_WIDTH][MAP_HEIGHT];
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                map[i][j] = new SlidingPuzzleSquare(i * MAP_HEIGHT + j + 1);
            }
        }
        final Random random = new Random();
        int emptyI = MAP_WIDTH - 1, emptyJ = MAP_HEIGHT - 1;
        for (int i = 0; i < 125 * MAP_HEIGHT; i++) {
            int nextI = random.nextInt(3) - 1;
            int nextJ = random.nextInt(3) - 1;
            if (swapEmptyNeighbor(emptyI, emptyJ, nextI, nextJ)) {
                emptyI += nextI;
                emptyJ += nextJ;
            }

        }
        invalidate();
    }

    final boolean swapEmptyNeighbor(int i, int j, int k, int l) {

        if ((k == 0 || l == 0) && k != l && map[i][j].isEmpty() && isWithinRange(i, j, k, l)) {
            final SlidingPuzzleSquare empty = map[i + k][j + l];
            map[i + k][j + l] = map[i][j];
            map[i][j] = empty;
            return true;
        }
        return false;
    }

    final void swapEmptyNeighbor(int i, int j) {
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                if ((k == 0 || l == 0) && k != l && isNeighborEmpty(i, j, k, l)) {
//                    gridPane.getChildren().remove(map[i][j].getStack());
//                    gridPane.getChildren().remove(map[i + k][j + l].getStack());
//                    gridPane.add(map[i][j].getStack(), j + l, i + k);
//                    gridPane.add(map[i + k][j + l].getStack(), j, i);
                    final SlidingPuzzleSquare empty = map[i + k][j + l];
                    map[i + k][j + l] = map[i][j];
                    map[i][j] = empty;
                }
            }
        }
    }

    boolean verifyEnd() {
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                if (map[i][j].getNumber() != i * MAP_HEIGHT + j + 1) {
                    return false;
                }
            }
        }

        return true;
    }


}