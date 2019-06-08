package red.guih.games.solitaire;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import red.guih.games.Card;

public class SolitaireCard extends Card {

    private final SolitaireNumber number;
    private final SolitaireSuit suit;
    private final Drawable drawable;
    private final Paint paint = new Paint();
    boolean autoMoved;
    private RectF bounds;
    private RectF boundsF;

    SolitaireCard(SolitaireNumber number, SolitaireSuit suit, Context c) {
        this.number = number;
        this.suit = suit;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        drawable = c.getResources().getDrawable(suit.getShape(), null);
    }

    public void draw(Canvas canvas, float layoutX, float layoutY) {
        RectF boundsF1 = getBoundsF();
        boundsF1.offset(layoutX, layoutY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(boundsF1, 5, 5, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(boundsF1, 5, 5, paint);
        if (!shown) {
            return;
        }
        paint.setTextSize(getCardWidth() / 4F);

        int x = (int) (layoutX + this.layoutX) + getCardWidth() / 3;
        int y = (int) (layoutY + this.layoutY) + getCardWidth() / 10 / 2;
        drawable.setBounds(x, y, x + getCardWidth() / 4, y + getCardWidth() / 4);
        drawable.draw(canvas);

        canvas.drawText(number.getRepresentation(), x - getCardWidth() / 4F,
                y + getCardWidth() / 4F, paint);
    }

    RectF getBoundsF() {
        if (boundsF == null) {
            boundsF = new RectF();
        }

        boundsF.set(layoutX, layoutY, layoutX + getCardWidth(),
                layoutY + getCardWidth());
        return boundsF;
    }

    @Override
    public String toString() {
        return getNumber().getRepresentation() + " " + suit;
    }

    public SolitaireNumber getNumber() {
        return number;
    }

    SolitaireSuit getSuit() {
        return suit;
    }

    public RectF getBounds() {
        if (bounds == null) {
            bounds = new RectF();
        }

        float width = getCardWidth();
        bounds.set(layoutX, layoutY, layoutX + width,
                layoutY + width);
        return bounds;
    }

    void relocate(float layoutX, float layoutY) {
        setLayoutX(layoutX);
        setLayoutY(layoutY);
    }
}
