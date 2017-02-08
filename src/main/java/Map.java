import javafx.scene.image.Image;
import java.util.HashMap;
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

    /**
     *
     * @param term
     * @return
     */
    public List<String> searchRoom(String term){
        return directory.searchRooms(term);
    }

    public List<String> subStringSearchRoom(String term) {
        return directory.subStringSearchRooms(term);
    }

    /**
     * @return get all entries in the directory by name
     */
    public List<String> getAllEntries () {
        return directory.getAllEntries();
    }
    /**
     * @return get all entries in the directory by name
     */
    public List<String> getAllRooms () {
        return directory.getAllRooms();
    }

    /**
     *
     * @param term
     * @return
     */
    public List<String> searchEntry(String term){
        return directory.searchEntries(term);
    }

    /**
     *
     * @param roomName
     * @return
     */
    public Room getRoomFromName(String roomName){
        return directory.getRoom(roomName);
    }

    /**
     *
     * @param node
     * @return
     */
    public Room getRoomFromNode(GraphNode node){
        return directory.getRoom(node);
    }

    /**
     *
     * @param room
     */
    public void deleteRoom(String room){
    }
    /**
     *
     * @param room
     * @return true if the room is added, false if it is a duplicate
     */
    public boolean addRoom(Room room){
        return directory.addRoom(room);
    }

    public boolean deleteRoom(GraphNode node) {
        Room roomAtNode = directory.getRoom(node);
        return directory.deleteRoom(roomAtNode);
    }

    /**
     *
     * @param entry
     * @return
     */
    public boolean addEntry(DirectoryEntry entry){
        return directory.addEntry(entry);
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean deleteEntry(String key){
        return directory.deleteEntry(key);
    }

    /**
     *
     * @param name
     * @return
     */
    public DirectoryEntry getEntry(String name){
        return directory.getEntry(name);
    }

    /**
     * Add node to graph
     * @param node node to add
     * @return
     */
    public boolean addNode(GraphNode node){
        return graph.addNode(node);
    }

    /**
     *Delete node from graph and delete the node from adjacent nodes
     * @param node
     * @return
     */
    public boolean deleteNode(GraphNode node){
        graph.deleteNode(node);
        return true;
    }

    /**
     * return the graph node closest to the location given
     * ignores points on different floors
     * @param point
     * @return
     */
    public GraphNode getGraphNode(FloorPoint point){
        return graph.getGraphNode(point);
    }

    /**
     *
     * @param start
     * @param end
     * @return
     */
    public List<GraphNode> getPath(GraphNode start, GraphNode end){
        return null;
    }

    /**
     * adds a connection between two nodes
     * @param nodeA
     * @param nodeB
     * @return true if the point is  added to both nodes
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        return graph.addConnection(nodeA, nodeB);
    }

    /**
     * Deletes the connection between two nodes
     * @param nodeA
     * @param nodeB
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
