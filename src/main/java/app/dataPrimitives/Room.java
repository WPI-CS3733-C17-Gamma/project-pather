package app.dataPrimitives;

import app.Ided;
import app.datastore.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class Room extends Ided implements Comparable {
    final Logger logger = LoggerFactory.getLogger(Room.class);

    GraphNode location;
    String name;

    List<DirectoryEntry> entries = new LinkedList<>();

    /**
     * Create a room without a location
     * @param name
     */
    public Room (String name) {
        this(null, name);
    }

    /**
     *
     * @param location graph node attached to the room
     * @param name : name of the room
     */
    public Room(GraphNode location, String name){
        this.location = location;
        this.name = name;
    }

    public GraphNode getLocation(){
        return this.location;
    }

    public void setLocation(GraphNode loc){
        this.location = loc;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public void addEntry(DirectoryEntry entry){
        entries.add(entry);
    }

    public List<DirectoryEntry> getEntries(){
        return this.entries;
    }

    /**
     * Compare the name of the room lexicographically so that the Collection.sort method can sort rooms alphabetically
     * @param room the given room that needs to be compared to the current instance of the object
     * @return negative if this is smaller than the given room, otherwise positive
     */
    @Override
    public int compareTo(Object room){
        try {
            Room p = (Room) room;
            logger.debug("Comparing rooms: {}\t{}", p.name, this.name);
            return(this.name.compareTo(p.name));
        }
        catch(ClassCastException e) {
            throw e;
        }

    }

    /**
     * Determine if the two room objects are equal by their attributes
     * @param obj the room that needs to be checked against this current instance of the object
     * @return true if rooms share the same attributes
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Room)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Room rhs = (Room) obj;
        if (this.location == null) {
            return this.name.equals(rhs.name);
        }
        else {
            return this.location.equals(rhs.location) &&
                this.name.equals(rhs.name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
