/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.square2048;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

/**
 * @author Note
 */
public class Square2048View extends BaseView {
    public static final int MAP_HEIGHT = 4;
    public static final int MAP_WIDTH = 4;
    public static final int ANIMATION_DURATION = 100;
    public static final int MAIN_GOAL = 2048;
    final Random random = new Random();
    private final Square2048[][] map = new Square2048[MAP_WIDTH][MAP_HEIGHT];
    private final List<Square2048> mapAsList = new ArrayList<>();
    private final List<Square2048> changedList = new LinkedList<>();
    private Map<Square2048, Square2048> movingSquares = new HashMap<>();
    private long nPlayed;
    private float initialX;
    private float initialY;

    public Square2048View(Context c, AttributeSet attr) {
        super(c, attr);
        reset();
    }

    public void reset() {
        mapAsList.clear();
        for (int i = 0; i < getMap().length; i++) {
            for (int j = 0; j < getMap()[i].length; j++) {
                getMap()[i][j] = new Square2048(i, j);
                mapAsList.add(getMap()[i][j]);
            }
        }
        getMap()[random.nextInt(MAP_WIDTH)][random.nextInt(MAP_HEIGHT)].setNumber(newNumber());
        getMap()[random.nextInt(MAP_WIDTH)][random.nextInt(MAP_HEIGHT)].setNumber(newNumber());

        nPlayed = 0;
        invalidate();
    }

    public Square2048[][] getMap() {
        return map;
    }

    private int newNumber() {
        return (random.nextInt(2) + 1) * 2;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            initialX = event.getX();
            initialY = event.getY();
        } else if (action == MotionEvent.ACTION_UP) {
            handleKeyPressed(getDirection(event));
            invalidate();
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < getMap().length; i++) {
            for (int j = 0; j < getMap()[i].length; j++) {
                map[i][j].draw(canvas);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int size = getWidth() / MAP_WIDTH;
        Square2048.setSquareSize(size);
        Square2048.setPadding((getHeight() - getWidth()) / 2);
        if (changed) {
            reset();
        }
    }

    Direction getDirection(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (Math.abs(initialX - x) > Math.abs(initialY - y)) {
            if (initialX > x) {
                return Direction.LEFT;
            }
            return Direction.RIGHT;
        }
        if (initialY > y) {
            return Direction.UP;
        }
        return Direction.DOWN;

    }


    public void handleKeyPressed(Direction direction) {
        int x = direction.x;
        int y = direction.y;
        movingSquares.clear();
        boolean changed = true;
        changedList.clear();
        while (changed) {
            changed = moveSquares(x, y);
        }
        movingSquares.forEach(this::animateMovingSquare);

        if (mapAsList.stream().anyMatch(f -> f.getNumber() == MAIN_GOAL)) {
            showDialogWinning();
            return;
        }
        List<Square2048> emptySquares =
                mapAsList.stream().filter(Square2048::isEmpty).collect(Collectors.toList());
        if (emptySquares.isEmpty() && noPossibleMove()) {
            showDialogLose();
            return;
        }
        if (!movingSquares.isEmpty()) {
            int index = random.nextInt(emptySquares.size());
            int value = this.newNumber();
            emptySquares.get(index).setNumber(value);
            nPlayed++;
        }

    }

    private boolean moveSquares(int x, int y) {
        boolean changed = false;
        for (int i = x > 0 ? getMap().length - 1 : 0;
             i < getMap().length && i >= 0; i += x > 0 ? -1 : 1) {
            for (int j = y > 0 ? getMap()[i].length - 1 : 0;
                 j < getMap()[i].length && j >= 0; j += y > 0 ? -1 : 1) {
                if (isInBounds(x, y, i, j)) {
                    if (getMap()[i + x][j + y].isEmpty()) {
                        getMap()[i + x][j + y].setNumber(getMap()[i][j].getNumber());
                        getMap()[i][j].setNumber(0);
                        movingSquares.put(getMap()[i + x][j + y],
                                movingSquares.getOrDefault(getMap()[i][j], getMap()[i][j]));
                        movingSquares.remove(getMap()[i][j]);
                        changed = true;
                    } else if (
                            isSameNumber(i, j, getMap()[i + x][j + y]) &&
                                    !changedList.contains(getMap()[i + x][j + y]) &&
                                    !changedList.contains(getMap()[i][j])) {
                        getMap()[i + x][j + y].setNumber(getMap()[i][j].getNumber() * 2);
                        getMap()[i][j].setNumber(0);
                        movingSquares.put(getMap()[i + x][j + y],
                                movingSquares.getOrDefault(getMap()[i][j], getMap()[i][j]));
                        movingSquares.remove(getMap()[i][j]);
                        changedList.add(getMap()[i + x][j + y]);

                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private boolean noPossibleMove() {

        for (Direction dir : Direction.values()) {
            int x = dir.x;
            int y = dir.y;

            for (int i = 0; i < getMap().length; i++) {
                for (int j = 0; j < getMap()[i].length; j++) {
                    if (isInBounds(x, y, i, j) && isSameNumber(i, j, getMap()[i + x][j + y])) {
                        return false;

                    }
                }
            }
        }
        return true;
    }

    private boolean isSameNumber(int i, int j, Square2048 square2048) {
        return square2048.getNumber() == getMap()[i][j].getNumber();
    }

    private boolean isInBounds(int x, int y, int i, int j) {
        return !getMap()[i][j].isEmpty() && i + x >= 0 && i + x < MAP_WIDTH && j + y >= 0
                && j + y < MAP_HEIGHT;
    }

    private void animateMovingSquare(Square2048 target, Square2048 origin) {
        PropertyValuesHolder pvhRotation = PropertyValuesHolder
                .ofKeyframe("layoutX", Keyframe.ofFloat(0, origin.getX() - target.getX()),
                        Keyframe.ofFloat(1, 0));
        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder
                .ofKeyframe("layoutY", Keyframe.ofFloat(0, origin.getY() - target.getY()),
                        Keyframe.ofFloat(1, 0));
        ObjectAnimator eatingAnimation =
                ObjectAnimator.ofPropertyValuesHolder(target, pvhRotation, pvhRotation2);
        eatingAnimation.setDuration(ANIMATION_DURATION);
        eatingAnimation.addUpdateListener(animation -> invalidate());
        eatingAnimation.start();
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
            this.reset();
            dialog.dismiss();
            invalidate();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showDialogWinning() {
        invalidate();

        String s = getResources().getString(R.string.you_win);
        String format = String.format(s, nPlayed + " moves");
        if (isRecordSuitable(nPlayed, UserRecord.SQUARE_2048, MAP_WIDTH, true)) {
            createRecordIfSuitable(nPlayed, format, UserRecord.SQUARE_2048, MAP_WIDTH, true);
            showRecords(MAP_WIDTH, UserRecord.SQUARE_2048, this::reset);
            return;
        }

        showDialogWinning(format, this::reset);
    }

    enum Direction {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);
        private final int x;
        private final int y;

        Direction(int x, int y) {

            this.x = x;
            this.y = y;
        }
    }

}