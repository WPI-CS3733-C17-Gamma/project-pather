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
        initialize();

        this.pStage = primaryStage;
        // load database
        map = databaseManager.load();
        createPatientDisplay();
        primaryStage.show();
    }

    // load from database
    public void initialize(){
        databaseManager = new DatabaseManager("main");
    }


    /**
     * load the patient display into the frame
     */
    public void createPatientDisplay(){
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
            pStage.setScene(new Scene(root, 1200, 1000));
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
            DirectoryAdminController controller = new DirectoryAdminController(map,this, "Maps/floor3.png");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("DirectoryAdmin");
            pStage.setScene(new Scene(root, 1200, 1000));
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

    /**
     * write current state to database
     */
    public void save () {
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
