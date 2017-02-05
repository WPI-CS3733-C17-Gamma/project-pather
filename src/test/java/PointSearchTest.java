import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by Saahil on 2/4/2017.
 */
public class PointSearchTest extends TestCase {
    FloorPoint one , two, three, four;
    // run every test
    protected void setUp() {
       one = new FloorPoint(0,0,"one");
       two = new FloorPoint(3,4,"one");
       three = new FloorPoint(6,8,"one");
       four = new FloorPoint(9,12, "one");
    }

    @Test
    public void testDistance() {
        double distance = one.distance(two);
        assertEquals(5, distance, .01);
    }

    @Test
    public void testGraph (){
        GraphNetwork network = new GraphNetwork();

        network.addNode(new GraphNode(one));
        network.addNode(new GraphNode(two));
        network.addNode(new GraphNode(three));
        network.addNode(new GraphNode(four));

        GraphNode closestToOne = network.getGraphNode(one);

        assertEquals(closestToOne.getLocation(), one);

    }
}
