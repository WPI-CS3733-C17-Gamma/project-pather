import java.util.HashMap;
import java.util.List;

public class Directory {
    HashMap<DirectoryEntry, Room> entries;
    HashMap<String, Room> rooms;

    public Directory(HashMap<DirectoryEntry, Room> entries,
                     HashMap<String, Room> rooms) {
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
        if(!rooms.containsKey(room.name)){
            rooms.put(room.name, room);
            return true;
        }
        // return false if the room already exists
        return false;
    }

    /**
     * @param roomName
     * @return
     */
    public Room getRoom(String roomName){
        if(rooms.containsKey(roomName)){
            return rooms.get(roomName);
        }

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
