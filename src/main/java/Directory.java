import java.util.List;
import java.util.Collection;

public class Directory {
    Collection<DirectoryEntry> entries;
    Collection<Room> rooms;

    public Directory(Collection<DirectoryEntry> entries,
                     Collection<Room> rooms) {
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Directory)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Directory rhs = (Directory) obj;
        return this.entries.equals(rhs.entries) &&
            this.rooms.equals(rhs.rooms);
    }

}
