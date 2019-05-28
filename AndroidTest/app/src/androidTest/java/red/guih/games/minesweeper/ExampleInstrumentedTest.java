package red.guih.games.minesweeper;

import android.util.Log;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import red.guih.games.GamesActivity;
import red.guih.games.R;
import red.guih.games.dots.DotsDrawingView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<GamesActivity> rule = new ActivityTestRule<>(GamesActivity.class);


    private void clickButton(Integer buttonId) {
        onView(withId(buttonId)).perform(click());
    }

    @Test
    public void testDots() {
        swipeAllDirections(R.id.dotsButton, R.id.game_board);
    }

    private void swipeAllDirections(int minesweeperButton, int minesweeperView) {
        clickButton(minesweeperButton);

        List<ViewAction> viewActions =
                Arrays.asList(swipe(true, true), swipe(true, false), swipe(false, true),
                        swipe(false, false));
        for (ViewAction ac : viewActions) {
            if (isVisible(minesweeperView)) {
                onView(withId(minesweeperView)).perform(ac);
            }
        }
        try {
            Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        } catch (Exception e) {
            Log.e("TEST", "ERROR IN SWIPE", e);
        }
        if (isVisible(R.id.config)) {
            clickButton(R.id.config);
        }
        if (isVisible(R.id.records)) {
            clickButton(R.id.records);
        }

    }

    private ViewAction swipe(boolean horizontal, boolean right) {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
                view -> getCoordinates(view, horizontal, right ? 1 : -1), Press.PINPOINT);
    }

    private boolean isVisible(int id) {
        try {
            // View is in hierarchy
            onView(withId(id)).check(matches(isDisplayed()));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }

    private float[] getCoordinates(View view, boolean horizontal, int right) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        float i = DotsDrawingView.getMazeWidth() / 2F;
        int width = view.getWidth();
        int squareSize = width / DotsDrawingView.getMazeWidth();
        float x = getPosition(xy[0], width, DotsDrawingView.getMazeWidth(),
                horizontal ? i + right : i);
        int mazeHeight = view.getHeight() / squareSize;
        float v = mazeHeight / 2F;
        float y = getPosition(xy[1], view.getHeight(), mazeHeight, horizontal ? v : v + right);
        return new float[]{x, y};
    }

    private float getPosition(int viewPos, float viewLength, float div, float mazeWidth) {
        return viewPos + (viewLength - 1F) / div * mazeWidth;
    }

    @Test
    public void testSquare2048() {
        swipeAllDirections(R.id.square2048Button, R.id.square2048_view);
    }

    @Test
    public void testPacman() {
        swipeAllDirections(R.id.pacmanButton, R.id.pacman_view);
    }

    @Test
    public void testTetris() {
        swipeAllDirections(R.id.tetrisButton, R.id.tetris_view);
    }

    @Test
    public void testPuzzle() {
        swipeAllDirections(R.id.puzzleButton, R.id.puzzle_view);
    }

    @Test
    public void testMinesweeper() {
        swipeAllDirections(R.id.minesweeperButton, R.id.minesweeper_view);
    }

    @Test
    public void testSlidingpuzzle() {
        clickButton(R.id.slidingPuzzleButton);
        onView(withId(R.id.sliding_puzzle_view))
                .perform(randomSwipe(), randomSwipe(), randomSwipe(), randomSwipe());
    }

    @Test
    public void testFreecell() {
        clickButton(R.id.freecellButton);
        onView(withId(R.id.freecell_view))
                .perform(randomSwipe(), randomSwipe(), randomSwipe(), randomSwipe());
    }

    @Test
    public void testSolitaire() {
        clickButton(R.id.solitaireButton);
        onView(withId(R.id.solitaire_view))
                .perform(randomSwipe(), randomSwipe(), randomSwipe(), randomSwipe());
    }

    @Test
    public void testJapanese() {
        clickButton(R.id.japaneseButton);
        for (float i = 0.01F; i < 1; i += 0.2) {
            for (float j = 0.01F; j < 1; j += 0.2) {
                if (isVisible(R.id.japaneseView)) {
                    onView(withId(R.id.japaneseView))
                            .perform(touch(i, j));
                    Log.i("TEST", "TOUCHING (" + i + "," + j + ")");
                }
            }
        }
    }

    private GeneralSwipeAction randomSwipe() {
        return new GeneralSwipeAction(Swipe.FAST, this::getRandomCoordinates,
                this::getRandomCoordinates, Press.PINPOINT);
    }

    private GeneralSwipeAction touch(float x, float y) {
        return new GeneralSwipeAction(Swipe.FAST, view -> getCoordinates(view, x, y),
                view -> getCoordinates(view, x, y), Press.PINPOINT);
    }

    @Test
    public void testGamesWorking() {
        List<Integer> buttons =
                Arrays.asList(R.id.minesweeperButton, R.id.dotsButton, R.id.tetrisButton,
                        R.id.puzzleButton, R.id.sudokuButton,
                        R.id.freecellButton, R.id.solitaireButton, R.id.japaneseButton,
                        R.id.slidingPuzzleButton, R.id.square2048Button, R.id.labyrinthButton);
        for (Integer buttonId : buttons) {
            clickButton(buttonId);
            pressBack();
        }
    }

    private float[] getRandomCoordinates(View view) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        float i = DotsDrawingView.getMazeWidth() / 2F;
        int width = view.getWidth();
        int squareSize = width / DotsDrawingView.getMazeWidth();
        float x = getPosition(xy[0], width * (float) Math.random(), DotsDrawingView.getMazeWidth(),
                i);
        int mazeHeight = view.getHeight() / squareSize;
        float v = mazeHeight / 2F;
        float y = getPosition(xy[1], view.getHeight() * (float) Math.random(), mazeHeight, v);
        return new float[]{x, y};
    }

    private float[] getCoordinates(View view, float propX, float propY) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        int width = view.getWidth();
        float x = xy[0] + width * propX;
        float y = xy[1] + view.getHeight() * propY;
        return new float[]{x, y};
    }
}
