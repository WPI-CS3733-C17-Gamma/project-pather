import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Saahil on 2/6/2017.
 */
public class PatientDisplayTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDisplay.fxml"));

        Map m = new Map(
            new Directory(new HashMap<>(), new HashMap<>()),
            new GraphNetwork(new LinkedList<>()),
            new HashMap<>());

        m.addEntry(new DirectoryEntry("A","doctor", new LinkedList<Room>()));
        m.addEntry(new DirectoryEntry("anotherB","doctor", new LinkedList<Room>()));
        m.addEntry(new DirectoryEntry("Cee","doctor", new LinkedList<Room>()));


        PatientController controller = new PatientController(m,null, null);
        loader.setController(controller);
        Parent root = loader.load();


        primaryStage.setTitle("PatientDisplay");
        primaryStage.setScene(new Scene(root, 800, 500));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
