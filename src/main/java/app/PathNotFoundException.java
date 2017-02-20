package app;

import app.dataPrimitives.GraphNode;

/**
 * Created by Saina on 2017-02-07.
 */
public class PathNotFoundException extends Exception{

    GraphNode nodeA;
    GraphNode nodeB;

    public PathNotFoundException(GraphNode nodeA, GraphNode nodeB){
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }



}
