import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;
    // probably not needed
    DisplayController currentDisplayController;
    Map map ;
    Stage pStage;

    Stage loginStage;
    Login login;
    Scene currentScene;

    // NOTE with proxy pattern this will change to a prox image
    HashMap<String, Image> images;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();

        this.pStage = primaryStage;
        loginStage = new Stage();
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
    public void createMapAdminDisplay(Login login){
        try {
                this.login = login;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDisplay.fxml"));
                MapAdminController controller = new MapAdminController(map, this, "floor3.png");
                loader.setController(controller);
                Parent root = loader.load();
                pStage.setTitle("MapAdmin");
                pStage.setScene(new Scene(root, 1000, 600));
                pStage.show();

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * Create map directory admin app
     */
    public void createDirectoryAdminDisplay(Login login){
        try {
                this.login = login;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DirectoryAdminDisplay.fxml"));
                DirectoryAdminController controller = new DirectoryAdminController(map, this, "floor3.png");
                loader.setController(controller);
                Parent root = loader.load();
                pStage.setTitle("DirectoryAdmin");
                pStage.setScene(new Scene(root, 1000, 600));
                pStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * login to admin
     *
     */
    public void createLoginAdmin(){     //not signing in... also add "wrong username or password"
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginDisplay.fxml"));
        LoginController loginController = new LoginController(this, loginStage);
        Scene newScene;
        try{
            loader.setController(loginController);
            Parent root = loader.load();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, 350, 150));
            loginStage.show();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }


    }


    /**
     * Change back to the patient display
     */
    public void logout(){
        try{
            login.signOut();
        } catch(NullPointerException n){
            System.out.println("Logout Error - no login class detected");
        }

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
