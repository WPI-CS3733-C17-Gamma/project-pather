import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;


public class PatientController extends DisplayController implements Initializable {
    //PatientDisplay display;
    GraphNode destination;
    ApplicationController appController;

    // FXML Things
    @FXML
    private TextField searchBar;

    @FXML
    private ListView<String> options;
    @FXML
    private void initialize() {
    }
    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane;

    public PatientController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        super(map,applicationController, currentMap);
    }

    public void displayImage() {
        Image floor3 = new Image("Maps/floor3.png");
        imageView.setImage(floor3);
    }

    /**
     * perform search
     */
    public void search () {
        String search = searchBar.getText();
        if (search.length() > 0) {
            options.setVisible(true);
        }
        else {
            options.setVisible(false);
            return;
        }
        System.out.println("search : " + search);
        List<String> results = search(search);
        for (String s: results){
            System.out.println(s);
        }
        if(!results.isEmpty()) {
            ObservableList<String> filter = FXCollections.observableArrayList(results);
            options.setItems(filter);
            options.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String selectedString = options.getSelectionModel().getSelectedItem();
                    System.out.println("clicked on " + selectedString);
                    select(selectedString);
                }
            });
        }
        else {
            options.setVisible(false);
        }
    }


    /**
     * get rooms / entries related to entry
     * @param room
     * @return
     */
    public List<String> search(String room) {
        if (((int)room.charAt(0)) < 58 && ((int)room.charAt(0)) > 47){
            return map.searchRoom(room);
        }
        else{
            String room2 = room.toLowerCase();
            return map.searchEntry(room2);
        }
    }

    public void switchToMapAdmin() {
        applicationController.createMapAdminDisplay();
    }
    public GraphNode select(String option) {
        System.out.println("select");
        DirectoryEntry entry = map.getEntry(option);
        if (entry != null) {
            System.out.println("Found Entry!");
        }
        else {
            Room room =  map.getRoomFromName(option);
            if (room != null) {
                System.out.println("FOUND ROOM! : " + room);
            }
            else {
                System.out.println("no entry :( ");
            }
        }
        return null;
    }

    public void displayPath(GraphNode[] path) {
    }

    public void loginDirectoryAdmin(String login) {
    }

    void update() {
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("INIT");
        displayImage();


    }
}
