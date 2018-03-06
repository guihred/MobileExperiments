package red.guih.games.puzzle;

import android.graphics.Path;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public enum PuzzlePath {

    STRAIGHT((x, y) -> p -> p.rLineTo(x, y)),
    ROUND((x, y) -> p -> {
        float q=0.75f;
        p.rCubicTo(Math.abs(y)*q-x*q, Math.abs(x)*q-y*q, Math.abs(y)*q+x*(1+q), Math.abs(x)*q+y*(1+q), x, y);
    }),

    ZIGZAGGED((x, y) -> p -> {
        float i = Math.signum(x + y);

        p.rLineTo(nonZero(i * y * PuzzlePiece.SQRT_0_5, x / 2f), nonZero(i * x * PuzzlePiece.SQRT_0_5, y / 2f));
        p.rLineTo(nonZero(i * -y * PuzzlePiece.SQRT_0_5, x / 2f), nonZero(i * -x * PuzzlePiece.SQRT_0_5, y / 2f));

    }),
    SQUARE((x, y) -> p -> {
        int i = x + y > 0 ? 1 : -1;
        p.rLineTo(i * y / 2, i * x / 2);
        p.rLineTo(x, y);
        p.rLineTo(i * -y / 2, i * -x / 2);
    }),
    WAVE((x, y) -> p -> {
//        boolean b = x + y > 0;
//        boolean c = x == 0;
//        boolean d = y == 0;


//                new ArcTo((x + y) / 4, (x + y) / 4, 0, x / 2, y / 2, false, b && c ^ !b && d);
//                new ArcTo((x + y) / 4, (x + y) / 4, 0, x / 2, y / 2, false, !b || !(c ^ !b) || !d);
    });


    private final BiFunction<Float, Float, Consumer<Path>> path;


    PuzzlePath(BiFunction<Float, Float, Consumer<Path>> path) {
        this.path = path;
    }

    private static float nonZero(float a, float b) {
        return a == 0 ? b : a;
    }


    public void getPath(float x, float y, Path p) {
        STRAIGHT.path.apply(x / 3, y / 3).accept(p);
        path.apply(x / 3, y / 3).accept(p);
        STRAIGHT.path.apply(x / 3, y / 3).accept(p);
    }

}
