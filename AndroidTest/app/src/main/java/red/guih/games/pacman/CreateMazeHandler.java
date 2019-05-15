package red.guih.games.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class CreateMazeHandler {
    private final Random random = new Random();
    private final List<MazeSquare> history = new ArrayList<>();
    private final List<PacmanDirection> check = new ArrayList<>();
    private final MazeSquare[][] createdMaze;
    private int r;
    private int c;

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
                final PacmanDirection direction = check.get(random.nextInt(check.size()));
                if (PacmanDirection.LEFT == direction) {
                    createdMaze[r][c].setWest(true);
                    c -= 1;
                    createdMaze[r][c].setEast();
                }
                if (PacmanDirection.UP == direction) {
                    createdMaze[r][c].setNorth(true);
                    r -= 1;
                    createdMaze[r][c].setSouth();
                }
                if (PacmanDirection.RIGHT == direction) {
                    createdMaze[r][c].setEast();
                    c += 1;
                    createdMaze[r][c].setWest(true);
                }
                if (PacmanDirection.DOWN == direction) {
                    createdMaze[r][c].setSouth();
                    r += 1;
                    createdMaze[r][c].setNorth(true);
                }
            } else {
                getBackIn(history);
            }
        }

        for (int i = 0; i < createdMaze.length; i++) {
            for (int j = 0; j < createdMaze[i].length; j++) {
                MazeSquare mazeSquare = createdMaze[i][j];
                setProperDirection(i, j, mazeSquare);
            }
        }


    }

    private void addPossibleDirections() {
        if (c > 0 && createdMaze[r][c - 1].isNotVisited()) {
            check.add(PacmanDirection.LEFT);
        }
        if (r > 0 && createdMaze[r - 1][c].isNotVisited()) {
            check.add(PacmanDirection.UP);
        }
        if (c < PacmanView.mazeHeight - 1 && createdMaze[r][c + 1].isNotVisited()) {
            check.add(PacmanDirection.RIGHT);
        }
        if (r < PacmanView.MAZE_WIDTH - 1 && createdMaze[r + 1][c].isNotVisited()) {
            check.add(PacmanDirection.DOWN);
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

    private void setProperDirection(int i, int j, MazeSquare mazeSquare) {
        if (i > 0 && mazeSquare.isNotEast() && mazeSquare.isNotNorth() &&
                mazeSquare.isNotWest()) {
            createdMaze[i][j].setNorth(true);
            createdMaze[i - 1][j].setSouth();
        }
        if (i < createdMaze.length - 1 && mazeSquare.isNotEast() && mazeSquare.isNotSouth() &&
                mazeSquare.isNotWest()) {
            createdMaze[i][j].setSouth();
            createdMaze[i + 1][j].setNorth(true);
        }
        if (j < createdMaze[i].length - 1 && mazeSquare.isNotNorth() &&
                mazeSquare.isNotEast() && mazeSquare.isNotSouth()) {
            createdMaze[i][j].setEast();
            createdMaze[i][j + 1].setWest(true);
        }
        if (j > 0 && mazeSquare.isNotNorth() && mazeSquare.isNotWest() &&
                mazeSquare.isNotSouth()) {
            createdMaze[i][j].setWest(true);
            createdMaze[i][j - 1].setEast();
        }
    }
}