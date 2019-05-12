package red.guih.games.japanese;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CompareAnswers {

    private CompareAnswers() {
    }

    public static float compare(String x, String y) {

        String s = Objects.toString(x, "");
        String s2 = Objects.toString(y, "");

        List<String> arrayList = bigrams(s);
        List<String> arrayList2 = bigrams(s2);
        float nx = arrayList.size();
        float ny = arrayList2.size();
        arrayList2.removeAll(arrayList);

        if (nx + ny == 0) {
            return 1;
        }

        return 1 - 2 * arrayList2.size() / (nx + ny);
    }

    private static List<String> bigrams(String s) {
        List<String> collect = s.chars().mapToObj(i -> Character.toString((char) i)).collect(Collectors.toList());
        List<String> bigrams = new ArrayList<>();
        for (int j = 0; j < collect.size() - 1; j++) {
            String string = collect.get(j);
            String string2 = collect.get(j + 1);
            bigrams.add(string + string2);
        }
        return bigrams;
    }

}
