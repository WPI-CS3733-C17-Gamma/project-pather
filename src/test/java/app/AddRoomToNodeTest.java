package app;

import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by Saahil on 2/5/2017.
 */
public class AddRoomToNodeTest extends TestCase {

   GraphNode dummyNode;
   Directory directory;
   protected void setUp(){
      dummyNode = new GraphNode(1,1,"");
       directory = new Directory (new HashMap<String, DirectoryEntry> (),
           new HashMap<String, Room>() );
   }

   @Test
   // see if a room can be added to the directory
   public void testAddRoom() {
      Room r = new Room (dummyNode, "dummyRoom");
      boolean successfulAdd = directory.addRoom(r);
      assertTrue(successfulAdd);
      assertEquals(r, directory.getRoom("dummyRoom"));
   }

   @Test
   // confirm that you cannot add two rooms with the same room
   public void testAddDuplicate() {
       Room r = new Room (dummyNode, "dummyRoom");
       boolean successfulAdd = directory.addRoom(r);
       successfulAdd = directory.addRoom(r);
       assertFalse(successfulAdd);
   }
   @Test
   // confirm that multiple things can be added and retrieved
    public void testAddMult() {
       Room r = new Room (dummyNode, "dummyRoom");
       Room r2 = new Room (new GraphNode(1,2,"3"), "secondRoom");
       boolean successfulAdd = directory.addRoom(r);
       successfulAdd = successfulAdd &&  directory.addRoom(r2);

       assertTrue(successfulAdd);
       assertEquals(directory.getRoom("dummyRoom"), r);
       assertEquals(directory.getRoom("secondRoom"), r2);
   }


}
