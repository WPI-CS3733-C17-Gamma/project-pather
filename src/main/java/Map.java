
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Eula on 2/3/2017.
 */
public class Map {

    //
    Directory directory;
    //
    GraphNetwork graph;
    //
    HashMap<String, Image> mapImages;

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
     */
    public void addRoom(Room room){

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
     *
     * @param node
     * @return
     */
    public boolean addNode(Node node){
        return true;
    }

    /**
     *
     * @param node
     * @return
     */
    public boolean deleteNode(Node node){
        return true;
    }

    /**
     *
     * @param point
     * @return
     */
    public GraphNode getGraphNode(FloorPoint point){
        return null;
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
     */
    public void addConnection(Node nodeA, Node nodeB){

    }

    /**
     * @return the images stored in the Map object
     */
    public HashMap<String, Image> getImages(){
        return this.mapImages;
    }
}
