import java.util.*;

public class DFS implements IPathFindingAlgorithm {
    Stack<GraphNode> todo;
    List<GraphNode> checked;
    List<GraphNode> path;

    GraphNode start, end;

    @Override
    public List<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException {
        checked = new ArrayList<>();
        path = new ArrayList<>();
        if (start.equals(end)) {
            path.add(start);
            return path;
        }
        this.start = start;
        this.end = end;
//        todo = new Stack<>();
//        todo.add(new DFSNode(null, start));
//        todo.add(start);
//        DFSNode current = new DFSNode(null, start);

        return DFSRecursion(start, end);
    }

    private List<GraphNode> DFSRecursion(GraphNode current, GraphNode end) throws PathNotFoundException{
        checked.add(current);
        if (current.getAdjacent().contains(end)){
            path.add(end);
            path.add(0, current);
            return path;
        }
        for (GraphNode neighbor : current.getAdjacent()){
            if (!checked.contains(neighbor)){
                List<GraphNode> result = DFSRecursion(neighbor, end);
                result.add(0, current);
                return result;
            }
        }
        throw new PathNotFoundException(start, end);
    }

    private boolean BFScontains(Collection<DFSNode> list, GraphNode node){
        for (DFSNode n : list){
            if (n.current == node){
                return true;
            }
        }
        return false;
    }

    private class DFSNode {
        DFSNode comeFrom;
        GraphNode current;

        DFSNode(DFSNode from, GraphNode current){
            comeFrom = from;
            this.current = current;
        }

    }
}
