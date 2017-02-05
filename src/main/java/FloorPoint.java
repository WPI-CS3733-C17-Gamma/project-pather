public class FloorPoint implements Comparable{

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

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    /**
     * return the cartisian distance between another point
     * @param point
     * @return
     */
    public double distance (FloorPoint point) {
        return Math.sqrt(point.x * point.x + point.y * point.y);
    }

    public String toString(){
        return "x: " + x + " y: " + y + " floor: " + floor;
    }
}


