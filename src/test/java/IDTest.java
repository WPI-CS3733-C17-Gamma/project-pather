import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IDTest extends TestCase{

    // run every test
    protected void setUp() {
    }

    @Test
    public void testId() {
        ArrayList<GraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nodes.add(new GraphNode(i,i,""));
        }
        List<Long> vals = nodes.stream().map(el -> el.id).collect(Collectors.toList());

        vals.sort((Long l ,Long r)->l.compareTo(r));

        boolean duplicate = false;
        for (int i = 0; i < vals.size() - 1; i++) {
            if(vals.get(i).longValue() == vals.get(i+1).longValue()){
               duplicate = true;
            }
        }

        assertFalse(duplicate);

    }
}
