import java.util.HashMap;

/**
 * Created by alext on 2/8/2017.
 */
public class GraphConnections extends Ided implements Comparable {


    HashMap<GraphNode, GraphNode> connections;

    GraphConnections(HashMap<GraphNode, GraphNode> h){
        this.connections = h;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
//To be implemented when time arises
