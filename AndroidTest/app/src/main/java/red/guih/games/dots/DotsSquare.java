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
    static int squareSize;

    final int i;
    final int j;
    private final Set<DotsSquare> adjacencies = new HashSet<>();
    private float[] center;

    DotsSquare(int i, int j) {
        this.i = i;
        this.j = j;
    }

    void clear() {
        adjacencies.clear();
    }

    Set<DotsSquare> getAdjacencies() {
        return adjacencies;
    }

    float[] getCenter() {
        if (center == null) {
            center = new float[]{i * squareSize + squareSize / 2F,
                    j * squareSize + squareSize / 2F};
        }
        return center;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !this.getClass().isInstance(obj)) {
            return false;
        }
        final DotsSquare other = (DotsSquare) obj;
        return i == other.i && j == other.j;
    }

    @Override
    public String toString() {
        return "(" + i + "," + j + ")";
    }

    void addAdj(DotsSquare selected) {
        adjacencies.add(selected);
        selected.adjacencies.add(this);
    }

    Set<Set<DotsSquare>> check() {
        Set<Set<DotsSquare>> points = new HashSet<>();
        for (DotsSquare a : adjacencies) {

            for (DotsSquare b : a.adjacencies) {
                if (b != DotsSquare.this) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && c.contains(DotsSquare.this)) {
                            points.add(new LinkedHashSet<>(Arrays.asList(a, b, c, this)));
                        }
                    }
                }
            }
        }
        return points;
    }

    boolean contains(DotsSquare selected) {
        return adjacencies.contains(selected);
    }

    List<DotsSquare> almostSquare() {
//         ONE link away from being a square
        List<DotsSquare> objects = new ArrayList<>();
        for (DotsSquare a : adjacencies) {
            for (DotsSquare b : a.adjacencies) {
                if (b != this) {
                    for (DotsSquare c : b.adjacencies) {
                        if (a != c && !c.contains(this) &&
                                Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
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
                        if (a != c && !c.contains(this) &&
                                Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
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
                        if (a != c && !c.contains(this) &&
                                Math.abs(c.i - i) + Math.abs(c.j - j) == 1) {
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

