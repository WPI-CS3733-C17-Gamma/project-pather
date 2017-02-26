package app.CustomMenus;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by dominic on 2/18/17.
 */
public class CircularContextMenu extends Popup {
    double innerRadius = 50;
    double outerRadius = 100;
    SVGPath background = new SVGPath();
    private List<ContextMenuElement> menuElements = new LinkedList<>();
    Group root;
    Scene scene;
    ContextMenuElement highlight = new ContextMenuElement(Color.rgb(232, 144,20),Color.rgb(232, 144,20),this,null);
    double angle;
    CentralDisplay display = new CentralDisplay(innerRadius,outerRadius);

    EventHandler<MouseEvent> windowMouseRelease = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            fireOption(event);
            getOwnerWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, windowMouseDrag);
            getOwnerWindow().removeEventFilter(MouseEvent.MOUSE_RELEASED, windowMouseRelease);

        }
    };
    EventHandler<MouseEvent> windowMouseDrag = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            drawHighlightDrag(event);

        }
    };
    private void setPoints(double point1x, double point1y, double point2x, double point2y, double point3x, double point3y, double point4x, double point4y, double point5x, double point5y){
        double difference = outerRadius - innerRadius;
        point1x = innerRadius*Math.cos(0);
        point1y = innerRadius*Math.sin(0);

        point2x = difference*Math.cos(0);
        point2y  = difference*Math.sin(0);

        point3x = outerRadius*Math.cos(359.9) - point2x - point1x;
        point3y = outerRadius*Math.sin(359.9) - point2y - point1y;

        point4x = -difference*Math.cos(359.9);
        point4y = -difference*Math.sin(359.9);

        point5x = -point4x - point3x - point2x;
        point5y = -point4y - point3y - point2y;
    }
    /**
     * Default Constructor
     */
    public CircularContextMenu(){
        double point1x = 0;
        double point1y = 0;
        double point2x = 0;
        double point2y = 0;
        double point3x = 0;
        double point3y = 0;
        double point4x = 0;
        double point4y = 0;
        double point5x = 0;
        double point5y = 0;

        setPoints(point1x, point1y, point2x, point2y, point3x, point3y, point4x, point4y, point5x, point5y);

        background.setContent("M" + -outerRadius + "," + -outerRadius + " m" + point1x + "," + point1y +
            " l " + point2x + "," + point2y + "a" + outerRadius + "," +
            outerRadius + " 0 1,1 " + point3x + "," + point3y + " l " + point4x + "," + point4y + "a" + innerRadius + "," +
            innerRadius + " 0 1,1 " + point5x + "," + point5y + " z");
        this.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawHighlightClick(event);
            }
        });
        highlight.background.setMouseTransparent(true);
        root = new Group();
        scene = new Scene(root,outerRadius,outerRadius);
        display = new CentralDisplay(innerRadius,outerRadius);
        setAutoHide(true);
    }

    /**
     * @param innerRadius Inner radius of Context Menu
     * @param outerRadius Outer Radius of Context Menu
     */
    public CircularContextMenu( double innerRadius, double outerRadius){
        double point1x = 0;
        double point1y = 0;
        double point2x = 0;
        double point2y = 0;
        double point3x = 0;
        double point3y = 0;
        double point4x = 0;
        double point4y = 0;
        double point5x = 0;
        double point5y = 0;

        setPoints(point1x, point1y, point2x, point2y, point3x, point3y, point4x, point4y, point5x, point5y);

        background.setContent("M" + -outerRadius + "," + -outerRadius + " m" + point1x + "," + point1y +
            " l " + point2x + "," + point2y + "a" + outerRadius + "," +
            outerRadius + " 0 1,1 " + point3x + "," + point3y + " l " + point4x + "," + point4y + "a" + innerRadius + "," +
            innerRadius + " 0 1,1 " + point5x + "," + point5y + " z");

        this.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawHighlightClick(event);
            }
        });
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
     * @param icon Fill for menu
     * @param background Background paint for icons
     * @param clickHandler Actioned to be performe by optiion
     * @param dragAndReleaseHandler Actioned to be performe by optiion
     */
    public void addOption(Paint icon, Paint background, Paint displayIcon, EventHandler clickHandler, EventHandler dragAndReleaseHandler){
        int size = menuElements.size();
        angle = (double)360 / (double)(size+1);
        double currentAngle = 0;

        menuElements.add(new ContextMenuElement(icon, background, displayIcon, this, display, clickHandler, dragAndReleaseHandler));
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
        getContent().add(display.centralDisplayBackground);
        getContent().add(display.centralDisplay);
    }

    /**
     * Adds an option that does not do anything
     * @param image
     * @param background
     */
    public void addOption(Paint image, Paint background){//redraw elements when a new one is added
        int size = menuElements.size();
        angle = (double)360 / (double)(size+1);
        double currentAngle = 0.0;

        menuElements.add(new ContextMenuElement(image, background, this, display));
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
                    System.out.println("Angle parameters must be positive");
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
        setMouseTransparent(false);

        super.show(nodeOwner, anchorX - outerRadius, anchorY - outerRadius);
        getOwnerWindow().addEventFilter(MouseEvent.MOUSE_DRAGGED, windowMouseDrag);
        getOwnerWindow().addEventFilter(MouseEvent.MOUSE_RELEASED, windowMouseRelease);
        super.requestFocus();
    }


    public String toString(){
        return("This menu has: " + menuElements.size() + " options.");
    }

    public void fireOption(MouseEvent event){
        double x = event.getScreenX() - this.getX()-outerRadius;
        double y = event.getScreenY() - this.getY() -outerRadius;
        double dist = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));

        double mouseAngle = calculateMouseAngle(x, y);
        mouseAngle = Math.toRadians(mouseAngle);
        for (ContextMenuElement element:menuElements) {
            double elementAngle = element.initialAngle + element.angle;
            if(dist > innerRadius) {
                if (mouseAngle > element.initialAngle && mouseAngle < elementAngle) {
                    System.out.println(event.toString());
                    element.background.fireEvent(event);
                }
            }
        }
    }
    public void drawHighlightDrag(MouseEvent event){
        if(highlight != null) {
            getContent().remove(highlight.path);
        }

        double x = event.getScreenX() - this.getX()-outerRadius;
        double y = event.getScreenY() - this.getY() -outerRadius;
        double dist = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        double mouseAngle = calculateMouseAngle(x, y);

        highlight.draw(mouseAngle - angle / 2, angle, innerRadius, outerRadius);
        if(getOwnerNode().getScene() != null)
            super.show(getOwnerNode(), getAnchorX(), getAnchorY());

        redraw();

    }

    public void drawHighlightClick(MouseEvent event){
        if(highlight != null) {
            getContent().remove(highlight.path);
        }

        double x = event.getSceneX() - outerRadius;
        double y = event.getSceneY() - outerRadius;
        double dist = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        double mouseAngle = calculateMouseAngle(x, y);

        highlight.draw(mouseAngle - angle / 2, angle, innerRadius, outerRadius);
        super.show(getOwnerNode(), getAnchorX(), getAnchorY());
        redraw();

    }

    private double calculateMouseAngle(double x, double y){
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
        return mouseAngle;
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
    @Override
    public void hide(){
        super.hide();

    }

    public void setMouseTransparent(boolean value){
        if(value) {
            for (ContextMenuElement element : menuElements) {
                element.background.setMouseTransparent(true);
            }
        }else {
            for (ContextMenuElement element : menuElements) {
                element.background.setMouseTransparent(false);
            }
        }
    }
}

