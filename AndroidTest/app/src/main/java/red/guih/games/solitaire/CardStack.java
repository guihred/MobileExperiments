package red.guih.games.solitaire;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import red.guih.games.StackOfCards;

class CardStack extends StackOfCards {
    final StackType type;
    private final int n;
    private final List<SolitaireCard> cards = new ArrayList<>();
    private final Paint paint = new Paint();

    private RectF boundsF;

    CardStack(StackType type, int n) {
        this.type = type;
        this.n = n;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "(type=%s, n=%d)", type, n);
    }

    List<SolitaireCard> removeLastCards(int n) {
        if (cards.isEmpty()) {
            return Collections.emptyList();
        }
        List<SolitaireCard> lastCards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            SolitaireCard solitaireCard = cards.remove(cards.size() - 1);
            lastCards.add(solitaireCard);
        }
        return lastCards;
    }

    SolitaireCard removeLastCards() {
        if (cards.isEmpty()) {
            return null;
        }

        return cards.remove(cards.size() - 1);
    }

    RectF getBoundsF() {

        if (boundsF == null) {
            boundsF = new RectF();
        }
        float right = SolitaireCard.getCardWidth();
        float bottom =
                cards.isEmpty() ? SolitaireCard.getCardWidth() : getLastCards().getBoundsF().bottom;
        boundsF.set(getLayoutX(), getLayoutY(), right + layoutX, bottom + layoutY);
        return boundsF;
    }

    SolitaireCard getLastCards() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }


    void addCards(Collection<SolitaireCard> cards) {
        addCards(cards.toArray(new SolitaireCard[0]));
    }

    void addCards(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
                solitaireCard.setLayoutY(0);
            }
        }
    }

    void addCardsVertically(Collection<SolitaireCard> cards) {
        addCardsVertically(cards.toArray(new SolitaireCard[0]));
    }

    private void addCardsVertically(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
            }
        }
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            SolitaireCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += SolitaireCard.getCardWidth() / (solitaireCard.isShown() ? 3 : 8);
        }

    }

    void adjust() {
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            SolitaireCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += SolitaireCard.getCardWidth() / (solitaireCard.isShown() ? 3 : 8);
        }
    }

    void removeCards(List<SolitaireCard> cards) {
        removeCards(cards.toArray(new SolitaireCard[0]));
    }

    private void removeCards(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            this.cards.remove(solitaireCard);
        }
    }

    public void draw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        float cardWidth = SolitaireCard.getCardWidth();
        canvas.drawRoundRect(getLayoutX(), getLayoutY(), cardWidth + layoutX,
                cardWidth + layoutY, 5, 5, paint);
        for (SolitaireCard card : cards) {
            card.draw(canvas, layoutX, layoutY);
        }

    }

    List<SolitaireCard> getCards() {
        return cards;
    }

    int getShownCards() {
        return (int) cards.stream().filter(SolitaireCard::isShown).count();
    }

    int getNotShownCards() {
        return (int) cards.stream().filter(e -> !e.isShown()).count();
    }

    List<SolitaireCard> removeAllCards() {
        List<SolitaireCard> collect = new ArrayList<>(cards);
        cards.clear();
        return collect;
    }

    public enum StackType {
        MAIN,
        DROP,
        SIMPLE,
        FINAL
    }
}