public class FloorPoint implements Comparable{

    public final int x;
    public final int y;
    public final String floor;

    public FloorPoint(int x, int y, String floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    public String getFloor() {
        return floor;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloorPoint)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        FloorPoint rhs = (FloorPoint) obj;
        return this.x == rhs.x &&
               this.y == rhs.y &&
            this.floor.equals(rhs.floor);
    }

    public String toString(){
        return "x: " + x + " y: " + y + " floor: " + floor;
    }
}


