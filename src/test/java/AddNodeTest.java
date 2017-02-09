import junit.framework.TestCase;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zht on 2/8/2017.
 */
public class AddNodeTest extends TestCase {

    MapAdminController ma;
    Map m;
    Directory d;

    GraphNode a = new GraphNode(new FloorPoint(4, 6, "one"));
    GraphNode b = new GraphNode(new FloorPoint(5, 7, "one"));
    GraphNode c = new GraphNode(new FloorPoint(38, 30, "two"));

    GraphNetwork g;

    Room albert = new Room(a, "3A");
    Room bernie = new Room(b, "4B");
    Room charles = new Room(c, "5C");
    Room albert2 = new Room(new GraphNode(new FloorPoint(308, 481, "one")), "3B");
    Room albert3 = new Room(new GraphNode(new FloorPoint(10, 634, "three")), "3A");

    @Before
    public void setUp(){
        d = new Directory(new HashMap<>(), new HashMap<>());
        LinkedList<GraphNode> list = new LinkedList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        c.addAdjacent(b);
        b.addAdjacent(c);
        g = new GraphNetwork(list);
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
        d.addRoom(albert2);
        m = new Map(d, g, null);
        ma = new MapAdminController(m, new ApplicationController(), null);
    }

    public void testAddNode(){
        LinkedList<GraphNode> list = new LinkedList<>();
        list.add(a);
        list.add(b);
        GraphNetwork graphNetwork = new GraphNetwork(list);
        FloorPoint point = new FloorPoint(3, 6, "one");
        GraphNode g = new GraphNode(point);
        graphNetwork.addNode(g);
        list.add(g);
        assertEquals(graphNetwork.getGraphNodesOnFloor("one"), list);
    }

    public void testAddDoubleNode(){
        FloorPoint point = new FloorPoint(3, 6, "one");
        GraphNode gn = new GraphNode(point);
        assertTrue(g.addNode(gn));
        assertFalse(g.addNode(gn));

    }
//need the new distance method from Saahil
//    public void testAddNodeFromMAC(){
//        FloorPoint point = new FloorPoint(3, 6, "one");
//        ma.addNode(point);
//        assertEquals(g.getGraphNode(point), new GraphNode(point));
//    }
}
