import junit.framework.TestCase;

import java.util.HashMap;

/**
 * Created by Saahil on 2/5/2017.
 */
public class DeleteRoomTest extends TestCase {

    GraphNode dummyNode;
    Directory directory;
    Room dummyRoom;
    protected void setUp(){
        dummyNode = new GraphNode(new FloorPoint(1,1,""));
        directory = new Directory (new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>() );
        dummyRoom = new Room(dummyNode, "room");
    }


    public void testDelete() {
        directory.addRoom(dummyRoom);
        boolean successfulDelete = directory.deleteRoom(dummyRoom);

        assertTrue(successfulDelete);


    }
}
