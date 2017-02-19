import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.SVGPath;

/**
 * Created by dominic on 2/18/17.
 */
public class ContextMenuElement{
    Image icon;
    SVGPath path = new SVGPath();
    int outerRadius = 150;
    int innerRadius = 100;
    double angle = 10;
    double initialAngle = 10;


    EventHandler<MouseEvent> onClickHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            System.out.println("Hi");
        }
    };
    ContextMenuElement(double initialAngle, double angle, Image icon, int innerRadius, int outerRadius){
        this.path.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.icon = icon;
        this.initialAngle = initialAngle*Math.PI/180;
        this.angle = angle*Math.PI/180;

        int difference = outerRadius - innerRadius;
        System.out.println("Initial angle "+ initialAngle + "Outer angle " + outerRadius);
        path.setContent("M0,0 m" + innerRadius*Math.sin(-angle + initialAngle) + ","+ -innerRadius*Math.cos(-angle + initialAngle) +", l"+
            difference*Math.sin(-angle + initialAngle)+","+ -difference*Math.cos(-angle + initialAngle)+" a"+outerRadius+","+outerRadius+" 0 0,0" +
            -outerRadius*(Math.sin(initialAngle) )+","+ outerRadius*(1- Math.cos(initialAngle) ) +" l" + difference*Math.sin(initialAngle)+"," +
            difference*Math.cos(initialAngle)+ "z");
        path.setFill(new ImagePattern(this.icon, 20, 20, 40, 40, false));
    }

    ContextMenuElement(double initialAngle, double angle, Image icon, EventHandler onClickHandler, int innerRadius, int outerRadius){
        this.onClickHandler = onClickHandler;
        this.path.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.icon = icon;
        this.initialAngle = initialAngle*Math.PI/180;
        this.angle = angle*Math.PI/180;

        int difference = outerRadius - innerRadius;
        angle = angle*Math.PI/180;
        path.setContent("M0,0 m 0, -"+ innerRadius +", v-"+difference+" a"+outerRadius+","+outerRadius+" 0 0,0" +
            -outerRadius*(Math.sin(angle) )+","+ outerRadius*(1- Math.cos(angle) ) +" l" + difference*Math.sin(angle)+"," +
            difference*Math.cos(angle)+ "z");
        path.setFill(new ImagePattern(this.icon, 20, 20, 40, 40, false));
    }


    public void resize(double initialAngle, double angle, int innerRadius, int outerRadius){
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;

        int difference = outerRadius - innerRadius;
        this.angle = angle*Math.PI/180;
        this. initialAngle = initialAngle*Math.PI/180;
        System.out.println("Initial angle "+ initialAngle + "Outer angle " + outerRadius);

        path.setContent("M0,0 m 0, -"+ innerRadius +", v-"+difference+" a"+outerRadius+","+outerRadius+" 0 0,0" +
            -outerRadius*(Math.sin(angle) )+","+ outerRadius*(1- Math.cos(angle) ) +" l" + difference*Math.cos(angle)+"," +
            difference*Math.sin(angle)+ "z");
        path.setFill(new ImagePattern(this.icon, 20, 20, 40, 40, false));
    }


}
