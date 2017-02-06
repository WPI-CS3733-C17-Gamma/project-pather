import java.util.Objects;

public class Room extends Ided implements Comparable {

    GraphNode location;
    String name;

    public Room(GraphNode location, String name){
        this.location = location;
        this.name = name;
    }

    public void setLocation(GraphNode loc){
    }

    /**
     *
     * @param room
     * @return negative if this is smaller than room, otherwise positive
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
}
