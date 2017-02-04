public class AStarNode {
    GraphNode node;
    double fScore;
    double gScore;
    AStarNode cameFrom;

    public void AStarNode(GraphNode graphnode){
    }

    public double getDistance(AStarNode other){
        return 0;
    }
}
