package red.guih.games.madmaze;

/**
 * Class made to keep track of distance
 *
 * Created by guilherme.hmedeiros on 28/01/2018.
 */

public class MadEdgeDistance implements Comparable<MadEdgeDistance> {

    protected float distance;
    protected MadLinha edge;

    public MadEdgeDistance(MadLinha edge, float distance) {
        this.edge = edge;
        this.distance = distance;
    }

    @Override
    public int compareTo(MadEdgeDistance o) {
        return Double.compare(distance, o.distance);
    }

}
