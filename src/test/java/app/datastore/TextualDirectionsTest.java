package app.datastore;

import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import app.datastore.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Isaac on 2/13/2017.
 */
public class TextualDirectionsTest {
    final Logger logger = LoggerFactory.getLogger(TextualDirectionsTest.class);

    @Test
    public void testGetDirections() throws Exception {
        LinkedList<GraphNode> path = new LinkedList<GraphNode>(
            Arrays.asList(new GraphNode(0, 0, ""),
                          new GraphNode(10, 0, ""),
                          new GraphNode(20, 10, ""),
                          new GraphNode(20, 20, ""),
                          new GraphNode(30, 20, "")));
        assertEquals("[Starting from an unknown location, Take a right, Take a right, Take a left, Arrive at your destination]",
            (new Map(new Directory(null, new HashMap<String, Room>()), null, null))
                .getTextualDirections(path, null)
                .stream().map(p -> p.getValue()).collect(Collectors.toList()).toString());

    }

    @Test
    public void testTextDirectionsStairs() throws Exception {
        GraphNode g1 = new GraphNode(20, 20, "floor1", 3);
        g1.addAdjacent(new GraphNode(20, 20, "floor2", 3));
        LinkedList<GraphNode> path = new LinkedList<GraphNode>(
            Arrays.asList(
                new GraphNode(0, 0, "floor1"),
                new GraphNode(10, 0, "floor1"),
                new GraphNode(20, 10, "floor1"),
                g1));
        System.out.println("Final Node type " + path.get(path.size()-1).getFloorTransitionType());
        assertEquals("[Starting from an unknown location, Take a right, Take a right, Take the stairs to floor 2]",
            (new Map(new Directory(null, new HashMap<String, Room>()), null, null))
                .getTextualDirections(path, "floor2")
                .stream().map(p -> p.getValue()).collect(Collectors.toList()).toString());
    }

    @Test
    public void testTextDirectionsElevator() throws Exception {
        GraphNode start = new GraphNode(0, 0, "floor1");
        GraphNode g1 = new GraphNode(20, 20, "floor1", 1);
        g1.addAdjacent(new GraphNode(20, 20, "floor2", 1));
        LinkedList<GraphNode> path = new LinkedList<GraphNode>(
            Arrays.asList(
                start,
                new GraphNode(10, 0, "floor1"),
                new GraphNode(20, 10, "floor1"),
                g1));
        System.out.println("Final Node type " + path.get(path.size()-1).getFloorTransitionType());
        assertEquals("[Starting from an unknown location, Take a right, Take a right, Take the elevator to floor 2]",
            (new Map(new Directory(null, new HashMap<String, Room>()), null, null))
                .getTextualDirections(path, "floor2")
                .stream().map(p -> p.getValue()).collect(Collectors.toList()).toString());
    }
}
