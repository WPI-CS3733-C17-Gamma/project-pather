package app.pathfinding;

import app.dataPrimitives.GraphNode;

import java.util.LinkedList;

public interface IPathFindingAlgorithm {

    /**
     * find the shortest path between the two given graph nodes
     * @param start
     * @param end
     * @param useStairs If true this forces the algorithm to use the stairs and not elevators
     * @return the path
     * @throws PathNotFoundException
     */
    public LinkedList<GraphNode> findPath(GraphNode start, GraphNode end, boolean useStairs) throws PathNotFoundException;
    public String getName();
}
