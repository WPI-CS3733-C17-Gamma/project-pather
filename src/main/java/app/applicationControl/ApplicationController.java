package app.applicationControl;

import app.datastore.Map;
import app.display.AdminController;
import app.display.DisplayController;
import app.display.LoginController;
import app.display.PatientController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationController extends Application {

    DatabaseManager databaseManager;

    DisplayController currentDisplayController;

    Map map ;
    Stage pStage;
    Stage adminStage;
    Login login;
    Scene currentScene;

    HashMap<String, ProxyImage> floorMaps;
    HashMap<String, ProxyImage> extraImages;

    boolean isLoginPage;


    final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    PatientController patientController;


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
        logger.info("Starting ApplicationController");

        databaseManager = new DatabaseManager("main");
        map = databaseManager.load();

        floorMaps = new HashMap<>();
        floorMaps.put("floor1", new ProxyImage("Saina's New Blue Maps/main_1_blue.png"));
        floorMaps.put("floor2", new ProxyImage("Saina's New Blue Maps/main_2_blue.png"));
        floorMaps.put("floor3", new ProxyImage("Saina's New Blue Maps/main_3_blue.png"));
        floorMaps.put("floor4", new ProxyImage("Saina's New Blue Maps/main_4_blue.png"));
        floorMaps.put("floor5", new ProxyImage("Saina's New Blue Maps/main_5_blue.png"));
        floorMaps.put("floor6", new ProxyImage("Saina's New Blue Maps/main_6_blue.png"));
        floorMaps.put("floor7", new ProxyImage("Saina's New Blue Maps/main_7_blue.png"));
        floorMaps.put("belkin1", new ProxyImage("Saina's New Blue Maps/Belkin_1_blue_temp.png"));
        floorMaps.put("belkin2", new ProxyImage("Saina's New Blue Maps/Belkin_2_blue.png"));
        floorMaps.put("belkin3", new ProxyImage("Saina's New Blue Maps/Belkin_3_blue.png"));
        floorMaps.put("belkin4", new ProxyImage("Saina's New Blue Maps/Belkin_4_blue.png"));
        floorMaps.put("campus", new ProxyImage("Saina's New Blue Maps/campus_blue.png"));

        extraImages = new HashMap<>();
        extraImages.put("", null);
        extraImages.put("Elevator", new ProxyImage("Icon_PNGs/newElevator.png"));
        extraImages.put("Cafe", new ProxyImage("Icon_PNGs/Cafe2T.png"));
        extraImages.put("Bathroom", new ProxyImage("Icon_PNGs/BathroomT.png"));
        extraImages.put("Waitroom", new ProxyImage("Icon_PNGs/WaitRoomT.png"));
        extraImages.put("Doctor", new ProxyImage("Icon_PNGs/DoctorT.png"));
        extraImages.put("Frontdesk", new ProxyImage("Icon_PNGs/AdmittingT.png"));
        extraImages.put("Library", new ProxyImage("Icon_PNGs/LibraryT.png"));
        extraImages.put("Giftshop", new ProxyImage("Icon_PNGs/GiftShopT.png"));
        extraImages.put("Family Room", new ProxyImage("Icon_PNGs/FamilyT.png"));
        extraImages.put("Emergency Room", new ProxyImage("Icon_PNGs/EmergencyT.png"));
        extraImages.put("Starbucks", new ProxyImage("Icon_PNGs/Starbucks.png"));
        extraImages.put("Stairs", new ProxyImage("Icon_PNGs/Stairs.png"));
        extraImages.put("Entrance", new ProxyImage("Icon_PNGs/Entrance.png"));
        extraImages.put("Cafeteria", new ProxyImage("Icon_PNGs/CafeteriaT.png"));
    }

    /**
     * reload the state of the database
     */
    public Map reload () {
        logger.debug("Reloading Map");
        map = databaseManager.load();
        return map;
    }

    /**
     * Get list of all floors in the application
     * TODO move this to map
     * @return
     */
    public List<String> getAllFloors () {
        return this.floorMaps.keySet().stream().collect(Collectors.toList());
    }

    public List<String> getAllIconNames(){
        return this.extraImages.keySet().stream().collect(Collectors.toList());
    }

    /**
     * load the patient display into the frame
     */
    public void createPatientDisplay(){

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PatientDisplay.fxml"));
            patientController = new PatientController();
            patientController.init(map, this, pStage, "floor3");
            loader.setController(patientController);
            Parent root = loader.load();
            pStage.setTitle("PatientDisplay");
            currentScene =  new Scene(root, 1000, 600);
            //pStage.setFullScreen(true);
            pStage.setScene(currentScene);
            currentScene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    logger.debug("Scaling {} Width: {}", this.getClass().getSimpleName(),newSceneWidth);
                    patientController.scaleWidth(oldSceneWidth, newSceneWidth);
                }
            });
            currentScene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    patientController.scaleHeight(oldSceneHeight, newSceneHeight);
                    logger.debug("Scaling {} Height: {}", this.getClass().getSimpleName(),newSceneHeight);
                }
            });
            pStage.setFullScreen(true);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.toString());
        }
    }

    /**
     * Get the floor with the given name
     * @param floor - floor name to retrieve
     * @return
     */
    public Image getFloorImage(String floor) {
        ProxyImage proxyFloor = floorMaps.get(floor);
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

    public Image getIconImage(String floor) {
        ProxyImage proxyFloor = extraImages.get(floor);
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
     * Create map directory admin app
     */
    public void createAdminDisplay(){
        adminStage.close();
        adminStage = new Stage();
        adminStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });
        //adminStage.initOwner(pStage);
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDisplay.fxml"));

                Parent root = loader.load();
                AdminController controller = loader.<AdminController>getController();
                controller.init(map, this, adminStage);
                adminStage.setTitle("Directory Admin");
                adminStage.setScene(new Scene(root, 800, 800));
                adminStage.centerOnScreen();
                adminStage.show();

        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.toString());
        }
    }

    /**
     * login to admin
     *
     */
    public void createLoginAdmin(){
        if(adminStage.isShowing()) {
            adminStage.toFront();
            return;
        }
        adminStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginDisplay.fxml"));
        Scene newScene;
        try{
            Parent root = loader.load();
            LoginController controller = loader.<LoginController>getController();
            controller.init(map, this, adminStage);
            adminStage.setTitle("Login");
            adminStage.initOwner(pStage);
            adminStage.setScene(new Scene(root));
            adminStage.show();
        } catch (IOException e){
            e.printStackTrace();
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.toString());
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.toString());
        }
    }

    public boolean login(String uname, String passwd){
        return login.signIn(uname, passwd);
    }

    public boolean isLoggedIn(){
        return login.isSignedIn();
    }

    public boolean addUser(String uname, String passwd) {
        return login.addUser(uname, passwd);
    }

    public boolean changePassword(String uname, String oldPasswd, String newPasswd){
        return login.changePassword(uname, oldPasswd, newPasswd);
    }

    /**
     * Closes Admin Display
     */
    public void logout(){
        try{
            login.signOut();
        } catch(NullPointerException n){
            logger.error("Logout Error - no login class detected");
        }

        save();
        adminStage.close();
        patientController.refreshDisplay();

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
        logger.debug("Saving to database in {}", this.getClass().getSimpleName());
        databaseManager.write(map);
    }
    /**
     * stop and write to database
     */
    @Override
    public void stop () {
        databaseManager.write(map);
        logger.info("Application Closed at {}\n", Calendar.getInstance().getTime().toString());
    }

    public static void main (String[] args) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat f1 = new SimpleDateFormat("dd-EEE 'at' hh-mm");
        System.setProperty("logname", f1.format(date));
        launch(args);
    }
}

