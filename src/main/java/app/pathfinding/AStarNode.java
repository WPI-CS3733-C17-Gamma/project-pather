package app.pathfinding;

import app.dataPrimitives.GraphNode;

public class AStarNode implements Comparable {
    GraphNode node;
    public double fScore = 1000000;
    public double gScore = 1000000;

    AStarNode cameFrom;

    public AStarNode(GraphNode graphnode){
        this.node = graphnode;
    }


    public double getDistance(AStarNode other){
        return this.node.distance(other.node);
    }

    @Override
    public String toString(){
        return this.node.toString();
    }

    @Override
    public boolean equals(Object other){
        AStarNode node = (AStarNode) other;
        return(this.node.equals(node.node));
    }

    @Override
    public int compareTo(Object other) {
        AStarNode rhs = (AStarNode) other;
        return(Double.compare(this.fScore, rhs.fScore));
    }

    public GraphNode getNode() {
        return node;
    }

    public void setNode(GraphNode node) {
        this.node = node;
    }

    public AStarNode getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(AStarNode cameFrom) {
        this.cameFrom = cameFrom;
    }
}
