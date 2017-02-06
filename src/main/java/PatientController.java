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

    public PatientController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        super(map,applicationController, currentMap);
    }

    public void search () {
        String search = searchBar.getText();
        System.out.println("search : " + search);

        ObservableList<String> filter = FXCollections.observableArrayList(map.searchEntry(search));
        options.setItems(filter);
    }



    public void search(String room) {
        if (((int)room.charAt(0)) < 58 && ((int)room.charAt(0)) > 47){
            map.searchRoom(room);
        }
        else{
            String room2 = room.toLowerCase();
            map.searchEntry(room2);
        }
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


    public static void main(String[] args) {
        Application app = new Application() {
            @Override
            public void start(Stage primaryStage) throws Exception {
                Parent root = FXMLLoader.load(getClass().getResource("PatientDisplay.fxml"));
                //  get input text
                primaryStage.setTitle("PatientDisplay");
                primaryStage.setScene(new Scene(root, 500, 300));
                primaryStage.show();
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
