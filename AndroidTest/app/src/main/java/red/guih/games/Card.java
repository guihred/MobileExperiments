package red.guih.games;

public class Card {
    private static int cardWidth;
    protected float layoutX;
    protected float layoutY;
    protected boolean shown;

    public static int getCardWidth() {
        return cardWidth * 4 / 5;
    }

    public static void setCardWidth(int cardWidth) {
        Card.cardWidth = cardWidth;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    public float getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean value) {
        shown = value;
    }

}
