/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.guih.games.freecell;

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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import red.guih.games.BaseView;
import red.guih.games.R;

import static red.guih.games.freecell.FreeCellStack.StackType.ASCENDING;
import static red.guih.games.freecell.FreeCellStack.StackType.SIMPLE;
import static red.guih.games.freecell.FreeCellStack.StackType.SUPPORT;


/**
 * @author Note
 */
public class FreeCellView extends BaseView {
    public static final int ANIMATION_DURATION = 250;
    public static final int DARK_GREEN = 0xFF008800;
    private final FreeCellStack[] ascendingStacks = new FreeCellStack[4];
    private final FreeCellStack[] supportingStacks = new FreeCellStack[4];
    private final DragContext dragContext = new DragContext();
    private final List<FreeCellStack> cardStackList = new ArrayList<>();
    private final List<MotionHistory> history = new ArrayList<>();
    private final FreeCellStack[] simpleStacks = new FreeCellStack[8];
    private final Drawable returnButtonIcon;
    boolean youwin;
    private final Drawable crown;
    private Rect returnButton;

    public FreeCellView(Context c, AttributeSet attrs) {
        super(c, attrs);

        crown = getResources().getDrawable(R.drawable.crown, null);

        returnButtonIcon = getResources().getDrawable(R.drawable.return_button, null);

        reset();
    }

    private static boolean isNullOrEmpty(Collection<?> cards) {
        return cards == null || cards.isEmpty();
    }

    public void reset() {
        youwin = false;
        cardStackList.clear();
        int yOffset = FreeCellCard.getCardWidth() / (getWidth() > getHeight() ? 10 : 2);

        int xOffset = FreeCellCard.getCardWidth() / 10;
        crown.setBounds(4 * getWidth() / 9 + xOffset, yOffset, 4 * getWidth() / 9 + xOffset + FreeCellCard.getCardWidth(), yOffset + FreeCellCard.getCardWidth());
        for (int i = 0; i < 4; i++) {
            supportingStacks[i] = new FreeCellStack(FreeCellStack.StackType.SUPPORT, 0);
            supportingStacks[i].setLayoutX(i * getWidth() / 9 + xOffset);
            supportingStacks[i].setLayoutY(yOffset);

            cardStackList.add(supportingStacks[i]);
        }
        for (int i = 0; i < 4; i++) {
            ascendingStacks[i] = new FreeCellStack(ASCENDING, 0);
            ascendingStacks[i].setLayoutX(getWidth() / 9 * (i + 5) + xOffset);
            ascendingStacks[i].setLayoutY(yOffset);

            cardStackList.add(ascendingStacks[i]);
        }

        for (int i = 0; i < 8; i++) {
            simpleStacks[i] = new FreeCellStack(FreeCellStack.StackType.SIMPLE, i + 1);
            simpleStacks[i].setLayoutX(getWidth() / 8 * i + xOffset);
            simpleStacks[i].setLayoutY(FreeCellCard.getCardWidth() + xOffset + yOffset);

            cardStackList.add(simpleStacks[i]);
        }
        List<FreeCellCard> allCards = getAllCards();
        for (int i = 0; i < allCards.size(); i++) {
            FreeCellCard card = allCards.get(i);
            card.setShown(true);
            simpleStacks[i % 8].addCardsVertically(card);
        }
        returnButton = new Rect(getWidth() - FreeCellCard.getCardWidth(),
                getHeight() - FreeCellCard.getCardWidth(),
                getWidth(),
                getHeight());
        returnButtonIcon.setBounds(returnButton);
        invalidate();
        Log.i("SOLITAIRE", "RESETED");
    }

