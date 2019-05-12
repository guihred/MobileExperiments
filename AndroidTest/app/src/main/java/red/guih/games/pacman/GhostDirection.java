package red.guih.games.pacman;

/**
 * Created by guilherme.hmedeiros on 11/02/2018.
 */
enum GhostDirection {
    EAST(-1, 0),
    NORTH(0, -1),
    SOUTH(0, 1),
    WEST(1, 0),
    NORTHEAST(-1, -1),
    SOUTHEAST(-1, 1),
    NORTHWEST(1, -1),
    SOUTHWEST(1, 1);
    final int x;
    final int y;

    GhostDirection(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
