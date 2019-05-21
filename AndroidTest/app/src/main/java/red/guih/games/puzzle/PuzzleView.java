package red.guih.games.puzzle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import red.guih.games.BaseView;
import red.guih.games.R;
import red.guih.games.db.UserRecord;

import static java.util.stream.Collectors.toList;


public class PuzzleView extends BaseView {

    static int puzzleImage = R.drawable.mona_lisa;
    static int puzzleWidth = 4;
    static int puzzleHeight = 6;
    private static Bitmap selectedImage;
    private final List<List<PuzzlePiece>> linkedPieces = new LinkedList<>();
    private Random random = new Random();
    private PuzzlePiece[][] puzzle;
    private int width;
    private int height;
    private Point2D intersectedPoint;
    private List<PuzzlePiece> chosenPuzzleGroup;
    private long startTime;

    public PuzzleView(Context c, AttributeSet v) {
        super(c, v);
    }

    public static void setPuzzleDimensions(int progress) {
        PuzzleView.puzzleWidth = progress;
        PuzzleView.puzzleHeight = progress * 3 / 2;
    }

    public static void setImage(int image) {
        PuzzleView.puzzleImage = image;
    }

    public static void setImage(Bitmap selectedImage) {
        PuzzleView.selectedImage = selectedImage;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (handleDrag(e)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                if (handleRelease()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                handleFirstTouch(e);
                break;
            default:
        }

        invalidate();
        return true;
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

    private boolean handleDrag(MotionEvent e) {
        if (intersectedPoint == null || chosenPuzzleGroup == null) {
            return true;
        }

        Point2D subtract = intersectedPoint.subtract(e);


        chosenPuzzleGroup.forEach(i -> i.move(subtract));


        intersectedPoint = Point2D.getIntersectedPoint(e);
        return false;
    }

    private boolean handleRelease() {
        List<PuzzlePiece> containsP = chosenPuzzleGroup;
        if (containsP != null) {
            List<List<PuzzlePiece>> pieces =
                    linkedPieces.stream().filter(l -> !Objects.equals(l, containsP))
                                .collect(toList());
            for (PuzzlePiece piece : containsP) {
                if (addPieceToRightGroup(containsP, pieces, piece)) {
                    return true;
                }
            }
        }
        chosenPuzzleGroup = null;
        intersectedPoint = null;
        return false;
    }

    private void handleFirstTouch(MotionEvent e) {
        if (intersectedPoint == null) {
            intersectedPoint = Point2D.getIntersectedPoint(e);
            List<PuzzlePiece> contains = groupWhichContains();
            if (contains != null) {
                chosenPuzzleGroup = contains;
                toFront(chosenPuzzleGroup);
            }
        }
    }

    private boolean addPieceToRightGroup(List<PuzzlePiece> containsP,
            List<List<PuzzlePiece>> pieces, PuzzlePiece piece) {
        for (int i = 0; i < pieces.size(); i++) {
            for (int j = 0; j < pieces.get(i).size(); j++) {
                PuzzlePiece puzzlePiece = pieces.get(i).get(j);
                if (!checkNeighbours(piece, puzzlePiece)) {
                    continue;
                }
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
                            float x = -puzzle[0][0].getLayoutX();
                            float y = -puzzle[0][0].getLayoutY();
                            containsPuzzle.forEach(z -> z.move(x, y));
                            showDialogWinning();
                        }
                        invalidate();
                        return true;
                    }
                }
            }
        }
        return false;
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

    private void toFront(List<PuzzlePiece> containsPuzzle) {
        linkedPieces.remove(containsPuzzle);
        linkedPieces.add(containsPuzzle);
    }

    private static boolean checkNeighbours(PuzzlePiece p, PuzzlePiece puzzlePiece) {
        return Math.abs(puzzlePiece.getX() - p.getX()) == 1 && puzzlePiece.getY() - p.getY() == 0
                ||
                Math.abs(puzzlePiece.getY() - p.getY()) == 1 && puzzlePiece.getX() - p.getX() == 0;
    }

