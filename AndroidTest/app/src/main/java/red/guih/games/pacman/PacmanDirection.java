package red.guih.games.pacman;

/**
 * Created by guilherme.hmedeiros on 10/02/2018.
 */

public enum PacmanDirection {

        DOWN(90),
        LEFT(180),
        RIGHT(0),
        UP(270);

         final int angle;

        PacmanDirection(int angle) {
            this.angle = angle;
        }

}
