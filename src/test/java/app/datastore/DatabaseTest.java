package app.datastore;

import app.applicationControl.DatabaseManager;
import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class DatabaseTest extends TestCase{
    final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    // Using databases in memory to not clutter up the file system
    private final String DBName = "memory:testDB";
    private Connection conn;
    DatabaseManager db;

    static int dbID = 0;

    @Override
    protected void setUp() {
        db = new DatabaseManager(DBName + Integer.toString(dbID));
        dbID++;
        conn = db.getConnection();
        // Execute all init statements
        execStatements(DatabaseManager.initStatements);
    }

    private void execStatements(String[] statements) {
        try {
            Statement statement = conn.createStatement();

            // Execute all given setup statements
            for (String s : statements) {
                statement.executeUpdate(s);
            }
            statement.close();
        }
        catch (SQLException e) {
            logger.debug("Got error in {} : {}", this.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Test
    public void testGraphNodesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        GraphNode nodeB = new GraphNode(4, 3, "bob1");
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        Map actual = db.load();
        assertEquals(graph, actual.getGraph());
    }

    @Test
    public void testGraphNodesAdjacentLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')",
             "insert into Edges (ID1, ID2) values (1, 2)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        GraphNode nodeB = new GraphNode(4, 3, "bob1");
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        Map actual = db.load();
        assertEquals(graph, actual.getGraph());
    }

    @Test
    public void testRoomLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        Room roomA = new Room(nodeA, "derFs Office");

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        Map actual = db.load();
        assertEquals(rooms, actual.getDirectoryRooms());
    }

    @Test
    public void testEntriesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor, FloorTransitionType) values (1, 4, 2, 'bob1', 0)",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)",
             "insert into Entries (eID, name, title, Icon) values (1, 'the land of derF', 'office', '')",
             "insert into RoomEntryAssoc (eID, rID) values (1, 1)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);

        Map actual = db.load();
        assertEquals(entries, actual.getDirectoryEntries());
    }

    @Test
    public void testLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 3, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)",
             "insert into Entries (eID, name, title) values (1, 'the land of derF', 'office')",
             "insert into RoomEntryAssoc (eID, rID) values (1, 1)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        GraphNode nodeB = new GraphNode(4, 3, "bob1");
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(entries, rooms);
        Map expected = new Map(directory, graph);

        db.write(expected);
        Map actual = db.load();

        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyLoad() {
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>());
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        Map expected = new Map(directory, graph);

        Map actual = db.load();
        assertEquals(expected, actual);
    }



    @Test
    public void testGraphNodeWrite() {
        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        GraphNode nodeB = new GraphNode(4, 3, "bob1");
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        Map expected = new Map(directory, graph);

        db.write(expected);
        Map actual = db.load();

        assertEquals(expected.getGraphNodes(), actual.getGraphNodes());
    }

    @Test
    public void testWrite() {
        GraphNode nodeA = new GraphNode(4, 2, "bob1");
        GraphNode nodeB = new GraphNode(4, 3, "bob1");
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.getName(), roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.getName(), entryA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(entries, rooms);
        Map expected = new Map(directory, graph);

        db.write(expected);
        Map actual = db.load();

        assertEquals(expected, actual);
    }

    @Test
    public void testAdjacentWrite() {
        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        GraphNode nodeC = new GraphNode(new FloorPoint(4, 4, "bob1"));
        GraphNode nodeD = new GraphNode(new FloorPoint(4, 5, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        nodeB.addAdjacent(nodeC);
        nodeC.addAdjacent(nodeB);

        nodeC.addAdjacent(nodeA);
        nodeA.addAdjacent(nodeC);

        nodeD.addAdjacent(nodeA);
        nodeA.addAdjacent(nodeD);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB, nodeC, nodeD)));
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        Map expected = new Map(directory, graph);

        db.write(expected);
        Map actual = db.load();

        assertEquals(expected.getGraphNodes(), actual.getGraphNodes());
    }

    @Test
    public void testSettingsWrite() {
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>());
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("testK", "testV");

        Map expected = new Map(directory, graph, settings);

        db.write(expected);
        Map actual = db.load();

        assertEquals(settings, actual.getSettings());
    }

    @Test
    public void testEmptyWrite() {
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>());
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        Map expected = new Map(directory, graph);

        db.write(expected);
        Map actual = db.load();
        assertEquals(expected, actual);
    }
}
