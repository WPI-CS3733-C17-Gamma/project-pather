import java.util.*;
import java.util.stream.Collectors;

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
        for (String s : rooms.keySet()){
            if (s.contains(key)){
                temp.add(s);
            }
        }
        return temp;
    }

    /**
     * Search for a room that contains the key as a substring
     * @param key
     * @return
     */
    public List<String> subStringSearchRooms(String key) {
        return rooms.values()
            .stream()
            .map(room -> room.name)
            .filter(name -> name.contains(key))
            .collect(Collectors.toList());
    }

    /**
     * return list of all entries
     * @return
     */
    public List<String> getAllEntries () {
        return entries.keySet().stream().collect(Collectors.toList());
    }

    /**
     * get list of all room names
     * @return
     */
    public List<String> getAllRooms () {
        return rooms.keySet().stream().collect(Collectors.toList());
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
     * Function to add an entry to the entry list
     * @param entry The entry to add to the list
     * @return True normally, false if would replaced an object
     */
    public boolean addEntry(DirectoryEntry entry){
        if( entries.containsKey(entry.getName()) ) {
            return false;
        }
        return ( entries.put(entry.getName(), entry) == null );
    }

    /**
     * The name of the entry must be the key of it in the hashmap
     * @param entry The entry to remove
     * @return true if the entry was deleted, false if it was not
     */
    public boolean deleteEntry(DirectoryEntry entry){
        return entries.remove(entry.getName(), entry);
    }

    /**
     * Gets an entry in the directory from the given key
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
     * @return the map of all directory entries
     */
    public HashMap<String, DirectoryEntry> getEntries(){
        return this.entries;
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
           entry.deleteLocation(room);
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
