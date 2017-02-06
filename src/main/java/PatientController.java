import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private void draw() {
        Arc a1 = new Arc(200, 300, 100, 100, 90, 90);
        a1.setType(ArcType.OPEN);
        a1.setStroke(Color.BLACK);
        a1.setFill(null);
        a1.setStrokeWidth(3);
        anchorPane.getChildren().addAll(a1);

        Image floor3 = new Image("Maps/floor3.png");
        imageView.setImage(floor3);

    }

    public PatientController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        super(map,applicationController, currentMap);
    }

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
        ObservableList<String> filter = FXCollections.observableArrayList(search(search));
        options.setItems(filter);
    }



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
        System.out.println("switch");
        applicationController.createMapAdminDisplay();
        draw();
    }
    public GraphNode select(String option) {
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

    }
}
