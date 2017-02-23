package app;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu {
    final Logger logger = LoggerFactory.getLogger(CircularContextMenu.class);

    int innerRadius;
    int outerRadius;
    private List<ContextMenuElement> menuElements = new LinkedList<>();
    List<Node> drawnItems = new LinkedList<>();
    AnchorPane pane;

    /**
     * Default Constructor
     */
    public CircularContextMenu(){
        this.innerRadius = 50;
        this.outerRadius = 100;
        menuElements = new LinkedList<>();
    }

    /**
     * Having innerRadius > outerRadius gives interesting shapes. Try it out!
     * @param innerRadius Inner radius of Context Menu
     * @param outerRadius Outer Radius of Context Menu
     */
    CircularContextMenu( int innerRadius, int outerRadius){
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        menuElements = new LinkedList<>();
    }

    /**
     * Adds an option to the context menu. The option does not work for places where the paint is transparent. Use solid paints for now
     * @param image Fill for menu
     * @param handler Actioned to be performe by optiion
     */
    public void addOption(Paint image, EventHandler handler){
        int size = menuElements.size();
        double angle = (double)360 / (double)(size+1);
        double currentAngle = 0;

        menuElements.add(new ContextMenuElement(image, handler));

        for(ContextMenuElement element:menuElements) {//redraw elements when a new one is added
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
            }catch(IllegalArgumentException e){
                logger.debug("Angle parameters must be positive with angle > initialAngle."
                + "They were CurrentAngle: {}, angle {}", currentAngle, angle);
            }
            currentAngle = currentAngle + angle;
        }
    }

    /**
     * Adds an option that does not do anything
     * @param image
     */
    public void addOption(Paint image){//redraw elements when a new one is added
        int size = menuElements.size();
        double angle = (double)360 / (double)(size+1);
        double currentAngle = 0.0;

        menuElements.add(new ContextMenuElement(image));

        for(ContextMenuElement element:menuElements) {
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
            }catch(IllegalArgumentException e){
                logger.debug("Angle parameters must be positive with angle > initialAngle."
                    + "They were CurrentAngle: {}, angle {}", currentAngle, angle);
            }

            currentAngle = currentAngle + angle;
        }
        logger.debug("Current angel: {}", currentAngle);
    }

    /**
     * Removes an option by index
     * @param index
     */
    public void removeOption(int index){//redraw elements when a new one is added
        int size = menuElements.size();
        if(size > 0 && index < size) {
            menuElements.remove(index);
            double angle = 360 / (size - 1);
            double currentAngle = 0;


            for (ContextMenuElement element : menuElements) {
                try {
                    element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
                } catch (IllegalArgumentException e) {
                    logger.debug("Angle parameters must be positive with angle > initialAngle."
                        + "They were CurrentAngle: {}, angle {}", currentAngle, angle);
                }

                currentAngle = currentAngle + angle;
            }
        }
    }

    /**
     * Get the number of options currently in menu
     * @return
     */
    public int getNumOptions(){
        return menuElements.size();
    }

    /**
     * Shows the context menu in a given location on the anchor Pane. Can only show one one anchor pane at a time
     * @param pane
     * @param layoutX
     * @param layoutY
     * @param radius
     */
    public void show(AnchorPane pane, int layoutX, int layoutY, int radius){
        this.pane = pane;

        if( radius > 0){
            Circle circ = new Circle(radius);
            pane.getChildren().add(circ);
            circ.setLayoutX(layoutX);
            circ.setLayoutY(layoutY);
            drawnItems.add(circ);
        }
        for (ContextMenuElement element: menuElements) {
            element.path.setLayoutX(layoutX);
            element.path.setLayoutY(layoutY);
            pane.getChildren().add(element.path);
            drawnItems.add(element.path);
        }
    }

    /**
     * Getter for Menu Elements
     * @return
     */
    public List<ContextMenuElement> getMenuElements(){
        return menuElements = new LinkedList<>();
    }

    public String toString(){
        return("This menu has: " + menuElements.size() + " options.");
    }
}

