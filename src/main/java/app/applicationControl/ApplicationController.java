package app.applicationControl;

import app.datastore.Map;
import app.display.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;
    // probably not needed
    DisplayController currentDisplayController;
    Map map ;
    Stage pStage;

    Stage adminStage;
    Login login;
    Scene currentScene;

    // NOTE with proxy pattern this will change to a prox image
    HashMap<String, ProxyImage> images;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();

        this.pStage = primaryStage;
        adminStage = new Stage();
        createPatientDisplay();
        primaryStage.show();
        login = new Login();
    }

    /**
     * Load from the database and setup images
     */
    public void initialize(){
        databaseManager = new DatabaseManager("main");
        map = databaseManager.load();


        images = new HashMap<>();
        images.put("floor1", new ProxyImage("Maps/floor1.png"));
        images.put("floor2", new ProxyImage("Maps/floor2.png"));
        images.put("floor3", new ProxyImage("Maps/floor3.png"));
        images.put("floor4", new ProxyImage("Maps/floor4.png"));
        images.put("floor5", new ProxyImage("Maps/floor5.png"));
        images.put("floor6", new ProxyImage("Maps/floor6.png"));
        images.put("floor7", new ProxyImage("Maps/floor7.png"));

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
     * Get list of all floors in the application
     * TODO move this to map
     * @return
     */
    public List<String> getAllFloors () {
        return images.keySet().stream().collect(Collectors.toList());
    }


    /**
     * load the patient display into the frame
     */
    public void createPatientDisplay(){

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PatientDisplay.fxml"));
            PatientController controller = new PatientController(map,this, "floor3");
            loader.setController(controller);
            Parent root = loader.load();
            pStage.setTitle("PatientDisplay");
            currentScene =  new Scene(root, 1000, 600);
            //pStage.setFullScreen(true);
            pStage.setScene(currentScene);
            currentScene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    System.out.println("Width: " + newSceneWidth);
                    controller.scaleWidth(oldSceneWidth, newSceneWidth);
                }
            });
            currentScene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    controller.scaleHeight(oldSceneHeight, newSceneHeight);
                    System.out.println("Height: " + newSceneHeight);
                }
            });
            pStage.setFullScreen(true);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    /**
     * Get the floor with the given name
     * @param floor - floor name to retrieve
     * @return
     */
    public Image getImage (String floor) {
        ProxyImage proxyFloor = images.get(floor);
        if (proxyFloor != null) {
            try {
                return proxyFloor.getValue();
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    /**
     * create map admin display
     */
    public void createMapAdminDisplay(){

        adminStage.close();
        adminStage = new Stage();
        adminStage.setResizable(false);
        adminStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapAdminDisplay.fxml"));
                MapAdminController controller = new MapAdminController(map, this, "floor3.png", adminStage);
                loader.setController(controller);
                Parent root = loader.load();
                adminStage.setTitle("MapAdmin");
                adminStage.setScene(new Scene(root, 600, 600));
                adminStage.centerOnScreen();
                adminStage.show();

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
        adminStage.close();
        adminStage = new Stage();
        adminStage.setResizable(false);
        adminStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });
        //adminStage.initOwner(pStage);
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DirectoryAdminDisplay.fxml"));
                DirectoryAdminController controller = new DirectoryAdminController(map, this, "floor3.png");
                loader.setController(controller);
                Parent root = loader.load();
                adminStage.setTitle("DirectoryAdmin");
                adminStage.setScene(new Scene(root, 600, 600));
                adminStage.centerOnScreen();
                adminStage.show();

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
    public void createLoginAdmin(){
        if(isLoggedIn()) {
            adminStage.toFront();
            return;
        }

        adminStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginDisplay.fxml"));
        LoginController loginController = new LoginController(map, this, "floor3.png", adminStage);
        Scene newScene;
        try{
            loader.setController(loginController);
            Parent root = loader.load();
            adminStage.setTitle("Login");
            adminStage.initOwner(pStage);
            adminStage.setScene(new Scene(root, 350, 150));
            adminStage.show();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    public boolean login(String uname, String passwd){
        return login.signIn(uname, passwd);
    }

    public boolean isLoggedIn(){
        return login.isSignedIn();
    }


    /**
     * Closes Admin Display
     */
    public void logout(){
        try{
            login.signOut();
        } catch(NullPointerException n){
            System.out.println("Logout Error - no login class detected");
        }

        save();
        adminStage.close();
        //createPatientDisplay();
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
