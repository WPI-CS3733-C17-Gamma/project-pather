/**
 * Created by Saina on 2017-02-07.
 */
public class PATH_NOT_FOUND_EXCEPTION extends Exception{

    GraphNode nodeA;
    GraphNode nodeB;

    PATH_NOT_FOUND_EXCEPTION(GraphNode nodeA, GraphNode nodeB){
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }



}
