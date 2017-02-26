package app.datastore;

import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.Room;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;

public class DirectoryImportTSVTest extends TestCase {
	@Test
	public void testSingleEntry() throws IOException {
		StringReader input = new StringReader(
			"the land of derF\tderFs Office\n");

		Room roomA = new Room(null, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "", Arrays.asList(roomA));

		HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);

        Directory expected = new Directory(entries, rooms);

		Directory actual = new Directory();
		actual.importTSV(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testSharedRoom() throws IOException {
		StringReader input = new StringReader(
			"the land of derF\tderFs Office\n" +
			"whatever\tderFs Office");

		Room roomA = new Room(null, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "", Arrays.asList(roomA));
        DirectoryEntry entryB = new DirectoryEntry(
            "whatever", "", Arrays.asList(roomA));

		HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);
        entries.put(entryB.getName(), entryB);

        Directory expected = new Directory(entries, rooms);

		Directory actual = new Directory();
		actual.importTSV(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testMultipleRooms() throws IOException {
		StringReader input = new StringReader(
			"the land of derF\tderFs Office\ttestRoom\n" +
			"test2\tderFs Office and testRoom\n" +
			"test3\tderFs Office/testRoom\n" +
			"test4\tderFs Office, testRoom\n");

		Room roomA = new Room(null, "derFs Office");
		Room roomB = new Room(null, "testRoom");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "", Arrays.asList(roomA, roomB));
        DirectoryEntry entryB = new DirectoryEntry(
            "test2", "", Arrays.asList(roomA, roomB));
        DirectoryEntry entryC = new DirectoryEntry(
            "test3", "", Arrays.asList(roomA, roomB));
        DirectoryEntry entryD = new DirectoryEntry(
            "test4", "", Arrays.asList(roomA, roomB));

		HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);
        rooms.put(roomB.getName(), roomB);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);
        entries.put(entryB.getName(), entryB);
        entries.put(entryC.getName(), entryC);
        entries.put(entryD.getName(), entryD);

        Directory expected = new Directory(entries, rooms);

		Directory actual = new Directory();
		actual.importTSV(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testTitle() throws IOException {
		StringReader input = new StringReader(
			"derF, George, MD\tderFs Office\n");

		Room roomA = new Room(null, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "derF, George", "MD", Arrays.asList(roomA));

		HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);

        Directory expected = new Directory(entries, rooms);

		Directory actual = new Directory();
		actual.importTSV(input);

		assertEquals(expected.entries, actual.entries);
	}
}
