package red.guih.games.dots;

import java.util.Objects;

public class Pair {
    final DotsSquare a;
    final DotsSquare b;

    Pair(DotsSquare a, DotsSquare b) {
        this.a = a;
        this.b = b;
    }

    DotsSquare getKey() {
        return a;
    }

    DotsSquare getValue() {
        return b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair) o;
        return Objects.equals(a, pair.a) &&
                Objects.equals(b, pair.b) || Objects.equals(b, pair.a) &&
                Objects.equals(a, pair.b);
    }
}
