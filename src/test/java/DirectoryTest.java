//import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DirectoryTest{
    Directory d;

    Room albert = new Room(null, "3A");
    Room bernie = new Room(null, "4B");
    Room charles = new Room(null, "5C");
    Room albert2 = new Room(null, "3A");

    static Scanner input = new Scanner(System.in);

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
    }
    @Test
    public void dummyTest(){
        assertEquals(1, 1);
    }

    @Test //**************CHANGE THIS LATER***************
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
    }

    /**
     *
     * This test tests the room compare method
     */
    @Test
    public void compareToTest() {
        assertEquals(albert.compareTo(albert2), 0);
        assertTrue(albert.compareTo(charles) < 0);
        assertTrue(charles.compareTo(albert) > 0);
    }


    private void assertListEquals(List actual, List expected){
        assertEquals(actual.size(), expected.size());
        Iterator ia = actual.iterator();
        Iterator ie = expected.iterator();
        while(ie.hasNext() &&  ia.hasNext()){
            assertEquals(ia.next(), (ie.next()));
        }
    }

    /**
     * This test tests if the serach result for entry is returning the correct list of
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
    }
}
