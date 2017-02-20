package app;

import app.dataPrimitives.GraphNode;
import app.datastore.GraphNetwork;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.LinkedList;

/**
 * Created by Bella on 2/6/17.
 */
public class DeleteNodeTest extends TestCase {

    GraphNode dummyNode;
    GraphNode dummyNode2;
    GraphNetwork dummynetwork;
    GraphNetwork dummynetwork1;

    @Override
    protected void setUp(){
        dummyNode = new GraphNode(1,2, "floor1");
        dummyNode2 = new GraphNode(2, 3, "floor2");
        dummynetwork = new GraphNetwork(new LinkedList<GraphNode>());
        dummynetwork1 = new GraphNetwork(new LinkedList<GraphNode>());
    }

    @Test
    public void testDelete() {
        dummynetwork.addNode(dummyNode);
        dummynetwork.addNode(dummyNode2);
        dummynetwork1.addNode(dummyNode2);
        dummynetwork.deleteNode(dummyNode);

        assertEquals(dummynetwork, dummynetwork1);
    }
}
