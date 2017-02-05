import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Directory {
    HashMap<String, DirectoryEntry> entries;
    HashMap<String, Room> rooms;

    public Directory(HashMap<String, DirectoryEntry> entries,
                     HashMap<String, Room> rooms) {
            this.entries = entries;
            this.rooms = rooms;
    }

    /**
     *
     * @param key
     * @return
     */
    public List<String> searchRooms(String key){
        List<String> temp = new ArrayList();
        if (rooms.get(key) != null) {
            temp.add(key);
        }
        return temp;
    }

    /**
     *
     * @param key
     * @return
     */
    public List<String> searchEntries(String key){
        key = key.toLowerCase();
        List<String> list = new ArrayList();
        Set<String> keySets = entries.keySet();
        Iterator<String> i = keySets.iterator();
        String temp;
        while(i.hasNext()){
            temp = i.next();
            if (temp.toLowerCase().contains(key)) {
                list.add(temp);
            }
        }
        return list;
    }

    /**
     *
     * @param entry
     * @return
     */
    public boolean addEntry(DirectoryEntry entry){
        entries.put(entry.getName(), entry);
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
        Optional<Room> roomAtNode = rooms.values().stream()
            .filter(room -> room.location.equals(node))
            .findFirst();

        if (roomAtNode.isPresent()) {
           return roomAtNode.get();
        }
        else {
            return null;
        }
    }


    /**
     * remove room from rooms
     * remove all entries that have this room as a location
     * @param room
     * @return
     */
    public boolean deleteRoom(Room room) {
        if(rooms.containsKey(room.name)){
            rooms.remove(room);
        }
        else {
            return false;
        }

        for(DirectoryEntry entry : entries.values()) {
           entry.deleteLocatoin(room);
        }

        return true;
    }
}
