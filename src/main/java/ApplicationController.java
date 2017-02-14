import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashMap;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;
    // probably not needed
    DisplayController currentDisplayController;
    Map map ;
    Stage pStage;
    Scene currentScene;

    // NOTE with proxy pattern this will change to a prox image
    HashMap<String, Image> images;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();
        this.pStage = primaryStage;
        createPatientDisplay();
        primaryStage.show();
    }

    /**
     * Load from the database and setup images
     */
    public void initialize(){
        databaseManager = new DatabaseManager("main");
        map = databaseManager.load();
        images = new HashMap<>();
        images.put("floor1", new Image("Maps/floor1.png"));
        images.put("floor2", new Image("Maps/floor2.png"));
        images.put("floor3", new Image("Maps/floor3.png"));
        images.put("floor4", new Image("Maps/floor4.png"));
        images.put("floor5", new Image("Maps/floor5.png"));
        images.put("floor6", new Image("Maps/floor6.png"));
        images.put("floor7", new Image("Maps/floor7.png"));

    }

    /**
     * reload the state of the database
     */
    public Map reload () {
        System.out.println("reload");
        map = databaseManager.load();
        return map;
    }



    /**
     * load the patient display into the frame
     */
    public void createPatientDisplay(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDisplay.fxml"));
            PatientController controller = new PatientController(map,this, "floor3");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("PatientDisplay");
            currentScene =  new Scene(root, 1000, 600);
            pStage.setScene(currentScene);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * Get the floor with the given name
     * @param floor
     * @return
     */
    public Image getImage (String floor) {
        return images.get(floor);
    }

    /**
     * create map admin display
     */
    public void createMapAdminDisplay(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MapAdminDisplay.fxml"));
            MapAdminController controller = new MapAdminController(map,this, "floor3");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("MapAdmin");
            currentScene =  new Scene(root, 1000, 600);
            pStage.setScene(currentScene);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * Create map directory admin app
     */
    public void createDirectoryAdminDisplay(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DirectoryAdminDisplay.fxml"));
            DirectoryAdminController controller = new DirectoryAdminController(map,this, "floor3");

            loader.setController(controller);
            Parent root = loader.load();
            currentScene =  new Scene(root, 1000, 600);
            pStage.setScene(currentScene);
            pStage.setTitle("DirectoryAdmin");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * login to map admin
     * @param login
     */
    public void loginMapAdmin(String login){
    }

    /**
     * login to directory admin
     * @param login
     */
    public void loginDirectoryAdmin(String login){
    }

    /**
     * Change back to the patient display
     */
    public void logout(){
        save();
        createPatientDisplay();
    }

    /**
     * get the current java fx scene
     * @return
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * write current state to database
     */
    public void save () {
        System.out.println("SAVE");
        databaseManager.write(map);
    }
    /**
     * stop and write to database
     */
    @Override
    public void stop () {
        databaseManager.write(map);
        System.out.println("Application Closed");
    }

    public static void main (String[] args) {
        launch(args);
    }
}
