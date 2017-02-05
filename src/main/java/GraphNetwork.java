import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphNetwork {
    LinkedList<GraphNode> graphNodes = new LinkedList();

    public GraphNetwork(LinkedList<GraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    public LinkedList getPath(GraphNode start, GraphNode end) {
        return null;
    }

    /**
     * return the node closest to the point on the same floor
     * Does not reject points based off distance, this must be handled
     * by the caller
     * @param loc
     * @return
     */
    public GraphNode getGraphNode(FloorPoint loc){
        List<GraphNode> sameFloor = getGraphNodesOnFloor(loc.floor);
        // get the closest point to the node
        double minDistance = Double.MAX_VALUE;
        GraphNode closestNode = null;

        // find the minimum distance on the same floor
        for (GraphNode currentNode : sameFloor) {
            double currentDistance = currentNode.location.distance(loc);
            if(currentDistance < minDistance){
                minDistance = currentDistance;
                closestNode = currentNode;
            }
        }
        return closestNode;
    }

    /**
     * Get a list of all nodes on a specified floor
     * @param floor string floor name
     * @return
     */
    public List<GraphNode> getGraphNodesOnFloor(String floor) {
       List<GraphNode> sameFloor = graphNodes.stream()
               .filter(node -> node.location.floor.equals(floor))
               .collect(Collectors.toList());
       return sameFloor;
    }

    /**
     * Add node to graph if it is not already there
     * @param node
     */
    public boolean addNode(GraphNode node){
        if(!graphNodes.contains(node)){
            graphNodes.add(node);
            return true;
        }
        return false;
    }

    public void deleteNode(GraphNode node){
    }

    public void addConnection(GraphNode nodeA, GraphNode nodeB){
    }

    LinkedList AStar(GraphNode start, GraphNode end){
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphNetwork)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        GraphNetwork rhs = (GraphNetwork) obj;
        return this.graphNodes.equals(rhs.graphNodes);
    }
}
