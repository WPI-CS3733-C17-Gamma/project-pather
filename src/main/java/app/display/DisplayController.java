package app.display;

import app.applicationControl.ApplicationController;
import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;
import app.datastore.Map;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayController {
    final Logger logger = LoggerFactory.getLogger(DisplayController.class);

    @FXML Label helpLabel;

    Map map;
    ApplicationController applicationController;
    Stage stage;

    public void init(Map map,
                     ApplicationController applicationController,
                     Stage stage){
        this.map = map;
        this.applicationController = applicationController;
        this.stage = stage;
    }

    void update(){
    }

    public void changeFloor(String floor){
    }

    boolean login(String uname, String passwd){
        return applicationController.login(uname, passwd);
    }

    void createAdminDisplay(){
        applicationController.createAdminDisplay();
    }

    /**
     * Preview changes without writing to the database
     * Creates patient display without changing the database
     */
    public void preview () {
        logger.info("Preview from {}", this.getClass().getSimpleName());
        applicationController.createPatientDisplay();
    }

    /**
     * toggle help message
     */
    public void help () {
        logger.debug("Opening help for {}", this.getClass().getSimpleName());
        if (helpLabel.isVisible()) {
            helpLabel.setVisible(false);
        }
        else {
            helpLabel.setVisible(true);
        }
    }

    /**
     *  called by undo button.
     *  Revert to previous database state
     *  Reset the state of all the objects
     */
    public void undo () {
        map = applicationController.reload();
    }

    void hideStage(Stage stage){
        stage.hide();
    }
        /**
     * convert the graph 1000 * 1000 coordinate to the size of the image view
     * The point must be added to the direct parent of the image view for this to work
     * OR all points must be added ot the scene
     * @param node
     * @param imageToBeDrawnOver image the the coordinate must be scaled to
     * @return
     */
    FloorPoint graphPointToImage (GraphNode node, ImageView imageToBeDrawnOver) {
        Parent currentParent = imageToBeDrawnOver.getParent();

        double imageWidth = imageToBeDrawnOver.getBoundsInLocal().getWidth();
        double imageHeight = imageToBeDrawnOver.getBoundsInLocal().getHeight();
        double offsetX = imageToBeDrawnOver.getLayoutX();
        double offsetY = imageToBeDrawnOver.getLayoutY();

        while(!(currentParent instanceof AnchorPane)){
            offsetX += currentParent.getLayoutX();
            offsetY += currentParent.getLayoutY();
            currentParent = currentParent.getParent();
        }

        logger.debug("Offsets: off x {}, off y {}", offsetX, offsetY);

        int newX = (int)(node.getLocation().getX() * imageWidth / 1000. + offsetX );
        int newY = (int)(node.getLocation().getY() * imageHeight / 1000. + offsetY );
        logger.debug("Image width : {}, Image Height : {}", imageWidth, imageHeight);

        return new FloorPoint(newX, newY, node.getLocation().getFloor());
    }
}
