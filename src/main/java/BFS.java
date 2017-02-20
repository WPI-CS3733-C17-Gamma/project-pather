import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.xml.soap.Node;
import java.util.*;

/**
 * Created by zht on 2/19/2017.
 */
public class BFS implements IPathFindingAlgorithm{

    Queue<BFSNode> todo;
    List<BFSNode> checked;
    List<GraphNode> shortestPath;

    @Override
    public List<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException {
        if (start.equals(end)) {
            shortestPath.add(start);
            return shortestPath;
        }
        todo = new LinkedList<>();
        checked = new ArrayList<>();
        shortestPath = new ArrayList<>();

        todo.add(new BFSNode(null, start));
        BFSNode current = new BFSNode(null, start);
        while(!todo.isEmpty()){
            current = todo.poll();
            checked.add(current);
            if (current.current.getAdjacent().contains(end)){
                shortestPath.add(end);
                break;
            }
            for (GraphNode neighbor : current.current.getAdjacent()){
                if (!BFScontains(checked, neighbor) && !BFScontains(todo, neighbor)){
                    todo.add(new BFSNode(current, neighbor));
                }
            }
        }
        if (shortestPath.isEmpty()){
            throw new PathNotFoundException(start, end);
        }
        while(current != null){
            shortestPath.add(0, current.current);
            current = current.comeFrom;
        }
        return shortestPath;
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
