package app.display;

import app.applicationControl.ApplicationController;
import app.applicationControl.Login;
import app.datastore.Map;
import javafx.stage.Stage;

public class DisplayController {

    Map map;
    //Kiosk kiosk;
    ApplicationController applicationController;


    public DisplayController(Map map,
                             /*Kiosk kiosk, */
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

    void createDirectoryAdminDisplay(){
        applicationController.createDirectoryAdminDisplay();
    }

    void createAdminTools(){
        applicationController.createAdminTools();
    }

    void createMapAdminDisplay(){
        applicationController.createMapAdminDisplay();
    }

    void logout(){
        applicationController.logout();
    }

    void hideStage(Stage stage){
        stage.hide();
    }
}
