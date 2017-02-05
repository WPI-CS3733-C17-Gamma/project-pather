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
}
