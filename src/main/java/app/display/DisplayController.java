package app.display;

import app.applicationControl.ApplicationController;
import app.applicationControl.Login;
import app.datastore.Map;
import javafx.stage.Stage;

public class DisplayController {

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

    void logout(){
        applicationController.logout();
    }

    void hideStage(Stage stage){
        stage.hide();
    }
}
