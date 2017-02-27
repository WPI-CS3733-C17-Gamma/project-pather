package app.datastore;

import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Directory {
    final Logger logger = LoggerFactory.getLogger(Directory.class);

    HashMap<String, DirectoryEntry> entries;
    HashMap<String, Room> rooms;

    public Directory(HashMap<String, DirectoryEntry> entries,
                     HashMap<String, Room> rooms) {
            this.entries = entries;
            this.rooms = rooms;
    }

    public Directory() {
            this.entries = new HashMap<String, DirectoryEntry>();
            this.rooms = new HashMap<String, Room>();
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
     * @param key key to search for
     * @return
     */
    public List<String> subStringSearchRooms(String key) {
        return rooms.values()
            .stream()
            .map(room -> room.getName())
            .filter(name -> name.contains(key))
            .collect(Collectors.toList());
    }

    /**
     * return list of all entries
     * @return a list of all entries as strings
     */
    public List<String> getAllEntries () {
        return entries.keySet().stream().collect(Collectors.toList());
    }

    /**
     * get list of all room names
     * @return a list of all room names as strings
     */
    public List<String> getAllRooms () {
        return rooms.keySet().stream().collect(Collectors.toList());
    }


    /**
     * Get a list of roomes without locations
     * @return List of room names for each room with a null location
     */
    public List<String> getRoomsWithoutLocations () {
        return rooms.values().stream()
            .filter(room -> ! room.hasLocation())
            .map(room -> room.getName())
            .collect(Collectors.toList()) ;
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
     * Function to get a hashmap of all entries
     * @return the map of all directory entries
     */
    public HashMap<String, DirectoryEntry> getEntries(){
        return this.entries;
    }

    /**
     * Function to add a room to the list of rooms
     * @param room room to be added
     * @return true if the room is added, false if it is a duplicate
     */
    public boolean addRoom(Room room){
        if(!rooms.containsKey(room.getName())){
            rooms.put(room.getName(), room);
            return true;
        }
        // return false if the room already exists
        return false;
    }

    /**
     * Function to get a room based on its name
     * @param roomName name of the room to search for
     * @return the room, or null if it could not be found
     */
    public Room getRoom(String roomName){
        if(rooms.containsKey(roomName)){
            return rooms.get(roomName);
        }

        return null;
    }

    /**
     * Gets a room based on its location node
     * @param node location of the room
     * @return the room at that location, or null
     */
    public Room getRoom(GraphNode node){
        Optional<Room> roomAtNode = rooms.values().stream()
            .filter(room -> room.getLocation() != null && room.getLocation().equals(node))
            .findFirst();

        if (roomAtNode.isPresent()) {
           return roomAtNode.get();
        }
        else {
            return null;
        }
    }

    /**
     * Gets the app.dataPrimitives.GraphNode for the app.dataPrimitives.Room named Kiosk in the directory
     * @return the app.dataPrimitives.GraphNode, or app.dataPrimitives.GraphNode(0, 0, "") if it does not exist
     */
    public GraphNode getKioskLocation(String kioskName) {
        try {
            return getRoom(kioskName).getLocation();
        }
        catch (Exception e) {
            logger.error("No Kiosk Found");
            return new GraphNode(0, 0, "");
        }
    }

    /**
     * remove room from rooms
     * @param room room to delete
     * @return true if deleted, else false
     */
    public boolean deleteRoom(Room room) {
        if(rooms.containsKey(room.getName())){
            rooms.remove(room.getName());
        }
        else {
            return false;
        }

        for(DirectoryEntry entry : entries.values()) {
           entry.deleteLocation(room);
        }

        return true;
    }

    /**
     * Change the name of an existing room
     * @param room
     * @param newName
     * @return
     */
    public boolean changeRoomName (Room room, String newName) {
        boolean containedOldRoom = rooms.remove(room.getName(), room);
        room.setName(newName);
        rooms.put(newName, room);
        return containedOldRoom;
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

    public void setEntries(HashMap<String, DirectoryEntry> entries) {
        this.entries = entries;
    }

    public HashMap<String, Room> getRooms() {
        return rooms;
    }

    public void setRooms(HashMap<String, Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Populates the Directory from a well-formated TSV file
     * @param file a File to parse from
     */
    public void importTSV(File file) throws IOException {
        importTSV(new FileReader(file));
    }

    /**
     * Populates the Directory from a well-formated TSV file
     *
     * Format: No header, each line should be of the one of the forms:
     *     (Lastname), (Firstname)[, title]\t(Location1)[(sep)(Location2)]
     *     (Service)\t(Location1)[(sep)(Location2)]
     *
     * Where [] denotes optional, () denotes a field to fill, \t is a
     * tab character, and (sep) is one of: " and " "\t" ", " or "/"
     *
     * Examples:
     *     Washington, George\tGeorge's Office, George's House
     *     Starbucks\tstarbucks
     *
     * @param reader a Reader to parse from
     */
    public void importTSV(Reader reader) throws IOException {
        try(BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine();
            while (line != null) {
                LinkedList<String> parts =
                    new LinkedList<String>(Arrays.asList(line.split("\t")));
                String name = parts.removeFirst();
                String title = "";
                LinkedList<Room> entryRooms = new LinkedList<Room>();
                LinkedList<String> roomNames = new LinkedList<String>();

                // Extract a title if it exists
                List<String> nameParts = Arrays.asList(name.split(", "));
                if (nameParts.size() > 2) {
                    name = String.join(", ", nameParts.subList(0, 2));
                    title = String.join(", ", nameParts.subList(2, nameParts.size()));
                }

                // split rooms list by various separators
                for (String part : parts) {
                    roomNames.addAll(Arrays.asList(part.split(" and |/|, ")));
                }

                for (String roomName : roomNames) {
                    Room room = getRoom(roomName);
                    if (room == null) { // room did not exist, make a new one
                        room = new Room(null, roomName);
                        addRoom(room);
                    }
                    entryRooms.add(room);
                }
                // TODO: handle existing entries
                addEntry(new DirectoryEntry(name, title, entryRooms, ""));

                line = br.readLine();
            }
        }
    }

    /**
     *
     * @param roomName
     * @param location
     * @return false if the room cannot be set to the given location
     */
    public boolean setRoomLocation(String roomName, GraphNode location) {
        Room roomAtNode = this.getRoom(location);
        if (roomAtNode != null) {
            logger.debug("Tried to set room location but there is already a room at the given node");
            return false;
        }
        Room roomToBeChanged = this.getRoom(roomName);
        if (roomToBeChanged == null) {
            logger.debug("Tried to set room location but room with name {} was not found", roomName);
            return false;
        }

        roomToBeChanged.setLocation(location);
        return true;
    }
}
