package app.display;

import app.applicationControl.ApplicationController;
import app.dataPrimitives.DirectoryEntry;
import app.dataPrimitives.GraphNode;
import app.datastore.Map;
import app.dataPrimitives.Room;
import app.applicationControl.Login;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DirectoryAdminController extends DisplayController{
/*    DirectoryAdminDisplay display;*/
    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;
    String activeEntryRoomSelected;

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
        System.out.println("INIT");
        helpLabel.setText("Welcome to the directory entry editor.\n You're an admin, you don't need help" );

        // get both entries
        List<String> entryList = map.getAllEntries();
        entryList.sort(String::compareTo);
        ObservableList<String> allEntries = FXCollections.observableList(entryList);
        listEntries.setItems(allEntries);
        listEntries.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = listEntries.getSelectionModel().getSelectedItem();
                selectEntry(selectedString);
            }
        });

        // add click handler for the dropdown list of poossible locations
        entryRoomOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = entryRoomOptions.getSelectionModel().getSelectedItem();
                entryAddRoom(selectedString);
            }
        });
        // add click handlers to the list of currentRooms
        entryCurrentLocations.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("remove entry");
                String selectedString = entryCurrentLocations.getSelectionModel().getSelectedItem();
                activeEntryRoomSelected = selectedString;
                entryDeleteRoom.setVisible(true);
                // entryRemoveRoom(selectedString);
            }
        });
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
    public void selectEntry(String entryName) throws IllegalArgumentException {
        activeDirectoryEntry = map.getEntry(entryName);
        if( activeDirectoryEntry == null ) {
            throw new IllegalArgumentException();
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
            System.out.println("No entry selected");
        }
    }

    /**
     * Create a new directory entry in the app.datastore.Directory object in the app.datastore.Map
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
        activeEntryRoomSelected = null;
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

        try {
            saveEntry(name,title,rooms);
        }
        catch (IllegalArgumentException arg) {
            System.out.println(arg.toString());
        }
        catch (IllegalStateException state) {
            System.out.println(state.toString());
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
    public void saveEntry(String name, String title, List<Room> rooms) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in app.display.DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, rooms);

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
        List<Room> locs = activeEntry.getLocation();
        List<String> locsAsString = locs
            .stream()
            .map(room -> room.getName())
            .collect(Collectors.toList());
        // FXCollections takes a normal list of strings and makes it viewable
        ObservableList<String> roomNames = FXCollections.observableList(locsAsString);
        entryCurrentLocations.setItems(roomNames);
    }

    /**
     * filter rooms for entries
     */
    public void filterRooms () {
        System.out.println("FILTER app.dataPrimitives.Room");
        String searchText = entryRoomSearch.getText();
        if (searchText.isEmpty()) {
            System.out.println("nothing entered");
        }
        else {
            System.out.println("search text" + searchText);
            List<String> results = map.subStringSearchRoom(searchText);
            List<String> currentLocs= entryCurrentLocations.getItems().stream()
                .map(String::toString)
                .collect(Collectors.toList());

            results = results.stream()
                .filter(res -> !currentLocs.contains(res))
                .collect(Collectors.toList());

            results.stream().forEach(System.out::println);

            ObservableList<String> roomEntries = FXCollections.observableList(results);
            entryRoomOptions.setVisible(true);
            entryRoomOptions.setItems(roomEntries);
        }
    }

    /**
     * Function to delete the activeEntryRoomSelected
     */
    public void entryDeleteSelectedRoom () {
        if (activeEntryRoomSelected != null) {
            entryRemoveRoom(activeEntryRoomSelected);
            entryDeleteRoom.setVisible(false);
            activeEntryRoomSelected = null;
        }
        else {
            System.out.println("no room selected");
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
            System.out.println("no such room");
        }
    }

    /**
     * filter entries by searching for new text
     */
    public void filterAllEntries () {
        System.out.println("FILTER");
        String searchText = searchBar.getText();
        if (searchText.isEmpty()) {
            List<String> entryList = map.getAllEntries();
            entryList.sort(String::compareTo);
//        entryList.admindAll(map.getAllRooms());
            ObservableList<String> allEntries = FXCollections.observableList(entryList);
            listEntries.setItems(allEntries);
        }
        else {
            System.out.println("search text");
            List<String> results = map.searchEntry(searchText);
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
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * go back to patient display
     */
    public void logout () {
        applicationController.logout();
    }
}
