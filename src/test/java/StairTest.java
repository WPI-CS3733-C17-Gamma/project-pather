import app.dataPrimitives.GraphNode;
import app.pathfinding.AStar;
import app.pathfinding.IPathFindingAlgorithm;
import app.pathfinding.PathNotFoundException;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import app.dataPrimitives.GraphNode;
import junit.framework.TestCase;

import static org.junit.Assert.assertTrue;


public class StairTest extends TestCase {
    GraphNode start;
    GraphNode end;
    GraphNode elevator;
    GraphNode stair;
    IPathFindingAlgorithm astar = new AStar();

    public void setUp() {
        start = new GraphNode (0,0,"1",GraphNode.NONE) ;
        elevator = new GraphNode (1,1,"1", GraphNode.ELEVATOR);
        stair = new GraphNode (100,100,"1", GraphNode.STAIR);
        end = new GraphNode (2,2,"2", GraphNode.NONE);

        start.addAdjacent(elevator);
        start.addAdjacent(stair);
        stair.addAdjacent(end);
        elevator.addAdjacent(end);
    }


    @Test
    public void testGetPathNormal () throws PathNotFoundException {
        List<GraphNode> path = new LinkedList(Arrays.asList(start, elevator, end));
        assertTrue(path.equals(astar.findPath(start, end, false)));
    }

    @Test
    public void testGetPathStairs () throws PathNotFoundException {
        List<GraphNode> path = new LinkedList(Arrays.asList(start, stair, end));
        assertTrue(path.equals(astar.findPath(start, end, true)));
    }
}
