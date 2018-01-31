package red.guih.games.dots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class DotsSquare {
    static int SQUARE_SIZE = 40;

    final int i, j;
    private final Set<DotsSquare> adjacencies = new HashSet<>();
    float[] center;

    DotsSquare(int i, int j) {
        this.i = i;
        this.j = j;
    }

    static void setSquareSize(int squareSize) {
        SQUARE_SIZE = squareSize;
    }

    void clear() {
        adjacencies.clear();
    }

    Set<DotsSquare> getAdjacencies() {
        return adjacencies;
    }

    @Override
    public String toString() {
        return "(" + i + "," + j + ")";
    }

    float[] getCenter() {
        if (center != null)
            return center;

        return center = new float[]{i * SQUARE_SIZE + ((float) SQUARE_SIZE) / 2, j * SQUARE_SIZE + ((float) (SQUARE_SIZE)) / 2};
    }


    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof DotsSquare)) {
            return false;
        }
        final DotsSquare other = (DotsSquare) obj;
        return i == other.i && j == other.j;
    }

    void addAdj(DotsSquare selected) {
        adjacencies.add(selected);
        selected.adjacencies.add(this);
    }

    boolean contains(DotsSquare selected) {
        return adjacencies.contains(selected);
    }

    Set<Set<DotsSquare>> check() {
        Set<Set<DotsSquare>> pontos = new HashSet<>();
        for (DotsSquare a : adjacencies) {

            for (DotsSquare b : a.adjacencies) {
                if ((b != DotsSquare.this)) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && c.contains(DotsSquare.this))
                            pontos.add(new LinkedHashSet<>(Arrays.asList(a, b, c, this)));
                    }
                }
            }
        }
        return pontos;
    }

    List<DotsSquare> almostSquare() {
//         ONE link away from being a square
        List<DotsSquare> objects = new ArrayList<>();
        for (DotsSquare a : adjacencies) {
            for (DotsSquare b : a.adjacencies) {
                if (b != this) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && !c.contains(this) && Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
                            objects.add(c);
                        }
                    }
                }
            }
        }
        return objects;
    }

    boolean hasAlmostSquare() {
        for (DotsSquare a : adjacencies) {
            for (DotsSquare b : a.adjacencies) {
                if (b != this) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && !c.contains(this) && Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    boolean checkMelhor(DotsSquare adj) {
        final Collection<DotsSquare> adjancenciesCopy = new HashSet<>(adjacencies);
        adjancenciesCopy.add(adj);
        for (DotsSquare a : adjancenciesCopy) {
            for (DotsSquare b : a.adjacencies) {
                if (b != this) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && !c.contains(this) && Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    void removeAdj(DotsSquare value) {
        adjacencies.remove(value);
        value.adjacencies.remove(this);
    }

}

