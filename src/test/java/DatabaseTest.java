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

    @Test
    public void testGraphNodesLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')"};
        Connection conn = initDB("test1", statements);

        Directory directory = null;
        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        DatabaseManager db = new DatabaseManager("memory:test1;");

        Map actual = db.load();

        assertEquals(graph, actual.graph);
    }

    @Test
    public void testGraphNodesAdjacentLoad() {
        String[] statements =
            {"insert into GraphNodes (ID, X, Y, Floor) values (1, 4, 2, 'bob1')",
             "insert into GraphNodes (ID, X, Y, Floor) values (2, 4, 3, 'bob1')",
             "insert into Edges (ID1, ID2) values (1, 2)"};
        Connection conn = initDB("test2", statements);

        Directory directory = null;
        GraphNode nodeA = new GraphNode(new FloorPoint(4, 2, "bob1"));
        GraphNode nodeB = new GraphNode(new FloorPoint(4, 3, "bob1"));
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(Arrays.asList(nodeA, nodeB)));

        DatabaseManager db = new DatabaseManager("memory:test2;");

        Map actual = db.load();

        assertEquals(graph, actual.graph);
    }
    }
}
