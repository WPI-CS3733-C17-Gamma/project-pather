import javafx.scene.Scene;

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
}