    private float distance(PuzzlePiece a, PuzzlePiece b) {
        float d = xDistance(a, b);
        float e = yDistance(a, b);
        return d * d + e * e;
    }

    private List<PuzzlePiece> groupWhichContains(PuzzlePiece p1) {
        return linkedPieces.stream().filter(l -> l.contains(p1))
                           .findAny().orElse(null);
    }

    private float xDistance(PuzzlePiece a, PuzzlePiece b) {
        return (-a.getX() + b.getX()) * width + a.getLayoutX() - b.getLayoutX();
    }

    private float yDistance(PuzzlePiece puzzlePiece, PuzzlePiece p) {
        return (-puzzlePiece.getY() + p.getY()) * height + puzzlePiece.getLayoutY() -
                p.getLayoutY();
    }

    private void showDialogWinning() {
        invalidate();
        long inSeconds = (System.currentTimeMillis() - startTime) / 1000;
        String format =
                getResources().getString(R.string.time_format,inSeconds / 60, inSeconds % 60);
        if (isRecordSuitable(inSeconds, UserRecord.PUZZLE, puzzleWidth, true)) {
            createRecordIfSuitable(inSeconds, format, UserRecord.PUZZLE, puzzleWidth, true);
            showRecords(puzzleWidth, UserRecord.PUZZLE, PuzzleView.this::reset);
            return;
        }

        String format1 = getResources().getString(R.string.you_win,format);
        showDialogWinning(format1, PuzzleView.this::reset);
    }

    private boolean containsPoint(PuzzlePiece p1) {
        RectF rectF = new RectF();
        p1.getTranslatedPath().computeBounds(rectF, true);
        return intersectedPoint != null && rectF.contains(intersectedPoint.x, intersectedPoint.y);

    }

    private void reset() {
        puzzle = initializePieces();
        linkedPieces.clear();
        for (int i = 0; i < puzzleWidth; i++) {
            for (int j = 0; j < puzzleHeight; j++) {
                List<PuzzlePiece> e = new ArrayList<>();
                e.add(puzzle[i][j]);
                linkedPieces.add(e);
            }
        }
        startTime = System.currentTimeMillis();
        invalidate();
    }

    private PuzzlePiece[][] initializePieces() {
        Bitmap image = selectedImage != null ? selectedImage :
                BitmapFactory.decodeResource(getResources(), puzzleImage);
        if (image == null) {
            image = BitmapFactory.decodeResource(getResources(), puzzleImage);
        }

        if (image.getWidth() > image.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix,
                    true);
        }
        width = getWidth() / puzzleWidth;
        height = getHeight() / puzzleHeight;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, width * PuzzleView.puzzleWidth,
                height * PuzzleView.puzzleHeight, false);
        PuzzlePiece[][] puzzlePieces = new PuzzlePiece[puzzleWidth][puzzleHeight];
        for (int i = 0; i < puzzleWidth; i++) {
            for (int j = 0; j < puzzleHeight; j++) {
                puzzlePieces[i][j] = new PuzzlePiece(i, j, width, height);
                puzzlePieces[i][j].setLayoutX(random.nextFloat() * (getWidth() - width));
                puzzlePieces[i][j].setLayoutY(random.nextFloat() * (getHeight() - height));
                puzzlePieces[i][j].setImage(scaledBitmap);
            }
        }
        PuzzlePath[] values = PuzzlePath.values();
        for (int i = 0; i < puzzleWidth; i++) {
            for (int j = 0; j < puzzleHeight; j++) {
                PuzzlePath puzzlePath2 = values[random.nextInt(values.length - 1) + 1];
                if (i < puzzleWidth - 1) {
                    puzzlePieces[i][j].setRight(puzzlePath2);
                    puzzlePieces[i + 1][j].setLeft(puzzlePath2);
                }
                if (j < puzzleHeight - 1) {
                    puzzlePieces[i][j].setDown(puzzlePath2);
                    puzzlePieces[i][j + 1].setUp(puzzlePath2);
                }
            }
        }
        return puzzlePieces;
    }
}
