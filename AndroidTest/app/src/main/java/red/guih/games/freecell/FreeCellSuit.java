package red.guih.games.freecell;

import android.graphics.Color;

import red.guih.games.R;

public enum FreeCellSuit {
    SPADES(Color.BLACK, R.drawable.spades),
    DIAMONDS(Color.RED, R.drawable.diamond),
    CLUBS(Color.BLACK, R.drawable.clubs),
    HEARTS(Color.RED, R.drawable.hearts),;

    private final transient int resource;
    private final transient int color;

    FreeCellSuit(int color, int resource) {
        this.color = color;
        this.resource = resource;

    }

    public int getColor() {
        return color;
    }


    public int getShape() {
        return resource;
    }
}
