package app.CustomMenus;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Created by Saina on 2017-02-22.
 */
public class CentralDisplay {
    SVGPath centralDisplay = new SVGPath();
    SVGPath centralDisplayBackground = new SVGPath();

    CentralDisplay(double innerRadius, double outerRadius){
        centralDisplay.setContent("M" + (-outerRadius) + " " + (-outerRadius - innerRadius) + " a " + innerRadius + " " + innerRadius +
            " 0 1 0 0.00001 0 z");
        centralDisplay.setFill(Color.rgb(255,255,255,0));

        centralDisplayBackground.setContent("M" + (-outerRadius) + " " + (-outerRadius - innerRadius) + " a " + innerRadius + " " + innerRadius +
            " 0 1 0 0.00001 0 z");
        centralDisplayBackground.setFill(Color.rgb(211,211,211,0.5));
        centralDisplayBackground.setVisible(true);
        centralDisplayBackground.setMouseTransparent(false);
    }
}
