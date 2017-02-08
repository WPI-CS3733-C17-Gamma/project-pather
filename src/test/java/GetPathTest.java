import junit.framework.TestCase;
import org.junit.Test;

import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * Created by dominic on 2/5/17.
 */

public class GetPathTest extends TestCase{

    GraphNode node1 = new GraphNode(0, 0, "");
    GraphNode node2 = new GraphNode(10, 0, "");
    GraphNode node3 = new GraphNode(20, 10, "");
    GraphNode node4 = new GraphNode(20, 20, "");
    GraphNode node5 = new GraphNode(30, 20, "");

    GraphNode node11 = new GraphNode(10, 10, "");
    GraphNode node14 = new GraphNode(10, 40, "");
    GraphNode node15 = new GraphNode(10, 50, "");
    GraphNode node34 = new GraphNode(30, 40, "");
    GraphNode node35 = new GraphNode(30, 50, "");
    GraphNode node45 = new GraphNode(40, 50, "");
    GraphNode node55 = new GraphNode(50, 50, "");
    GraphNode node53 = new GraphNode(50, 30, "");
    GraphNode node51 = new GraphNode(50, 10, "");
    GraphNode node41 = new GraphNode(40, 10, "");
    GraphNode node43 = new GraphNode(40, 30, "");
    GraphNode node33 = new GraphNode(30, 30, "");
    GraphNode node31 = new GraphNode(30, 10, "");
    GraphNode node23 = new GraphNode(20, 30, "");
    GraphNode node22 = new GraphNode(20, 20, "");

    LinkedList<GraphNode> nodes =
        new LinkedList<>(Arrays.asList(node1, node2, node3, node4, node5));
    GraphNetwork graph = new GraphNetwork(nodes);

    LinkedList<GraphNode> betterNodes =
        new LinkedList<>(Arrays.asList(node11, node14, node15, node34, node35,
                                       node45, node55, node53, node51, node41,
                                       node43, node33, node31, node23, node22));
    GraphNetwork betterGraph = new GraphNetwork(betterNodes);

    public void setUp() {
        node1.addAdjacent(node2);
        node1.addAdjacent(node3);
        node2.addAdjacent(node1);
        node2.addAdjacent(node4);
        node3.addAdjacent(node1);
        node3.addAdjacent(node4);
        node4.addAdjacent(node2);
        node4.addAdjacent(node3);
        node4.addAdjacent(node5);
        node5.addAdjacent(node4);

        node11.addAdjacent(node31);
        node11.addAdjacent(node14);

        node14.addAdjacent(node15);
        node14.addAdjacent(node22);
        node14.addAdjacent(node11);

        node15.addAdjacent(node34);
        node15.addAdjacent(node14);

        node34.addAdjacent(node35);
        node34.addAdjacent(node33);
        node34.addAdjacent(node15);

        node35.addAdjacent(node34);
        node35.addAdjacent(node45);

        node45.addAdjacent(node55);
        node45.addAdjacent(node53);
        node45.addAdjacent(node35);

        node55.addAdjacent(node45);

        node53.addAdjacent(node45);
        node53.addAdjacent(node51);
        node53.addAdjacent(node43);

        node51.addAdjacent(node53);
        node51.addAdjacent(node41);

        node41.addAdjacent(node43);
        node41.addAdjacent(node51);

        node43.addAdjacent(node33);
        node43.addAdjacent(node53);
        node43.addAdjacent(node41);

        node33.addAdjacent(node31);
        node33.addAdjacent(node23);
        node33.addAdjacent(node34);
        node33.addAdjacent(node43);

        node31.addAdjacent(node11);
        node31.addAdjacent(node33);

        node23.addAdjacent(node22);
        node23.addAdjacent(node33);

        node22.addAdjacent(node23);
        node22.addAdjacent(node14);
    }

    public void testGetPath() {
        LinkedList<GraphNode> path1 = new LinkedList(Arrays.asList(node5, node4, node2, node1));
        for(GraphNode neighbour: nodes){
            System.err.println(neighbour.toString());
        }
        LinkedList<GraphNode> path2 = new LinkedList<>();
        try {path2 = graph.getPath(node1, node5);}
        catch(PathNotFoundException e){
            assertTrue(false);
        }
        assertTrue(path1.equals(path2));
    }


    public void testGetPath2() {
        LinkedList<GraphNode> path1 = new LinkedList(Arrays.asList(node35, node45, node53, node51));
//        for(GraphNode neighbour: betterNodes) {
//            System.err.println(neighbour.toString());
//        }
        LinkedList<GraphNode> path2 = new LinkedList<>();
        try {path2 = betterGraph.getPath(node51, node35);}
        catch (PathNotFoundException e){
            assertTrue(false);
        }
        for(GraphNode neighbour: path2) {
            System.err.println(neighbour.toString());
        }
        assertTrue(path1.equals(path2));
    }


    public void testGetPath3() {
        LinkedList<GraphNode> path1 = new LinkedList(Arrays.asList(node55, node45, node35, node34,
                                                                   node33, node31, node11));
//        for(GraphNode neighbour: betterNodes) {
//            System.err.println(neighbour.toString());
//        }
        LinkedList<GraphNode> path2 = new LinkedList<>();
        try {path2 = betterGraph.getPath(node11, node55);}
        catch (PathNotFoundException e){
            assertTrue(false);
        }
        for(GraphNode neighbour: path2) {
            System.err.println(neighbour.toString());
        }
        assertTrue(path1.equals(path2));

    }



    public void testGetPath4a() {
        LinkedList<GraphNode> path1 = new LinkedList(Arrays.asList(node33, node34, node15, node14));
        for(GraphNode neighbour: betterNodes) {
            System.err.println(neighbour.toString());
        }
        LinkedList<GraphNode> path2 = new LinkedList<>();
        try{path2 = betterGraph.getPath(node14, node33);}
        catch(PathNotFoundException e){
            assertTrue(false);
        }
        assertTrue(path1.equals(path2));
    }

    @Test(expected = PathNotFoundException.class)
    public void testPathNotFoundException(){
        Throwable e = null;
        try{
            graph.getPath(node1, node11);
        }catch (Throwable ex){
            e = ex;
        }
        assertTrue(e instanceof PathNotFoundException);
    }
}
