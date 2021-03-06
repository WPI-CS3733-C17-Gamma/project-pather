package app.applicationControl;

import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.datastore.Directory;
import app.datastore.GraphNetwork;
import app.datastore.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

public class DatabaseManager {
    final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    String connectionURL;
    Connection connection;
    public static final String[] initStatements = {
        "create table GraphNodes (ID integer primary key, X integer, Y integer, Floor varchar(100), FloorTransitionType integer)",
        "create table Edges (ID1 integer, ID2 integer," +
            "constraint pk_e Primary key (ID1, ID2)," +
            "constraint id1_fk foreign key (ID1) references GraphNodes(ID)," +
            "constraint id2_fk foreign key (ID2) references GraphNodes(ID))",
        "create table Rooms (rID Integer Primary key, Name varchar(100), nID Integer," +
            "constraint fk_gn foreign key (nID) references GraphNodes(ID))",
        "create table Entries (eID Integer Primary Key, Title varchar(100), Name varchar(100), Icon varchar(100))",
        "create table RoomEntryAssoc (eID integer, rID integer," +
            "constraint pk_rea primary key (eID, rID),"+
            "constraint eID_fk foreign key (eID) references Entries(eID),"+
            "constraint rID_fk foreign key (rID) references Rooms(rID))",
        "create table Settings (Name varchar(20) Primary Key, Value varchar(50))"};

    public static final String[] dropStatements = {
        "drop table RoomEntryAssoc",
        "drop table Rooms",
        "drop table Entries",
        "drop table Edges",
        "drop table GraphNodes",
        "drop table Settings"};


    public DatabaseManager(String dbName) {
        // define the Derby connection URL to use
        this.connectionURL = "jdbc:derby:" + dbName + ";create=true";
        try {
            this.connection = DriverManager.getConnection(connectionURL);
            logger.info("Connected to database {}", dbName);
        }
        catch (SQLException e) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            logger.error("Also got error {}", e.getNextException().getMessage());
        }
    }

    /** Loads a app.datastore.Map from the database
     * @return the loaded map
     */
    public Map load(){
        // app.datastore.Map IDs for Rooms, Entries and Nodes to Rooms, Entries, and
        // Nodes, respectively
        TreeMap<Integer, GraphNode> nodes = new TreeMap<Integer, GraphNode>();
        TreeMap<Integer, Room> roomsID = new TreeMap<Integer, Room>();
        TreeMap<Integer, DirectoryEntry> entriesID =
            new TreeMap<Integer, DirectoryEntry>();

        HashMap<String, Room> rooms = new HashMap<String, Room>();
        HashMap<String, DirectoryEntry> entries =
            new HashMap<String, DirectoryEntry>();
        HashMap<String, String> settings = new HashMap<String, String>();

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
                          new GraphNode(result.getInt(2),
                                        result.getInt(3),
                                        result.getString(4),
                                        result.getInt(5)));
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
            result = s.executeQuery("select * from Rooms order by rID");
            while(result.next()) {
                Room room = new Room(nodes.get(result.getInt(3)),
                                     result.getString(2));
                roomsID.put(result.getInt(1), room);
                rooms.put(room.getName(), room);
            }

            // Get all Entries
            result = s.executeQuery("select * from Entries order by eID");
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
                                                          locations,
                                                          result.getString(4));
                entriesID.put(result.getInt(1), entry);
                entries.put(result.getString(3), entry);
            }

            // Get all Settings
            result = s.executeQuery("select * from Settings order by Name");
            while(result.next()) {
                settings.put(result.getString(1), result.getString(2));
            }
        }
        catch (SQLException e) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.getMessage());
        }

        Directory directory = new Directory(entries, rooms);
        GraphNetwork graph = new GraphNetwork(
            new LinkedList<GraphNode>(nodes.values()));

        return new Map(directory, graph, settings);
    }

    /** Write a map to the database
     * @param data A app.datastore.Map to save
     */
    public void write(Map data) {
        try {
            // Clear/reinit DB
            // TODO: probably a better way to do this
            execStatements(dropStatements);
            execStatements(initStatements);

            HashSet<Long> addedNodes = new HashSet<Long>();

            PreparedStatement insertGraphNode = connection.prepareStatement(
                "insert into GraphNodes (ID, X, Y, Floor, FloorTransitionType) values (?, ?, ?, ?, ?)");
            PreparedStatement insertEdge = connection.prepareStatement(
                "insert into Edges (ID1, ID2) values (?, ?)");
            PreparedStatement insertRoom = connection.prepareStatement(
                "insert into Rooms (rID, name, nID) values (?, ?, ?)");
            PreparedStatement insertEntry = connection.prepareStatement(
                "insert into Entries (eID, name, title, Icon) values (?, ?, ?, ?)");
            PreparedStatement insertRoomEntryAssoc = connection.prepareStatement(
                "insert into RoomEntryAssoc (eID, rID) values (?, ?)");
            PreparedStatement insertSetting = connection.prepareStatement(
                "insert into Settings (Name, Value) values (?, ?)");

            // Insert each node
            for (GraphNode node : data.getGraphNodes()) {
                insertGraphNode.setLong(1, node.id);
                insertGraphNode.setInt(2, node.getLocation().getX());
                insertGraphNode.setInt(3, node.getLocation().getY());
                insertGraphNode.setString(4, node.getLocation().getFloor());
                insertGraphNode.setInt(5,node.getFloorTransitionType());
                insertGraphNode.executeUpdate();
                addedNodes.add(node.id);

                // Insert an edge for each adjacent node
                for (GraphNode adjacentNode : node.getAdjacent()) {
                    if(addedNodes.contains(adjacentNode.id)) {
                        insertEdge.setLong(1, node.id);
                        insertEdge.setLong(2, adjacentNode.id);
                        insertEdge.executeUpdate();
                    }
                }
            }

            // Insert each room
            for (Room room : data.getDirectoryRooms().values()) {
                insertRoom.setLong(1, room.id);
                insertRoom.setString(2, room.getName());
                if (room.hasLocation()) {
                    insertRoom.setLong(3, room.getLocation().id);
                }
                else {
                    insertRoom.setNull(3, Types.INTEGER);
                }
                insertRoom.executeUpdate();
            }

            // Insert each entry
            for (DirectoryEntry entry : data.getDirectoryEntries().values()) {
                insertEntry.setLong(1, entry.id);
                insertEntry.setString(2, entry.getName());
                insertEntry.setString(3, entry.getTitle());
                insertEntry.setString(4,entry.getIcon());
                insertEntry.executeUpdate();

                // Insert each room association
                for (Room room : entry.getLocation()) {
                    insertRoomEntryAssoc.setLong(1, entry.id);
                    insertRoomEntryAssoc.setLong(2, room.id);
                    insertRoomEntryAssoc.executeUpdate();
                }
            }

            // Insert each Setting
            for (java.util.Map.Entry<String, String> entry : data.getSettings().entrySet()) {
                insertSetting.setString(1, entry.getKey());
                insertSetting.setString(2, entry.getValue());
                insertSetting.executeUpdate();
            }
        }
        catch (SQLException e) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.toString() + e.getMessage());
        }
    }

    private void execStatements(String[] statements) {
        try {
            Statement statement = connection.createStatement();

            // Execute all given setup statements
            for (String s : statements) {
                statement.executeUpdate(s);
            }
            statement.close();
        }
        catch (SQLException e) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

