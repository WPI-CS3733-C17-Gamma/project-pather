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

    /**
     * search entry by keyword
     * @param search
     * @return
     */
    public List<String> searchEntry(String search) {
        return map.searchEntry(search);
    }

    public DirectoryEntry selectEntry(String entryName) {
        return null;
    }

    /**
     * remove the given directory entry in Directory
     * @param entry
     * @return
     */
    public boolean deleteEntry(DirectoryEntry entry) {
        return map.deleteEntry(entry);
    }

    /**
     * Create a new directory entry in the Directory object in the Map
     * @param name
     * @param title
     * @param room
     */
    public void createEntry(String name, String title, List<Room> room) {
        map.addEntry(new DirectoryEntry(name, title, room));
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

    /**
     * activeDirectoryEntry must have the entry that is being edited
     * Throws error IllegalStateException if entry is not selected
     * and throws IllegalArgumentException if entry already exits
     * @param name name of the new entry
     * @param title title of the new entry
     * @param room list of room associated with the new entry
     */
    public void saveEntry(String name, String title, List<Room> room) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, room);

        if( map.getEntry(name) != null && map.getEntry(name).equals(newEntry) ) {
            throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
            + " already in the directory");
        }

        System.out.print(activeDirectoryEntry.getName());
        map.deleteEntry(activeDirectoryEntry);
        map.addEntry(newEntry);
        return;

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
