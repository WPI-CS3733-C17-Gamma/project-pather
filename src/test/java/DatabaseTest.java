import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.sql.*;

import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest extends TestCase{
    private Connection initDB(String name, String[] statements) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:memory:" + name + ";create=true");
            Statement statement = conn.createStatement();
            for (String s : DatabaseManager.initStatements) {
                statement.executeUpdate(s);
            }
            for (String s : statements) {
                statement.executeUpdate(s);
            }
            statement.close();
            return conn;
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void closeDB(String name) {
        try {
            DriverManager.getConnection("jdbc:derby:memory:" + name + ";drop=true");
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
        initDB("test1", statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        DatabaseManager db = new DatabaseManager("memory:test1;");

        Map actual = db.load();

        assertEquals(graph, actual.graph);
        closeDB("test1");
    }

    @Test
    public void testGraphNodesAdjacentLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')",
             "insert into Edges (ID1, ID2) values (1, 2)"};
        initDB("test2", statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        DatabaseManager db = new DatabaseManager("memory:test2;");

        Map actual = db.load();

        assertEquals(graph, actual.graph);
        closeDB("test2");
    }

    @Test
    public void testRoomLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)"};
        initDB("test3", statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        Room roomA = new Room(nodeA, "derFs Office");
        Directory directory = new Directory(
            new LinkedList<DirectoryEntry>(),
            new LinkedList<Room>(Arrays.asList(roomA)));

        DatabaseManager db = new DatabaseManager("memory:test3;");

        Map actual = db.load();

        assertEquals(directory, actual.directory);
        closeDB("test3");
    }

    @Test
    public void testEntriesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into Rooms (rID, name, nID) values (1, 'derFs Office', 1)",
             "insert into Entries (eID, name, title) values (1, 'the land of derF', 'office')",
             "insert into RoomEntryAssoc (eID, rID) values (1, 1)"};
        initDB("test4", statements);

        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        Room roomA = new Room(nodeA, "derFs Office");
        DirectoryEntry entryA = new DirectoryEntry(
            "the land of derF", "office", Arrays.asList(roomA));
        Directory directory = new Directory(
            new LinkedList<DirectoryEntry>(Arrays.asList(entryA)),
            new LinkedList<Room>(Arrays.asList(roomA)));

        DatabaseManager db = new DatabaseManager("memory:test4;");

        Map actual = db.load();

        assertEquals(directory.entries, actual.directory.entries);
        closeDB("test4");
    }
}
