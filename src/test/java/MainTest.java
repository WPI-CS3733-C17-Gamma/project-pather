import junit.framework.*; 
import org.junit.Test; 
import org.junit.Ignore; 
import static org.junit.Assert.assertEquals; 


public class MainTest extends TestCase {

    // run every test 
    protected void setUp() {
	System.out.println("Set up test");
    }

    @Test
    public void testFirst() {
	int x = 1;
	x = 2; 
	assertEquals(x , 2); 
    }

}
