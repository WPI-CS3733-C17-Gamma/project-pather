package app.CustomMenus;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu extends Popup {
    double innerRadius = 50;
    double outerRadius = 100;
    private List<ContextMenuElement> menuElements = new LinkedList<>();
    Group root;
    Scene scene;
    ContextMenuElement highlight = new ContextMenuElement(Color.rgb(41, 191,191),this,null);
    double angle;
    CentralDisplay display = new CentralDisplay(innerRadius,outerRadius);

    /**
     * Default Constructor
     */
    public CircularContextMenu(){
        highlight.background.setMouseTransparent(true);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Saw release");
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawHighlight(event);
            }
        });
        root = new Group();
        scene = new Scene(root,outerRadius,outerRadius);

        setAutoHide(true);
    }

    /**
     * @param innerRadius Inner radius of Context Menu
     * @param outerRadius Outer Radius of Context Menu
     */
    public CircularContextMenu( double innerRadius, double outerRadius){
        highlight.background.setMouseTransparent(true);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        root = new Group();
        scene = new Scene(root,outerRadius,outerRadius);
        display = new CentralDisplay(innerRadius,outerRadius);
        setAutoHide(true);
    }

    /**
     * Adds an option to the context menu.
     * @param image Fill for menu
     * @param clickHandler Actioned to be performe by optiion
     * @param dragHandler Actioned to be performe by optiion
     */
    public void addOption(Paint image, EventHandler clickHandler, EventHandler dragHandler){
        int size = menuElements.size();
        angle = (double)360 / (double)(size+1);
        double currentAngle = 0;

        menuElements.add(new ContextMenuElement(image, this, display, clickHandler, dragHandler));
        getContent().clear();

        for(ContextMenuElement element:menuElements) {
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
                getContent().add(element.background);
                getContent().add(element.path);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive");
                e.printStackTrace();
            }

            currentAngle = currentAngle + angle;
        }
        getContent().add(display.centralDisplay);
    }

    /**
     * Adds an option that does not do anything
     * @param image
     */
    public void addOption(Paint image){//redraw elements when a new one is added
        int size = menuElements.size();
        angle = (double)360 / (double)(size+1);
        double currentAngle = 0.0;

        menuElements.add(new ContextMenuElement(image, this, display));
        getContent().clear();

        for(ContextMenuElement element:menuElements) {
            try{
                element.draw(currentAngle, angle, this.innerRadius, this.outerRadius);
                getContent().add(element.background);
                getContent().add(element.path);
            }catch(IllegalArgumentException e){
                System.out.println("Angle parameters must be positive");
                e.printStackTrace();
            }

            currentAngle = currentAngle + angle;
        }
        getContent().add(display.centralDisplay);
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

    public String toString(){
        return("This menu has: " + menuElements.size() + " options.");
    }

    public void drawHighlight(MouseEvent event){
        if(highlight != null) {
            getContent().remove(highlight.path);
        }

        double x = event.getSceneX() - outerRadius;
        double y = event.getSceneY() - outerRadius;
        double dist = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        double mouseAngle = Math.atan(y/x);

        mouseAngle = (mouseAngle + 2*Math.PI)* 180/Math.PI;
        if(y < 0){
            if(x < 0){
                mouseAngle += 180;
            }else{
                mouseAngle += 360;
            }
        }else {
            if(x < 0){
                mouseAngle += 180;
            }
        }
        while(mouseAngle < 0)
            mouseAngle += 360;

        mouseAngle %= 360;
        if(dist < outerRadius) {
            highlight.draw(mouseAngle - angle / 2, angle, innerRadius, outerRadius);
            super.show(getOwnerNode(), getAnchorX(), getAnchorY());
        }
        redraw();

    }

    /**
     * reraws the context Menu. Icons are drawn on top with highlight below.
     */
    public void redraw(){
        for (ContextMenuElement element: menuElements){
            getContent().remove(element.path);
        }

        getContent().add(highlight.path);
        for (ContextMenuElement element: menuElements) {
            getContent().add(element.path);
        }
    }

}

