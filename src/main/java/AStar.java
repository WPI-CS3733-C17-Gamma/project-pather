import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by zht on 2/19/2017.
 */
public class AStar implements IPathFindingAlgorithm { public LinkedList<GraphNode> findPath(GraphNode startNode, GraphNode goalNode) throws PathNotFoundException {
    AStarNode start = new AStarNode(startNode);
    AStarNode goal = new AStarNode(goalNode);
    LinkedList<AStarNode> openSet = new LinkedList<>();
    LinkedList<AStarNode> closedSet = new LinkedList<>();

    AStarNode current;

    openSet.add(start);

    start.gScore = 0;

    start.fScore = start.getDistance(goal);

    while(!openSet.isEmpty()){
        //Sort List of nodes
        Collections.sort(openSet);

        current = openSet.getFirst();
        if(current.equals(goal))
            return reconstruct_path(current);

        openSet.remove(current);
        closedSet.add(current);
        current.node.getAdjacent().sort(
            (GraphNode a, GraphNode b) -> ((int)a.distance(goalNode) - (int)b.distance(goalNode)));
        for (GraphNode gNeighbour: current.node.getAdjacent()) {
            AStarNode neighbour = new AStarNode(gNeighbour);
            if (closedSet.contains(neighbour))
                continue;
            double tentative_gscore = current.gScore + neighbour.getDistance(current);
            if(!openSet.contains(neighbour)) {
                openSet.add(neighbour);
                Collections.sort(openSet);
            }
            else if (tentative_gscore >= neighbour.gScore)
                continue;
            neighbour.cameFrom = current;
            neighbour.gScore = tentative_gscore;
            neighbour.fScore = tentative_gscore + neighbour.getDistance(goal);
        }
    }
    throw new PathNotFoundException(startNode, goalNode);
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
            total_path.add(0, current.node);
        }
        return total_path;
    }


}
