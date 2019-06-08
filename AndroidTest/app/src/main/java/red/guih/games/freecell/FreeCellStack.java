package red.guih.games.freecell;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import red.guih.games.StackOfCards;

class FreeCellStack extends StackOfCards {
    final StackType type;
    private final int n;
    private final List<FreeCellCard> cards = new ArrayList<>();
    private final Paint paint = new Paint();
    private float maxHeight;
    private RectF boundsF;

    FreeCellStack(StackType type, int n) {
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


    FreeCellCard getLastCards() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }

    void removeLastCards() {
        if (cards.isEmpty()) {
            return;
        }

        cards.remove(cards.size() - 1);
    }

    void addCards(Collection<FreeCellCard> cards) {
        if (type == StackType.SIMPLE) {
            addCardsVertically(cards);
        } else {
            addCards(cards.toArray(new FreeCellCard[0]));
        }
    }

    private void addCardsVertically(Collection<FreeCellCard> cards) {
        addCardsVertically(cards.toArray(new FreeCellCard[0]));
    }

    void addCards(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
                solitaireCard.setLayoutY(0);
            }
        }
    }

    void addCardsVertically(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
            }
        }
        adjust();

    }

    float adjust() {
        return adjust(cards.size());
    }

    float adjust(int cards) {
        if (type != StackType.SIMPLE) {
            return 0;
        }
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            FreeCellCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += FreeCellCard.getCardWidth() / 3;
        }
        float spaceToDisplay = maxHeight - layoutY - FreeCellCard.getCardWidth() / 3F;
        if (FreeCellCard.getCardWidth() / 3 * cards <= spaceToDisplay) {
            return layout - FreeCellCard.getCardWidth() / 3F;
        }
        float newGap = spaceToDisplay / cards;
        layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            FreeCellCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += newGap;
        }
        return layout - newGap;

    }

    void removeCards(List<FreeCellCard> cards) {
        removeCards(cards.toArray(new FreeCellCard[0]));
    }

    private void removeCards(FreeCellCard... cards) {
        for (FreeCellCard solitaireCard : cards) {
            this.cards.remove(solitaireCard);
        }
    }

    public void draw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        float right = (float) FreeCellCard.getCardWidth() + layoutX;
        float bottom = (float) FreeCellCard.getCardWidth() + layoutY;
        canvas.drawRoundRect(getLayoutX(), getLayoutY(), right, bottom, 5, 5, paint);
        for (FreeCellCard card : cards) {
            card.draw(canvas, layoutX, layoutY);
        }

    }

    List<FreeCellCard> getCards() {
        return cards;
    }

    int getShownCards() {
        return (int) cards.stream().filter(FreeCellCard::isShown).count();
    }

    void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    RectF getBoundsF() {

        if (boundsF == null) {
            boundsF = new RectF();
        }
        float right = FreeCellCard.getCardWidth();
        float bottom = cards.isEmpty() ? FreeCellCard.getCardWidth() : getLastCards()
                .getBoundsF().bottom;


        boundsF.set(getLayoutX(), getLayoutY(), right + layoutX, bottom + layoutY);
        return boundsF;
    }

    public enum StackType {
        SIMPLE,
        ASCENDING,
        SUPPORT
    }
}