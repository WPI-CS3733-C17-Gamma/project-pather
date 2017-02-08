//import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Testing the search function from both the directory and the Patient Controller
 */
public class SearchTest{
    Directory d;

    Room albert = new Room(null, "3A");
    Room bernie = new Room(null, "4B");
    Room charles = new Room(null, "5C");
    Room albert2 = new Room(null, "3B");
    Room albert3 = new Room(null, "3A");

    static Scanner input = new Scanner(System.in);

    PatientController p;
    Map m;

    @Before
    public void setUp(){
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
        d.addRoom(albert2);
        m = new Map(d, null, null);
        p = new PatientController(m, null, null);
    }
    @Test
    public void dummyTest(){
        assertEquals(1, 1);
    }

    @Test
    /**
     * This checks to see if the search result returns the right rooms
     */
    public void CheckRoomTest(){
        List<String> temp = new ArrayList<>();
        temp.add("3A");
        List result = d.searchRooms("3A");
        // System.err.println(result.toString()+"***********************************************");
        assertNotNull(result);
        assertListEquals(result, temp);
        assertListEquals(d.searchRooms("3C"), new ArrayList());
    }

    /**
     * This test tests the CompareTo method for Room objects
     */
    @Test
    public void compareToTest() {
        assertEquals(albert.compareTo(albert3), 0);
        assertTrue(albert.compareTo(charles) < 0);
        assertTrue(charles.compareTo(albert) > 0);
    }

    /**
     * check if two lists contain the same elements and in the exact same order
     * @param actual
     * @param expected
     */
    private void assertListEquals(List actual, List expected){
        assertEquals(actual.size(), expected.size());
        Iterator ia = actual.iterator();
        Iterator ie = expected.iterator();
        while(ie.hasNext() &&  ia.hasNext()){
            assertEquals(ia.next(), (ie.next()));
        }
    }

    /**
     * This test tests if the search result for entry is returning the correct list of
     * entry names
     */
    @Test
    public void searchEntryTest() {
        List<String> temp = new ArrayList();
        temp.add("Albert");
        List result = d.searchEntries("Albert");
        // System.err.println(result.toString()+"***********************************************");
        assertListEquals(result, temp);
        assertListEquals(d.searchEntries("Alb"), temp);
        temp.add("Bernie");
        assertListEquals(d.searchEntries("er"), temp);//return more than one results
    }

    @Test
    public void searchEntryTestFromPC() {
        List<String> temp = new ArrayList();
        temp.add("Albert");
        List result = p.search("Albert");
        // System.err.println(result.toString()+"***********************************************");
        assertListEquals(result, temp);
        assertListEquals(p.search("Alb"), temp);
        temp.add("Bernie");
        assertListEquals(p.search("er"), temp);//return more than one results
    }

    @Test
    public void CheckRoomTestFromPC(){
        List<String> temp = new ArrayList<>();
        temp.add("3A");
        List result = p.search("3A");
        assertListEquals(result, temp);
        temp.clear();
        temp.add("3B");
        assertListEquals(p.search("3B"), temp);
        temp.clear();
        temp.add("3A");
        temp.add("3B");
        assertListEquals(p.search("3"), temp);
    }
}
