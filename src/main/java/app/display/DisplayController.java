package app.display;

import app.applicationControl.ApplicationController;
import app.applicationControl.Login;
import app.datastore.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DisplayController {

    @FXML Label helpLabel;

    Map map;
    //Kiosk kiosk;
    ApplicationController applicationController;


    public DisplayController(Map map,
                             ApplicationController applicationController){
        this.map = map;
        this.applicationController = applicationController;

    }

    void update(){
    }

    public void changeFloor(String floor){
    }

    boolean login(String uname, String passwd){
        return applicationController.login(uname, passwd);
    }

    /**
     * Return to patient display from DirectoryAdminController or MapAdminController
     */
    void logout(){
        applicationController.logout();
    }

    void createDirectoryAdminDisplay(){
        applicationController.createDirectoryAdminDisplay();
    }

    void createAdminTools(){
        applicationController.createAdminTools();
    }

    void createMapAdminDisplay(){
        applicationController.createMapAdminDisplay();
    }

    /**
     * Preview changes without writing to the database
     * Creates patient display without changing the database
     */
    public void preview () {
        System.out.println("Preview");
        applicationController.createPatientDisplay();
    }

    /**
     * toggle help message
     */
    public void help () {
        System.out.println("Here is how to use this...");
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
}
