import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphNetwork {
    LinkedList<GraphNode> graphNodes = new LinkedList();

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
        // filter the nodes for all the ones on the same floor
        List<GraphNode> sameFloor = graphNodes.stream()
                .filter(node -> node.location.floor.equals(loc.floor))
                .collect(Collectors.toList());

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

    public void addNode(GraphNode node){
        if(!graphNodes.contains(node)){
            graphNodes.add(node);
        }
    }

    public void deleteNode(GraphNode node){
    }

    public void addConnection(GraphNode nodeA, GraphNode nodeB){
    }

    LinkedList AStar(GraphNode start, GraphNode end){
        return null;
    }
}
