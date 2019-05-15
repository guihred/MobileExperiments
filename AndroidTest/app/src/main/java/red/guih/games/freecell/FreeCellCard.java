package red.guih.games.freecell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class FreeCellCard {


    private static int cardWidth;
    private final FreeCellNumber number;
    private final FreeCellSuit suit;
    private final Drawable drawable;
    private final Paint paint = new Paint();
    private boolean autoMoved;
    private boolean shown;
    private float layoutX;
    private float layoutY;
    private RectF bounds;
    private RectF boundsF;

    FreeCellCard(FreeCellNumber number, FreeCellSuit suit, Context c) {
        this.number = number;
        this.suit = suit;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        drawable = c.getResources().getDrawable(suit.getShape(), null);
    }

    public void draw(Canvas canvas, float layoutX, float layoutY) {
        RectF rectF = getBoundsF();
        rectF.offset(layoutX, layoutY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, 5, 5, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectF, 5, 5, paint);
        if (!shown) {
            return;
        }
        paint.setTextSize(getCardWidth() / 4F);

        int left = (int) (layoutX + this.layoutX) + getCardWidth() / 3;
        int top = (int) (layoutY + this.layoutY) + getCardWidth() / 10 / 2;
        drawable.setBounds(left, top, left + getCardWidth() / 4, top + getCardWidth() / 4);
        drawable.draw(canvas);

        canvas.drawText(number.getRepresentation(), left - getCardWidth() / 4F,
                top + getCardWidth() / 4F, paint);
    }

    RectF getBoundsF() {
        if (boundsF == null) {
            boundsF = new RectF();
        }

        boundsF.set(layoutX, layoutY, layoutX + getCardWidth(),
                layoutY + getCardWidth());
        return boundsF;
    }

    static int getCardWidth() {
        return cardWidth * 4 / 5;
    }

    static void setCardWidth(int cardWidth) {
        FreeCellCard.cardWidth = cardWidth;
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

    FreeCellSuit getSuit() {
        return suit;
    }

    boolean isShown() {
        return shown;
    }

    void setShown() {
        shown = true;
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

        bounds.set(layoutX, layoutY, layoutX + getCardWidth(), layoutY + getCardWidth());
        return bounds;
    }

    void relocate(float layoutX, float layoutY) {
        setLayoutX(layoutX);
        setLayoutY(layoutY);
    }

    boolean isAutoMoved() {
        return autoMoved;
    }

    void setAutoMoved(boolean autoMoved) {
        this.autoMoved = autoMoved;
    }
}
