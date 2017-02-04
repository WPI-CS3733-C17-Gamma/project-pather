import java.util.List;

public class GraphNode implements Comparable {

    List<GraphNode> adjacent;
    FloorPoint location;

    public GraphNode(FloorPoint p){

    }

    public List<GraphNode> getAdjacent(){
        return null;
    }

    public boolean addAdjacent(GraphNode node){
        return true;
    }

    public boolean removeAdjacent(GraphNode node){
        return true;
    }

    public int compareTo(GraphNode other){
        return 0;
    }

    public FloorPoint getLocation() {
        return location;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
