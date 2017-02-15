import junit.framework.TestCase;
import org.junit.Before;

import java.util.*;

/**
 * Created by jonandrews on 2/6/17.
 */
public class DeleteConnectionTest extends TestCase {
    GraphNode node11 = new GraphNode(10, 10, "");
    GraphNode node14 = new GraphNode(10, 40, "");
    GraphNode node15 = new GraphNode(10, 50, "");
    GraphNode node34 = new GraphNode(30, 40, "");
    GraphNode node35 = new GraphNode(30, 50, "");
    GraphNode node45 = new GraphNode(40, 50, "");
    GraphNode node55 = new GraphNode(50, 50, "");
    GraphNode node53 = new GraphNode(50, 30, "");
    GraphNode node51 = new GraphNode(50, 10, "");
    GraphNode node41 = new GraphNode(40, 10, "");
    GraphNode node43 = new GraphNode(40, 30, "");
    GraphNode node33 = new GraphNode(30, 30, "");
    GraphNode node31 = new GraphNode(30, 10, "");
    GraphNode node23 = new GraphNode(20, 30, "");
    GraphNode node22 = new GraphNode(20, 20, "");

    LinkedList<GraphNode> betterNodes =
        new LinkedList<>(Arrays.asList(node11, node14, node15, node34, node35,
                                       node45, node55, node53, node51, node41,
                                       node43, node33, node31, node23, node22));
    GraphNetwork betterGraph = new GraphNetwork(betterNodes);

    Directory d;

    Room albert = new Room(null, "3A");
    Room bernie = new Room(null, "4B");
    Room charles = new Room(null, "5C");
    Room albert2 = new Room(null, "3A");

    Map testMap;


    @Before
    public void setUp() {
        node11.addAdjacent(node31);
        node11.addAdjacent(node14);

        node14.addAdjacent(node15);
        node14.addAdjacent(node22);
        node14.addAdjacent(node11);

        node15.addAdjacent(node34);
        node15.addAdjacent(node14);

        node34.addAdjacent(node35);
        node34.addAdjacent(node33);
        node34.addAdjacent(node15);

        node35.addAdjacent(node34);
        node35.addAdjacent(node45);

        node45.addAdjacent(node55);
        node45.addAdjacent(node53);
        node45.addAdjacent(node35);

        node55.addAdjacent(node45);

        node53.addAdjacent(node45);
        node53.addAdjacent(node51);
        node53.addAdjacent(node43);

        node51.addAdjacent(node53);
        node51.addAdjacent(node41);

        node41.addAdjacent(node43);
        node41.addAdjacent(node51);

        node43.addAdjacent(node33);
        node43.addAdjacent(node53);
        node43.addAdjacent(node41);

        node33.addAdjacent(node31);
        node33.addAdjacent(node23);
        node33.addAdjacent(node34);
        node33.addAdjacent(node43);

        node31.addAdjacent(node11);
        node31.addAdjacent(node33);

        node23.addAdjacent(node22);
        node23.addAdjacent(node33);

        node22.addAdjacent(node23);
        node22.addAdjacent(node14);

        d = new Directory(new HashMap<>(), new HashMap<>());

        List<Room> list1 = new ArrayList<>();
        List<Room> list2 = new ArrayList<>();
        List<Room> list3 = new ArrayList<>();
        list1.add(albert);
        list2.add(bernie);
        list3.add(charles);
        d.addEntry(new DirectoryEntry("Albert", "Doctor", list1));
        d.addEntry(new DirectoryEntry("Bernie", "Doctor", list2));
        d.addEntry(new DirectoryEntry("Charles", "Doctor", list3));
        d.addRoom(albert);
        d.addRoom(bernie);
        d.addRoom(charles);

        testMap = new Map(d, betterGraph);
    }

    public void testDeleteConnection() {
        assertTrue(node23.adjacent.contains(node22));
        betterGraph.deleteConnection(node23, node22);
        assertFalse(node23.adjacent.contains(node22));
        assertFalse(node22.adjacent.contains(node23));

    }

    public void testDeleteConnectionViaMap() {
        assertTrue(node23.adjacent.contains(node22));
        testMap.deleteConnection(node23, node22);
        assertFalse(node23.adjacent.contains(node22));
        assertFalse(node22.adjacent.contains(node23));
    }

    public void testConnectionMapAdminController() {
        assertTrue(node23.adjacent.contains(node22));
        testMap.deleteConnection(node23, node22);
        assertFalse(node23.adjacent.contains(node22));
        assertFalse(node22.adjacent.contains(node23));
    }
}
