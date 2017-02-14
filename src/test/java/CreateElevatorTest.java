import junit.framework.TestCase;

/**
 * Created by Saahil on 2/13/2017.
 */
public class CreateElevatorTest extends TestCase {

    GraphNetwork network ;
    Map map ;
    protected void setUp() {
        network = new GraphNetwork();
        map = new Map(null, network, null);
    }

}
