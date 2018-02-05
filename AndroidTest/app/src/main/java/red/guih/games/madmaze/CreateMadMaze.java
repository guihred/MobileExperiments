package red.guih.games.madmaze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class CreateMadMaze {

    int r = 0;
    List<MadEdge> allEdges;
    List<MadTriangle> triangles;

    public static CreateMadMaze createLabyrinth(Collection<MadCell> allCells) {
        CreateMadMaze a = new CreateMadMaze();
        List<MadTriangle> triangulate = a.triangulate(allCells);
        a.handle(triangulate);
        return a;
    }

    void handle(List<MadTriangle> maze) {
        final Random random = new Random();
        final List<MadTriangle> history = new ArrayList<>();
        final List<String> check = new ArrayList<>();
        history.add(maze.get(0));
        while (!history.isEmpty()) {

            maze.get(r).setVisited();
            check.clear();
            Optional<MadTriangle> openC = maze.stream().filter(t -> t.hasVertex(maze.get(r).getA())
                    && t.hasVertex(maze.get(r).getB()) && !t.hasVertex(maze.get(r).getC())).filter(e -> !e.isVisited())
                    .findAny();
            Optional<MadTriangle> openB = maze.stream().filter(t -> t.hasVertex(maze.get(r).getA())
                    && !t.hasVertex(maze.get(r).getB()) && t.hasVertex(maze.get(r).getC())).filter(e -> !e.isVisited())
                    .findAny();
            Optional<MadTriangle> openA = maze.stream()
                    .filter(t -> !t.hasVertex(maze.get(r).getA()) && t.hasVertex(maze.get(r).getB())
                            && t.hasVertex(maze.get(r).getC()))
                    .filter(e -> !e.isVisited())
                    .findAny();

            if (openA.isPresent()) {
                check.add("A");
            }
            if (openB.isPresent()) {
                check.add("B");
            }
            if (openC.isPresent()) {
                check.add("C");
            }
            if (check.isEmpty()) {
                getBackIn(maze, history);
                continue;
            }

            history.add(maze.get(r));
            final String direction = check.get(random.nextInt(check.size()));
            if ("A".equals(direction) && openA.isPresent()) {
                linkNeighborTriangles(maze, openA.get(), maze.get(r).getB().getCell(), maze.get(r).getC().getCell());
            }
            if ("B".equals(direction) && openB.isPresent()) {
                linkNeighborTriangles(maze, openB.get(), maze.get(r).getC().getCell(), maze.get(r).getA().getCell());
            }
            if ("C".equals(direction) && openC.isPresent()) {
                linkNeighborTriangles(maze, openC.get(), maze.get(r).getA().getCell(), maze.get(r).getB().getCell());
            }
        }

    }

    private void linkNeighborTriangles(List<MadTriangle> maze, MadTriangle open1, MadCell cell2, MadCell cell3) {
        allEdges.removeIf(e -> e.getSource().equals(cell3) && e.getTarget().equals(cell2)
                || e.getSource().equals(cell2) && e.getTarget().equals(cell3));
        r = maze.indexOf(open1);
    }

    private List<MadPonto> getPointSet(Collection<MadCell> all) {
        return all.stream().map(c -> new MadPonto(c.getX(), c.getY(), c)).collect(Collectors.toList());
    }

    private void legalizeEdge(List<MadTriangle> triangleSoup1, MadTriangle triangle, MadLinha edge,
                              MadPonto newVertex) {
        MadTriangle neighbourMadTriangle = triangleSoup1.stream().filter(t -> t.isNeighbour(edge) && t != triangle)
                .findFirst().orElse(null);
        if (neighbourMadTriangle != null && neighbourMadTriangle.isPointInCircumcircle(newVertex)) {
            triangleSoup1.remove(triangle);
            triangleSoup1.remove(neighbourMadTriangle);

            MadPonto noneEdgeVertex = neighbourMadTriangle.getNoneEdgeVertex(edge);

            MadTriangle firstMadTriangle = new MadTriangle(noneEdgeVertex, edge.a, newVertex);
            MadTriangle secondMadTriangle = new MadTriangle(noneEdgeVertex, edge.b, newVertex);

            triangleSoup1.add(firstMadTriangle);
            triangleSoup1.add(secondMadTriangle);

            legalizeEdge(triangleSoup1, firstMadTriangle, new MadLinha(noneEdgeVertex, edge.a), newVertex);
            legalizeEdge(triangleSoup1, secondMadTriangle, new MadLinha(noneEdgeVertex, edge.b), newVertex);
        }
    }

    private List<MadTriangle> triangulate(Collection<MadCell> all) {
        List<MadTriangle> triangleSoup = new ArrayList<>();
        float maxOfAnyCoordinate = 0.0f;
        List<MadPonto> pointSet = getPointSet(all);
        for (MadPonto vector : pointSet) {
            maxOfAnyCoordinate = Math.max(Math.max(vector.x, vector.y), maxOfAnyCoordinate);
        }
        // Creates a big triangles which surrounds all the others
        maxOfAnyCoordinate *= 16.0D;
        MadPonto p1 = new MadPonto(0.0f, 3.0f * maxOfAnyCoordinate, null);
        MadPonto p2 = new MadPonto(3.0f * maxOfAnyCoordinate, 0.0f, null);
        MadPonto p3 = new MadPonto(-3.0f * maxOfAnyCoordinate, -3.0f * maxOfAnyCoordinate, null);
        MadTriangle superMadTriangle = new MadTriangle(p1, p2, p3);
        triangleSoup.add(superMadTriangle);
        for (int i = 0; i < pointSet.size(); i++) {
            MadPonto point = pointSet.get(i);
            MadTriangle triangle = triangleSoup.stream().filter(t6 -> t6.contains(point)).findFirst().orElse(null);
//NO TRIANGLES CONTAIN THE POINT
            if (triangle == null) {
                MadPonto point2 = pointSet.get(i);
                Optional<MadEdgeDistance> findFirst = triangleSoup.stream().map(t7 -> t7.findNearestEdge(point2))
                        .sorted().findFirst();
                if (!findFirst.isPresent()) {
                    continue;
                }
                MadLinha edge = findFirst.get().edge;

                MadTriangle first = triangleSoup.stream().filter(t4 -> t4.isNeighbour(edge)).findFirst().orElse(null);
                MadTriangle second = triangleSoup.stream().filter(t5 -> t5.isNeighbour(edge) && t5 != first).findFirst()
                        .orElse(null);

                MadPonto firstNoneEdgeVertex = first.getNoneEdgeVertex(edge);
                MadPonto secondNoneEdgeVertex = second.getNoneEdgeVertex(edge);

                triangleSoup.remove(first);
                triangleSoup.remove(second);

                MadTriangle triangle1 = new MadTriangle(edge.a, firstNoneEdgeVertex, pointSet.get(i));
                MadTriangle triangle2 = new MadTriangle(edge.b, firstNoneEdgeVertex, pointSet.get(i));
                MadTriangle triangle3 = new MadTriangle(edge.a, secondNoneEdgeVertex, pointSet.get(i));
                MadTriangle triangle4 = new MadTriangle(edge.b, secondNoneEdgeVertex, pointSet.get(i));

                triangleSoup.add(triangle1);
                triangleSoup.add(triangle2);
                triangleSoup.add(triangle3);
                triangleSoup.add(triangle4);

                legalizeEdge(triangleSoup, triangle1, new MadLinha(edge.a, firstNoneEdgeVertex), pointSet.get(i));
                legalizeEdge(triangleSoup, triangle2, new MadLinha(edge.b, firstNoneEdgeVertex), pointSet.get(i));
                legalizeEdge(triangleSoup, triangle3, new MadLinha(edge.a, secondNoneEdgeVertex), pointSet.get(i));
                legalizeEdge(triangleSoup, triangle4, new MadLinha(edge.b, secondNoneEdgeVertex), pointSet.get(i));
            } else {
//ONE TRIANGLE CONTAINS THE POINT
                MadPonto a = triangle.getA();
                MadPonto b = triangle.getB();
                MadPonto c = triangle.getC();

                triangleSoup.remove(triangle);

                MadTriangle first = new MadTriangle(a, b, pointSet.get(i));
                MadTriangle second = new MadTriangle(b, c, pointSet.get(i));
                MadTriangle third = new MadTriangle(c, a, pointSet.get(i));

                triangleSoup.add(first);
                triangleSoup.add(second);
                triangleSoup.add(third);

                legalizeEdge(triangleSoup, first, new MadLinha(a, b), pointSet.get(i));
                legalizeEdge(triangleSoup, second, new MadLinha(b, c), pointSet.get(i));
                legalizeEdge(triangleSoup, third, new MadLinha(c, a), pointSet.get(i));
            }
        }

        triangleSoup.removeIf(t1 -> t1.hasVertex(superMadTriangle.getA()));
        triangleSoup.removeIf(t2 -> t2.hasVertex(superMadTriangle.getB()));
        triangleSoup.removeIf(t3 -> t3.hasVertex(superMadTriangle.getC()));
        allEdges = new ArrayList<>();
        for (MadTriangle t : triangleSoup) {
            MadCell cella = t.getA().getCell();
            MadCell cellb = t.getB().getCell();
            MadCell cellc = t.getC().getCell();
            allEdges.add(new MadEdge(cella, cellb));
            allEdges.add(new MadEdge(cellb, cellc));
            allEdges.add(new MadEdge(cellc, cella));
        }
        triangles = triangleSoup;
        return triangleSoup;
    }

    private void getBackIn(List<MadTriangle> createdMaze, List<MadTriangle> history) {
        final MadTriangle remove = history.remove(history.size() - 1);
        r = createdMaze.indexOf(remove);
    }
}