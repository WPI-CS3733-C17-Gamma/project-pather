package app;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dominic on 2/18/17.
 */
public class ContextMenuElement{
    final Logger logger = LoggerFactory.getLogger(ContextMenuElement.class);

    Paint icon;
    SVGPath path = new SVGPath();
    int outerRadius = 150;
    int innerRadius = 100;
    double angle = 10;
    double initialAngle = 10;
    CircularContextMenu parentMenu = new CircularContextMenu();



    EventHandler<MouseEvent> onClickHandler = new EventHandler<MouseEvent>() { //Default Event Handler
        @Override
        public void handle(MouseEvent event) {
            logger.debug("Clicked a button with no event");
        }
    };

    /**
     * Constructor for a button that does not do anything
     * @param icon
     */
    ContextMenuElement( Paint icon){
        this.path.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
        this.icon = icon;

    }

    /**
     * Constructor
     * @param icon
     * @param onClickHandler
     */
    ContextMenuElement( Paint icon, EventHandler onClickHandler){
        this.onClickHandler = onClickHandler;
        this.path.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
        this.icon = icon;
    }

    /**
     * Draws contextmenu element starting at an initial angle, arcing through angle degrees
     * @param initialAngle Angle to start drawing from
     * @param angle Angle swept through
     * @param innerRadius inner radius size
     * @param outerRadius Size of element
     */
    public void draw(double initialAngle, double angle, int innerRadius, int outerRadius) throws IllegalArgumentException{
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        int difference = outerRadius - innerRadius;

        if((angle  < 0) || initialAngle < 0 ){
            throw new IllegalArgumentException();
        }

        this.angle = angle*Math.PI/180.0;
        this.initialAngle = initialAngle*Math.PI/180.0;

    //Calculate x and y coordinates of points used for drawing SVG path
        double point1x = innerRadius*Math.cos(this.initialAngle);
        double point1y = innerRadius*Math.sin(this.initialAngle);

        double point2x = difference*Math.cos(this.initialAngle);
        double point2y = difference*Math.sin(this.initialAngle);

        double point3x = outerRadius*Math.cos(this.angle + this.initialAngle) - point2x - point1x;
        double point3y = outerRadius*Math.sin(this.angle + this.initialAngle) - point2y - point1y;

        double point4x = -difference*Math.cos(this.angle + this.initialAngle);
        double point4y = -difference*Math.sin(this.angle + this.initialAngle);


        if((this.angle) <= 180.0) {//If the angle swept out is less than or equal to 180, choose the small arc. Doc on paths
            path.setContent("M0,0 m" + point1x + "," + point1y +//https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 0,1 " + point3x + "," + point3y + "l " + point4x + "," + point4y + " z");
        }
        else {
            path.setContent("M0,0 m" + point1x + "," + point1y +
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 1,1 " + point3x + "," + point3y + " l " + point4x + "," + point4y + " z");
        }


        path.setFill(icon);
    }


}
