import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu extends Popup {
    double innerRadius;
    double outerRadius;
    private List<ContextMenuElement> menuElements = new LinkedList<>();
    Group root;
    Scene scene;
    Circle circ;
    /**
     * Default Constructor
     */
    CircularContextMenu(){

        //this.buildEventDispatchChain(chain);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Saw release");
            }
        });
        this.innerRadius = 50;
        this.outerRadius = 100;
        root = new Group();
        scene = new Scene(root,outerRadius,outerRadius);
        setAutoHide(true);
    }

    /**
     * Having innerRadius > outerRadius gives interesting shapes. Try it out!
     * @param innerRadius Inner radius of Context Menu
     * @param outerRadius Outer Radius of Context Menu
     */
    CircularContextMenu( double innerRadius, double outerRadius){
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        root = new Group();
        scene = new Scene(root,outerRadius,outerRadius);
        setAutoHide(true);
    }

    /**
     * Adds an option to the context menu. The option does not work for places where the paint is transparent. Use solid paints for now
     * @param image Fill for menu
     * @param clickHandler Actioned to be performe by optiion
     * @param dragHandler Actioned to be performe by optiion
     */
    public void addOption(Paint image, EventHandler clickHandler, EventHandler dragHandler){
        int size = menuElements.size();
        double angle = (double)360 / (double)(size+1);
        double currentAngle = 0;
        setAutoHide(true);

        menuElements.add(new ContextMenuElement(image, clickHandler, dragHandler, this));
        getContent().removeAll();

        for(ContextMenuElement element:menuElements) {//redraw elements when a new one is added
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
                getContent().add(element.path);
                getContent().add(element.background);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive");
                e.printStackTrace();
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

        menuElements.add(new ContextMenuElement(image, this));
        getContent().clear();

        for(ContextMenuElement element:menuElements) {
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
                getContent().add(element.path);
                getContent().add(element.background);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive");
                e.printStackTrace();
            }

            currentAngle = currentAngle + angle;
        }
        System.out.println(currentAngle);
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
                    System.out.println("Angle parameters must be positive with angle > initialAngle.");
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
     * Removes context menu from pane
     *
     */
//    public void hide() {
//
//        List<Node> anchorPaneElements = pane.getChildren();
//        for (Node element : drawnItems) {
//            anchorPaneElements.remove(element);
//        }
//        this.pane = null;//Set pane to null since the menu is now hidden
//    }
    /**
     * Getter for Menu Elements
     * @return
     */
    public List<ContextMenuElement> getMenuElements(){
        return menuElements = new LinkedList<>();
    }

    /**
     * Shows contxtmenu
     * @param nodeOwner
     * @param anchorX
     * @param anchorY
     */
    public void show(Node nodeOwner, double anchorX, double anchorY ){
        super.show(nodeOwner, anchorX - outerRadius, anchorY - outerRadius);
        super.requestFocus();
    }

    public void fireEvent(double screenX, double screenY){
        for (ContextMenuElement element: menuElements) {
            element.fireEvent(screenX, screenY);
        }
    }
    public String toString(){
        return("This menu has: " + menuElements.size() + " options.");
    }


}

