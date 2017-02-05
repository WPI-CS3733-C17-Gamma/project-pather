import com.sun.corba.se.impl.orbutil.graph.Graph;
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
        return null;
    }

    /**
     *
     * @param term
     * @return
     */
    public List<String> searchEntry(String term){
        return null;
    }

    /**
     *
     * @param roomName
     * @return
     */
    public Room getRoomFromName(String roomName){
        return null;
    }

    /**
     *
     * @param node
     * @return
     */
    public Room getRoomFromNode(GraphNode node){
        return null;
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
        return true;
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean deleteEntry(String key){
        return false;
    }

    /**
     *
     * @param name
     * @return
     */
    public DirectoryEntry getEntry(String name){
        return null;
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
     *
     * @param node
     * @return
     */
    public boolean deleteNode(GraphNode node){
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
     *
     * @param nodeA
     * @param nodeB
     * @return true if the point is  added to both nodes
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        return nodeA.addAdjacent(nodeB) && nodeB.addAdjacent(nodeA);
    }

    /**
     * @return the images stored in the Map object
     */
    public HashMap<String, Image> getImages(){
        return this.mapImages;
    }
}
