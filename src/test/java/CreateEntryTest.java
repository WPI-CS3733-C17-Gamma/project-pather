import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

public class CreateEntryTest extends TestCase {

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
        h.put(e.name, e);
        h.put(f.name, f);
        d = new Directory(h, new HashMap<>());
        m = new Map(d, null);
        c = new DirectoryAdminController(m, null, null);
    }

    @Test ( expected = IllegalArgumentException.class)
    public void testCreateEntryDAC(){
        assertTrue(2 == d.getEntries().entrySet().size());
        c.createEntry("g", "g", new LinkedList<>());
        assertTrue(d.getEntries().size() == 3);
        try {
            c.createEntry("f", "f", new LinkedList<>());
        } catch (IllegalArgumentException e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        assertTrue(d.getEntries().size() == 3);
        for (String s : d.getEntries().keySet()){
            System.err.println(s);
        }
    }


}
