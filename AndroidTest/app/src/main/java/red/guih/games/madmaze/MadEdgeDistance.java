package red.guih.games.madmaze;

import android.support.annotation.NonNull;

/**
 * Class made to keep track of distance
 *
 * Created by guilherme.hmedeiros on 28/01/2018.
 */

public class MadEdgeDistance implements Comparable<MadEdgeDistance> {

    protected float distance;
    protected MadLine edge;

    public MadEdgeDistance(MadLine edge, float distance) {
        this.edge = edge;
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull MadEdgeDistance o) {
        return Double.compare(distance, o.distance);
    }

}
