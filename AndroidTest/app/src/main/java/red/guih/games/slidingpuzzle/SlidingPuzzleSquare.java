package red.guih.games.slidingpuzzle;

public final class SlidingPuzzleSquare {


    private final int number;


    public SlidingPuzzleSquare(int number) {
        this.number = number;
//        setPadding(new Insets(10));
//        if (isEmpty()) {
//            setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, new Insets(1))));
//        } else {
//            setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(1))));
//            text.textProperty().bind(this.number.asString());
//        }
//        setPrefSize(50, 50);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != SlidingPuzzleSquare.class) {
            return false;
        }
        if (((SlidingPuzzleSquare) obj).number == number) {
            return true;
        }
        return false;

    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        return number;
    }

    public final boolean isEmpty() {
        return number == SlidingPuzzleView.MAP_WIDTH * SlidingPuzzleView.MAP_HEIGHT;
    }
}
