import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu {
    List<ContextMenuElement> menuElements = new LinkedList<>();

    CircularContextMenu(){
        menuElements = new LinkedList<>();
    }

    public void addOption(Image image, EventHandler handler){
        int size = menuElements.size();
            int angle = 360 / (size+1);
        menuElements.add(new ContextMenuElement(angle, angle, image,50, 100));

    }
    public void addOption(Image image){
        int size = menuElements.size();
        int angle = 360 / (size+1);
        int currentAngle = 0;
        menuElements.add(new ContextMenuElement(currentAngle, currentAngle, image, 50, 100));

        for(ContextMenuElement element:menuElements) {
            element.resize(currentAngle, currentAngle, 50, 100);
            currentAngle += angle;
        }

    }

    public void show(AnchorPane pane){
        int numOptions = menuElements.size();
        int angle = 360 / numOptions;
        int currentAngle = 0;
        int roatationAngle = 0;

        double x;
        double y;
        for (ContextMenuElement element: menuElements) {
            System.out.println("x: " +element.path.getLayoutX() + "y: " +element.path.getLayoutY());

            roatationAngle += 360/(numOptions - 1);

//            element.path.setRotate(currentAngle);
//            x =  element.outerRadius*Math.cos(currentAngle*Math.PI/360);
//            y =  element.outerRadius*Math.sin(currentAngle*Math.PI/360);
//            System.out.println("x: " + x + "y: " + y);
            element.path.setLayoutX(300);
            element.path.setLayoutY(300);
//            currentAngle += angle;
//            element.resize(angle, 150, 100);
            pane.getChildren().add(element.path);
            System.out.println("The angle is "+currentAngle);
//            System.out.println("after x: " + x + "y: " + y);
        }
    }

    public String toString(){
        System.out.println("Number of Elements" + menuElements.size());
        return("Number of Elements" + menuElements.size());
    }
}

