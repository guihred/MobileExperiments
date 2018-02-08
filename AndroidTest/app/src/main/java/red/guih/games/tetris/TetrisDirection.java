package red.guih.games.tetris;

public enum TetrisDirection {
    DOWN,
    LEFT,
    UP,
    RIGHT;

    TetrisDirection next() {
        final TetrisDirection[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}