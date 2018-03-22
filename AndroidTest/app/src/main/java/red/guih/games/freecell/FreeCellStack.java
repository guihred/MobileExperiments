package red.guih.games.freecell;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FreeCellStack {


    public enum StackType {
        SIMPLE,
        ASCENDING,
        SUPPORT,;
    }

    public final StackType type;
    private final int n;
    private final List<FreeCellCard> cards = new ArrayList<>();
    private int layoutX, layoutY;
    private final Paint paint = new Paint();
    private RectF boundsF;

    public FreeCellStack(StackType type, int n) {
        this.type = type;
        this.n = n;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public String toString() {
        return "(" +
                "type=" + type +
                ", n=" + n +
                ')';
    }

    public int getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(int layoutX) {
        this.layoutX = layoutX;
    }

    public int getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(int layoutY) {
        this.layoutY = layoutY;
    }

    public FreeCellCard getLastCards() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }


    public RectF getBoundsF() {

        if (boundsF == null) {
            boundsF = new RectF();
        }
        int right = FreeCellCard.getCardWidth();
        float bottom = cards.isEmpty() ? FreeCellCard.getCardWidth() : getLastCards().getBoundsF().bottom;


        boundsF.set(getLayoutX(), getLayoutY(), right + layoutX, bottom + layoutY);
        return boundsF;
    }


    public List<FreeCellCard> removeLastCards(int n) {
        if (cards.isEmpty()) {
            return null;
        }
        List<FreeCellCard> lastCards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            FreeCellCard solitaireCard = cards.remove(cards.size() - 1);
            lastCards.add(solitaireCard);
        }
        return lastCards;
    }

    public FreeCellCard removeLastCards() {
        if (cards.isEmpty()) {
            return null;
        }

        return cards.remove(cards.size() - 1);
    }

    public void addCards(Collection<FreeCellCard> cards) {
        if (type == StackType.SIMPLE) {
            addCardsVertically(cards);
        } else {
            addCards(cards.toArray(new FreeCellCard[0]));
        }
    }

    private void addCardsVertically(Collection<FreeCellCard> cards) {
        addCardsVertically(cards.toArray(new FreeCellCard[0]));
    }

    public void addCardsVertically(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
            }
        }
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            FreeCellCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += FreeCellCard.getCardWidth() / (solitaireCard.isShown() ? 3 : 8);
        }

    }

    void adjust() {
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            FreeCellCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += FreeCellCard.getCardWidth() / (solitaireCard.isShown() ? 3 : 8);
        }
    }

    public void addCards(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
                solitaireCard.setLayoutY(0);
            }
        }
    }

    public void removeCards(List<FreeCellCard> cards) {
        removeCards(cards.toArray(new FreeCellCard[0]));
    }

    public void draw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(getLayoutX(), getLayoutY(), FreeCellCard.getCardWidth() + layoutX, FreeCellCard.getCardWidth() + layoutY, 5, 5, paint);
        for (FreeCellCard card : cards) {
            card.draw(canvas, layoutX, layoutY);
        }

    }


    public void removeCards(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            if (this.cards.contains(solitaireCard)) {
                this.cards.remove(solitaireCard);
            }
        }
    }

    public List<FreeCellCard> getCards() {
        return cards;
    }

    public int getShownCards() {
        return (int) cards.stream().filter(FreeCellCard::isShown).count();
    }

    public int getNotShownCards() {
        return (int) cards.stream().filter(e -> !e.isShown()).count();
    }

    public List<FreeCellCard> removeAllCards() {
        List<FreeCellCard> collect = new ArrayList<>(cards);
        cards.clear();
        return collect;
    }
}