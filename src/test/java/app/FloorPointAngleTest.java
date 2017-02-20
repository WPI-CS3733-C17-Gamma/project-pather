package app;

import app.dataPrimitives.FloorPoint;
import junit.framework.TestCase;
import org.junit.Test;

public class FloorPointAngleTest extends TestCase {

	private void testAngle(int aX, int aY,
						   int Bx, int By,
						   int Cx, int Cy,
						   double expectedAngle) {
		assertEquals(expectedAngle,
					 new FloorPoint(aX, aY, "").getAngle(
						 new FloorPoint(Bx, By, ""),
						 new FloorPoint(Cx, Cy, "")), 0.1);
	}

	@Test
	public void testAngles() {
		/* ---
           -td
           -s- */
	    testAngle(1, 1,
				  1, 0,
				  2, 1,
				  90.0);
		/* ---
           dt-
           -s- */
	    testAngle(1, 1,
				  1, 0,
				  0, 1,
				  270.0);
		/* -d-
           -t-
           -s- */
	    testAngle(1, 1,
				  1, 0,
				  1, 2,
				  180.0);
	}

	@Test
	public void testAngles2() {
		/* ---
           st-
           -d- */
	    testAngle(1, 1,
				  0, 1,
				  1, 0,
				  90.0);
		/* ---
           dt-
           -s- */
	    testAngle(1, 1,
				  1, 0,
				  0, 1,
				  270.0);
		/* -d-
           -t-
           -s- */
	    testAngle(1, 1,
				  1, 0,
				  1, 2,
				  180.0);
	}

}
