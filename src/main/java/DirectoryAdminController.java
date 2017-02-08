import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.IllegalFormatCodePointException;
import java.util.IllegalFormatException;
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

    /** See the method {@link Map#searchEntry(String)} */
    public List<String> searchEntry(String search) {
        return map.searchEntry(search);
    }

    /** See the method {@link Map#getEntry(String)}
     * Side Effect: Sets activeDirectoryEntry to the entry that is found
     * @throws IllegalArgumentException if an entry could not be found*/
    public void selectEntry(String entryName) throws IllegalArgumentException {
        activeDirectoryEntry = map.getEntry(entryName);
        if( activeDirectoryEntry == null ) {
            throw new IllegalArgumentException();
        }
        return;
    }

    /** See the method {@link Map#deleteEntry(DirectoryEntry)} */
    public boolean deleteEntry(DirectoryEntry entry) {
        return map.deleteEntry(entry);
    }

     /**
     * Create a new directory entry in the Directory object in the Map
     * @param name Name of entry to add
     * @param title Title of entry to add
     * @param room List of room the entry is associated with
     * @throws IllegalArgumentException if there new entry would be a duplicate
     */
    public void createEntry(String name, String title, List<Room> room) throws IllegalArgumentException {
        DirectoryEntry newEntry = new DirectoryEntry(name, title, room);

        if( !map.addEntry(newEntry) ) {
            throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
                + " already in the directory");
        }
        return;
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
     * @param name name of the new entry
     * @param title title of the new entry
     * @param room list of room associated with the new entry
     * @throws IllegalStateException if entry is not selected
     * @throws IllegalArgumentException if key already exits
     */
    public void saveEntry(String name, String title, List<Room> room) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, room);

        if( map.getEntry(name) != null ) {
            if( map.getEntry(name).equals(newEntry) ) {
                throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
                    + " already in the directory");
            }

            if( !map.getEntry(name).equals(activeDirectoryEntry)) {
                throw new IllegalArgumentException("Tried to save entry that would replace another one");
            }
        }

        map.deleteEntry(activeDirectoryEntry);
        activeDirectoryEntry = null;
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
