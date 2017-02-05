public class Room extends Ided implements Comparable {

    GraphNode location;
    String name;

    public Room(GraphNode location, String name){
        this.location = location;
        this.name = name;
    }

    public void setLocation(GraphNode loc){
    }

    @Override
    public int compareTo(Object o) {
        return 0;
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
