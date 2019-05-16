package red.guih.games.minesweeper;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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
    public void shouldUpdateTextAfterButtonClick() {
        List<Integer> buttons =
                Arrays.asList(R.id.minesweeperButton, R.id.dotsButton, R.id.tetrisButton,
                        R.id.pacmanButton, R.id.puzzleButton, R.id.sudokuButton,
                        R.id.freecellButton, R.id.solitaireButton, R.id.japaneseButton,
                        R.id.slidingPuzzleButton, R.id.square2048Button);
        for (Integer buttonId : buttons) {
            clickButton(buttonId);
            Espresso.pressBack();
        }
    }

    private void clickButton(Integer buttonId) {
        Espresso.onView(ViewMatchers.withId(buttonId)).perform(ViewActions.click());
    }
}
