package red.guih.games.labyrinth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

class CreateLabyrinth {

    final Random random = new Random();
    int r = 0, c = 0;

    public static void createLabyrinth(LabyrinthSquare[][] maze) {
        new CreateLabyrinth().handle(maze);
    }

    void handle(LabyrinthSquare[][] maze) {
        final List<LabyrinthSquare> history = new ArrayList<>();
        final List<String> check = new ArrayList<>();
        history.add(maze[0][0]);

        while (!history.isEmpty()) {
            maze[r][c].setVisited();
            check.clear();
            addPossibleDirections(maze, check);
            if (!check.isEmpty()) {
                history.add(maze[r][c]);
                final String direction = check.get(random.nextInt(check.size()));
                if ("L".equals(direction)) {
                    maze[r][c].setWest(true);
                    c -= 1;
                    maze[r][c].setEast(true);
                }
                if ("U".equals(direction)) {
                    maze[r][c].setNorth(true);
                    r -= 1;
                    maze[r][c].setSouth(true);
                }
                if ("R".equals(direction)) {
                    maze[r][c].setEast(true);
                    c += 1;
                    maze[r][c].setWest(true);
                }
                if ("D".equals(direction)) {
                    maze[r][c].setSouth(true);
                    r += 1;
                    maze[r][c].setNorth(true);
                }
            } else {
                getBackIn(maze, history);
            }
        }


    }

    private void addPossibleDirections(LabyrinthSquare[][] maze, Collection<String> check) {
        if (c > 0 && !maze[r][c - 1].isVisited()) {
            check.add("L");
        }
        if (r > 0 && !maze[r - 1][c].isVisited()) {
            check.add("U");
        }
        if (c < LabyrinthView.MAZE_HEIGHT - 1 && !maze[r][c + 1].isVisited()) {
            check.add("R");
        }
        if (r < LabyrinthView.MAZE_WIDTH - 1 && !maze[r + 1][c].isVisited()) {
            check.add("D");
        }
    }

    private void getBackIn(LabyrinthSquare[][] createdMaze, List<LabyrinthSquare> history) {
        final LabyrinthSquare remove = history.remove(history.size() - 1);
        for (int i = 0; i < createdMaze.length; i++) {
            for (int j = 0; j < createdMaze[i].length; j++) {
                if (createdMaze[i][j] == remove) {
                    r = i;
                    c = j;
                    return;
                }
            }
        }
    }
}