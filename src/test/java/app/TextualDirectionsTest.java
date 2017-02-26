package app;

import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import app.datastore.Map;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Isaac on 2/13/2017.
 */
public class TextualDirectionsTest {
    @Test
    public void testGetDirections() throws Exception {
        LinkedList<GraphNode> path = new LinkedList<GraphNode>(
            Arrays.asList(new GraphNode(0, 0, ""),
                          new GraphNode(10, 0, ""),
                          new GraphNode(20, 10, ""),
                          new GraphNode(20, 20, ""),
                          new GraphNode(30, 20, "")));
        assertEquals("[Take a right, Take a right, Take a left, Arrive at your destination]",
            (new Map(new Directory(null, new HashMap<String, Room>()), null, null))
                .getTextualDirections(path, null)
                .stream().map(p -> p.getValue()).collect(Collectors.toList()).toString());

    }
}
