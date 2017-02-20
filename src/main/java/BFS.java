import java.util.*;

/**
 * Created by zht on 2/19/2017.
 */
public class BFS implements IPathFindingAlgorithm{

    Queue<BFSNode> todo;
    List<BFSNode> checked;
    List<GraphNode> path;

    @Override
    public List<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException {
        todo = new LinkedList<>();
        checked = new ArrayList<>();
        path = new ArrayList<>();
        if (start.equals(end)) {
            path.add(start);
            return path;
        }

        todo.add(new BFSNode(null, start));
        BFSNode current = new BFSNode(null, start);
        while(!todo.isEmpty()){
            current = todo.poll();
            checked.add(current);
            if (current.current.getAdjacent().contains(end)){
                path.add(end);
                break;
            }
            for (GraphNode neighbor : current.current.getAdjacent()){
                if (!BFScontains(checked, neighbor) && !BFScontains(todo, neighbor)){
                    todo.add(new BFSNode(current, neighbor));
                }
            }
        }
        if (path.isEmpty()){
            throw new PathNotFoundException(start, end);
        }
        while(current != null){
            path.add(0, current.current);
            current = current.comeFrom;
        }
        return path;
    }

    private boolean BFScontains(Collection<BFSNode> list, GraphNode node){
        for (BFSNode n : list){
            if (n.current == node){
                return true;
            }
        }
        return false;
    }

    private class BFSNode{
        BFSNode comeFrom;
        GraphNode current;

        BFSNode(BFSNode from, GraphNode current){
            comeFrom = from;
            this.current = current;
        }

    }
}
