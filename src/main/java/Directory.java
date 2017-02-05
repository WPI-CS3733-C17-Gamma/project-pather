import java.util.HashMap;
import java.util.List;

public class Directory {
    HashMap<DirectoryEntry, Room> entries;
    HashMap<Room, GraphNode> rooms;

    public Directory(HashMap<DirectoryEntry, Room> entries,
                     HashMap<Room, GraphNode> rooms) {
            this.entries = entries;
            this.rooms = rooms;
    }

    /**
     *
     * @param key
     * @return
     */
    public List searchRooms(String key){
        return null;
    }

    /**
     *
     * @param key
     * @return
     */
    public List<String> searchEntries(String key){
        return null;
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
     * @param key
     * @return
     */
    public DirectoryEntry getEntry(String key){
        return null;
    }

    /**
     *
     * @param room
     * @return
     */
    public boolean addRoom(Room room){
        return true;
    }

    /**
     *
     * @param roomName
     * @return
     */
    public Room getRoom(String roomName){
        return null;
    }

    /**
     *
     * @param node
     * @return
     */
    public Room getRoom(GraphNode node){
        return null;
    }
}
