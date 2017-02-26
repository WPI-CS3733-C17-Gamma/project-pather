package app.dataPrimitives;

import app.datastore.GraphNetwork;
import app.datastore.Map;
import app.pathfinding.PathNotFoundException;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
public class MultiFloorSubPathTest extends TestCase{
    GraphNode A1;
    GraphNode A2;
    GraphNode A3;
    GraphNode B1;
    GraphNode B2;

    GraphNetwork network;
    Map map;

    protected void setUp() {
         network = new GraphNetwork();
         A1 = new GraphNode(10,10,"A");
         A2 = new GraphNode(11,10,"A");
         B1 = new GraphNode(12,10,"B");
         A3 = new GraphNode(13,10,"A");
         B2 = new GraphNode(14,10,"B");

        network.addNode(A1);
        network.addNode(A2);
        network.addNode(B1);
        network.addNode(A3);
        network.addNode(B2);

        network.addConnection(A1,A2);
        network.addConnection(A2,B1);
        network.addConnection(B1,A3);
        network.addConnection(B2,A3);
        map = new Map(null, network);
    }

    @Test
    // Test case assumes a star is searching in reverse order
    public void testOneFloor() throws PathNotFoundException {

        SubPath expected = new SubPath("A");
        expected.path.add(A1);
        expected.path.add(A2);
        ArrayList<SubPath> expSubPath = new ArrayList<>();
        expSubPath.add(expected);

        assertEquals("Sub path should be one floor" , expSubPath, map.getPathByFloor(A1,A2, false) );

    }

    @Test
    // Test case assumes a star is searching in reverse order
    public void testTwoFloor() throws PathNotFoundException {

        SubPath expected1 = new SubPath("A");
        expected1.path.add(A1);
        expected1.path.add(A2);
        SubPath expected2 = new SubPath("B");
        expected2.path.add(B1);
        ArrayList<SubPath> expSubPath = new ArrayList<>();
        expSubPath.add(expected1);
        expSubPath.add(expected2);

        assertEquals("Sub path should be one floor" , expSubPath, map.getPathByFloor(A1,B1, false) );
    }
    @Test
    // Test case assumes a star is searching in reverse order
    public void testThreeFloor() throws PathNotFoundException {

        SubPath expected0 = new SubPath("A");
        expected0.path.add(A1);
        expected0.path.add(A2);
        SubPath expected1 = new SubPath("B");
        expected1.path.add(B1);
        SubPath expected2 = new SubPath("A");
        expected2.path.add(A3);
        ArrayList<SubPath> expSubPath = new ArrayList<>();
        expSubPath.add(expected0);
        expSubPath.add(expected1);
        expSubPath.add(expected2);

        assertEquals("Sub path should be one floor" , expSubPath, map.getPathByFloor(A1,A3, false) );
    }

    @Test (expected = PathNotFoundException.class)
    public void testNoPath () {
        map.getPath(A1, new GraphNode (1,1,""), false);
    }
}
