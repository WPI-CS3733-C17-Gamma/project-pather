//TOO MUCH UI STUFF INVOLVED THAT IT CAN'T BE TESTED
//import junit.framework.TestCase;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.After;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import static junit.framework.TestCase.assertEquals;
//
///**
// * test for select funciton in PatientController
// */
//public class SelectTest extends TestCase{
//
//    PatientController p;
//    Map m;
//    Directory d;
//
//    GraphNode a = new GraphNode(new FloorPoint(4, 6, "one"));
//    GraphNode b = new GraphNode(new FloorPoint(5, 7, "one"));
//    GraphNode c = new GraphNode(new FloorPoint(38, 30, "two"));
//
//    GraphNetwork g;
//
//    Room albert = new Room(a, "3A");
//    Room bernie = new Room(b, "4B");
//    Room charles = new Room(c, "5C");
//    Room albert2 = new Room(new GraphNode(new FloorPoint(308, 481, "one")), "3B");
//    Room albert3 = new Room(new GraphNode(new FloorPoint(10, 634, "three")), "3A");
//
//    @Before
//    public void setUp(){
//        d = new Directory(new HashMap<>(), new HashMap<>());
//        LinkedList<GraphNode> list = new LinkedList<>();
//        list.add(a);
//        list.add(b);
//        list.add(c);
//        c.addAdjacent(b);
//        b.addAdjacent(c);
//        g = new GraphNetwork(list);
//        List<Room> list1 = new ArrayList<>();
//        List<Room> list2 = new ArrayList<>();
//        List<Room> list3 = new ArrayList<>();
//        list1.add(albert);
//        list2.add(bernie);
//        list3.add(charles);
//        d.addEntry(new DirectoryEntry("Albert", "Doctor", list1));
//        d.addEntry(new DirectoryEntry("Bernie", "Doctor", list2));
//        d.addEntry(new DirectoryEntry("Charles", "Doctor", list3));
//        d.addRoom(albert);
//        d.addRoom(bernie);
//        d.addRoom(charles);
//        d.addRoom(albert2);
//        m = new Map(d, g, null);
//        p = new PatientController(m, new ApplicationController(), null);
//    }
//
//    @Test
//    public void testSeleteRoomTestFromPC(){
//        System.err.println(a);
//        System.err.println(p.select("Albert") + "****************************");
//        try {
//            assertEquals(p.select("Albert"), a);
//        }catch(NullPointerException e){
//            System.err.println("hello");
//        }
//    }
