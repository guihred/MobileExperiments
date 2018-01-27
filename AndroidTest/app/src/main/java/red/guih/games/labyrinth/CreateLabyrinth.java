package red.guih.games.labyrinth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class CreateLabyrinth {

    public static void createLabyrinth(LabyrinthSquare[][] maze){
        new CreateLabyrinth().handle(maze);
    }


    int r = 0, c = 0;
    void handle(LabyrinthSquare[][] maze) {
        final Random random = new Random();
        final List<LabyrinthSquare> history = new ArrayList<>();
        final List<String> check = new ArrayList<>();
        history.add(maze[0][0]);

        while (!history.isEmpty()) {
            maze[r][c].setVisited(true);
            check.clear();

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
            if (!check.isEmpty()) {
                history.add(maze[r][c]);
                final String direction = check.get(random.nextInt(check.size()));
                if ("L".equals(direction)) {
                    maze[r][c].setWest(true);
                    c = c - 1;
                    maze[r][c].setEast(true);
                }
                if ("U".equals(direction)) {
                    maze[r][c].setNorth(true);
                    r = r - 1;
                    maze[r][c].setSouth(true);
                }
                if ("R".equals(direction)) {
                    maze[r][c].setEast(true);
                    c = c + 1;
                    maze[r][c].setWest(true);
                }
                if ("D".equals(direction)) {
                    maze[r][c].setSouth(true);
                    r = r + 1;
                    maze[r][c].setNorth(true);
                }
            } else {
                getBackIn(maze, history);
            }
        }


    }

    private boolean getBackIn(LabyrinthSquare[][] createdMaze, List<LabyrinthSquare> history) {
        final LabyrinthSquare remove = history.remove(history.size() - 1);
        for (int i = 0; i < createdMaze.length; i++) {
            for (int j = 0; j < createdMaze[i].length; j++) {
                if (createdMaze[i][j] == remove) {
                    r = i;
                    c = j;
                    return true;
                }
            }
        }
        return false;
    }
}