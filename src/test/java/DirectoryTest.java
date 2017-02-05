//import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class DirectoryTest{
    Directory d;

    static Scanner input = new Scanner(System.in);

    @Before
    public void setUp(){
        d = new Directory();
        Room albert = new Room(null, "3A");
        Room bernie = new Room(null, "4B");
        Room charles = new Room(null, "5C");
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

    @Test
    public void CheckRoomTest(){
        List<String> temp = new ArrayList<>();
        temp.add("3A");
        System.err.print(d.searchRooms("3A").toString());
        assertNotNull(d.searchRooms("3A"));
        assertListEquals(d.searchRooms("3A"), new ArrayList());
    }

    private void assertListEquals(List actual, List expected){
        assertEquals(actual.size(), expected.size());
        Iterator ia = actual.iterator();
        Iterator ie = expected.iterator();
        while(ie.hasNext() &&  ia.hasNext()){
            assertEquals(ia.next(), (ie.next()));
        }
    }
}
