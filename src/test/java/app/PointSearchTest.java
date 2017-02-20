package app;

import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;
import app.datastore.GraphNetwork;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.LinkedList;

/**
 * Class to test searching for points from a given location
 */
public class PointSearchTest extends TestCase {
    FloorPoint one , two, three, four;
    // run every test
    protected void setUp() {
       one = new FloorPoint(0,0,"one");
       two = new FloorPoint(3,4,"one");
       three = new FloorPoint(6,8,"one");
       four = new FloorPoint(9,12, "one");
    }

    @Test
    public void testDistance() {
        double distance = one.distance(two);
        assertEquals(5, distance, .01);
    }

    @Test
    // test if minimum distance will be picked from multiple nodes
    public void testGraph(){
        GraphNetwork network = new GraphNetwork(new LinkedList<GraphNode>());
        network.addNode(new GraphNode(one));
        network.addNode(new GraphNode(two));
        network.addNode(new GraphNode(three));
        network.addNode(new GraphNode(four));

        GraphNode closestToOne = network.getGraphNode(one);

        assertEquals(closestToOne.getLocation(), one);
    }
    @Test
    // ensure that different floors will be ignored
    public void testDifferentFloor() {
        GraphNode floorTwo = new GraphNode(0,0,"two");
        GraphNetwork network = new GraphNetwork(new LinkedList<GraphNode>());
        network.addNode(new GraphNode(two));
        network.addNode(floorTwo);
        // get closest
        GraphNode closestToOne = network.getGraphNode(one);
        // assert it is expected
        assertEquals(closestToOne.getLocation(), two);

    }
}
