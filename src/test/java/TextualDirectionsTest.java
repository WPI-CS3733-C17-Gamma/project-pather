import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Isaac on 2/13/2017.
 */
public class TextualDirectionsTest {
    LinkedList<GraphNode> path = new LinkedList<GraphNode>(Arrays.asList(
        new GraphNode(0, 0, ""),
        new GraphNode(10, 0, ""),
        new GraphNode(20, 10, ""),
        new GraphNode(20, 20, ""),
        new GraphNode(30, 20, "")
    ));

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetDirections() throws Exception {
        assertEquals("[Take a right, Take a right, Take a left]", GraphNetwork.getDirections(path).toString());
    }

}
