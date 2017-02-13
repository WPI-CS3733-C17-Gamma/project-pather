import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
public class MultiFloorSubPathTest extends TestCase{

    protected void setUp() {
    }

    @Test
    public void testOneFloor() {
        GraphNetwork network = new GraphNetwork();
        GraphNode A1 = new GraphNode(10,10,"A");
        GraphNode A2 = new GraphNode(11,10,"A");
        GraphNode B1 = new GraphNode(12,10,"B");
        GraphNode A3 = new GraphNode(13,10,"A");
        GraphNode B2 = new GraphNode(14,10,"B");

        network.addNode(A1);
        network.addNode(A2);
        network.addNode(B1);
        network.addNode(A3);
        network.addNode(B2);

        network.addConnection(A1,A2);

        Map m = new Map(null, network, null);
        SubPath expected = new SubPath("A");
        expected.path.add(A1);
        expected.path.add(A2);
        ArrayList<SubPath> expSubPath = new ArrayList<>();
        expSubPath.add(expected);

        assertEquals("Sub path should be one floor" , expSubPath, m.getPathByFloor(A1,A2) );

    }

}