    public void rescale() {
        Log.i("SOLITAIRE", "RESCALED");
        youwin = false;

        int yOffset = FreeCellCard.getCardWidth() / (getWidth() > getHeight() ? 10 : 2);
        int xOffset = FreeCellCard.getCardWidth() / 10;
        crown.setBounds(4 * getWidth() / 9 + xOffset, yOffset, 4 * getWidth() / 9 + xOffset + FreeCellCard.getCardWidth(), yOffset + FreeCellCard.getCardWidth());
        for (int i = 0; i < 4; i++) {
            supportingStacks[i].setLayoutX(i * getWidth() / 9 + xOffset);
            supportingStacks[i].setLayoutY(yOffset);
        }
        for (int i = 0; i < 4; i++) {
            ascendingStacks[i].setLayoutX(getWidth() / 9 * (i + 5) + xOffset);
            ascendingStacks[i].setLayoutY(yOffset);
        }
        for (int i = 0; i < 8; i++) {
            simpleStacks[i].setLayoutX(getWidth() / 8 * i + xOffset);
            simpleStacks[i].setLayoutY(FreeCellCard.getCardWidth() + xOffset + yOffset);
            simpleStacks[i].adjust();
        }
        returnButton = new Rect(getWidth() - FreeCellCard.getCardWidth(),
                getHeight() - FreeCellCard.getCardWidth(),
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
                handleMouseReleased();
                automaticCard();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(DARK_GREEN);
        crown.draw(canvas);

        for (FreeCellStack e : cardStackList) {
            e.draw(canvas);
        }
        if (!isNullOrEmpty(dragContext.cards)) {
            for (FreeCellCard e : dragContext.cards) {
                e.draw(canvas, 0, 0);
            }
        }
        returnButtonIcon.draw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int cardWidth = FreeCellCard.getCardWidth();
        FreeCellCard.setCardWidth(getWidth() / 8);
        if (cardWidth == 0) {
            reset();
        } else {
            rescale();
        }
    }

    private List<FreeCellCard> getAllCards() {
        FreeCellNumber[] solitaireNumbers = FreeCellNumber.values();
        FreeCellSuit[] solitaireSuits = FreeCellSuit.values();
        List<FreeCellCard> allCards = new ArrayList<>();
        for (FreeCellNumber number : solitaireNumbers) {
            for (FreeCellSuit suit : solitaireSuits) {
                FreeCellCard solitaireCard = new FreeCellCard(number, suit, getContext());
                allCards.add(solitaireCard);
            }
        }
        Collections.shuffle(allCards);
        return allCards;
    }

    private void handleMouseDragged(MotionEvent event) {

        if (dragContext.stack == null) {
            return;
        }
        float offsetX = event.getX() + dragContext.x;
        float offsetY = event.getY() + dragContext.y;
        if (dragContext.cards != null) {
            int i = 0;
            for (FreeCellCard c : dragContext.cards) {
                c.relocate(offsetX, offsetY + i * FreeCellCard.getCardWidth() / 3);
                i++;
            }
        }
    }

    private void handleMousePressed(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (returnButton.contains((int) x, (int) y) && !history.isEmpty()) {
            MotionHistory remove = history.remove(history.size() - 1);
            remove.cards.forEach(e -> createMovingCardAnimation(remove.targetStack, remove.originStack, e));
            dragContext.reset();
            return;
        }

        FreeCellStack stack =
                cardStackList.stream().filter(e -> e.getBoundsF().contains(x, y)).findFirst().orElse(null);
        if (stack == null) {
            dragContext.reset();
            return;
        }
        dragContext.x = stack.getLayoutX() - event.getX();
        dragContext.y = stack.getLayoutY() - event.getY();
        if (stack.type == FreeCellStack.StackType.SIMPLE || stack.type == FreeCellStack.StackType.SUPPORT) {
            List<FreeCellCard> cards = stack.getCards();
            List<FreeCellCard> lastCards = new ArrayList<>();
            List<FreeCellCard> showCards = cards.stream().filter(FreeCellCard::isShown).collect(Collectors.toList());
            for (FreeCellCard solitaireCard : showCards) {
                if (solitaireCard.getLayoutY() + stack.getLayoutY() < event.getY() || !lastCards.isEmpty() && !isStackContinuous(lastCards)) {
                    lastCards.clear();
                }

                if (lastCards.size() >= pileMaxSize()) {
                    lastCards.remove(0);
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

        }
//        FreeCellCard lastCards = stack.removeLastCards();
//        if (stack.type == FreeCellStack.StackType.FINAL && lastCards != null) {
//            dragContext.cards.clear();
//            dragContext.cards.add(lastCards);
//            dragContext.stack = stack;
//            handleMouseDragged(event);
//        }
    }

    void automaticCard() {

        int solitaireNumber =

                Stream.of(ascendingStacks).map(e -> e.getLastCards() != null ? e.getLastCards().getNumber().getNumber() : 0)
                        .min(Comparator.comparing(e -> e))
                        .orElse(1);


        List<FreeCellStack> collect = Stream.concat(Stream.of(simpleStacks), Stream.of(supportingStacks)).collect(Collectors.toList());
        for (FreeCellStack stack : collect) {

            for (FreeCellStack cardStack : ascendingStacks) {
                FreeCellCard solitaireCard = stack.getLastCards();
                if (solitaireCard != null
                        && !isNotAscendingStackCompatible(cardStack, solitaireCard)
                        && !Objects.equals(dragContext.stack, stack)
                        && !solitaireCard.autoMoved
                        && solitaireCard.getNumber().getNumber() <= solitaireNumber + 2) {
                    solitaireCard.autoMoved = true;
                    createMovingCardAnimation(stack, cardStack, solitaireCard);
                    MotionHistory motionHistory = new MotionHistory(solitaireCard, stack, cardStack);
                    history.add(motionHistory);
                    return;
                }
            }
        }
        if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == FreeCellNumber.values().length)) {
            showDialogWinning();
        }

    }

    private void createMovingCardAnimation(FreeCellStack originStack, FreeCellStack targetStack, FreeCellCard solitaireCard) {

        cardStackList.remove(targetStack);
        cardStackList.add(targetStack);
        solitaireCard.setShown(true);
        originStack.removeLastCards();
        float x = -targetStack.getLayoutX() + originStack.getLayoutX();
        float y = -targetStack.getLayoutY() + originStack.getLayoutY() + solitaireCard.getLayoutY();
        targetStack.addCards(solitaireCard);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("layoutX", Keyframe.ofFloat(0, x), Keyframe.ofFloat(1, 0));
        int value = 0;
        if (targetStack.type == FreeCellStack.StackType.SIMPLE) {
            value = (targetStack.getShownCards() - 1) * FreeCellCard.getCardWidth() / 3;
            if (targetStack.getNotShownCards() > 0) {
                value += targetStack.getNotShownCards() * FreeCellCard.getCardWidth() / 8;
            }
        }

        PropertyValuesHolder pvhRotation2 = PropertyValuesHolder.ofKeyframe("layoutY", Keyframe.ofFloat(0, y), Keyframe.ofFloat(1, value));
        ObjectAnimator eatingAnimation = ObjectAnimator.ofPropertyValuesHolder(solitaireCard, pvhRotation, pvhRotation2);

        eatingAnimation.setDuration(ANIMATION_DURATION);

        eatingAnimation.addUpdateListener(animation -> invalidate());
        eatingAnimation.addListener(new AutomaticCardsListener());
        eatingAnimation.start();
    }

    private void handleMouseReleased() {
        if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == FreeCellNumber.values().length)) {
            showDialogWinning();
        }
        if (isNullOrEmpty(dragContext.cards)) {
            return;
        }
        if (dragContext.stack == null) {
            dragContext.reset();
            return;
        }


