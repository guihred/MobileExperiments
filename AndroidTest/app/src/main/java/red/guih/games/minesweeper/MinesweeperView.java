package red.guih.games.minesweeper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

import static java.lang.Math.abs;

/**
 * A view with the rules of minesweeper.
 * <p>
 * Created by guilherme.hmedeiros on 16/01/2018.
 */

public class MinesweeperView extends BaseView {
    public static final int BOMBS_STEP = 15;
    public static final int DELAY_LONG_PRESS = 500;
    static int numberOfBombs = 50;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private final Paint hiddenColor;
    private final Paint shownColor;
    private final Paint textPaint = new Paint(Color.BLACK);
    private int mapHeight = 30;
    private int mapWidth = 16;
    private int boxWidth = 50;
    private boolean goneFlag;
    private MinesweeperSquare[][] map = new MinesweeperSquare[mapWidth][mapHeight];
    private int nPlayed;
    private float pressedX;
    private float pressedY;
    private final Runnable mLongPressed = () -> {
        goneFlag = true;
        float x = pressedX;
        float y = pressedY;
        toggleFlag(getBestSquareMatch(x, y));
        invalidate();
    };
    private long startTime;

    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hiddenColor = new Paint();
        hiddenColor.setTypeface(Typeface.DEFAULT);
        hiddenColor.setShadowLayer(1, 5, 5, Color.BLACK);
        hiddenColor.setColor(Color.LTGRAY);
        textPaint.setTextSize(boxWidth - 5F);

        shownColor = new Paint();
        shownColor.setTypeface(Typeface.DEFAULT);
        shownColor.setColor(Color.WHITE);

