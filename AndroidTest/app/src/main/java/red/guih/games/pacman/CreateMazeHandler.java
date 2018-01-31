package red.guih.games.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class CreateMazeHandler {
    private int r, c;
    private final Random random = new Random();

    private final List<MazeSquare> history = new ArrayList<>();
    private final List<String> check = new ArrayList<>();
    private final MazeSquare[][] createdMaze;

    CreateMazeHandler(MazeSquare[][] maze) {
        createdMaze = maze;
        history.add(maze[0][0]);
    }


    void handle() {
        while (!history.isEmpty()) {
            createdMaze[r][c].setVisited();
            check.clear();

            addPossibleDirections();
            if (!check.isEmpty()) {
                history.add(createdMaze[r][c]);
                final String direction = check.get(random.nextInt(check.size()));
                if ("L".equals(direction)) {
                    createdMaze[r][c].setWest(true);
                    c -= 1;
                    createdMaze[r][c].setEast();
                }
                if ("U".equals(direction)) {
                    createdMaze[r][c].setNorth(true);
                    r -= 1;
                    createdMaze[r][c].setSouth();
                }
                if ("R".equals(direction)) {
                    createdMaze[r][c].setEast();
                    c += 1;
                    createdMaze[r][c].setWest(true);
                }
                if ("D".equals(direction)) {
                    createdMaze[r][c].setSouth();
                    r += 1;
                    createdMaze[r][c].setNorth(true);
                }
            } else {
                getBackIn(history);
            }
        }


    }

    private void addPossibleDirections() {
        if (c > 0 && !createdMaze[r][c - 1].isVisited()) {
            check.add("L");
        }
        if (r > 0 && !createdMaze[r - 1][c].isVisited()) {
            check.add("U");
        }
        if (c < PacmanView.MAZE_HEIGHT - 1 && !createdMaze[r][c + 1].isVisited()) {
            check.add("R");
        }
        if (r < PacmanView.MAZE_WIDTH - 1 && !createdMaze[r + 1][c].isVisited()) {
            check.add("D");
        }
    }

    private void getBackIn(List<MazeSquare> history) {
        final MazeSquare remove = history.remove(history.size() - 1);
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