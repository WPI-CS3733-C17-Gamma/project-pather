import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;
    DisplayController currentDisplayController;
    Map map ;
    Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.pStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDisplay.fxml"));
        // load database
        map = new Map(
            new Directory(new HashMap<>(), new HashMap<>()),
            new GraphNetwork(new LinkedList<>()),
            new HashMap<>());
        map.addEntry(new DirectoryEntry("A","doctor", new LinkedList<Room>()));
        map.addEntry(new DirectoryEntry("anotherB","doctor", new LinkedList<Room>()));
        map.addEntry(new DirectoryEntry("Cee","doctor", new LinkedList<Room>()));
        ////
        PatientController controller = new PatientController(map,this, "Maps/floor3.png");
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("PatientDisplay");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    public void initialize(){
    }

    private void createPatientDisplay(){
    }

    /**
     * switch scene
     */
    public void createMapAdminDisplay(){
        FXMLLoader loader = new FXMLLoader((getClass().getResource("AdminDisplay.fxml")));
        MapAdminController controller = new MapAdminController(map, this, "Maps/floor3.png");
        loader.setController(controller);
        try {
            Parent root = loader.load();
            pStage.setScene(new Scene(root, 800,500));
            pStage.setTitle("AdminDisplay");
            System.out.println("loaded new scene");
        }
        catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    public void createDirectoryAdminDisplay(){
    }

    public void loginMapAdmin(String login){
    }

    public void loginDirectoryAdmin(String login){
    }

    public void logout(){
    }

    public static void main (String[] args) {
        launch(args);
    }
}
