package app.dataPrimitives;

public class FloorPoint {

    final int x;
    final int y;
    final String floor;

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

   /**
     * return the cartisian distance between another point
     * @param point
     * @return
     */
    public double distance (FloorPoint point) {
        int diffx = this.x - point.x;
        int diffy = this.y - point.y;
        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy,2));
    }

    /**
     * Get the angle between the given points with this point as the center
     * @param pB the first point
     * @param pC the second point
     * @return the angle
     */
    public double getAngle(FloorPoint pB, FloorPoint pC) {
        double result =  Math.toDegrees(Math.atan2(pC.y - this.y, pC.x - this.x) -
                                        Math.atan2(pB.y - this.y, pB.x - this.x));
        if (result < 0) {
            result += 360;
        }
        return result;
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


