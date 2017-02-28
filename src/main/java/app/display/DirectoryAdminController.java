package app.display;

import app.applicationControl.ApplicationController;
import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.datastore.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryAdminController extends DisplayController{
    final Logger logger = LoggerFactory.getLogger(DirectoryAdminController.class);

    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;

    // FXML stuff
    @FXML TextField searchBar;
    @FXML ListView<String> listEntries;
    @FXML VBox entryEditor;
    @FXML TextField entryName;
    @FXML TextField entryTitle;
    @FXML TextField entryRoomSearch;
    @FXML ListView<String> entryRoomOptions;
    @FXML Button entryDeleteRoom;
    @FXML ListView<String> entryCurrentLocations;
    @FXML ChoiceBox iconOption;

    /** Cruddy Constructor for tests */
    public DirectoryAdminController (Map map,
                     ApplicationController applicationController,
                     Stage stage) {
        super.init(map, applicationController, stage);
    }

    public DirectoryAdminController () {}

    public void init(Map map,
                     ApplicationController applicationController,
                     Stage stage) {
        super.init(map, applicationController, stage);
        logger.info("INIT DirectoryAdminController");
        helpLabel.setText("Welcome to the directory entry editor.\n You're an admin, you don't need help" );

        //set up the choice box for choosing the icon
        List<String> iconOptions = applicationController.getAllIconNames();
        iconOption.setItems(FXCollections.observableList(iconOptions));
        iconOption.setValue(new Tooltip("Choose icon"));

        // get both entries
        List<String> entryList = map.getAllEntries();
        entryList.sort(String::compareTo);
        ObservableList<String> allEntries = FXCollections.observableList(entryList);
        listEntries.setItems(allEntries);
        listEntries.getSelectionModel().selectedItemProperty().addListener(
            (ov, old_val, new_val) -> selectEntry(new_val));

        // add change handler for the dropdown list of possible locations
        entryRoomOptions.getSelectionModel().selectedItemProperty().addListener(
            (ov, old_val, new_val) -> entryAddRoom(new_val));

        // add click handlers to the list of currentRooms
        entryCurrentLocations.getSelectionModel().selectedItemProperty().addListener(
            (ov, old_val, new_val) -> entryDeleteRoom.setVisible(true));
    }

    /** See the method {@link Map#searchEntry(String)} */
    public List<String> searchEntry(String search) {
        return map.searchEntry(search);
    }

    /** See the method {@link Map#deleteEntry(DirectoryEntry)} */
    public boolean deleteEntry(DirectoryEntry entry) {
        return map.deleteEntry(activeDirectoryEntry);
    }

    /** See the method {@link Map#getEntry(String)}
     * Side Effect: Sets activeDirectoryEntry to the entry that is found
     * @throws IllegalArgumentException if an entry could not be found*/
    public void selectEntry(String entryName) {//  throws IllegalArgumentException {
        activeDirectoryEntry = map.getEntry(entryName);
        //this thingy throws an error every time an entry is saved
        if( activeDirectoryEntry == null ) {
            return;
            //throw new IllegalArgumentException();
        }
        displayEntry(activeDirectoryEntry);
        return;
    }

    /**
     * create a empty entry for the display
     */
    public void createEntry() {
        activeDirectoryEntry = new DirectoryEntry("", "", new LinkedList<Room>());
        displayEntry(activeDirectoryEntry);
    }

    /**
     * Removed the activeDirectoryEntry from the entry list
     */
    public void entryDeleteSelectedEntry () {
        if (activeDirectoryEntry != null) {
            deleteEntry(activeDirectoryEntry);
            activeDirectoryEntry = null ;
            entryEditor.setVisible(false);
            filterAllEntries();
        }
        else {
            logger.error("Cannot delete entry, no entry selected");
        }
    }

    /**
     * Create a new directory entry in the app.datastore.Directory object in the app.datastore.Map
     * @param name Name of entry to add
     * @param title Title of entry to add
     * @param room List of room the entry is associated with
     * @throws IllegalArgumentException if there new entry would be a duplicate
     */
    public void createEntry(String name, String title, List<Room> room, String icon) throws IllegalArgumentException {
        DirectoryEntry newEntry = new DirectoryEntry(name, title, room, icon);

        if( !map.addEntry(newEntry) ) {
            throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
                + " already in the directory");
        }
        return;
    }

    /**
     *  called by undo button.
     *  Revert to previous database state
     *  Reset the state of all the objects
     */
    public void undo () {
        super.undo();
        entryEditor.setVisible(false);
        searchBar.setText("");
        activeDirectoryEntry = null;
        activeRoom = null;
        filterAllEntries();
    }

    /**
     * Function to associate a room with a directory entry
     * Add room to activeDirectoryEntry, so it must not be null
     * @param room room to add to the entry
     */
    public void addLocationToEntry(String room) {
        activeDirectoryEntry.addLocation(map.getRoomFromName(room));
    }

    /* Functions for Rooms */
    public List<String> searchRoom(String search) {
        return null;
    }

    public void createRoom(String name, GraphNode node) {
        map.addRoom(new Room(node, name));
    }

    /** See method {@link Map#deleteRoom(Room)} */
    public boolean deleteRoom(Room room) {
        return map.deleteRoom(room);
    }

    /**
     * Shows the currently active directory entry with displayEntry
     */
    public void entryCancel () {
        if(activeDirectoryEntry != null) {
            displayEntry(activeDirectoryEntry);
        }
    }

    /**
     * save entry, calls the other save entry function to do the heavy lifting
     * handles the UI for saving
     */
    public void saveEntry () {
        if(activeDirectoryEntry == null) {
            return;
        }
        String name = entryName.getText();
        String title = entryTitle.getText();
        List<String> roomNames = entryCurrentLocations.getItems()
            .stream().map(String::toString)
            .collect(Collectors.toList());

        List<Room> rooms = roomNames
            .stream()
            .map(roomName -> map.getRoomFromName(roomName))
            .collect(Collectors.toList());

        String icon = (String)iconOption.getValue();

        try {
            saveEntry(name,title,rooms, icon);
        }
        catch (IllegalArgumentException arg) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), arg.toString());
        }
        catch (IllegalStateException state) {
            logger.error("Got error in {} : {}", this.getClass().getSimpleName(), state.toString());
        }

        filterAllEntries();
        activeDirectoryEntry = null;
        entryEditor.setVisible(false);
    }

    /**
     * activeDirectoryEntry must have the entry that is being edited
     * @param name name of the new entry
     * @param title title of the new entry
     * @param rooms list of room associated with the new entry
     * @throws IllegalStateException if entry is not selected
     * @throws IllegalArgumentException if key already exits
     */
    public void saveEntry(String name, String title, List<Room> rooms, String icon) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in app.display.DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, rooms, icon);

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
     * Take the active entry and display it in the entry editor
     * @param activeEntry
     */
    public void displayEntry (DirectoryEntry activeEntry) {
        entryEditor.setVisible(true);
        entryName.setText(activeEntry.getName());
        entryTitle.setText(activeEntry.getTitle());
        iconOption.setValue(activeEntry.getIcon());
        List<Room> locs = activeEntry.getLocation();
        List<String> locsAsString = locs
            .stream()
            .map(room -> room.getName())
            .collect(Collectors.toList());
        // FXCollections takes a normal list of strings and makes it viewable
        ObservableList<String> roomNames = FXCollections.observableList(locsAsString);

        iconOption.getSelectionModel().select(activeEntry.getIcon());

        entryCurrentLocations.setItems(roomNames);
    }

    /**
     * filter rooms for entries
     */
    public void filterRooms () {
        String searchText = entryRoomSearch.getText();
        logger.debug("Filter Room by {}", searchText);
        if (searchText.isEmpty()) {
            logger.debug("nothing entered");
        }
        else {
            List<String> results = map.subStringSearchRoom(searchText);
            List<String> currentLocs= entryCurrentLocations.getItems().stream()
                .map(String::toString)
                .collect(Collectors.toList());

            results = results.stream()
                .filter(res -> !currentLocs.contains(res))
                .collect(Collectors.toList());

            //TODO How to log this
            results.stream().forEach(System.out::println);

            ObservableList<String> roomEntries = FXCollections.observableList(results);
            entryRoomOptions.setVisible(true);
            entryRoomOptions.setItems(roomEntries);
        }
    }

    /**
     * Function to delete the selected Room
     */
    public void entryDeleteSelectedRoom () {
        String selectedString = entryCurrentLocations.getSelectionModel().getSelectedItem();
        if (selectedString != null) {
            entryRemoveRoom(selectedString);
            entryDeleteRoom.setVisible(false);
        }
        else {
            logger.error("Cannot delete room, no room selected");
            entryDeleteRoom.setVisible(false);
        }
    }

    /** TODO Figure out what this does
     * remove room from list
     * @param selection
     */
    public void entryRemoveRoom (String selection) {
        boolean didDelete = entryCurrentLocations.getItems().remove(selection);
        List<String> currentLocs= entryCurrentLocations.getItems().stream().
            map(String::toString).collect(Collectors.toList());


        List<String> newLocs = currentLocs.stream()
            .filter(str -> !str.equals(selection))
            .collect(Collectors.toList());

        ObservableList<String> newRoomNames = FXCollections.observableList(newLocs);
        entryCurrentLocations.setItems(newRoomNames);
    }

    /** TODO figure out what this does
     * Function to add
     * @param selection
     */
    public void entryAddRoom (String selection) {
        Room room = map.getRoomFromName(selection);
        if(room != null) {
            entryCurrentLocations.getItems().add(selection);
            entryRoomOptions.setVisible(false);
            entryRoomSearch.setText("");
        }
        else {
            logger.debug("Cannot add room to entry, no such room");
        }
    }

    /**
     * filter entries by searching for new text
     */
    public void filterAllEntries () {
        String searchText = searchBar.getText();
        logger.info("Filter entries by {}", searchText);
        if (searchText.isEmpty()) {
            List<String> entryList = map.getAllEntries();
            entryList.sort(String::compareTo);
//        entryList.admindAll(map.getAllRooms());
            ObservableList<String> allEntries = FXCollections.observableList(entryList);
            listEntries.setItems(allEntries);
        }
        else {
            List<String> results = map.searchEntry(searchText);
            //TODO how to log this
            results.stream().forEach(System.out::println);
            ObservableList<String> allEntries = FXCollections.observableList(results);
            listEntries.setItems(allEntries);
        }
    }

    /**
     * Allow selecting a file to import from
     */
    public void handleImportTSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import TSV");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("TSV or CSV", "*.tsv", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                map.importTSV(file);
            }
            catch (IOException e) {
                logger.error("Got error in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            }
        }
        filterAllEntries(); //Refresh entries
    }

    /**
     * go back to patient display
     */
    public void logout () {
        applicationController.logout();
    }
}
