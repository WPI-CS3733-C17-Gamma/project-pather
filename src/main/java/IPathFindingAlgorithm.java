import java.util.List;

public interface IPathFindingAlgorithm {

    /**
     * find the shortest path between the two given graph nodes
     * @param start
     * @param end
     * @return the path
     * @throws PathNotFoundException
     */
    public List<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException;
}
