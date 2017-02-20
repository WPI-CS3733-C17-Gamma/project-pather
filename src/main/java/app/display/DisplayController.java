package app.display;

import app.applicationControl.ApplicationController;
import app.applicationControl.Login;
import app.datastore.Map;
import javafx.stage.Stage;

public class DisplayController {

    Map map;
    //Kiosk kiosk;
    ApplicationController applicationController;
    String currentMap;

    public DisplayController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        this.map = map;
        this.applicationController = applicationController;
        this.currentMap = currentMap;
    }

    void update(){
    }

    public void changeFloor(String floor){
    }

    boolean login(String uname, String passwd){
        return applicationController.login(uname, passwd);
    }

    void createDirectoryAdminDisplay(){
        applicationController.createMapAdminDisplay();
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
