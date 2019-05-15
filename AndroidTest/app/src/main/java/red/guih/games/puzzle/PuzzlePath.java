package red.guih.games.puzzle;

import android.graphics.Path;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public enum PuzzlePath {
    //___
    STRAIGHT((x, y) -> p -> p.rLineTo(x, y)),
    //    _( )_
    ROUND((x, y) -> p -> {
        float q = 0.75F;
        p.rCubicTo(Math.abs(y) * q - x * q, Math.abs(x) * q - y * q, Math.abs(y) * q + x * (1 + q),
                Math.abs(x) * q + y * (1 + q), x, y);
    }),
    // _/\_
    TRIANGLE((x, y) -> p -> {
        float i = Math.signum(x + y);

        p.rLineTo(nonZero(i * y * PuzzlePiece.SQRT_0_5, x / 2F),
                nonZero(i * x * PuzzlePiece.SQRT_0_5, y / 2F));
        p.rLineTo(nonZero(i * -y * PuzzlePiece.SQRT_0_5, x / 2F),
                nonZero(i * -x * PuzzlePiece.SQRT_0_5, y / 2F));

    }),
    //      _
//    _| |_
    SQUARE((x, y) -> p -> {
        int i = x + y > 0 ? 1 : -1;
        p.rLineTo(i * y / 2, i * x / 2);
        p.rLineTo(x, y);
        p.rLineTo(i * -y / 2, i * -x / 2);
    }),
    //      _
//    _| |  _
//       |_|
    CUBIC_WAVE((x, y) -> p -> {
        p.rLineTo(y / 2, x / 2);
        p.rLineTo(x / 2, y / 2);
        p.rLineTo(-y, -x);
        p.rLineTo(x / 2, y / 2);
        p.rLineTo(y / 2, x / 2);
    }),
    //    _( ) _
//      ( )
    WAVE((x, y) -> p -> {
        p.rQuadTo(y / 4, x / 4, y / 4 + x / 4, x / 4 + y / 4);
        p.rQuadTo(x / 4, y / 4, -y / 4 + x / 4, -x / 4 + y / 4);
        p.rQuadTo(-y / 4, -x / 4, -y / 4 + x / 4, -x / 4 + y / 4);
        p.rQuadTo(x / 4, y / 4, y / 4 + x / 4, x / 4 + y / 4);

    }),
    //    _/\   _
//       \/
    ZIGZAG((x, y) -> p -> {
        p.rLineTo(y / 2 + x / 4, x / 2 + y / 4);
        p.rLineTo(-y + x / 2, -x + y / 2);
        p.rLineTo(y / 2 + x / 4, x / 2 + y / 4);
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
