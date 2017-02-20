package app.pathfinding;

import app.dataPrimitives.GraphNode;

import java.util.*;

public class DFS implements IPathFindingAlgorithm {
    List<GraphNode> checked;
    LinkedList<GraphNode> path;

    GraphNode start, end;

    /**
     * finds a path from the start and the end node using depth first search algorithm
     * @param start
     * @param end
     * @return
     * @throws PathNotFoundException
     */
    @Override
    public LinkedList<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException {
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
        return DFSRecursion(start, end);
    }

    /**
     * loop through to find a path
     * @param current
     * @param end
     * @return
     * @throws PathNotFoundException
     */
    private LinkedList<GraphNode> DFSRecursion(GraphNode current, GraphNode end) throws PathNotFoundException{
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
                    LinkedList<GraphNode> result = DFSRecursion(neighbor, end);
                    result.add(0, current);
                    return result;
                }catch(PathNotFoundException e){
                    System.err.println("Dead end");
                }
            }
        }
        throw new PathNotFoundException(start, end);
    }
}
