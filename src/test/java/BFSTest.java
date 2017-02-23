import app.dataPrimitives.GraphNode;
import app.datastore.GraphNetwork;
import app.pathfinding.BFS;
import app.pathfinding.PathNotFoundException;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BFSTest extends TestCase{
    final Logger logger = LoggerFactory.getLogger(BFSTest.class);
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

        List<GraphNode> nodes =
            new LinkedList<>(Arrays.asList(node1, node2, node3, node4, node5));
        GraphNetwork graph = new GraphNetwork((LinkedList)nodes);
        BFS bfs =  new BFS();

        List<GraphNode> betterNodes =
            new LinkedList<>(Arrays.asList(node11, node14, node15, node34, node35,
                node45, node55, node53, node51, node41,
                node43, node33, node31, node23, node22));
        GraphNetwork betterGraph = new GraphNetwork((LinkedList)betterNodes);

        public void setUp() {
            node1.addAdjacent(node2);
            node1.addAdjacent(node3);
            node2.addAdjacent(node1);
            node2.addAdjacent(node3);
            node3.addAdjacent(node1);
            node3.addAdjacent(node4);
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

        @Test
        public void testGetPath() throws PathNotFoundException {
            List<GraphNode> path1 = new LinkedList(Arrays.asList(node1, node3, node4, node5));
            for(GraphNode neighbour: nodes){
                logger.debug("testGetPath neighbour: {}", neighbour.toString());
            }
            List<GraphNode> path2 = new LinkedList<>();
            path2 = bfs.findPath(node1, node5);
            assertTrue(path1.equals(path2));
        }


        @Test
        public void testGetPath2() throws PathNotFoundException{
            LinkedList<GraphNode> path1 = new LinkedList<GraphNode>(Arrays.asList(node51, node53, node45, node35));
            List<GraphNode> path2 = new LinkedList<>();
            path2 = bfs.findPath(node51, node35);
            for(GraphNode neighbour: path2) {
                logger.debug("testGetPath2 neighbour: {}", neighbour.toString());
            }
            assertTrue(path1.equals(path2));
        }


        @Test
        public void testGetPath3() throws PathNotFoundException {
            List<GraphNode> path1 = new LinkedList<GraphNode>(Arrays.asList(node11, node31, node33, node34,
                node35, node45, node55));
            List<GraphNode> path2 = new LinkedList<>();
            path2 = bfs.findPath(node11, node55);
            for(GraphNode neighbour: path2) {
                logger.debug("testGetPath3 neighbour: {}", neighbour.toString());
            }
            assertTrue(path1.equals(path2));

        }

        @Test
        public void testGetPath4a() throws PathNotFoundException {
            List<GraphNode> path1 = new ArrayList<>(Arrays.asList(node14, node22, node23, node33));
            List<GraphNode> path3 = new ArrayList<>(Arrays.asList(node14, node15, node34, node33));
            logger.debug("testGetPath4a testing path {}", path1.toString());
            List<GraphNode> path2 = bfs.findPath(node14, node33);
            for (GraphNode neighbour : path2) {
                logger.debug("testGetPath4a neighbour: {}", neighbour.toString());
            }
            assertTrue(path1.equals(path2) || path2.equals(path3));
        }

    @Test(expected = PathNotFoundException.class)
    public void testPathNotFoundException() {
        Throwable e = null;
        try {
            bfs.findPath(node1, node11);
        } catch (Throwable ex) {
            e = ex;
        }
        assertTrue(e instanceof PathNotFoundException);
    }

        @Test
        public void testStartOnSameNode()throws PathNotFoundException{
            List<GraphNode> expected = new LinkedList<>(Arrays.asList(node1));
            List<GraphNode> result = bfs.findPath(node1, node1);
            assertTrue(expected.equals(result));
        }
    }
