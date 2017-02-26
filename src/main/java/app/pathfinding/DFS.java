package app.pathfinding;

import app.dataPrimitives.GraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DFS implements IPathFindingAlgorithm {
    final Logger logger = LoggerFactory.getLogger(IPathFindingAlgorithm.class);

    List<GraphNode> checked;
    LinkedList<GraphNode> path;

    GraphNode start, end;
    public String name = "DFS";

    /**
     * finds a path from the start and the end node using depth first search algorithm
     * @param start
     * @param end
     * @param useStairs
     * @return
     * @throws PathNotFoundException
     */
    @Override
    public LinkedList<GraphNode> findPath(GraphNode start, GraphNode end, boolean useStairs) throws PathNotFoundException {
        //initialize variables
        checked = new ArrayList<>();
        path = new LinkedList<>();

        //if the path starts and ends at the same place, the path contains that one node (path is found tho)
        if (start.equals(end)) {
            path.add(start);
            return path;
        }
        //for exception reference
        this.start = start;
        this.end = end;
        return DFSRecursion(start, end, useStairs);
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * loop through to find a path
     * @param current
     * @param end
     * @param useStairs
     * @return
     * @throws PathNotFoundException
     */
    private LinkedList<GraphNode> DFSRecursion(GraphNode current, GraphNode end, boolean useStairs) throws PathNotFoundException{
        // ignores Elevator
        if (useStairs && current.getFloorTransitionType() == GraphNode.ELEVATOR) {
            throw new PathNotFoundException(start, end);
        }
        else if (current.getFloorTransitionType() == GraphNode.STAIR) {
            throw new PathNotFoundException(start, end);
        }

        checked.add(current);
        //check if the neighbor contains the target node; if yes, end search
        if (current.getAdjacent().contains(end)){
            path.add(end);
            path.add(0, current);
            return path;
        }
        //if does not contain, start searching in all neighbors; except it continues down neighbor first then
            //goes to another neighbor
        for (GraphNode neighbor : current.getAdjacent()){
            if (!checked.contains(neighbor)){
                try{
                    LinkedList<GraphNode> result = DFSRecursion(neighbor, end, useStairs);
                    if (!locationEqual(current, result.get(0))){
                        result.add(0, current);
                    }
                    return result;
                }catch(PathNotFoundException e){
                    logger.debug("DFS found a dead end");
                }
            }
        }
        throw new PathNotFoundException(start, end);
    }

    public boolean locationEqual(GraphNode a, GraphNode b){
        return a.getLocation().getX() == b.getLocation().getX() && a.getLocation().getY() == b.getLocation().getY();
    }
}
