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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import red.guih.games.BaseView;
import red.guih.games.R;


/**
 * @author Note
 */
public class SolitaireView extends BaseView {
    public static final int ANIMATION_DURATION = 250;
    public static final int DARK_GREEN = 0xFF008800;
    private final CardStack[] ascendingStacks = new CardStack[4];
    private final DragContext dragContext = new DragContext();
    private final Collection<CardStack> cardStackList = new ArrayList<>();
    private final CardStack[] simpleStacks = new CardStack[7];
    private CardStack mainCardStack;
    private CardStack dropCardStack;
    private final List<MotionHistory> history = new ArrayList<>();
    private Rect returnButton;
    private final Drawable returnButtonIcon;

    public SolitaireView(Context c, AttributeSet attrs) {
        super(c, attrs);
        returnButtonIcon = getResources().getDrawable(R.drawable.return_button, null);

        Log.i("SOLITAIRE", "NEW INSTANCE");
        reset();
    }

    private static boolean isNullOrEmpty(Collection<?> cards) {
        return cards == null || cards.isEmpty();
    }
    <T> void copy(T[] destination, T[] origin) {
        System.arraycopy(origin, 0, destination, 0, origin.length);
    }


    void copy(SolitaireView solitaireView) {
        copy(ascendingStacks, solitaireView.ascendingStacks);
        copy(simpleStacks, solitaireView.simpleStacks);
        cardStackList.clear();
        cardStackList.addAll(solitaireView.cardStackList);
        mainCardStack=solitaireView.mainCardStack;
        dropCardStack=solitaireView.dropCardStack;
        history.clear();
        history.addAll(solitaireView.history);
    }


    public void reset() {
        youwin = false;
        history.clear();
        cardStackList.clear();
        int yOffset = SolitaireCard.getCardWidth() / 2;

        List<SolitaireCard> allCards = getAllCards();
        mainCardStack = new CardStack(CardStack.StackType.MAIN, 0);
        mainCardStack.setLayoutX(SolitaireCard.getCardWidth() / 10);
        mainCardStack.setLayoutY(yOffset);
        mainCardStack.addCards(allCards);
        cardStackList.add(mainCardStack);

        dropCardStack = new CardStack(CardStack.StackType.DROP, 0);
        dropCardStack.setLayoutX(getWidth() / 7 + SolitaireCard.getCardWidth() / 10);
        dropCardStack.setLayoutY(yOffset);
        cardStackList.add(dropCardStack);

        for (int i = 0; i < 7; i++) {
            simpleStacks[i] = new CardStack(CardStack.StackType.SIMPLE, i + 1);
            simpleStacks[i].setLayoutX(getWidth() / 7 * i + SolitaireCard.getCardWidth() / 10);
            simpleStacks[i].setLayoutY(SolitaireCard.getCardWidth() + SolitaireCard.getCardWidth() / 10 + yOffset);
            List<SolitaireCard> removeLastCards = mainCardStack.removeLastCards(i + 1);
            removeLastCards.forEach(card -> card.setShown(false));
            removeLastCards.get(i).setShown(true);
            simpleStacks[i].addCardsVertically(removeLastCards);
            cardStackList.add(simpleStacks[i]);
        }
        for (int i = 0; i < 4; i++) {
            ascendingStacks[i] = new CardStack(CardStack.StackType.FINAL, i + 1);
            ascendingStacks[i].setLayoutX(getWidth() / 7 * (3 + i) + SolitaireCard.getCardWidth() / 10);
            ascendingStacks[i].setLayoutY(yOffset);
            cardStackList.add(ascendingStacks[i]);
        }
        returnButton = new Rect(getWidth() - SolitaireCard.getCardWidth(),
                getHeight() - SolitaireCard.getCardWidth(),
                getWidth(),
                getHeight());
        returnButtonIcon.setBounds(returnButton);
        invalidate();
        Log.i("SOLITAIRE", "RESETED");

    }

