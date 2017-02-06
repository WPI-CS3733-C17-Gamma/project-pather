import junit.framework.TestCase;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * Created by dominic on 2/5/17.
 */

public class GetPathTest extends TestCase{

    FloorPoint point1 = new FloorPoint(0, 0, "");
    FloorPoint point2 = new FloorPoint(1, 0, "");
    FloorPoint point3 = new FloorPoint(2, 1, "");
    FloorPoint point4 = new FloorPoint(2, 2, "");
    FloorPoint point5 = new FloorPoint(3, 2, "");


    GraphNode node1 = new GraphNode(point1);
    GraphNode node2 = new GraphNode(point2);
    GraphNode node3 = new GraphNode(point3);
    GraphNode node4 = new GraphNode(point4);
    GraphNode node5 = new GraphNode(point5);

    LinkedList<GraphNode> nodes = new LinkedList<>(Arrays.asList(node1, node2, node3, node4, node5));
    GraphNetwork graph = new GraphNetwork(nodes);

    public void setUp() {
        node1.addAdjacent(node2);
        node1.addAdjacent(node3);
        node2.addAdjacent(node1);
        node2.addAdjacent(node4);
        node3.addAdjacent(node1);
        node3.addAdjacent(node4);
        node4.addAdjacent(node2);
        node4.addAdjacent(node3);
        node4.addAdjacent(node5);
        node5.addAdjacent(node4);
    }

    public void testGetPath() {
        LinkedList<GraphNode> path1 = new LinkedList(Arrays.asList(node5, node4, node2, node1));
        for(GraphNode neighbour: nodes){
            System.err.println(neighbour.toString());
        }
        LinkedList<GraphNode> path2 = graph.getPath(node1, node5);
        assertTrue(path1.equals(path2));
    }

}
