/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.solitaire;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import red.guih.games.BaseView;
import red.guih.games.R;


/**
 * @author Note
 */
public class SolitaireView extends BaseView {
    private CardStack[] ascendingStacks = new CardStack[4];
    private DragContext dragContext = new DragContext();
    private List<CardStack> gridPane = new ArrayList<>();
    private CardStack[] simpleStacks = new CardStack[7];
    private CardStack mainCardStack;
    private CardStack dropCardStack;

    public SolitaireView(Context c, AttributeSet attrs) {
        super(c, attrs);
        reset();
    }

    private static boolean isNullOrEmpty(Collection<?> cards) {
        return cards == null || cards.isEmpty();
    }

    private void reset() {
        youwin = false;
        gridPane.clear();
        List<SolitaireCard> allCards = getAllCards();
        mainCardStack = new CardStack(CardStack.StackType.MAIN, 0);
        mainCardStack.setLayoutX(SolitaireCard.getCardWidth() / 10);
        mainCardStack.setLayoutY(0);
        mainCardStack.addCards(allCards);
        gridPane.add(mainCardStack);

        dropCardStack = new CardStack(CardStack.StackType.DROP, 0);
        dropCardStack.setLayoutX(getWidth() / 7 + SolitaireCard.getCardWidth() / 10);
        dropCardStack.setLayoutY(0);
        gridPane.add(dropCardStack);

        for (int i = 0; i < 7; i++) {
            simpleStacks[i] = new CardStack(CardStack.StackType.SIMPLE, i + 1);
            simpleStacks[i].setLayoutX(getWidth() / 7 * i + SolitaireCard.getCardWidth() / 10);
            simpleStacks[i].setLayoutY((int) (SolitaireCard.getCardWidth() * 1.1));
            List<SolitaireCard> removeLastCards = mainCardStack.removeLastCards(i + 1);
            removeLastCards.forEach(card -> card.setShown(false));
            removeLastCards.get(i).setShown(true);
            simpleStacks[i].addCardsVertically(removeLastCards);
            gridPane.add(simpleStacks[i]);
        }
        for (int i = 0; i < 4; i++) {
            ascendingStacks[i] = new CardStack(CardStack.StackType.FINAL, i + 1);
            ascendingStacks[i].setLayoutX(getWidth() / 7 * (3 + i) + SolitaireCard.getCardWidth() / 10);
            gridPane.add(ascendingStacks[i]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleMousePressed(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMouseDragged(event);
                break;
            case MotionEvent.ACTION_UP:
                handleMouseReleased(event);
                automaticCard();
                break;
        }
        invalidate();
        return true;
    }

    private void clickFirstStack() {
        if (!mainCardStack.getCards().isEmpty()) {
            SolitaireCard lastCards = mainCardStack.removeLastCards();
            lastCards.setShown(true);
            dropCardStack.addCards(lastCards);
        } else {
            List<SolitaireCard> removeAllCards = dropCardStack.removeAllCards();
            Collections.reverse(removeAllCards);
            removeAllCards.forEach(card -> card.setShown(false));
            mainCardStack.addCards(removeAllCards);
        }
        dragContext.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (CardStack e : gridPane) {
            e.draw(canvas);
        }
        if (!isNullOrEmpty(dragContext.cards)) {
            for (SolitaireCard e : dragContext.cards) {
                e.draw(canvas, 0, 0);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        SolitaireCard.setCardWidth(getWidth() / 7);
        reset();
    }

    private List<SolitaireCard> getAllCards() {
        SolitaireNumber[] solitaireNumbers = SolitaireNumber.values();
        SolitaireSuit[] solitaireSuits = SolitaireSuit.values();
        List<SolitaireCard> allCards = new ArrayList<>();
        for (SolitaireNumber number : solitaireNumbers) {
            for (SolitaireSuit suit : solitaireSuits) {
                SolitaireCard solitaireCard = new SolitaireCard(number, suit, getContext());
                allCards.add(solitaireCard);
            }
        }
        Collections.shuffle(allCards);
        return allCards;
    }

    private void handleMouseDragged(MotionEvent event) {

        if (dragContext.stack == null || dragContext.stack.type == CardStack.StackType.MAIN) {
            return;
        }
        CardStack node = dragContext.stack;
        Log.i("DRAGGED", " stack: " + node + " bounds:" + node.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");


        float offsetX = event.getX() + dragContext.x;
        float offsetY = event.getY() + dragContext.y;
        if (dragContext.cards != null) {
            int i = 0;
            for (SolitaireCard c : dragContext.cards) {
                c.relocate(offsetX, offsetY + i * SolitaireCard.getCardWidth() / 4);
                i++;
            }
        }
    }

    private void handleMousePressed(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        CardStack stack =
                gridPane.stream().filter(e -> e.getBoundsF().contains(x, y)).findFirst().orElse(null);
        if (stack == null) {
            dragContext.reset();
            return;
        }
        Log.i("PRESSED", " stack: " + stack + " bounds:" + stack.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");


        if (stack.type == CardStack.StackType.MAIN) {
            clickFirstStack();
            return;
        }
        dragContext.x = stack.getLayoutX() - event.getX();
        dragContext.y = stack.getLayoutY() - event.getY();
        if (stack.type == CardStack.StackType.SIMPLE || stack.type == CardStack.StackType.DROP) {
            List<SolitaireCard> cards = stack.getCards();
            List<SolitaireCard> lastCards = new ArrayList<>();
            List<SolitaireCard> showCards = cards.stream().filter(SolitaireCard::isShown).collect(Collectors.toList());
            for (SolitaireCard solitaireCard : showCards) {
                if (solitaireCard.getLayoutY() + stack.getLayoutY() < event.getY()) {
                    lastCards.clear();
                }
                lastCards.add(solitaireCard);
            }
            dragContext.cards.clear();
            dragContext.stack = stack;
            if (!lastCards.isEmpty()) {
                stack.removeCards(lastCards);
                dragContext.cards.addAll(lastCards);
                dragContext.y = stack.getLayoutY() + lastCards.get(0).getLayoutY() - event.getY();
                handleMouseDragged(event);
                dragContext.stack = stack;
            }
            return;
        }
        SolitaireCard lastCards = stack.removeLastCards();
        if (stack.type == CardStack.StackType.FINAL && lastCards != null) {
            dragContext.cards.clear();
            dragContext.cards.add(lastCards);
            dragContext.stack = stack;
            handleMouseDragged(event);
        }
    }

    int animationCount = 0;

    void automaticCard() {

        int solitaireNumber =

                Stream.of(ascendingStacks).map(e -> e.getLastCards() != null ? e.getLastCards().getNumber().getNumber() : 0)
                        .min(Comparator.comparing(e -> e))
                        .orElse(1);


        List<CardStack> collect = Stream.concat(Stream.of(simpleStacks), Stream.of(dropCardStack)).collect(Collectors.toList());
        int i = 0;
        for (CardStack stack : collect) {

            for (CardStack cardStack : ascendingStacks) {
                SolitaireCard solitaireCard = stack.getLastCards();
                if (solitaireCard != null && !isNotAscendingStackCompatible(cardStack, solitaireCard) && solitaireCard.getNumber().getNumber() <= solitaireNumber + 2) {
//                    solitaireCard.
                    stack.removeLastCards();
                    float x = -cardStack.getLayoutX() + stack.getLayoutX();
                    float y = -cardStack.getLayoutY() + stack.getLayoutY() + solitaireCard.getLayoutY();
                    cardStack.addCards(solitaireCard);
                    if (isStackAllHidden(stack)) {
                        stack.getLastCards().setShown(true);
                    }
                    PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("layoutX", Keyframe.ofFloat(0, x), Keyframe.ofFloat(1, 0));
                    PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("layoutY", Keyframe.ofFloat(0, y), Keyframe.ofFloat(1, 0));
                    ObjectAnimator eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(solitaireCard, pvhRotation, pvhRotation2);

                    eatingAnimation.setDuration(500);

                    eatingAnimation.addUpdateListener(animation -> invalidate());
//                    animationCount++;
                    eatingAnimation.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                                automaticCard();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    eatingAnimation.start();
                    return;
                }
            }
        }
        if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
            showDialogWinning();
        }

    }

    boolean youwin = false;

    private void handleMouseReleased(MotionEvent event) {
        if (Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
            showDialogWinning();
        }
        if (isNullOrEmpty(dragContext.cards)) {
            return;
        }
        if (dragContext.stack == null || dragContext.stack.type == CardStack.StackType.MAIN) {
            dragContext.reset();
            return;
        }
        CardStack node = dragContext.stack;
        Log.i("DROPPED", " stack: " + node + " bounds:" + node.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");


        if (isDoubleClicked(event)) {
            for (CardStack cardStack : ascendingStacks) {
                SolitaireCard solitaireCard = dragContext.cards.toArray(new SolitaireCard[0])[dragContext.cards.size() - 1];
                if (isNotAscendingStackCompatible(cardStack, solitaireCard)) {
                    continue;
                }
                cardStack.addCards(solitaireCard);
                if (isStackAllHidden(dragContext.stack)) {
                    dragContext.stack.getLastCards().setShown(true);
                }
                dragContext.cards.clear();
                return;
            }
        }

        SolitaireCard first = dragContext.cards.iterator().next();
        if (dragContext.cards.size() == 1) {
            Iterable<CardStack> hoveredStacks = getHoveredStacks(ascendingStacks);
            for (CardStack cardStack : hoveredStacks) {
                if (isNotAscendingStackCompatible(cardStack, first)) {
                    continue;
                }
                cardStack.addCards(dragContext.cards);
                if (isStackAllHidden(dragContext.stack)) {
                    dragContext.stack.getLastCards().setShown(true);
                }
                dragContext.cards.clear();
                if (Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
                    showDialogWinning();
                }
                return;
            }
        }

        for (CardStack cardStack : getHoveredStacks(simpleStacks)) {
            if (isCardNotCompatibleWithStack(cardStack, first)) {
                continue;
            }
            cardStack.addCardsVertically(dragContext.cards);
            if (isStackAllHidden(dragContext.stack)) {
                dragContext.stack.getLastCards().setShown(true);
            }
            dragContext.cards.clear();
            return;
        }
        if (dragContext.stack.type == CardStack.StackType.SIMPLE) {
            dragContext.stack.addCardsVertically(dragContext.cards);
            dragContext.cards.clear();
            return;
        }
        dragContext.stack.addCards(dragContext.cards);
        dragContext.cards.clear();


    }

    private void showDialogWinning() {
        youwin = true;
//        String s = getResources().getString(R.string.you_win);
//        String format = String.format(s, moves + " moves");
//        if (isRecordSuitable(moves, UserRecord.SOLITAIRE, MAP_WIDTH, true)) {
//            createRecordIfSuitable(moves, format, UserRecord.SLIDING_PUZZLE, MAP_WIDTH, true);
//            showRecords(MAP_WIDTH, UserRecord.SOLITAIRE, this::reset);
//            return;
//        }


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.minesweeper_dialog);
        dialog.setTitle(R.string.you_win);
        // set the custom minesweeper_dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(R.string.game_over);
        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom minesweeper_dialog
        dialogButton.setOnClickListener(v -> {
            this.reset();
            invalidate();
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        invalidate();
    }

    private boolean isNotAscendingStackCompatible(CardStack cardStack, SolitaireCard solitaireCard) {
        return isStackEmptyAndCardIsNotAce(cardStack, solitaireCard)
                || isNotNextCardInStack(cardStack, solitaireCard);
    }

    private boolean isDoubleClicked(MotionEvent event) {
//		return event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
        return false;
    }

    private Iterable<CardStack> getHoveredStacks(CardStack[] stacks) {
        SolitaireCard next = dragContext.cards.iterator().next();
        return Stream.of(stacks)
                .filter(s -> RectF.intersects(s.getBoundsF(), next.getBounds(0, 0)))
                .collect(Collectors.toList());
    }

    private boolean isCardNotCompatibleWithStack(CardStack cardStack, SolitaireCard solitaireCard) {
        return cardStack.getCards().isEmpty() && solitaireCard.getNumber() != SolitaireNumber.KING || !cardStack
                .getCards().isEmpty()
                && (solitaireCard.getSuit().getColor() == cardStack.getLastCards().getSuit().getColor() || solitaireCard
                .getNumber().getNumber() != cardStack.getLastCards().getNumber().getNumber() - 1);
    }

    private boolean isStackAllHidden(CardStack stack) {
        return !stack.getCards().isEmpty() && stack.getCards().stream().noneMatch(SolitaireCard::isShown);
    }

    private boolean isStackEmptyAndCardIsNotAce(CardStack cardStack, SolitaireCard solitaireCard) {
        return cardStack.getCards().isEmpty() && solitaireCard.getNumber() != SolitaireNumber.ACE;
    }

    private boolean isNotNextCardInStack(CardStack cardStack, SolitaireCard solitaireCard) {
        return !cardStack.getCards().isEmpty() && (solitaireCard.getSuit() != cardStack.getLastCards().getSuit()
                || solitaireCard.getNumber().getNumber() != cardStack.getLastCards().getNumber().getNumber() + 1);
    }

    private static class DragContext {
        protected final Set<SolitaireCard> cards = new LinkedHashSet<>();
        protected CardStack stack;
        protected float x;
        protected float y;

        void reset() {
            cards.clear();
            stack = null;
        }
    }

//	public void makeDraggable(final Node node) {
//		node.setOnMousePressed(this::handleMousePressed);
//		node.setOnMouseDragged(this::handleMouseDragged);
//		node.setOnMouseReleased(this::handleMouseReleased);
//	}

//	public static SolitaireModel create(Pane gridPane, Scene scene) {
//		return new SolitaireModel(gridPane, scene);
//	}

}
