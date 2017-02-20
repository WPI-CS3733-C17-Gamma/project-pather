import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by zht on 2/19/2017.
 */
public class BFS implements IPathFindingAlgorithm{

    Queue<GraphNode> todo;
    List<GraphNode> checked;
    List<GraphNode> shortestPath;
    List<Integer> layers;

    public BFS(){
        todo = new LinkedList<>();
        checked = new ArrayList<>();
        shortestPath = new ArrayList<>();
        layers  = new ArrayList<>();
    }


    @Override
    public List<GraphNode> findPath(GraphNode start, GraphNode end) throws PathNotFoundException {
        if (start.equals(end)){
            shortestPath.add(start);
            shortestPath.add(end);
            return shortestPath;
        }

        todo.add(start);
        GraphNode head = start;
        GraphNode current;
        int layerIndex = 0;
        while(!todo.isEmpty()){
            current = todo.poll();
            checked.add(current);
            if (current.equals(end)){
                if (current.equals(head)){
                    layers.add(layerIndex);
                }
                shortestPath.add(current);
                break;
            }
            for (GraphNode neighbor : current.getAdjacent()){
                if (!checked.contains(neighbor) && !todo.contains(neighbor)){
                    if (current.equals(head)){
                        layers.add(layerIndex);
                        head = neighbor;
                    }
                    todo.add(neighbor);
                }
            }
            layerIndex++;
        }

        if (shortestPath.isEmpty()) {
            throw new PathNotFoundException(start, end);
        }

        GraphNode temp;
        current = end;
        for (int x = layers.size() - 1; x > 0; x--){
            for (int y = layers.get(x) - 1; y >= layers.get(x - 1); y--){
                temp  =checked.get(y);
                if (temp.getAdjacent().contains(current)){
                    shortestPath.add(0, temp);
                    current = temp;
                    break;
                }
            }
        }

        return shortestPath;
    }

}
