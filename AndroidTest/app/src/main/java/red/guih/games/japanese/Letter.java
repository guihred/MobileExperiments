package red.guih.games.japanese;

import android.graphics.RectF;

/**
 * Created by guilherme.hmedeiros on 16/03/2018.
 */
class Letter {
    RectF bound;
    String character;

    Letter(RectF bound,
           String character) {
        this.bound = bound;
        this.character = character;
    }
}
