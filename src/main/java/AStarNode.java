public class AStarNode{
    GraphNode node;
    public double fScore = 1000000;
    public double gScore = 1000000;
    AStarNode cameFrom;


    AStarNode(GraphNode graphnode){
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
}
