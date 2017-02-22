package app.CustomMenus;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * Created by dominic on 2/18/17.
 */
public class ContextMenuElement{
    Paint icon;
    SVGPath path = new SVGPath();
    SVGPath background = new SVGPath();
    double outerRadius;
    double innerRadius;
    double angle = 10;
    double initialAngle = 10;
    CircularContextMenu parentMenu;
    CentralDisplay centralDisplay;

    EventHandler<MouseEvent> onClickHandler = new EventHandler<MouseEvent>() { //Default click handler
        @Override
        public void handle(MouseEvent event) {
            System.out.println("This button has no click event");
            parentMenu.hide();
        }
    };
    EventHandler<MouseEvent> mouseEnterHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            centralDisplay.centralDisplay.setFill(path.getFill());
        }
    };
    EventHandler<MouseEvent> mouseExitHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            centralDisplay.centralDisplay.setFill(Color.TRANSPARENT);
        }
    };
    EventHandler onDragHandler = new EventHandler<MouseEvent>() { //Default drag event handler
        @Override
        public void handle(MouseEvent event) {
            System.out.println("This button has no release event");
        parentMenu.hide();
        }
    };



    /**
     * Constructor for a button that does not do anything
     * @param icon
     */
    ContextMenuElement( Paint icon, CircularContextMenu parentMenu, CentralDisplay centralDisplay){
        path.setMouseTransparent(true);
        this.centralDisplay = centralDisplay;
        this.parentMenu = parentMenu;
        this.background.setOnMouseClicked(onClickHandler);
        //this.background.setOnMouseMoved(onClickHandler);
        this.background.setOnMouseEntered(mouseEnterHandler);
        this.background.setOnMouseExited(mouseExitHandler);
        this.icon = icon;

    }

    /**
     * Constructor
     * @param icon
     * @param onClickHandler
     */
    ContextMenuElement (Paint icon, CircularContextMenu parentMenu, CentralDisplay centralDisplay,  EventHandler onClickHandler, EventHandler onDragHandler){
        path.setMouseTransparent(true);
        this.centralDisplay = centralDisplay;
        this.parentMenu = parentMenu;
        this.onClickHandler = onClickHandler;
        this.background.setOnMouseEntered(mouseEnterHandler);
        this.background.setOnMouseExited(mouseExitHandler);
        if (onClickHandler != null) {
            this.background.setOnMouseClicked( onClickHandler);}
        if (onDragHandler != null) {
            this.background.setOnMouseReleased(onDragHandler);}
        this.icon = icon;


    }

    /**
     * Draws contextmenu element starting at an initial angle, arcing through angle degrees
     * @param initialAngle Angle to start drawing from
     * @param angle Angle swept through
     * @param innerRadius inner radius size
     * @param outerRadius Size of element
     */
    public void draw(double initialAngle, double angle, double innerRadius, double outerRadius) throws IllegalArgumentException{
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        double difference = outerRadius - innerRadius;

//         if((angle  < 0.0) || initialAngle < 0.0 ){
//            throw new IllegalArgumentException();
//        }

        this.angle = angle*Math.PI/180.0;
        this.initialAngle = initialAngle*Math.PI/180.0;

    //Calculate x and y coordinates of points used for drawing SVG path
        double point1x = innerRadius*Math.cos(this.initialAngle);
        double point1y = innerRadius*Math.sin(this.initialAngle);

        double point2x = difference*Math.cos(this.initialAngle);
        double point2y  = difference*Math.sin(this.initialAngle);

        double point3x = outerRadius*Math.cos(this.angle + this.initialAngle) - point2x - point1x;
        double point3y = outerRadius*Math.sin(this.angle + this.initialAngle) - point2y - point1y;

        double point4x = -difference*Math.cos(this.angle + this.initialAngle);
        double point4y = -difference*Math.sin(this.angle + this.initialAngle);

        double point5x = -point4x - point3x - point2x;
        double point5y = -point4y - point3y - point2y;

        double originX = -outerRadius;
        double originY = -outerRadius;


        if((this.angle) <= 180.0) {//If the angle swept out is less than or equal to 180, choose the small arc. Doc on paths
            path.setContent("M" + originX + "," + originY + " m" + point1x + "," + point1y +//https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 0,1 " + point3x + "," + point3y + "l " + point4x + "," + point4y +  "a" + innerRadius + "," +
                innerRadius + " 0 0,0 " + point5x + "," + point5y  +" z");
            background.setContent("M" + originX + "," + originY + " m" + point1x + "," + point1y +//https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 0,1 " + point3x + "," + point3y + "l " + point4x + "," + point4y + "a" + innerRadius + "," +
                innerRadius + " 0 0,0 " + point5x + "," + point5y +" z");
        }
        else {
            path.setContent("M" + originX + "," + originY + " m" + point1x + "," + point1y +
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 1,1 " + point3x + "," + point3y + " l " + point4x + "," + point4y + "a" + innerRadius + "," +
                innerRadius + " 0 1,1 " + point5x + "," + point5y + " z");
            background.setContent("M" + originX + "," + originY + " m" + point1x + "," + point1y +
                " l " + point2x + "," + point2y + "a" + outerRadius + "," +
                outerRadius + " 0 1,1 " + point3x + "," + point3y + " l " + point4x + "," + point4y + "a" + innerRadius + "," +
                innerRadius + " 0 1,1 " + point5x + "," + point5y + " z");
        }


        path.setFill(icon);
        background.setFill(Color.rgb(211, 211, 211, 1));
    }

    public void fireEvent(Event event){
//        MouseEvent mouseEvent = (MouseEvent) event;
//        double x = mouseEvent.getScreenX();
//        double y = mouseEvent.getScreenY();
//        x = x - this.parentMenu.getX();
//        y = y - this.parentMenu.getY();
//        System.out.println("x: " + x + "y: " + y);
//        System.out.println(path.getLayoutX() + " : " + path.getLayoutY());
//        if(path.contains(x,y))
            path.fireEvent(event);
//        System.out.println("works");
    }
}
