import java.util.List;

public class GraphNode extends Ided implements Comparable {

    List<GraphNode> adjacent;
    FloorPoint location;

    public GraphNode(FloorPoint p){
        this.location = p;
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

    /**
     * return cartisian distance to another graph node
     * @param graphNode
     * @return
     */
    public double distance (GraphNode graphNode){
        return this.location.distance(graphNode.location);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
