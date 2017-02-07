import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphNetwork {
    LinkedList<GraphNode> graphNodes = new LinkedList<>();

    public GraphNetwork(){}

    public GraphNetwork(LinkedList<GraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    public static LinkedList<GraphNode> getPath(GraphNode startNode, GraphNode goalNode){
        AStarNode start = new AStarNode(startNode);
        AStarNode goal = new AStarNode(goalNode);
        LinkedList<AStarNode> openSet = new LinkedList<>();
        LinkedList<AStarNode> closedSet = new LinkedList<>();

        AStarNode current;

        openSet.add(start);

        start.gScore = 0;

        start.fScore = start.getDistance(goal);

        while(openSet.size() > 0){
            //Sort List of nodes
            openSet.sort(new Comparator<AStarNode>(){
                @Override
                public int compare(AStarNode a, AStarNode b) {
                    return ((int)b.fScore - (int)a.fScore);
                }
            });

            current = openSet.getFirst();
            if(current.equals(goal))
                return reconstruct_path(current);

            openSet.remove(current);
            closedSet.add(current);
            current.node.getAdjacent().sort(new Comparator<GraphNode>(){
                @Override
                public int compare(GraphNode a, GraphNode b) {
                    return ((int)b.distance(goalNode) - (int)a.distance(goalNode));
                }
            });
            for (GraphNode gNeighbour: current.node.getAdjacent()) {
                AStarNode neighbour = new AStarNode(gNeighbour);
                if (closedSet.contains(neighbour))
                    continue;
                double tentative_gscore = current.gScore + neighbour.getDistance(current);
                if(!openSet.contains(neighbour))
                    openSet.add(neighbour);

                else if (tentative_gscore >= neighbour.gScore)
                    continue;
                neighbour.cameFrom = current;
                neighbour.gScore = tentative_gscore;
                neighbour.fScore = tentative_gscore + neighbour.getDistance(goal);
            }
        }
        return null;
    }

    /***
     *
     * @param current
     * @return final Node in A* and reconstructs path
     */
    private static LinkedList<GraphNode> reconstruct_path(AStarNode current){
        LinkedList<GraphNode> total_path = new LinkedList<>();
        total_path.add(current.node);

        while(current.cameFrom != null){
            current = current.cameFrom;
            total_path.add(current.node);
        }
        return total_path;
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

    /**
     * Delete a node from graph and delete the node from the adjacent nodes
     * @param node
     */

    public void deleteNode(GraphNode node){
        // 1. remove node from list
        graphNodes.remove(node);
        // 2. remove node from all adjacent nodes
       for(GraphNode s: node.adjacent){
           s.adjacent.remove(node);
       }
    }

    /**
     * Adds a connection between two nodes
     * @param nodeA
     * @param nodeB
     * @return true if connection was successful
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        if(nodeA.getAdjacent().contains(nodeB) && nodeB.getAdjacent().contains(nodeA))
            return true;
        else
            return false;
    }

    /**
     * Deletes the connection between two nodes
     * @param nodeA
     * @param nodeB
     * @return true if successful
     */
    public boolean deleteConnection(GraphNode nodeA, GraphNode nodeB) {
        if (nodeB.adjacent.contains(nodeA)) {
            //do the thing
            nodeA.removeAdjacent(nodeB);
            nodeB.removeAdjacent(nodeA);
            return true;
        }
        return false;
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
