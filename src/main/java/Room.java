import java.util.Objects;

public class Room extends Ided implements Comparable {

    GraphNode location;
    String name;

    public Room(GraphNode location, String name){
    }

    public void setLocation(GraphNode loc){
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean equals(Object room) {
        if (room instanceof Room) {
            Room p = (Room) room;
            return (p.location.equals(this.location) && p.name.equals(this.name));
        }
        return false;
    }
}
