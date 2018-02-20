package red.guih.games.minesweeper;

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
    public int mapHeight = 30;
    public int mapWidth = 16;
    public static int NUMBER_OF_BOMBS = 45;
    private int boxWidth = 50;
    private boolean goneFlag;
    private final Handler handler = new Handler();
    private final Paint hiddenColor;
    private MinesweeperSquare[][] map = new MinesweeperSquare[mapWidth][mapHeight];
    private Runnable mLongPressed = new Runnable() {
        public void run() {
            goneFlag = true;
            float x = pressedX;
            float y = pressedY;
            toggleFlag(getBestSquareMatch(x, y));
            invalidate();
        }
    };
    private int nPlayed;
    private float pressedX, pressedY;
    private final Paint shownColor;
    private long startTime;
    private final Paint textPaint = new Paint(Color.BLACK);

    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hiddenColor = new Paint();
        hiddenColor.setTypeface(Typeface.DEFAULT);
        hiddenColor.setShadowLayer(1, 5, 5, Color.BLACK);
        hiddenColor.setColor(Color.LTGRAY);
        textPaint.setTextSize(boxWidth - 5);

        shownColor = new Paint();
        shownColor.setTypeface(Typeface.DEFAULT);
        shownColor.setColor(Color.WHITE);

        reset();
    }


    public static void setNumberOfBombs(int selectedItemPosition) {
        MinesweeperView.NUMBER_OF_BOMBS = (selectedItemPosition + 1) * MinesweeperView.BOMBS_STEP;
    }

    private void clickEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        MinesweeperSquare mem = getBestSquareMatch(x, y);
        if (mem == null)
            return;


        if (mem.getState() == MinesweeperSquare.State.HIDDEN) {
            nPlayed++;
            mem.setState(MinesweeperSquare.State.SHOWN);
            if (mem.getMinesweeperImage().equals(MinesweeperImage.BOMB)) {
                if (nPlayed == 0) {
                    reset();
                    return;
                }

                showDialogLose();
            }
            if (mem.getMinesweeperImage().equals(MinesweeperImage.BLANK)) {
                showNeighbours(mem.getI(), mem.getJ());
            }
            if (verifyEnd()) {
                showDialogWinning();
            }

        }
        invalidate();
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


    private void drawHidden(Canvas canvas, int i, int j) {
        hiddenColor.setStyle(Paint.Style.FILL);
        hiddenColor.setColor(Color.LTGRAY);
        canvas.drawRect(i * boxWidth, j * boxWidth, (i + 1) * boxWidth, (j + 1) * boxWidth, hiddenColor);
        hiddenColor.setStyle(Paint.Style.STROKE);
        hiddenColor.setColor(Color.BLACK);
        canvas.drawRect(i * boxWidth, j * boxWidth, (i + 1) * boxWidth, (j + 1) * boxWidth, hiddenColor);
    }

    private MinesweeperSquare getBestSquareMatch(float x, float y) {
        MinesweeperSquare min = null;
        float minimum = Float.MAX_VALUE;
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {

                float abs = abs((i + 0.5f) * boxWidth - x);
                float abs2 = abs((j + 0.5f) * boxWidth - y);
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
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                MinesweeperSquare.State state = map[i][j].getState();
                switch (state) {
                    case HIDDEN:
                        drawHidden(canvas, i, j);
                        break;
                    case SHOWN:
                        canvas.drawRect(i * boxWidth, j * boxWidth, (i + 1) * boxWidth, (j + 1) * boxWidth, shownColor);
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
                        break;
                    case FLAGGED:
                        drawHidden(canvas, i, j);
                        drawFlag(canvas, i, j);
                        break;
                }
            }
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
        canvas.drawText(Integer.toString(num), (0.5f + i) * boxWidth, (0.75f + j) * boxWidth, textPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = this.getWidth();
        boxWidth = width / mapWidth;
        mapHeight = this.getHeight() / boxWidth;
        textPaint.setTextSize(boxWidth - 5);
        textPaint.setTextAlign(Paint.Align.CENTER);
        reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressedX = event.getX();
                pressedY = event.getY();
                handler.postDelayed(mLongPressed, DELAY_LONG_PRESS);
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(mLongPressed);
                if (!goneFlag) {
                    clickEvent(event);
                    return true;
                }
                goneFlag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                handler.removeCallbacks(mLongPressed);
                break;
            default:
        }
        return true;
    }

    private void reset() {
        nPlayed = 0;
        initializeMines();
        addRandomBombs();
        setNumbersAroundBombs();
        startTime = System.currentTimeMillis();
        invalidate();
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

    private void addRandomBombs() {
        final Random random = new Random();
        long count = 0;
        while (count < NUMBER_OF_BOMBS) {
            int j = random.nextInt(mapWidth);
            int k = random.nextInt(mapHeight);

            final MinesweeperSquare mem = map[j][k];
            mem.setMinesweeperImage(MinesweeperImage.BOMB);
            count = countBombs();
        }
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

    private void showDialogLose() {
        invalidate();
        final Dialog dialog = new Dialog(getContext());
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
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        long emSegundos = (System.currentTimeMillis() - startTime) / 1000;
        String s = getResources().getString(R.string.time_format);
        String format = String.format(s, emSegundos / 60, emSegundos % 60);

        if (isRecordSuitable(emSegundos, UserRecord.MINESWEEPER, NUMBER_OF_BOMBS, true)) {
            createRecordIfSuitable(emSegundos, format, UserRecord.MINESWEEPER, NUMBER_OF_BOMBS, true);
            showRecords(NUMBER_OF_BOMBS, UserRecord.MINESWEEPER, () -> MinesweeperView.this.reset());
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
                    if (map[i + k][j + l].getMinesweeperImage().equals(MinesweeperImage.BLANK)
                            && map[i + k][j + l].getState().equals(MinesweeperSquare.State.HIDDEN)) {
                        showNeighbours(i + k, j + l);
                    }
                    map[i + k][j + l].setState(MinesweeperSquare.State.SHOWN);
                }
            }
        }

    }

    private void toggleFlag(MinesweeperSquare mem) {
        if (mem == null)
            return;

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
