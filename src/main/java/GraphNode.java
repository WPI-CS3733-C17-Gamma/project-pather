import java.util.LinkedList;
import java.util.List;

public class GraphNode extends Ided implements Comparable {

    List<GraphNode> adjacent;
    FloorPoint location;

    /**
     * construct a node with no adjacent nodes
     * at location p
     * @param p
     */
    public GraphNode(FloorPoint p){
        location = p;
        this.location = p;
        adjacent = new LinkedList<>();
    }

    public List<GraphNode> getAdjacent(){
        return adjacent;
    }

    /**
     * add a connection to node if none exists, and node is not not this
     * @param node
     * @return
     */
    public boolean addAdjacent(GraphNode node){
        // add adjacent  node if the connection does not exist and the
        // connection is different from this
        if (adjacent.contains(node) || this.equals(node)) {
            return false;
        }
        else {
            adjacent.add(node);
            return true;
        }
    }

    public boolean removeAdjacent(GraphNode node){
        adjacent.remove(node);
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