        reset();
    }

    private void reset() {
        nPlayed = 0;
        initializeMines();
        addRandomBombs();
        setNumbersAroundBombs();
        startTime = System.currentTimeMillis();
        invalidate();
    }

    private void initializeMines() {
        map = new MinesweeperSquare[mapWidth][mapHeight];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                map[i][j] = new MinesweeperSquare(i, j);
                map[i][j].setMinesweeperImage(MinesweeperImage.BLANK);
                map[i][j].setState(MinesweeperSquare.State.HIDDEN);
            }
        }
    }

    private void addRandomBombs() {
        long count = 0;
        while (count < numberOfBombs) {
            int j = random.nextInt(mapWidth);
            int k = random.nextInt(mapHeight);

            final MinesweeperSquare mem = map[j][k];
            mem.setMinesweeperImage(MinesweeperImage.BOMB);
            count = countBombs();
        }
    }

    private void setNumbersAroundBombs() {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (map[i][j].getMinesweeperImage() == MinesweeperImage.BLANK) {
                    int num = countBombsAround(i, j);
                    if (num != 0) {
                        map[i][j].setNum(num);
                        map[i][j].setMinesweeperImage(MinesweeperImage.NUMBER);
                    }
                }
            }
        }
    }

    private long countBombs() {
        long n = 0;
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (map[i][j].getMinesweeperImage() == MinesweeperImage.BOMB) {
                    n++;
                }
            }
        }
        return n;
    }

    private int countBombsAround(int i, int j) {
        int num = 0;
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (l == 0 && k == 0) {
                    continue;
                }
                if (i + k >= 0 && i + k < mapWidth && j + l >= 0 && j + l < mapHeight
                        && map[i + k][j + l].getMinesweeperImage() == MinesweeperImage.BOMB) {
                    num++;
                }
            }
        }
        return num;
    }

    public static void setNumberOfBombs(int selectedItemPosition) {
        MinesweeperView.numberOfBombs = (selectedItemPosition + 1) * MinesweeperView.BOMBS_STEP;
    }

    private void clickEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        MinesweeperSquare mem = getBestSquareMatch(x, y);
        if (mem == null) {
            return;
        }
        if (mem.getState() == MinesweeperSquare.State.HIDDEN) {
            nPlayed++;
            mem.setState(MinesweeperSquare.State.SHOWN);
            if (mem.getMinesweeperImage() == MinesweeperImage.BOMB) {
                if (nPlayed == 0) {
                    reset();
                    return;
                }

                showDialogLose();
            }
            if (mem.getMinesweeperImage() == MinesweeperImage.BLANK) {
                showNeighbours(mem.getI(), mem.getJ());
            }
            if (verifyEnd()) {
                showDialogWinning();
            }

        }
        invalidate();
    }

    private MinesweeperSquare getBestSquareMatch(float x, float y) {
        MinesweeperSquare min = null;
        float minimum = Float.MAX_VALUE;
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {

                float abs = abs((i + 0.5F) * boxWidth - x);
                float abs2 = abs((j + 0.5F) * boxWidth - y);
                float a = abs * abs + abs2 * abs2;
                if (a < minimum) {
                    minimum = a;
                    min = map[i][j];
                }
            }
        }
        return min;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            pressedX = event.getX();
            pressedY = event.getY();
            handler.postDelayed(mLongPressed, DELAY_LONG_PRESS);
        } else if (action == MotionEvent.ACTION_UP) {
            handler.removeCallbacks(mLongPressed);
            if (!goneFlag) {
                clickEvent(event);
                return true;
            }
            goneFlag = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            handler.removeCallbacks(mLongPressed);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                MinesweeperSquare.State state = map[i][j].getState();
                switch (state) {
                    case HIDDEN:
                        drawHidden(canvas, i, j);
                        break;
                    case SHOWN:
                        float width = this.boxWidth;
                        canvas.drawRect(i * width, j * width, (i + 1) * width,
                                (j + 1) * width, shownColor);
                        drawImage(canvas, i, j);
                        break;
                    case FLAGGED:
                        drawHidden(canvas, i, j);
                        drawFlag(canvas, i, j);
                        break;
                }
            }
        }

    }

    private void drawHidden(Canvas canvas, int i, int j) {
        hiddenColor.setStyle(Paint.Style.FILL);
        hiddenColor.setColor(Color.LTGRAY);
        float width = this.boxWidth;
        canvas.drawRect(i * width, j * width, (i + 1) * width, (j + 1) * width,
                hiddenColor);
        hiddenColor.setStyle(Paint.Style.STROKE);
        hiddenColor.setColor(Color.BLACK);
        canvas.drawRect(i * width, j * width, (i + 1) * width, (j + 1) * width,
                hiddenColor);
    }

    private void drawImage(Canvas canvas, int i, int j) {
        MinesweeperImage minesweeperImage = map[i][j].getMinesweeperImage();
        switch (minesweeperImage) {
            case BLANK:
                break;
            case BOMB:
                drawBomb(canvas, i, j);
                break;
            case NUMBER:
                drawNumber(canvas, i, j);
                break;
        }
    }

    private void drawFlag(Canvas canvas, int i, int j) {
        Drawable drawable = getResources().getDrawable(R.drawable.flag, null);
        drawable.setBounds(i * boxWidth, j * boxWidth, (i + 1) * boxWidth, (j + 1) * boxWidth);
        drawable.draw(canvas);
    }

    private void drawBomb(Canvas canvas, int i, int j) {
        Drawable drawable = getResources().getDrawable(R.drawable.bomb, null);
        drawable.setBounds(i * boxWidth, j * boxWidth, (i + 1) * boxWidth, (j + 1) * boxWidth);
        drawable.draw(canvas);
    }

    private void drawNumber(Canvas canvas, int i, int j) {
        int num = map[i][j].getNum();

        if (countHiddenAround(i, j) == num) {
            textPaint.setColor(Color.BLACK);
        } else {
            textPaint.setColor(getResources().getColor(R.color.colorPrimary, null));
        }
        canvas.drawText(Integer.toString(num), (0.5F + i) * boxWidth, (3 / 4F + j) * boxWidth,
                textPaint);
    }

    private int countHiddenAround(int i, int j) {
        int num = 0;
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (l == 0 && k == 0) {
                    continue;
                }
                if (i + k >= 0 && i + k < mapWidth && j + l >= 0 && j + l < mapHeight
                        && map[i + k][j + l].getState() == MinesweeperSquare.State.HIDDEN) {
                    num++;
                }
            }
        }
        return num;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = this.getWidth();
        boxWidth = width / mapWidth;
        mapHeight = this.getHeight() / boxWidth;
        textPaint.setTextSize(boxWidth - 5F);
        textPaint.setTextAlign(Paint.Align.CENTER);
        reset();
    }

    private void showDialogLose() {
        invalidate();
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.you_lose);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);

        text.setText(R.string.you_lose);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener((View v) -> {
            MinesweeperView.this.reset();
            dialog.dismiss();
            invalidate();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showDialogWinning() {
        invalidate();
        final Dialog dialog = getDialog();
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        long inSeconds = (System.currentTimeMillis() - startTime) / 1000;
        String s = getResources().getString(R.string.time_format);
        String format = String.format(s, inSeconds / 60, inSeconds % 60);

        if (isRecordSuitable(inSeconds, UserRecord.MINESWEEPER, numberOfBombs, true)) {
            createRecordIfSuitable(inSeconds, format, UserRecord.MINESWEEPER, numberOfBombs,
                    true);
            showRecords(numberOfBombs, UserRecord.MINESWEEPER, MinesweeperView.this::reset);
            return;
        }
        text.setText(String.format(getResources().getString(R.string.you_win), format));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog


        dialogButton.setOnClickListener(v -> {
            MinesweeperView.this.reset();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void showNeighbours(int i, int j) {
        map[i][j].setState(MinesweeperSquare.State.SHOWN);
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (l == 0 && k == 0) {
                    continue;
                }
                if (i + k >= 0 && i + k < mapWidth && j + l >= 0 && j + l < mapHeight) {
                    if (map[i + k][j + l].getMinesweeperImage() == MinesweeperImage.BLANK
                            && map[i + k][j + l].getState() == MinesweeperSquare.State.HIDDEN) {
                        showNeighbours(i + k, j + l);
                    }
                    map[i + k][j + l].setState(MinesweeperSquare.State.SHOWN);
                }
            }
        }

    }

    private static void toggleFlag(MinesweeperSquare mem) {
        if (mem == null) {
            return;
        }
        if (mem.getState() == MinesweeperSquare.State.HIDDEN) {
            mem.setState(MinesweeperSquare.State.FLAGGED);
        } else if (mem.getState() == MinesweeperSquare.State.FLAGGED) {
            mem.setState(MinesweeperSquare.State.HIDDEN);
        }
    }


    private boolean verifyEnd() {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                MinesweeperSquare s = map[i][j];
                if (s.getState() == MinesweeperSquare.State.HIDDEN
                        && s.getMinesweeperImage() != MinesweeperImage.BOMB) {
                    return false;
                }
            }
        }

        return true;
    }

}
