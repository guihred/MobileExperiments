package red.guih.games.puzzle;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import red.guih.games.R;
import red.guih.games.db.UserRecord;
import red.guih.games.db.UserRecordDatabase;

import static java.util.stream.Collectors.toList;


public class PuzzleView extends View {

    public static int PUZZLE_WIDTH = 4;
    public static int PUZZLE_HEIGHT = 6;

    PuzzlePiece[][] puzzle;
    private int width;
    private int height;
    private Point2D intersectedPoint;
    private final List<List<PuzzlePiece>> linkedPieces = new ArrayList<>();
    private List<PuzzlePiece> chosenPuzzleGroup;
    private long startTime;

    final UserRecordDatabase db = Room.databaseBuilder(getContext(),
            UserRecordDatabase.class, UserRecord.DATABASE_NAME).build();

    public PuzzleView(Context c, AttributeSet v) {
        super(c, v);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (List<PuzzlePiece> group : linkedPieces) {
            for (PuzzlePiece p : group) {
                p.draw(canvas);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reset();
    }

    private void reset() {
        puzzle = initializePieces();
        linkedPieces.clear();
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                List<PuzzlePiece> e = new ArrayList<>();
                e.add(puzzle[i][j]);
                linkedPieces.add(e);
            }
        }
        startTime = System.currentTimeMillis();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (intersectedPoint == null || chosenPuzzleGroup == null) {
                    return true;
                }
                Point2D intersectedPoint2 = getIntersectedPoint(e);
                Point2D subtract = intersectedPoint2.subtract(intersectedPoint);


                chosenPuzzleGroup.forEach(i -> i.move(subtract));


                intersectedPoint = getIntersectedPoint(e);
                break;
            case MotionEvent.ACTION_UP:

                List<PuzzlePiece> containsP = chosenPuzzleGroup;
                if (containsP != null) {
                    List<List<PuzzlePiece>> collect = linkedPieces.stream().filter(l -> l != containsP).collect(toList());
                    for (PuzzlePiece piece : containsP) {
                        for (int i = 0; i < collect.size(); i++) {
                            for (int j = 0; j < collect.get(i).size(); j++) {
                                PuzzlePiece puzzlePiece = collect.get(i).get(j);
                                if (checkNeighbours(piece, puzzlePiece)) {
                                    if (distance(puzzlePiece, piece) < width * width / 16) {

                                        List<PuzzlePiece> containsPuzzle = groupWhichContains(puzzlePiece);
                                        if (containsPuzzle != null
                                                && !containsP.equals(containsPuzzle)) {
                                            containsPuzzle.addAll(containsP);
                                            linkedPieces.remove(containsP);
                                            float a = xDistance(puzzlePiece, piece);
                                            float b = yDistance(puzzlePiece, piece);
                                            toFront(containsPuzzle);
                                            containsP.forEach(z -> z.move(a, b));
                                            chosenPuzzleGroup = null;
                                            intersectedPoint = null;
                                            if (linkedPieces.size() == 1) {
                                                showDialogWinning();
                                            }
                                            invalidate();


                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                chosenPuzzleGroup = null;
                intersectedPoint = null;
                break;
            case MotionEvent.ACTION_DOWN:
                if (intersectedPoint == null) {
                    intersectedPoint = getIntersectedPoint(e);
                    List<PuzzlePiece> contains = groupWhichContains();
                    if (contains != null) {
                        chosenPuzzleGroup = contains;
                        toFront(chosenPuzzleGroup);
                    }
                }
                break;
            default:
        }

        invalidate();
        return true;
    }

    private void showDialogWinning() {
        invalidate();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.game_over);

        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        long inSeconds = (System.currentTimeMillis() - startTime) / 1000;
        String s = getResources().getString(R.string.time_format);
        String format = String.format(s, inSeconds / 60, inSeconds % 60);

        new Thread(() -> createUserRecord(inSeconds, format)).start();

        text.setText(String.format(getResources().getString(R.string.you_win), format));
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog


        dialogButton.setOnClickListener(v -> {
            PuzzleView.this.reset();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void createUserRecord(long emSegundos, String format) {
        try {
            UserRecord userRecord = new UserRecord();
            userRecord.setDescription(format);
            userRecord.setPoints(emSegundos);
            userRecord.setGameName(UserRecord.PUZZLE);
            userRecord.setDifficulty(PUZZLE_WIDTH);
            db.userDao().insertAll(userRecord);
        } catch (Exception e) {
            Log.e("PUZZLE", "ERROR WHEN CREATING USER RECORD", e);
        }
    }

    private void toFront(List<PuzzlePiece> containsPuzzle) {
        linkedPieces.remove(containsPuzzle);
        linkedPieces.add(containsPuzzle);
    }

    private Point2D getIntersectedPoint(MotionEvent e) {
        Point2D point3D = new Point2D();
        point3D.x = e.getX();
        point3D.y = e.getY();
        return point3D;
    }


    private List<PuzzlePiece> groupWhichContains() {

        for (int i = linkedPieces.size() - 1; i >= 0; i--) {
            List<PuzzlePiece> group = linkedPieces.get(i);
            if (group.stream().anyMatch(this::containsPoint)) {
                return group;
            }
        }


        return null;
    }

    private List<PuzzlePiece> groupWhichContains(PuzzlePiece p1) {
        return linkedPieces.stream().filter(l -> l.contains(p1))
                .findAny().orElse(null);
    }

    private boolean containsPoint(PuzzlePiece p1) {
        RectF rectF = new RectF();
        p1.getTranslatedPath().computeBounds(rectF, true);
        return intersectedPoint != null && rectF.contains(intersectedPoint.x, intersectedPoint.y);

    }

    private float yDistance(PuzzlePiece puzzlePiece, PuzzlePiece p) {
        return (-puzzlePiece.getY() + p.getY()) * height + puzzlePiece.getLayoutY() - p.getLayoutY();
    }

    private boolean checkNeighbours(PuzzlePiece p, PuzzlePiece puzzlePiece) {
        return Math.abs(puzzlePiece.getX() - p.getX()) == 1 && puzzlePiece.getY() - p.getY() == 0
                || Math.abs(puzzlePiece.getY() - p.getY()) == 1 && puzzlePiece.getX() - p.getX() == 0;
    }

    private float distance(PuzzlePiece a, PuzzlePiece b) {
        float d = xDistance(a, b);
        float e = yDistance(a, b);
        return d * d + e * e;
    }

    private float xDistance(PuzzlePiece a, PuzzlePiece b) {
        return (-a.getX() + b.getX()) * width + a.getLayoutX() - b.getLayoutX();
    }

    private PuzzlePiece[][] initializePieces() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.mona_lisa);
        width = getWidth() / PUZZLE_WIDTH;
        height = getHeight() / PUZZLE_HEIGHT;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, width * PuzzleView.PUZZLE_WIDTH, height * PuzzleView.PUZZLE_HEIGHT, false);
        Random random = new Random();
        PuzzlePiece[][] puzzlePieces = new PuzzlePiece[PUZZLE_WIDTH][PUZZLE_HEIGHT];
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                puzzlePieces[i][j] = new PuzzlePiece(i, j, width, height);
                puzzlePieces[i][j].setLayoutX(random.nextFloat() * (getWidth() - width));
                puzzlePieces[i][j].setLayoutY(random.nextFloat() * (getHeight() - height));
                puzzlePieces[i][j].setImage(scaledBitmap);
            }
        }
        PuzzlePath[] values = {PuzzlePath.ZIGZAGGED, PuzzlePath.SQUARE, PuzzlePath.ROUND};
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                PuzzlePath puzzlePath2 = values[random.nextInt(values.length)];
                if (i < PUZZLE_WIDTH - 1) {
                    puzzlePieces[i][j].setRight(puzzlePath2);
                    puzzlePieces[i + 1][j].setLeft(puzzlePath2);
                }
                if (j < PUZZLE_HEIGHT - 1) {
                    puzzlePieces[i][j].setDown(puzzlePath2);
                    puzzlePieces[i][j + 1].setUp(puzzlePath2);
                }
            }
        }
        return puzzlePieces;
    }

    public static void setPuzzleDimensions(int progress) {
        PuzzleView.PUZZLE_WIDTH = progress;
        PuzzleView.PUZZLE_HEIGHT = progress * 3 / 2;
    }

}
