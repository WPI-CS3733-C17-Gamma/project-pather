import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.sql.*;

import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest extends TestCase{
    // Using databases in memory to not clutter up the file system
    private final String DBName = "memory:testDB";
    private Connection conn;

    @Override
    protected void setUp() {
        try {
            conn = DriverManager.getConnection(
                "jdbc:derby:" + DBName + ";create=true");
            // Execute all init statements
            execStatements(DatabaseManager.initStatements);
        }
        catch (SQLException e) {
            // Sometimes these are fatal, sometimes not. Don't think
            // it's worth the effort to figure out which is which
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected void tearDown() {
        try {
            // drop DB from last test
            DriverManager.getConnection("jdbc:derby:" + DBName + ";drop=true");
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
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
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testGraphNodesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        Map actual = new DatabaseManager(DBName).load();
        assertEquals(graph, actual.graph);
    }

    @Test
    public void testGraphNodesAdjacentLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')",
             "insert into Edges (ID1, ID2) values (1, 2)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        Map actual = new DatabaseManager(DBName).load();
        assertEquals(graph, actual.graph);
    }

    @Test
    public void testRoomLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        Room roomA = new Room(nodeA, "derFs Office");

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.name, roomA);

        Map actual = new DatabaseManager(DBName).load();
        assertEquals(rooms, actual.directory.rooms);
    }

    @Test
    public void testEntriesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)",
             "insert into Entries (eID, name, title) values (1, 'the land of derF', 'office')",
             "insert into RoomEntryAssoc (eID, rID) values (1, 1)"};
        execStatements(statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.name, entryA);

        Map actual = new DatabaseManager(DBName).load();
        assertEquals(entries, actual.directory.entries);
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

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.name, roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.name, entryA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(entries, rooms);
        HashMap<String, Image> mapImages = new HashMap<String, Image>();
        Map expected = new Map(directory, graph, mapImages);

        DatabaseManager db = new DatabaseManager(DBName);
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
        HashMap<String, Image> mapImages = new HashMap<String, Image>();
        Map expected = new Map(directory, graph, mapImages);

        Map actual = new DatabaseManager(DBName).load();
        assertEquals(expected, actual);
    }



    @Test
    public void testGraphNodeWrite() {
        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        HashMap<String, Image> mapImages = new HashMap<String, Image>();
        Map expected = new Map(directory, graph, mapImages);

        DatabaseManager db = new DatabaseManager(DBName);
        db.write(expected);
        Map actual = db.load();

        assertEquals(expected.graph.graphNodes, actual.graph.graphNodes);
    }

    @Test
    public void testWrite() {
        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);

        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        rooms.put(roomA.name, roomA);

        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        entries.put(entryA.name, entryA);

        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));
        Directory directory = new Directory(entries, rooms);
        HashMap<String, Image> mapImages = new HashMap<String, Image>();
        Map expected = new Map(directory, graph, mapImages);

        DatabaseManager db = new DatabaseManager(DBName);
        db.write(expected);
        Map actual = db.load();

        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyWrite() {
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>());
        Directory directory = new Directory(
            new HashMap<String, DirectoryEntry>(),
            new HashMap<String, Room>());
        HashMap<String, Image> mapImages = new HashMap<String, Image>();
        Map expected = new Map(directory, graph, mapImages);

        DatabaseManager db = new DatabaseManager(DBName);
        db.write(expected);
        Map actual = db.load();

        assertEquals(expected, actual);
    }
}
