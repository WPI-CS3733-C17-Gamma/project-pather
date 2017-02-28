package app.display;

import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import app.datastore.Map;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by Isaac on 2/6/2017.
 */
public class DirectoryAdminControllerTest {
    DirectoryAdminController testDirCon;

    @Before
    public void setup() {
        HashMap<String, DirectoryEntry> entries = new HashMap<>();

        entries.put("Bill", new DirectoryEntry("Bill", "Doctor", new LinkedList<Room>()));
        entries.put("Jeff", new DirectoryEntry("Jeff", "Nurse", new LinkedList<Room>()));
        entries.put("John", new DirectoryEntry("John", "Janitor", new LinkedList<Room>()));
        entries.put("Dude", new DirectoryEntry("Dude", "The Dude", new LinkedList<Room>()));

        Directory dir = new Directory(entries, null);
        Map map = new Map(dir, null);
        DirectoryAdminController dirCon = new DirectoryAdminController(map, null, null);

        testDirCon = dirCon;
    }

    @Test
    public void testSaveEntrySimple() {
        // Testing a simple modification
        testDirCon.activeDirectoryEntry = testDirCon.map.getEntry("Bill");
        testDirCon.saveEntry("Billy", "Doctor", new LinkedList<Room>(), "");
        assertNull(testDirCon.map.getEntry("Bill"));
        assertTrue(testDirCon.map.getEntry("Billy").equals(
            new DirectoryEntry("Billy", "Doctor", new LinkedList<Room>())));
    }

    @Test
    public void testSaveEntrySameName() {
        // Testing if you leave the name the same but change something else
        testDirCon.activeDirectoryEntry = testDirCon.map.getEntry("Bill");
        testDirCon.saveEntry("Bill", "Medicine Man", new LinkedList<Room>(), "");
        assertNotNull(testDirCon.map.getEntry("Bill"));
        assertTrue(testDirCon.map.getEntry("Bill").equals(
            new DirectoryEntry("Bill", "Medicine Man", new LinkedList<Room>())));
    }

    @Test ( expected = IllegalArgumentException.class)
    public void testSaveEntryThatWouldReplace() {
        // Testing if you change the entry but it would replace a different entry
        testDirCon.activeDirectoryEntry = testDirCon.map.getEntry("Bill");
        DirectoryEntry oldJohn = testDirCon.map.getEntry("John");
        testDirCon.saveEntry("John", "Medicine Man", new LinkedList<>(), "");
    }

    @Test ( expected=IllegalStateException.class )
    public void testSaveEntryStateException() {
        // Testing if nothing was selected when function was called
        //   (this shouldn't happen normally, only if the code is wonky)
        testDirCon.saveEntry("Billy", "Doctor", new LinkedList<Room>(),"");
    }

    @Test ( expected=IllegalArgumentException.class )
    public void testSaveEntryArgsException() {
        // Testing if you are trying to create a duplicate entry
        testDirCon.activeDirectoryEntry = testDirCon.map.getEntry("Bill");
        testDirCon.saveEntry("Jeff", "Nurse", new LinkedList<Room>(), "");
    }
}
