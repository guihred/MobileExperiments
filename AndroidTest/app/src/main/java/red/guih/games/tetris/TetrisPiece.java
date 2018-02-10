package red.guih.games.tetris;

import android.graphics.Color;

public enum TetrisPiece {
    I(new int[][]{
        {1, 1, 1, 1}
    }, Color.GREEN),
    J(new int[][]{
        {0, 1},
        {0, 1},
        {1, 1}
    },Color.rgb(0xFF,0x80,0x00)),
    L(new int[][]{
        {1, 0},
        {1, 0},
        {1, 1}
    }, Color.CYAN),
    O(new int[][]{
        {1, 1},
        {1, 1}},
        Color.YELLOW),
    S(new int[][]{
        {1, 0},
        {1, 1},
        {0, 1}
    }, Color.RED),
    T(new int[][]{
        {0, 1},
        {1, 1},
        {0, 1}
    },  Color.BLUE),
    Z(new int[][]{
        {0, 1},
        {1, 1},
        {1, 0}
    }, Color.rgb(0xFF,0x00,0xFF));
	private final int[][] map;
    private int color;

    TetrisPiece(int[][] map,int color) {
        this.map = map;
        this.color = color;
    }

	public int[][] getMap() {
		return map;
	}

    public int getColor() {
        return color;
    }
}