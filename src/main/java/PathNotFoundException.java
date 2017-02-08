/**
 * Created by Saina on 2017-02-07.
 */
public class PathNotFoundException extends Exception{

    GraphNode nodeA;
    GraphNode nodeB;

    PathNotFoundException(GraphNode nodeA, GraphNode nodeB){
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }



}
