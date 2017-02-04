public class FloorPoint {

    public final int x;
    public final int y;
    public final String floor;

    public FloorPoint(int x, int y, String floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public int compareTo(FloorPoint FloorPoint){
        return 0;
    }


    public String toString(){
        return "x: " + x + " y: " + y + " floor: " + floor;
    }
}


