package red.guih.games.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class CreateMazeHandler {
    private int r, c;
    private final Random random = new Random();

    private final List<MazeSquare> history = new ArrayList<>();
    private final List<PacmanDirection> check = new ArrayList<>();
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
                final PacmanDirection direction = check.get(random.nextInt(check.size()));
                if (PacmanDirection.LEFT == direction) {
                    createdMaze[r][c].setWest(true);
                    c -= 1;
                    createdMaze[r][c].setEast(true);
                }
                if (PacmanDirection.UP == direction) {
                    createdMaze[r][c].setNorth(true);
                    r -= 1;
                    boolean b = true;
                    createdMaze[r][c].setSouth(b);
                }
                if (PacmanDirection.RIGHT == direction) {
                    createdMaze[r][c].setEast(true);
                    c += 1;
                    createdMaze[r][c].setWest(true);
                }
                if (PacmanDirection.DOWN == direction) {
                    boolean b = true;
                    createdMaze[r][c].setSouth(b);
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
                if (i > 0 && !mazeSquare.isEast() && !mazeSquare.isNorth() && !mazeSquare.isWest()) {
                    createdMaze[i][j].setNorth(true);
                    createdMaze[i - 1][j].setSouth(true);
                }
                if (i < createdMaze.length - 1 && !mazeSquare.isEast() && !mazeSquare.isSouth() && !mazeSquare.isWest()) {
                    createdMaze[i][j].setSouth(true);
                    createdMaze[i + 1][j].setNorth(true);
                }
                if (j < createdMaze[i].length - 1 && !mazeSquare.isNorth() && !mazeSquare.isEast() && !mazeSquare.isSouth()) {
                    createdMaze[i][j].setEast(true);
                    createdMaze[i][j + 1].setWest(true);
                }
                if (j > 0 && !mazeSquare.isNorth() && !mazeSquare.isWest() && !mazeSquare.isSouth()) {
                    createdMaze[i][j].setWest(true);
                    createdMaze[i][j - 1].setEast(true);
                }
            }
        }


    }

    private void addPossibleDirections() {
        if (c > 0 && !createdMaze[r][c - 1].isVisited()) {
            check.add(PacmanDirection.LEFT);
        }
        if (r > 0 && !createdMaze[r - 1][c].isVisited()) {
            check.add(PacmanDirection.UP);
        }
        if (c < PacmanView.MAZE_HEIGHT - 1 && !createdMaze[r][c + 1].isVisited()) {
            check.add(PacmanDirection.RIGHT);
        }
        if (r < PacmanView.MAZE_WIDTH - 1 && !createdMaze[r + 1][c].isVisited()) {
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
}