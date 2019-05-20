package red.guih.games.minesweeper;

import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.ViewActions;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<GamesActivity> rule = new ActivityTestRule<>(GamesActivity.class);

    @Test
    public void testGamesWorking() {
        List<Integer> buttons =
                Arrays.asList(R.id.minesweeperButton, R.id.dotsButton, R.id.tetrisButton,
                        R.id.pacmanButton, R.id.puzzleButton, R.id.sudokuButton,
                        R.id.freecellButton, R.id.solitaireButton, R.id.japaneseButton,
                        R.id.slidingPuzzleButton, R.id.square2048Button, R.id.labyrinthButton);
        for (Integer buttonId : buttons) {
            clickButton(buttonId);
            pressBack();
        }
    }

    private void clickButton(Integer buttonId) {
        onView(withId(buttonId)).perform(ViewActions.click());
    }

    @Test
    public void testDots() {
        clickButton(R.id.dotsButton);
        onView(withId(R.id.game_board))
                .perform(swipe(true, true), swipe(true, false), swipe(false, true),
                        swipe(false, false));
    }
    @Test
    public void testSquare2048() {
        clickButton(R.id.square2048Button);
        onView(withId(R.id.square2048_view))
                .perform(swipe(true, true), swipe(true, false), swipe(false, true),
                        swipe(false, false));
    }

    private ViewAction swipe(boolean horizontal, boolean right) {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
                view -> getCoordinates(view, horizontal, right ? 1 : -1), Press.PINPOINT);
    }

    private float[] getCoordinates(View view, boolean horizontal, int right) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        float i = DotsDrawingView.mazeWidth / 2F;
        int width = view.getWidth();
        int squareSize = width / DotsDrawingView.mazeWidth;
        float x = getPosition(xy[0], width, DotsDrawingView.mazeWidth, horizontal ? i + right : i);
        int mazeHeight = view.getHeight() / squareSize;
        float v = mazeHeight / 2F;
        float y = getPosition(xy[1], view.getHeight(), mazeHeight, horizontal ? v : v + right);
        return new float[]{x, y};
    }

    private float getPosition(int viewPos, int viewLength, float div, float mazeWidth) {
        return viewPos + (viewLength - 1F) / div * mazeWidth;
    }
}
