package red.guih.games.minesweeper;

import org.junit.Test;

import java.util.TreeSet;

import red.guih.games.japanese.CompareAnswers;
import red.guih.games.madmaze.MadEdgeDistance;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testCompareAnswers() {
        float compare = CompareAnswers.compare("abc", "abd");
        assertEquals("TEST WRONG", 0.5, compare, 0.1);
        TreeSet<MadEdgeDistance> objects = new TreeSet<>();
        objects.add(new MadEdgeDistance(null, 0.1F));
        objects.add(new MadEdgeDistance(null, 0.2F));
        objects.add(new MadEdgeDistance(null, 0.3F));
        assertEquals("TEST WRONG", 3, objects.size());
    }
}