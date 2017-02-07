import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;
    // probably not needed
    DisplayController currentDisplayController;
    Map map ;
    Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.pStage = primaryStage;
        // load database
        //TODO remove!!
        map = new Map(
            new Directory(new HashMap<>(), new HashMap<>()),
            new GraphNetwork(new LinkedList<>()),
            new HashMap<>());
        map.addEntry(new DirectoryEntry("A","doctor", new LinkedList<Room>()));
        map.addEntry(new DirectoryEntry("anotherB","doctor", new LinkedList<Room>()));
        map.addEntry(new DirectoryEntry("Cee","doctor", new LinkedList<Room>()));
        Room tempRoom = new Room(new GraphNode(new FloorPoint(1,1,"1")), "402");
        Room tempRoom2 = new Room(new GraphNode(new FloorPoint(2,1,"1")), "401");
        Room tempRoom3 = new Room(new GraphNode(new FloorPoint(3,1,"1")), "432");
        Room tempRoom4 = new Room(new GraphNode(new FloorPoint(4,1,"1")), "502");
        map.getEntry("Cee").addLocation(tempRoom);
        map.getEntry("Cee").addLocation(tempRoom2);
        map.getEntry("Cee").addLocation(tempRoom3);
        map.getEntry("Cee").addLocation(tempRoom4);
        map.getEntry("A").addLocation(tempRoom);
        map.addRoom(tempRoom);
        map.addRoom(tempRoom2);
        map.addRoom(tempRoom3);
        map.addRoom(tempRoom4);
        ////
        createPatientDisplay();
        primaryStage.show();
    }

    // load from database
    public void initialize(){
        databaseManager = new DatabaseManager("main");
    }


    private void createPatientDisplay(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDisplay.fxml"));
            PatientController controller = new PatientController(map,this, "Maps/floor3.png");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("PatientDisplay");
            pStage.setScene(new Scene(root, 1200, 1000));
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * create map admin display
     */
    public void createMapAdminDisplay(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDisplay.fxml"));
            MapAdminController controller = new MapAdminController(map,this, "Maps/floor3.png");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("MapAdmin");
            pStage.setScene(new Scene(root, 800, 500));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDisplay.fxml"));
            PatientController controller = new PatientController(map,this, "Maps/floor3.png");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("DirectoryAdmin");
            pStage.setScene(new Scene(root, 800, 500));
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
        createPatientDisplay();
    }

    public static void main (String[] args) {
        launch(args);
    }
}
