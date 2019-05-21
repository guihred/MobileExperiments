package red.guih.games.madmaze;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Class made to keep track of distance
 *
 * Created by guilherme.hmedeiros on 28/01/2018.
 */

public class MadEdgeDistance implements Comparable<MadEdgeDistance> {

    private float distance;
    MadLine edge;

    public MadEdgeDistance(MadLine edge, float distance) {
        this.edge = edge;
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull MadEdgeDistance o) {
        return Double.compare(distance, o.distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().isInstance(obj)) {
            return false;
        }
        final MadEdgeDistance other = (MadEdgeDistance) obj;
        return edge == other.edge && distance == other.distance;
    }
    @Override
    public int hashCode() {
        return Objects.hash(edge,distance);
    }
}
