package app;

import app.display.DirectoryAdminController;
import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import app.datastore.Map;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEntryTest extends TestCase {
    final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    DirectoryAdminController c;
    Map m;
    Directory d;
    DirectoryEntry e, f;

    Room albert = new Room(null, "3A");
    Room bernie = new Room(null, "4B");
    Room charles = new Room(null, "5C");
    Room albert2 = new Room(null, "3A");

    @Before
    public void setUp(){
        e = new  DirectoryEntry("e", "e", new LinkedList<>());
        f = new DirectoryEntry("f", "f", new LinkedList<>());
        HashMap <String, DirectoryEntry> h = new HashMap<>();
        h.put(e.getName(), e);
        h.put(f.getName(), f);
        d = new Directory(h, new HashMap<>());
        m = new Map(d, null);

        c = new DirectoryAdminController(m, null, null);

    }

    @Test ( expected = IllegalArgumentException.class)
    public void testCreateEntryDAC(){
        assertEquals(2, d.getEntries().entrySet().size());
        c.createEntry("g", "g", new LinkedList<>());
        assertEquals(3, d.getEntries().size());
        try {
            c.createEntry("f", "f", new LinkedList<>());
        } catch (IllegalArgumentException e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        assertEquals(3, d.getEntries().size());
        for (String s : d.getEntries().keySet()){
            logger.debug(s);
        }
    }


}
