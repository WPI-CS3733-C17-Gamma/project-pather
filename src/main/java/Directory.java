import java.util.*;

public class Directory {
    HashMap<String, DirectoryEntry> entries;
    HashMap<String, Room> rooms;

    public Directory(){
        entries = new HashMap<>();
        rooms = new HashMap<>();
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
        rooms.put(room.name, room);
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
