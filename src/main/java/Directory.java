import java.util.*;

public class Directory {
    HashMap<String, DirectoryEntry> entries;
    HashMap<String, Room> rooms;

    public Directory(HashMap<String, DirectoryEntry> entries,
                     HashMap<String, Room> rooms) {
            this.entries = entries;
            this.rooms = rooms;
    }

    /**
     * Search through a list of room to see if there is a name of the room matches the input;
     * Since searching based on the name of the room (which only consists of two characters, a number that
     * indicates which floor the room is on, and one capital letter) is very specific and hard to mess up, the list
     * should contain only one result
     * @param key the given input string that needs to be searched on
     * @return an empty list if the given name of the room is not found, or a list containing the given name of the room
     */
    public List<String> searchRooms(String key){
        List<String> temp = new ArrayList<String>();
        if (rooms.get(key) != null) {
            temp.add(key);
        }
        return temp;
    }

    /**
     * Search through names of all possible entries and return a list of all possible entry names containing that substring
     * @param key the given substring that needs to be searched for
     * @return a list of all possible entry names containing that substring
     */
    public List<String> searchEntries(String key){
        key = key.toLowerCase();
        List<String> list = new ArrayList<String>();
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
        return entries.remove(key, entries.get(key));
    }

    /**
     *
     * @param key the key of the entry to get
     * @return the entry or null if it could not be found
     */
    public DirectoryEntry getEntry(String key){
        DirectoryEntry entry;
        entry = entries.get(key);
        return entry;
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
