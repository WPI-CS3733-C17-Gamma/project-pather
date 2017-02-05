import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DatabaseManager {
    String connectionURL;
    Connection connection;
    public static final String[] initStatements = {
        "create table GraphNodes (ID integer primary key, X integer, Y integer, Floor varchar(20))",
        "create table Edges (ID1 integer, ID2 integer," +
            "constraint pk_e Primary key (ID1, ID2)," +
            "constraint id1_fk foreign key (ID1) references GraphNodes(ID)," +
            "constraint id2_fk foreign key (ID2) references GraphNodes(ID))"};

    public DatabaseManager(String dbName) {
		// define the Derby connection URL to use
		this.connectionURL = "jdbc:derby:" + dbName + ";create=true";
		try {
            this.connection = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + dbName);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            System.err.println(e.getNextException().getMessage());
        }
    }

    /** Loads a Map from the database
     * @returns the loaded map
     */
    public Map load(){
        HashMap<Integer, GraphNode> nodes = new HashMap<Integer, GraphNode>();
        try {
            // Get all GraphNodes
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(
                "select * from GraphNodes order by ID");
            // For each result node, add a node to the hashmap by ID
            while(result.next()) {
                nodes.put(result.getInt(1),
                          new GraphNode(new FloorPoint(
                                            result.getInt(2),
                                            result.getInt(3),
                                            result.getString(4))));
            }

            // Get all edges
            result = s.executeQuery("select * from Edges");
            while(result.next()) {
                // Add a to b
                nodes.get(result.getInt(1))
                    .addAdjacent(nodes.get(result.getInt(2)));
                // Add b to a
                nodes.get(result.getInt(2))
                    .addAdjacent(nodes.get(result.getInt(1)));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Directory directory = null;
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(nodes.values()));
        return new Map(directory, graph, null);
    }

    public boolean write(Map data){
        return true;
    }

}
