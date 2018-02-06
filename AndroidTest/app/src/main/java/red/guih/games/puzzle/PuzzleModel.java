package red.guih.games.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import red.guih.games.R;

import static java.util.stream.Collectors.toList;


public class PuzzleModel extends View {

    public static final int PUZZLE_WIDTH = 2;
    public static final int PUZZLE_HEIGHT = 4;

    PuzzlePiece[][] puzzle;
    private int width;
    private int height;
    private Point3D intersectedPoint;
    private List<List<PuzzlePiece>> linkedPieces = new ArrayList<>();
    private List<PuzzlePiece> chosenPuzzleGroup;


    public PuzzleModel(Context c, AttributeSet v) {
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
        puzzle = initializePieces();
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                List<PuzzlePiece> e = new ArrayList<>();
                e.add(puzzle[i][j]);
                linkedPieces.add(e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (intersectedPoint == null || chosenPuzzleGroup == null) {
                    return true;
                }
                Point3D intersectedPoint2 = getIntersectedPoint(e);
                Point3D subtract = intersectedPoint2.subtract(intersectedPoint);


                chosenPuzzleGroup.forEach(i -> i.move(subtract));


                intersectedPoint = getIntersectedPoint(e);
                break;
            case MotionEvent.ACTION_UP:

                List<PuzzlePiece> containsP = chosenPuzzleGroup;
                if (containsP != null) {
                    List<PuzzlePiece> puzzlePieces = containsP;
                    List<List<PuzzlePiece>> collect = linkedPieces.stream().filter(l -> l != puzzlePieces).collect(toList());
                    for (PuzzlePiece piece : puzzlePieces) {
                        for (int i = 0; i < collect.size(); i++) {
                            for (int j = 0; j < collect.get(i).size(); j++) {
                                PuzzlePiece puzzlePiece = collect.get(i).get(j);
                                if (checkNeighbours(piece, puzzlePiece)) {
                                    if (distance(puzzlePiece, piece) < width * width) {

                                        Optional<List<PuzzlePiece>> containsPuzzle = groupWhichContains(puzzlePiece);
                                        if (containsPuzzle.isPresent()
                                                && !containsP.equals(containsPuzzle.get())) {
                                            containsPuzzle.get().addAll(containsP);
                                            linkedPieces.remove(containsP);
                                            float a = xDistance(puzzlePiece, piece);
                                            float b = yDistance(puzzlePiece, piece);
//                                          containsPuzzle.get().forEach(PuzzlePiece::toFront);
                                            toFront(containsPuzzle.get());
                                            containsP.forEach(z -> z.move(a, b));
                                            List<?> puzzlePieces1 = linkedPieces;
                                            Log.i("PUZZLE", puzzlePieces1.size() + " " + puzzlePieces1);
                                            invalidate();
                                            chosenPuzzleGroup = null;
                                            intersectedPoint = null;
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
                    Optional<List<PuzzlePiece>> contains = groupWhichContains();
                    if (contains.isPresent()) {

                        chosenPuzzleGroup = contains.get();
                        toFront(chosenPuzzleGroup);
                    }
                }
                break;
            default:
        }

        invalidate();
        return true;
    }

    private void toFront(List<PuzzlePiece> containsPuzzle) {
        linkedPieces.remove(containsPuzzle);
        linkedPieces.add(containsPuzzle);
    }

    private Point3D getIntersectedPoint(MotionEvent e) {
        Point3D point3D = new Point3D();
        point3D.x = e.getX();
        point3D.y = e.getY();
        return point3D;
    }


    private Optional<List<PuzzlePiece>> groupWhichContains() {

        for (int i = linkedPieces.size() - 1; i >= 0; i--) {
            List<PuzzlePiece> group = linkedPieces.get(i);
            if (group.stream().anyMatch(p1 -> containsPoint(p1))) {
                return Optional.of(group);
            }
        }


        return Optional.empty();
    }

    private Optional<List<PuzzlePiece>> groupWhichContains(PuzzlePiece p1) {
        return linkedPieces.stream().filter(l -> l.contains(p1))
                .findAny();
    }

    private boolean containsPoint(PuzzlePiece p1) {
        RectF rectF = new RectF();
        p1.getTranslatedPath().computeBounds(rectF, true);
        if (intersectedPoint == null)
            return false;

        return rectF.contains(intersectedPoint.x, intersectedPoint.y);
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
        width = (int) (getWidth() / PUZZLE_WIDTH);
        height = (int) (getHeight() / PUZZLE_HEIGHT);
        float scaleX = image.getWidth() / getWidth();
        float scaleY = image.getHeight() / getHeight();

        Random random = new Random();
        PuzzlePiece[][] puzzlePieces = new PuzzlePiece[PUZZLE_WIDTH][PUZZLE_HEIGHT];
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                puzzlePieces[i][j] = new PuzzlePiece(i, j, width, height);
                puzzlePieces[i][j].setLayoutX(i * width);
                puzzlePieces[i][j].setLayoutY(j * height);
                puzzlePieces[i][j].setImage(image);

            }
        }
        PuzzlePath[] values = {PuzzlePath.ZIGZAGGED, PuzzlePath.SQUARE,};
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
                puzzlePieces[i][j].getPath();

            }
        }
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_HEIGHT; j++) {
                puzzlePieces[i][j].setScale(scaleX,scaleY);

            }
        }
        return puzzlePieces;
    }

}
