import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by saahil claypool on 2/5/2017.
 */
public class AddConnectionTest extends TestCase{

    GraphNode a;
    GraphNode b;
    GraphNode c;

    GraphNetwork graph;

    protected void setUp() {
       a = new GraphNode(new FloorPoint(1,1,""));
       b = new GraphNode(new FloorPoint(1,2,""));
       c = new GraphNode(new FloorPoint(1,3,""));
        graph = new GraphNetwork(new LinkedList<>(Arrays.asList(a,b,c)));
    }

    @Test
    // ensure that a connection can be added
    public void testAddConnection() {
        a.addAdjacent(b);
        assertEquals(a.getAdjacent().get(0), b);
    }


    @Test
    // ensure that each point can only be added once
    public void testDuplicateConnectin() {
        a.addAdjacent(b);
        boolean addedDuplicate = a.addAdjacent(b);
        assertFalse(addedDuplicate);
        assertEquals(a.getAdjacent().size() , 1);
    }

    @Test
    // ensure that given two nodes, the graph controller will connect them both to eachother
    public void testTwoWayConnection() {
        Map m = new Map(null, graph, null);
        boolean added = m.addConnection(a, b);
        assertEquals(a.getAdjacent().get(0), b);
        assertEquals(b.getAdjacent().get(0), a);
        assertTrue(added);
    }

    @Test
    // ensure that a node cannot be connected to itself
    public void testSelfAddAdjacent() {
        assertTrue(a.getAdjacent().size() == 0);
        Map m = new Map(null, graph, null);
        boolean added = m.addConnection(a, a);
        assertTrue(a.getAdjacent().size() == 0);
        assertFalse(added);
    }

    @Test
    public void testAddConectionGraphNetwork(){
        assertFalse(a.getAdjacent().contains(b));
        assertFalse(b.getAdjacent().contains(a));
        graph.addConnection(a, b);

        assertTrue(a.getAdjacent().contains(b));
        assertTrue(b.getAdjacent().contains(a));
    }
    @Test
    public void testAddConectionMap(){
        Map map= new Map(null, graph, null);
        assertFalse(a.getAdjacent().contains(b));
        assertFalse(b.getAdjacent().contains(a));
        map.addConnection(a, b);

        assertTrue(a.getAdjacent().contains(b));
        assertTrue(b.getAdjacent().contains(a));
    }
    @Test
    public void testSelfAddConnetion(){
        assertFalse(a.getAdjacent().contains(a));
        assertFalse(graph.addConnection(a,a));
        assertFalse(a.getAdjacent().contains(a));
    }
    @Test
    public void testDoubleAddConnection(){
        assertTrue(graph.addConnection(a,b));
        assertFalse(graph.addConnection(a,b));
//        assertTrue(a.getAdjacent().contains(b));
//        assertTrue(b.getAdjacent().contains(a));
    }
}
