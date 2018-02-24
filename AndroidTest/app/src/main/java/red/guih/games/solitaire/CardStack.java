package red.guih.games.solitaire;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CardStack {


    public enum StackType {
        MAIN,
        DROP,
        SIMPLE,
        FINAL;
    }

    public final StackType type;
    private final int n;
    private List<SolitaireCard> cards = new ArrayList<>();
    private int layoutX, layoutY;
    private Paint paint = new Paint();
    private RectF boundsF;

    public CardStack(StackType type, int n) {
        this.type = type;
        this.n = n;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
//		setPrefSize(50, 75);
//		setPadding(new Insets(10));
//		setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(5), new Insets(1))));
//		setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
//		styleProperty().bind(Bindings.createStringBinding(() -> {
//			StringBuilder style = new StringBuilder();
//			style.append("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
//			if (cards.isEmpty()) {
//				style.append("-fx-background-color:green;");
//			}
//			return style.toString();
//		}, cards));

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

    public SolitaireCard getLastCards() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }


    public RectF getBoundsF() {

        if (boundsF == null) {
            boundsF = new RectF();
        }
        int right = SolitaireCard.getCardWidth();
        float bottom = cards.isEmpty() ? SolitaireCard.getCardWidth() : getLastCards().getBoundsF().bottom;


        boundsF.set(getLayoutX(), getLayoutY(), right + layoutX, bottom + layoutY);
        return boundsF;
    }


    public List<SolitaireCard> removeLastCards(int n) {
        if (cards.isEmpty()) {
            return null;
        }
        List<SolitaireCard> lastCards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            SolitaireCard solitaireCard = cards.remove(cards.size() - 1);
//			getChildren().remove(solitaireCard);
            lastCards.add(solitaireCard);
        }
        return lastCards;
    }

    public SolitaireCard removeLastCards() {
        if (cards.isEmpty()) {
            return null;
        }

        return cards.remove(cards.size() - 1);
    }

    public void addCards(Collection<SolitaireCard> cards) {
        addCards(cards.toArray(new SolitaireCard[0]));
    }

    public void addCardsVertically(Collection<SolitaireCard> cards) {
        addCardsVertically(cards.toArray(new SolitaireCard[0]));
    }

    public void addCardsVertically(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
//				getChildren().add(solitaireCard);
            }
        }
        int layout = 0;
        for (int i = 0; i < this.cards.size(); i++) {
            SolitaireCard solitaireCard = this.cards.get(i);
            solitaireCard.setLayoutY(layout);
            layout += SolitaireCard.getCardWidth() / (solitaireCard.isShown() ? 3 : 8) ;
        }

    }

    public void addCards(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            if (!this.cards.contains(solitaireCard)) {
                this.cards.add(solitaireCard);
                solitaireCard.setLayoutX(0);
                solitaireCard.setLayoutY(0);
            }
        }
    }

    public void removeCards(List<SolitaireCard> cards) {
        removeCards(cards.toArray(new SolitaireCard[0]));
    }

    public void draw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(getLayoutX(), getLayoutY(), SolitaireCard.getCardWidth() + layoutX, SolitaireCard.getCardWidth() + layoutY, 5, 5, paint);
        for (SolitaireCard card : cards) {
            card.draw(canvas, layoutX, layoutY);
        }

    }


    public void removeCards(SolitaireCard... cards) {
        for (SolitaireCard solitaireCard : cards) {
            if (this.cards.contains(solitaireCard)) {
                this.cards.remove(solitaireCard);
//				getChildren().remove(solitaireCard);
            }
        }
    }

    public List<SolitaireCard> getCards() {
        return cards;
    }

    public List<SolitaireCard> removeAllCards() {
//		getChildren().clear();
        List<SolitaireCard> collect = new ArrayList<>(cards);
        cards.clear();
        return collect;
    }
}