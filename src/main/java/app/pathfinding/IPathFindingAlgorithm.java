package app.pathfinding;
import app.dataPrimitives.GraphNode;
import java.util.LinkedList;

public interface IPathFindingAlgorithm {

    /**
     * find the shortest path between the two given graph nodes
     * @param start
     * @param end
     * @return the path
     * @throws PathNotFoundException
     */
    public LinkedList<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException;
}