        FreeCellCard first = dragContext.cards.iterator().next();
        if (dragContext.cards.size() == 1) {
            Collection<FreeCellStack> hoveredStacks = getHoveredStacks(ascendingStacks);
            hoveredStacks.addAll(getHoveredStacks(supportingStacks));
            for (FreeCellStack cardStack : hoveredStacks) {
                if (Objects.equals(cardStack, dragContext.stack) || cardStack.type == ASCENDING && isNotAscendingStackCompatible(cardStack, first)) {
                    continue;
                }
                if (cardStack.type == SUPPORT && !cardStack.getCards().isEmpty()) {
                    continue;
                }
                MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, cardStack);
                history.add(motionHistory);
                cardStack.addCards(dragContext.cards);
                dragContext.reset();
                if (!youwin && Stream.of(ascendingStacks).allMatch(e -> e.getCards().size() == FreeCellNumber.values().length)) {
                    showDialogWinning();
                }
                return;
            }
        }

        for (FreeCellStack cardStack : getHoveredStacks(simpleStacks)) {
            while (dragContext.cards.size() > pileMaxSize(cardStack)) {
                FreeCellCard remove = dragContext.cards.remove(0);
                dragContext.stack.addCards(remove);
            }
            if (cardStack.getCards().isEmpty()
                    || first.getSuit().getColor() == cardStack.getLastCards().getSuit().getColor()
                    || first.getNumber().getNumber() != cardStack.getLastCards().getNumber().getNumber() - 1
                    || Objects.equals(cardStack, dragContext.stack)) {
                continue;
            }

            MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, cardStack);
            history.add(motionHistory);
            cardStack.addCards(dragContext.cards);
            dragContext.reset();
            return;
        }
        cardStackList.sort(Comparator.comparing((FreeCellStack e) -> e.type).thenComparing((FreeCellStack e) -> -e.getCards().size()));

        for (FreeCellStack e : cardStackList) {
            if (Objects.equals(e, dragContext.stack)) {
                continue;
            }
            if (e.type == SIMPLE && isSimpleStackCompatible(first, e) && isStackContinuous(dragContext.cards)) {
                while (dragContext.cards.size() > pileMaxSize(e)) {
                    FreeCellCard remove = dragContext.cards.remove(0);
                    dragContext.stack.addCards(remove);
                }
                dragContext.stack.addCards(dragContext.cards);
                for (FreeCellCard c : dragContext.cards) {
                    createMovingCardAnimation(dragContext.stack, e, c);
                }
                MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, e);
                history.add(motionHistory);
                dragContext.reset();
                return;
            }

            if (e.type == ASCENDING && dragContext.cards.size() == 1 && isCompatibleAscending(first, e)) {
                dragContext.stack.addCards(dragContext.cards);
                createMovingCardAnimation(dragContext.stack, e, first);
                MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, e);
                history.add(motionHistory);
                dragContext.reset();
                return;
            }
            if (dragContext.stack.type == SIMPLE && e.type == SUPPORT && e.getCards().isEmpty() && dragContext.cards.size() == 1) {
                dragContext.stack.addCards(dragContext.cards);
                createMovingCardAnimation(dragContext.stack, e, first);
                MotionHistory motionHistory = new MotionHistory(dragContext.cards, dragContext.stack, e);
                history.add(motionHistory);
                dragContext.reset();
                return;
            }
        }


        dragContext.stack.addCards(dragContext.cards);
        dragContext.reset();
    }

    private boolean isSimpleStackCompatible(FreeCellCard first, FreeCellStack e) {
        return e.getCards().isEmpty() || !(e.getCards().isEmpty()
                || first.getSuit().getColor() == e.getLastCards().getSuit().getColor()
                || first.getNumber().getNumber() != e.getLastCards().getNumber().getNumber() - 1);
    }

    private boolean isStackContinuous(Collection<FreeCellCard> first) {
        if (first.isEmpty()) {
            return false;
        }
        int n = -1;
        int color = -1;
        for (FreeCellCard c : first) {
            if (n == -1) {
                n = c.getNumber().getNumber();
                color = c.getSuit().getColor();
                continue;
            }
            if (color == c.getSuit().getColor()) {
                return false;
            }
            if (n != c.getNumber().getNumber() + 1) {
                return false;
            }
            n = c.getNumber().getNumber();
            color = c.getSuit().getColor();
        }


        return true;
    }

    long pileMaxSize(FreeCellStack cardStack) {
        long supporting = Stream.of(supportingStacks).filter(e -> e.getCards().isEmpty()).count() + 1;
        supporting *= Stream.of(simpleStacks).filter(e -> e.getCards().isEmpty() && !Objects.equals(cardStack, e)).count() + 1;

        return supporting;
    }

    long pileMaxSize() {
        return pileMaxSize(null);
    }


    private boolean isCompatibleAscending(FreeCellCard first, FreeCellStack e) {
        return first.getNumber() == FreeCellNumber.ACE && e.getCards().isEmpty()
                || !e.getCards().isEmpty() && first.getSuit() == e.getLastCards().getSuit()
                && first.getNumber().getNumber() == e.getLastCards().getNumber().getNumber() + 1;
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

    private boolean isNotAscendingStackCompatible(FreeCellStack cardStack, FreeCellCard solitaireCard) {
        return isStackEmptyAndCardIsNotAce(cardStack, solitaireCard)
                || !cardStack.getCards().isEmpty() && (solitaireCard.getSuit() != cardStack.getLastCards().getSuit()
                || solitaireCard.getNumber().getNumber() != cardStack.getLastCards().getNumber().getNumber() + 1);
    }


    private Collection<FreeCellStack> getHoveredStacks(FreeCellStack[] stacks) {
        FreeCellCard next = dragContext.cards.iterator().next();
        return Stream.of(stacks)
                .filter(s -> RectF.intersects(s.getBoundsF(), next.getBounds()))
                .collect(Collectors.toList());
    }

    private boolean isStackEmptyAndCardIsNotAce(FreeCellStack cardStack, FreeCellCard solitaireCard) {
        return cardStack.getCards().isEmpty() && solitaireCard.getNumber() != FreeCellNumber.ACE;
    }

    private static class DragContext {
        protected final List<FreeCellCard> cards = new ArrayList<>();
        protected FreeCellStack stack;
        protected float x;
        protected float y;

        void reset() {
            cards.clear();
            stack = null;
        }
    }

    private static class MotionHistory {
        protected final List<FreeCellCard> cards = new ArrayList<>();
        protected FreeCellStack originStack;
        protected FreeCellStack targetStack;


        MotionHistory(Collection<FreeCellCard> cards, FreeCellStack originStack, FreeCellStack targetStack) {
            this.originStack = originStack;
            this.targetStack = targetStack;
            this.cards.addAll(cards);
        }

        MotionHistory(FreeCellCard cards, FreeCellStack originStack, FreeCellStack targetStack) {
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