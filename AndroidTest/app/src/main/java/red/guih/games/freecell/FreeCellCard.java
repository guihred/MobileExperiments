package red.guih.games.freecell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class FreeCellCard {


    private final FreeCellNumber number;
    private final FreeCellSuit suit;
    private boolean shown;
     boolean autoMoved;
    private float layoutX, layoutY;
    private final Drawable drawable;
    private RectF bounds;
    private final Paint paint = new Paint();
    private RectF boundsF;
    private static int cardWidth;

    public FreeCellCard(FreeCellNumber number, FreeCellSuit suit, Context c) {
        this.number = number;
        this.suit = suit;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        drawable = c.getResources().getDrawable(suit.getShape(), null);
    }

    public static void setCardWidth(int cardWidth) {
        FreeCellCard.cardWidth = cardWidth;
    }

    public void draw(Canvas canvas, float layoutX, float layoutY) {
        RectF boundsF = getBoundsF();
        boundsF.offset(layoutX, layoutY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(boundsF, 5, 5, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(boundsF, 5, 5, paint);
        if (!shown) {
            return;
        }
        paint.setTextSize(getCardWidth() / 4);

        int left = (int) (layoutX + this.layoutX) + getCardWidth() / 3;
        int top = (int) (layoutY + this.layoutY) + getCardWidth() / 10 / 2;
        drawable.setBounds(left, top, left + getCardWidth() / 4, top + getCardWidth() / 4);
        drawable.draw(canvas);

        canvas.drawText(number.getRepresentation(), left - getCardWidth() / 4, top + getCardWidth() / 4, paint);
    }

    public static int getCardWidth() {
        return cardWidth * 4 / 5;
    }

    public float getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    @Override
    public String toString() {
        return getNumber().getRepresentation() + " " + suit;
    }

    public FreeCellNumber getNumber() {
        return number;
    }

    public FreeCellSuit getSuit() {
        return suit;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean value) {
        shown = value;
    }

    public float getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    public RectF getBounds() {
        if (bounds == null) {
            bounds = new RectF();
        }

        bounds.set((int) layoutX , (int) layoutY , (int) layoutX +  getCardWidth(), (int) layoutY +  getCardWidth());
        return bounds;
    }

    public RectF getBoundsF() {
        if (boundsF == null) {
            boundsF = new RectF();
        }

        boundsF.set((int) layoutX, (int) layoutY, (int) layoutX + getCardWidth(), (int) layoutY + getCardWidth());
        return boundsF;
    }

    public void relocate(float layoutX, float layoutY) {
        setLayoutX(layoutX);
        setLayoutY(layoutY);
    }
}
