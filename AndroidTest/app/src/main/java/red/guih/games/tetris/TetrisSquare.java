package red.guih.games.tetris;

public class TetrisSquare {

    private TetrisPieceState state = TetrisPieceState.EMPTY;
    private int color;

    public void setState(TetrisPieceState value) {
        state = value;
    }

    public TetrisPieceState getState() {
        return state;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
