package app.dataPrimitives;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a path for a given floor.
 * Used when the patients path crosses over multiple floors
 */
public class SubPath {
    String floor;
    List<GraphNode> path;

    /**
     * Construct sub path
     * @param floor
     */
    public SubPath (String floor) {
        this.floor = floor;
        this.path = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SubPath) {
            SubPath other = (SubPath) obj;
            return this.floor.equals(other.floor) && this.path.equals(other.path);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        String s = "floor: " + floor+  " , path { ";
        for (GraphNode node : path) {
            s += "\n\t" + node.toString() ;
        }
        s += "\n}";
        return s;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public List<GraphNode> getPath() {
        return path;
    }

    public void setPath(List<GraphNode> path) {
        this.path = path;
    }
}
