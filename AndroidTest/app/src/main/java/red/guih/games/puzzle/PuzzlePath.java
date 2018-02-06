package red.guih.games.puzzle;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public enum PuzzlePath {

    STRAIGHT((x, y) -> p -> p.rLineTo(x, y)),
    ROUND((x, y) -> p -> {
//        ArcTo((x + y) / 2, (x + y) / 2, 0, x, y, false, x + y > 0));
    }),
    ZIGZAGGED((x, y) -> p -> {
        int i = x + y > 0 ? 1 : -1;

        p.rLineTo(nonZero(i * y * PuzzlePiece.SQRT_2, x / 2f), nonZero(i * x * PuzzlePiece.SQRT_2, y / 2f));
        p.rLineTo(nonZero(i * -y * PuzzlePiece.SQRT_2, x / 2f), nonZero(i * -x * PuzzlePiece.SQRT_2, y / 2f));

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

    private BiFunction<Float, Float, Consumer<Path>> path;


    PuzzlePath(BiFunction<Float, Float, Consumer<Path>> path) {
        this.path = path;
    }

    private static float nonZero(float a, float b) {
        return a == 0 ? b : a;
    }


    public List<Path> getPath(float x, float y, Path p) {
        List<Path> arrayList = new ArrayList<>();
        STRAIGHT.path.apply(x / 4, y / 4).accept(p);
        path.apply(x / 2, y / 2).accept(p);
        STRAIGHT.path.apply(x / 4, y / 4).accept(p);
        return arrayList;
    }

}
