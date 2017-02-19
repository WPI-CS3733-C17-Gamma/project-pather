import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu {
    int innerRadius;
    int outerRadius;
    private List<ContextMenuElement> menuElements = new LinkedList<>();
    List<Node> drawnItems = new LinkedList<>();

    CircularContextMenu(){
        this.innerRadius = 50;
        this.outerRadius = 100;
        menuElements = new LinkedList<>();
    }

    CircularContextMenu(int innerRadius, int outerRadius){
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        menuElements = new LinkedList<>();
    }

    public void addOption(Paint image, EventHandler handler){
        int size = menuElements.size();
        double angle = 360 / (size+1);
        double currentAngle = 0;

        menuElements.add(new ContextMenuElement(image, handler));

        for(ContextMenuElement element:menuElements) {//redraw elements when a new one is added
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive with angle > initialAngle.");
            }
            currentAngle = currentAngle + angle;
        }

    }
    public void addOption(Paint image){//redraw elements when a new one is added
        int size = menuElements.size();
        double angle = 360 / (size+1);
        double currentAngle = 0;

        menuElements.add(new ContextMenuElement(image));

        for(ContextMenuElement element:menuElements) {
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive");
            }

            currentAngle = currentAngle + angle;
        }

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
    public void show(AnchorPane pane, int layoutX, int layoutY, int radius){
        if( radius > 0){
            Circle circ = new Circle(radius);
            pane.getChildren().add(circ);
            circ.setLayoutX(layoutX);
            circ.setLayoutY(layoutY);
            drawnItems.add(circ);
        }
        int i = 0;
        for (ContextMenuElement element: menuElements) {
            System.out.println(i++);
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

