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
            "constraint id2_fk foreign key (ID2) references GraphNodes(ID))",
        "create table Rooms (rID Integer Primary key, Name varchar(30), nID Integer," +
            "constraint fk_gn foreign key (nID) references GraphNodes(ID))",
        "create table Entries (eID Integer Primary Key, Title varchar(20), Name varchar(30))",
        "create table RoomEntryAssoc (eID integer, rID integer," +
            "constraint pk_rea primary key (eID, rID),"+
            "constraint eID_fk foreign key (eID) references Entries(eID),"+
            "constraint rID_fk foreign key (rID) references Rooms(rID))"};

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
        // Map IDs for Rooms, Entries and Nodes to Rooms, Entries, and
        // Nodes, respectively
        HashMap<Integer, GraphNode> nodes = new HashMap<Integer, GraphNode>();
        HashMap<Integer, Room> roomsID = new HashMap<Integer, Room>();
        HashMap<Integer, DirectoryEntry> entriesID =
            new HashMap<Integer, DirectoryEntry>();

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();

        try {
            // Prepared Statement for room association query, gets room
            // IDs matching the current entity ID.
            // Is this better than just normal queries? No idea.
            PreparedStatement queryRoomAssoc =
                connection.prepareStatement(
                    "select rID from RoomEntryAssoc where eID=?");

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

            // Get all rooms
            result = s.executeQuery("select * from Rooms");
            while(result.next()) {
                Room room = new Room(nodes.get(result.getInt(3)),
                                     result.getString(2));
                roomsID.put(result.getInt(1), room);
                rooms.put(room.name, room);
            }

            // Get all Entries
            result = s.executeQuery("select * from Entries");
            while(result.next()) {
                LinkedList<Room> locations = new LinkedList<Room>();

                // set eID to query
                queryRoomAssoc.setInt(1, result.getInt(1));
                ResultSet roomAssoc = queryRoomAssoc.executeQuery();
                // construct a list of associated rooms
                while(roomAssoc.next()) {
                    locations.add(roomsID.get(roomAssoc.getInt(1)));
                }

                DirectoryEntry entry = new DirectoryEntry(result.getString(3),
                                                          result.getString(2),
                                                          locations);
                entriesID.put(result.getInt(1), entry);
                entries.put(result.getString(3), entry);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        Directory directory = new Directory(entries, rooms);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(nodes.values()));
        return new Map(directory, graph, null);
    }

    public boolean write(Map data){
        return true;
    }

}
