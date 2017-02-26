package app;

import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;
import app.datastore.Directory;
import app.datastore.GraphNetwork;
import app.datastore.Map;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saahil on 2/13/2017.
 */
public class CreateElevatorTest extends TestCase {

    GraphNetwork network ;
    Map map ;
    ArrayList<String> floors ;
    protected void setUp() {
        floors = new ArrayList<>();
        network = new GraphNetwork();
        map = new Map(null, network);
    }


    @Test
    // Adding elevator to no floor should result in no elevators
    public void testAddElevatorNoFloor () {
        map.addElevator(new FloorPoint(10,12,"floor3"), new ArrayList<>(), GraphNode.ELEVATOR);
        GraphNode floor3El = map.getGraphNode(new FloorPoint (10,12,"floor3"));
        assertEquals(floor3El, null);
    }
    @Test
    // Adding elevator to no floor should result in no elevators
    public void testAddElevatorOneFloor() {
        floors.add("floor3");
        map.addElevator(new FloorPoint(10,12,""), floors, GraphNode.ELEVATOR);
        GraphNode floor3El = map.getGraphNode(new FloorPoint (10,12,"floor3"));

        GraphNode expected = new GraphNode (10,12,"floor3");

        assertEquals(expected, floor3El );
    }

    @Test
    // Check elevators will be added to other floors
    public void testAddElevatorTwoFloor() {
        floors.add("floor3");
        floors.add("floor4");
        map.addElevator(new FloorPoint(10,12,""), floors, GraphNode.ELEVATOR);
        GraphNode floor3El = map.getGraphNode(new FloorPoint (10,12,"floor3"));
        GraphNode floor4E1 = map.getGraphNode(new FloorPoint (10,12,"floor4"));


        GraphNode expected = new GraphNode (10,12,"floor3");
        GraphNode expected2 = new GraphNode (10,12,"floor4");
        expected.addAdjacent(expected2);
        expected2.addAdjacent(expected);
        assertEquals(expected, floor3El);
        assertEquals(expected2, floor4E1);
    }

    @Test
    // check if doesCrossFloor is working
    public void testIsElevator () {
        floors.add("floor3");
        floors.add("floor4");
        map.addElevator(new FloorPoint(10,12,""), floors, GraphNode.ELEVATOR);
        GraphNode floor3El = map.getGraphNode(new FloorPoint (10,12,"floor3"));
        GraphNode floor4E1 = map.getGraphNode(new FloorPoint (10,12,"floor4"));
        assertTrue(floor3El.doesCrossFloor());
    }


    @Test
    public void testGetAdjacentElevators () {
        floors.add("floor3");
        floors.add("floor4");
        map.addElevator(new FloorPoint(10,12,""), floors, GraphNode.ELEVATOR);
        GraphNode floor3El = map.getGraphNode(new FloorPoint (10,12,"floor3"));
        GraphNode floor4E1 = map.getGraphNode(new FloorPoint (10,12,"floor4"));

        ArrayList<GraphNode> adjacentElevators  = new ArrayList<>();
        adjacentElevators.add(floor4E1);

        assertEquals(adjacentElevators, map.getGraphNode(new FloorPoint(10,12, "floor3")).getConnectedElevators());
    }

    @Test
    // delete elevator should delete all adjacent elevators
    public void testDeleteElevator () {
        map.setDirectory(new Directory(new HashMap<>(), new HashMap<>()));
        floors.add("floor3");
        floors.add("floor4");
        map.addElevator(new FloorPoint(10,12,""), floors, GraphNode.ELEVATOR);

        // delete the elevator on floor three
        map.deleteElevator(map.getGraphNode(new FloorPoint(10,12,"floor3")));

        assertEquals(map.getGraphNode(new FloorPoint(10,12,"floor4")), null);



    }


}
