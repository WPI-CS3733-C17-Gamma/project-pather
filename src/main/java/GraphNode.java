import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphNode extends Ided implements Comparable {

    List<GraphNode> adjacent;
    FloorPoint location;

    /**
     * construct a node with no adjacent nodes
     * params are passed to a constructed FloorPoint
     * @param x the X coordinate
     * @param y the y coordinate
     * @param floor the floor name
     */
    public GraphNode(int x, int y, String floor){
        this.location = new FloorPoint(x, y, floor);
        adjacent = new LinkedList<GraphNode>();
    }

    /**
     * construct a node with no adjacent nodes
     * @param location location for the new node
     */
    public GraphNode(FloorPoint location){
        this.location = location;
        this.adjacent = new LinkedList<GraphNode>();
    }

    public List<GraphNode> getAdjacent(){
        return adjacent;
    }

    /**
     * add a connection to node if none exists, and node is not not this
     * @param node
     * @return
     */
    public boolean addAdjacent(GraphNode node){
        // add adjacent  node if the connection does not exist and the
        // connection is different from this
        if (adjacent.contains(node) || this.location.equals(node.location)) {
            return false;
        }
        else {
            adjacent.add(node);
            return true;
        }
    }

    public boolean removeAdjacent(GraphNode node){
        adjacent.remove(node);
        return true;
    }

    public int compareTo(GraphNode other){
        return 0;
    }

    public FloorPoint getLocation() {
        return location;
    }

    /**
     * return cartisian distance to another graph node
     * @param graphNode
     * @return
     */
    public double distance (GraphNode graphNode){
        return this.location.distance(graphNode.location);
    }

    /** See method {@link FloorPoint#getAngle(FloorPoint, FloorPoint)} */
    public double getAngle(GraphNode pB, GraphNode pC) {
        return this.location.getAngle(pB.location, pC.location);
    }


        @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public int hashCode() {
        return location.x + 10 * location.y + 100 * location.floor.hashCode() ;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphNode)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        GraphNode rhs = (GraphNode) obj;

        // Check all adjacent points locations are equal
        boolean allFpEqual = false;
        // if both are null, they are equal
        if (this.adjacent == null && rhs.adjacent == null) {
            allFpEqual = true;
        }
        // If both are not null, and the same length, check if their
        // contents match
        else if (this.adjacent != null && rhs.adjacent != null &&
                 this.adjacent.size() == rhs.adjacent.size()) {
            allFpEqual = true;
            // For each adjacent here...
            for (GraphNode graphNode : this.adjacent) {
                boolean fpEqual = false;
                // check for an adjacent there
                for (GraphNode rhsGraphNode : rhs.adjacent) {
                    // We have found a corresponding point
                    if (graphNode.location.equals(rhsGraphNode.location)) {
                        fpEqual = true;
                        break;
                    }
                }
                // becomes false if any are false
                allFpEqual = allFpEqual && fpEqual;
            }
        }
        return this.location.equals(rhs.location) && allFpEqual;
    }


    /**
     * Return true when this node is attached to another node on a different floor
     * @return
     */
    public boolean isElevator () {
        boolean isElev = false;
        for (GraphNode adj : adjacent) {
            if ( ! adj.getLocation().getFloor().equals(this.getLocation().getFloor())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Get list of nodes that are connected by elevator (not including self)
     * @return
     */
    public List<GraphNode> getConnectedElevators () {
        List<GraphNode> elevators = new ArrayList<GraphNode> () ;

        for (GraphNode adj : adjacent) {
            if ( ! adj.getLocation().getFloor().equals(this.getLocation().getFloor())) {
                elevators.add(adj);
            }
        }
        return elevators;
    }


    public String toString(){

        String s = "<Node at " + location.toString() + "> adj : " ;
        for (GraphNode adj : this.adjacent) {
            s += adj.location.toString() + ", " ;
        }

        return s;
    }
}
