package red.guih.games.tetris;

public class TetrisSquare {

    private TetrisPieceState state = (TetrisPieceState.EMPTY);
    private int color ;
    public TetrisSquare() {
//        setPadding(new Insets(10));
//        setPrefSize(30, 30);
//        setShape(new Rectangle(30, 30));
//
//        styleProperty().bind(
//                Bindings.when(state.isEqualTo(TetrisPieceState.EMPTY))
//                .then("-fx-background-color:black;")
//                .otherwise("-fx-background-color:green; "));
    }

    public void setState(TetrisPieceState value) {
        state=(value);
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
