package red.guih.games.pacman;

import android.graphics.Color;

import red.guih.games.R;

/**
 * Created by guilherme.hmedeiros on 11/02/2018.
 */

public enum GhostColor {

    RED(R.drawable.red_ghost, Color.RED),
    BLUE(R.drawable.blue_ghost, Color.BLUE),
    ORANGE(R.drawable.orange_ghost, Color.YELLOW),
    GREEN(R.drawable.green_ghost, Color.GREEN);

    private final int color;
    private final int color2;

    GhostColor(int color, int color2) {
        this.color = color;
        this.color2 = color2;
    }

    public int getColor() {
        return color;
    }

    public int getColor2() {
        return color2;
    }
}
