package app.dataPrimitives;

import app.Ided;

public class Room extends Ided implements Comparable {

    GraphNode location;
    String name;

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

    /**
     * Compare the name of the room lexicographically so that the Collection.sort method can sort rooms alphabetically
     * @param room the given room that needs to be compared to the current instance of the object
     * @return negative if this is smaller than the given room, otherwise positive
     */
    @Override
    public int compareTo(Object room){
        try {
            Room p = (Room) room;
            System.out.println(p.name + "\t" + this.name);
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
        return this.location.equals(rhs.location) &&
            this.name.equals(rhs.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