    public void rescale() {
        Log.i("SOLITAIRE", "RESCALED");
        youwin = false;

        int yOffset = SolitaireCard.getCardWidth() / (getWidth() > getHeight() ? 4 : 2);


        mainCardStack.setLayoutX(SolitaireCard.getCardWidth() / 10);
        mainCardStack.setLayoutY(yOffset);

        dropCardStack.setLayoutX(getWidth() / 7 + SolitaireCard.getCardWidth() / 10);
        dropCardStack.setLayoutY(yOffset);

        for (int i = 0; i < 7; i++) {
            simpleStacks[i].setLayoutX(getWidth() / 7 * i + SolitaireCard.getCardWidth() / 10);
            simpleStacks[i].setLayoutY(SolitaireCard.getCardWidth() + SolitaireCard.getCardWidth() / 10 + yOffset);
            simpleStacks[i].adjust();
        }
        for (int i = 0; i < 4; i++) {
            ascendingStacks[i].setLayoutX(getWidth() / 7 * (3 + i) + SolitaireCard.getCardWidth() / 10);
            ascendingStacks[i].setLayoutY(yOffset);
        }
        returnButton = new Rect(getWidth() - SolitaireCard.getCardWidth(),
                getHeight() - SolitaireCard.getCardWidth(),
                getWidth(),
                getHeight());
        returnButtonIcon.setBounds(returnButton);
        invalidate();
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

    private void clickMainStack() {
        if (!mainCardStack.getCards().isEmpty()) {
            SolitaireCard lastCards = mainCardStack.removeLastCards();
            lastCards.setShown(true);
            MotionHistory motionHistory = new MotionHistory(lastCards, mainCardStack, dropCardStack);
            motionHistory.shownCard = lastCards;
            history.add(motionHistory);
            dropCardStack.addCards(lastCards);
        } else {
            List<SolitaireCard> removeAllCards = dropCardStack.removeAllCards();
            Collections.reverse(removeAllCards);
            removeAllCards.forEach(card -> card.setShown(false));
            mainCardStack.addCards(removeAllCards);
            MotionHistory motionHistory = new MotionHistory(removeAllCards, dropCardStack, mainCardStack);

            history.add(motionHistory);
        }
        dragContext.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(DARK_GREEN);
        for (CardStack e : cardStackList) {
            e.draw(canvas);
        }
        if (!isNullOrEmpty(dragContext.cards)) {
            for (SolitaireCard e : dragContext.cards) {
                e.draw(canvas, 0, 0);
            }
        }
        returnButtonIcon.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int cardWidth = SolitaireCard.getCardWidth();
        SolitaireCard.setCardWidth(getWidth() / 7);
        if (cardWidth == 0) {
            reset();
        } else {
            rescale();
        }
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
        Log.i("DRAGGED", " originStack: " + node + " bounds:" + node.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");


        float offsetX = event.getX() + dragContext.x;
        float offsetY = event.getY() + dragContext.y;
        if (dragContext.cards != null) {
            int i = 0;
            for (SolitaireCard c : dragContext.cards) {
                c.relocate(offsetX, offsetY + i * SolitaireCard.getCardWidth() / 3);
                i++;
            }
        }
    }

    private void handleMousePressed(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (returnButton.contains((int) x, (int) y) && !history.isEmpty()) {
            MotionHistory remove = history.remove(history.size() - 1);
            if (remove.shownCard != null) {
                remove.shownCard.setShown(false);
            }
            if (remove.targetStack.type == CardStack.StackType.MAIN) {
                remove.cards.forEach(e -> e.setShown(true));
                Collections.reverse(remove.cards);
            }

            remove.cards.forEach(e -> createMovingCardAnimation(remove.targetStack, remove.originStack, e));
            dragContext.reset();
            return;
        }


        CardStack stack =
                cardStackList.stream().filter(e -> e.getBoundsF().contains(x, y)).findFirst().orElse(null);
        if (stack == null) {
            dragContext.reset();
            return;
        }
        Log.i("PRESSED", " originStack: " + stack + " bounds:" + stack.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");


        if (stack.type == CardStack.StackType.MAIN) {
            clickMainStack();
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

    void automaticCard() {

        int solitaireNumber =

                Stream.of(ascendingStacks).map(e -> e.getLastCards() != null ? e.getLastCards().getNumber().getNumber() : 0)
                        .min(Comparator.comparing(e -> e))
                        .orElse(1);


        List<CardStack> collect = Stream.concat(Stream.of(simpleStacks), Stream.of(dropCardStack)).collect(Collectors.toList());
        for (CardStack stack : collect) {

            for (CardStack cardStack : ascendingStacks) {
                SolitaireCard solitaireCard = stack.getLastCards();
                if (solitaireCard != null
                        && !isNotAscendingStackCompatible(cardStack, solitaireCard)
                        && !Objects.equals(dragContext.stack, stack)
                        && !solitaireCard.autoMoved
                        && solitaireCard.getNumber().getNumber() <= solitaireNumber + 2) {
                    solitaireCard.autoMoved = true;
                    createMovingCardAnimation(stack, cardStack, solitaireCard);
                    MotionHistory motionHistory = new MotionHistory(solitaireCard, stack, cardStack);
                    history.add(motionHistory);
                    if (isStackAllHidden(stack)) {
                        stack.getLastCards().setShown(true);
                        motionHistory.shownCard = stack.getLastCards();
                    }
                    return;
                }
            }
        }
        if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
            showDialogWinning();
        }

    }

    private void createMovingCardAnimation(CardStack originStack, CardStack targetStack, SolitaireCard solitaireCard) {

        cardStackList.remove(targetStack);
        cardStackList.add(targetStack);

        originStack.removeLastCards();
        float x = -targetStack.getLayoutX() + originStack.getLayoutX();
        float y = -targetStack.getLayoutY() + originStack.getLayoutY() + solitaireCard.getLayoutY();
        targetStack.addCards(solitaireCard);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("layoutX", Keyframe.ofFloat(0, x), Keyframe.ofFloat(1, 0));
        int value = 0;
        if (targetStack.type == CardStack.StackType.SIMPLE) {
            value = (targetStack.getShownCards() - 1) * SolitaireCard.getCardWidth() / 3;
            if (targetStack.getNotShownCards() > 0) {
                value += targetStack.getNotShownCards() * SolitaireCard.getCardWidth() / 8;
            }
        }

        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("layoutY", Keyframe.ofFloat(0, y), Keyframe.ofFloat(1, value));
        ObjectAnimator eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(solitaireCard, pvhRotation, pvhRotation2);

        eatingAnimation.setDuration(ANIMATION_DURATION);

        eatingAnimation.addUpdateListener(animation -> invalidate());
        eatingAnimation.addListener(new AutomaticCardsListener());
        eatingAnimation.start();
    }

    boolean youwin;

    private void handleMouseReleased(MotionEvent event) {
        if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
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
        Log.i("DROPPED", " originStack: " + node + " bounds:" + node.getBoundsF() + " (x,y): (" + event.getX() + "," + event.getY() + ")");
        SolitaireCard first = dragContext.cards.iterator().next();
        if (dragContext.cards.size() == 1) {
            Collection<CardStack> hoveredStacks = getHoveredStacks(ascendingStacks);
            for (CardStack cardStack : hoveredStacks) {
                if (isNotAscendingStackCompatible(cardStack, first)) {
                    continue;
                }
                MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, cardStack);
                history.add(motionHistory);
                cardStack.addCards(dragContext.cards);
                if (isStackAllHidden(dragContext.stack)) {
                    dragContext.stack.getLastCards().setShown(true);
                    motionHistory.shownCard = dragContext.stack.getLastCards();
                }
                dragContext.reset();
                if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == SolitaireNumber.values().length)) {
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
            MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, cardStack);
            history.add(motionHistory);
            if (isStackAllHidden(dragContext.stack)) {
                dragContext.stack.getLastCards().setShown(true);
                motionHistory.shownCard = dragContext.stack.getLastCards();
            }
            dragContext.reset();
            return;
        }
        if (dragContext.stack.type == CardStack.StackType.SIMPLE) {
            dragContext.stack.addCardsVertically(dragContext.cards);
            dragContext.reset();
            return;
        }
        dragContext.stack.addCards(dragContext.cards);
        dragContext.reset();


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


    private Collection<CardStack> getHoveredStacks(CardStack[] stacks) {
        SolitaireCard next = dragContext.cards.iterator().next();
        return Stream.of(stacks)
                .filter(s -> RectF.intersects(s.getBoundsF(), next.getBounds()))
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

    private static class MotionHistory {
        protected final List<SolitaireCard> cards = new ArrayList<>();
        protected CardStack originStack;
        protected CardStack targetStack;
        protected SolitaireCard shownCard;

        MotionHistory(Collection<SolitaireCard> cards, CardStack originStack, CardStack targetStack) {
            this.originStack = originStack;
            this.targetStack = targetStack;
            this.cards.addAll(cards);
        }


        MotionHistory(SolitaireCard cards, CardStack originStack, CardStack targetStack) {
            this.originStack = originStack;
            this.targetStack = targetStack;
            this.cards.add(cards);
        }

    }


    private class AutomaticCardsListener implements Animator.AnimatorListener {

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
    }
}
