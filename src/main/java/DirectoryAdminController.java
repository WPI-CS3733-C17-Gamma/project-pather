import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DirectoryAdminController extends DisplayController implements Initializable{
/*    DirectoryAdminDisplay display;*/
    GraphNode activePoint;
    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;


    @FXML
    Label helpLabel;

    public DirectoryAdminController(Map map,
                                    /*Kiosk kiosk, */
                                    ApplicationController applicationController,
                                    String currentMap) {
        super(map,
              /*kiosk,*/
              applicationController,
              currentMap);
    }

    public List<String> searchEntry(String search) {
        return null;
    }

    public DirectoryEntry selectEntry(String entryName) {
        return null;
    }

    public boolean deleteEntry(DirectoryEntry entry) {
        return false;
    }

    public void createEntry(String name, String title, List<Room> room) {
    }


    public void addLocationToEntry(String room) {
    }

    public void deleteLocation(String room) {
    }

    public void changeTitle(String title) {
    }

    /* Functions for Rooms */
    public List<String> searchRoom(String search) {
        return null;
    }

    public void createRoom(String name, GraphNode node) {
    }

    public boolean deleteRoom(Room room) {
        return false;
    }

    public Room getRoom(FloorPoint location) {
        return null;
    }

    public Room getRoom(String name) {
        return null;
    }


    public GraphNode selectNode(FloorPoint loc) {
        return null;
    }

    public void saveEntry(String name, String title, List<Room> room) {
    }

    /**
     * toggle help message
     */
    public void help () {
        System.out.println("Here is how to use this...");
        if (helpLabel.isVisible()) {
            helpLabel.setVisible(false);
        }
        else {
            helpLabel.setVisible(true);
        }

    }

    void update() {
    }

    public void logout () {
        applicationController.createPatientDisplay();
    }

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("INIT");


        helpLabel.setText("Hello! Thanks for using project-pather.\n\nTo get started, start typing into the search bar. " +
            "\n Then, select the option you would like to get a path to.\n\nTo close this menu, click on it");

    }

}
