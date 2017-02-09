import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by Saahil on 2/5/2017.
 */
public class GetRoomByNodeTest extends TestCase {
    Directory dir;
    GraphNode pos;
    Room room ;

    protected void setUp() {

        dir = new Directory(new HashMap<>(), new HashMap<>());
        pos = new GraphNode(1,1,"1");
        room = new Room (pos, "dummyNode");
        dir.addRoom(room);
    }

    @Test
    // test to see if getting a room by a node value works
    public void testGetRoomByNode () {

        Room getRoom  = dir.getRoom(pos);
        assertEquals(room, getRoom);
    }

    @Test
    // make sure no room is found when a incorrect search is done
    public void testFailToFindRoom () {
        Room getRoom = dir.getRoom(new GraphNode(1,2,"1"));
        assertEquals(getRoom, null);


    }
}
