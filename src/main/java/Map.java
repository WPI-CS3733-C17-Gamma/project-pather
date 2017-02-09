import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Map {
    Directory directory;
    GraphNetwork graph;
    HashMap<String, Image> mapImages;

    public Map(Directory directory,
               GraphNetwork graph,
               HashMap<String, Image> mapImages) {
        this.directory = directory;
        this.graph = graph;
        this.mapImages = mapImages;
    }

    /** See method {@link Directory#searchRooms(String)} */
    public List<String> searchRoom(String term){
        return directory.searchRooms(term);
    }

    /** See method {@Link Directory#subStringSearchRoom} */
    public List<String> subStringSearchRoom(String term) {
        return directory.subStringSearchRooms(term);
    }

    /**
     * Function to get all entries as a list
     * @return get all entries in the directory by name
     */
    public List<String> getAllEntries () {
        return directory.getAllEntries();
    }

    /**
     * Funcition to get all rooms as a list
     * @return get all entries in the directory by name
     */
    public List<String> getAllRooms () {
        return directory.getAllRooms();
    }

    /** See method {@link Directory#searchEntries(String)} */
    public List<String> searchEntry(String term){
        return directory.searchEntries(term);
    }

    /** See method {@link Directory#getRoom(String)} */
    public Room getRoomFromName(String roomName){
        return directory.getRoom(roomName);
    }

    /** See method {@link Directory#getRoom(GraphNode)} */
    public Room getRoomFromNode(GraphNode node){
        return directory.getRoom(node);
    }

    /** See method {@link Directory#addRoom(Room)} */
    public boolean addRoom(Room room){
        return directory.addRoom(room);
    }

    /** See method {@link Directory#deleteRoom(Room)} */
    public boolean deleteRoom(Room room){
        return directory.deleteRoom(room);
    }

    /**
     * Function to delete a room by its node
     * @param node location of the room to delete
     * @return true if deleted, else false
     */
    public boolean deleteRoom(GraphNode node) {
        Room roomAtNode = directory.getRoom(node);
        return directory.deleteRoom(roomAtNode);
    }

    /** See method {@link Directory#addEntry(DirectoryEntry)} */
    public boolean addEntry(DirectoryEntry entry){
        return directory.addEntry(entry);
    }

    /** See method {@link Directory#deleteEntry(DirectoryEntry)} */
    public boolean deleteEntry(DirectoryEntry entry){
        return directory.deleteEntry(entry);
    }

    /** See method {@link Directory#getEntry(String)} */
    public DirectoryEntry getEntry(String name){
        return directory.getEntry(name);
    }

    /** See method {@link GraphNetwork#addNode(GraphNode)} */
    public boolean addNode(GraphNode node){
        return graph.addNode(node);
    }

    /**
     * Delete node from graph and delete the node from adjacent nodes
     * @param node node to be deleted
     * @return true
     */
    public boolean deleteNode(GraphNode node){
        graph.deleteNode(node);
        return true;
    }

    /**
     * return the graph node closest to the location given
     * ignores points on different floors
     * @param point point to get the node for
     * @return the graph node at the point
     */
    public GraphNode getGraphNode(FloorPoint point){
        return graph.getGraphNode(point);
    }

    /** See method {@Link GraphNetwork#getPath(startNode, goalNode)} */
    public List<GraphNode> getPath(GraphNode start, GraphNode end){
        try {
            return graph.getPath(start, end);
        } catch( PathNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return new LinkedList<>();
    }

    /**
     * adds a connection between two nodes
     * @param nodeA First node
     * @param nodeB Second node
     * @return true if the point is  added to both nodes
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        return graph.addConnection(nodeA, nodeB);
    }

    /**
     * Deletes the connection between two nodes
     * @param nodeA first node
     * @param nodeB second node
     * @return true if successful
     */
    public boolean deleteConnection(GraphNode nodeA, GraphNode nodeB) {
        return this.graph.deleteConnection(nodeA, nodeB);
    }

    /**
     * @return the images stored in the Map object
     */
    public HashMap<String, Image> getImages(){
        return this.mapImages;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Map rhs = (Map) obj;
        return this.directory.equals(rhs.directory) &&
            this.graph.equals(rhs.graph) &&
            this.mapImages.equals(rhs.mapImages);
    }

}
