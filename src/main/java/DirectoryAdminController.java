import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DirectoryAdminController extends DisplayController implements Initializable{
/*    DirectoryAdminDisplay display;*/
    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;
    String activeEntryRoomSelected; 

    // FXML stuff
    @FXML
    TextField searchBar;

    @FXML
    ListView<String> listEntries;

    @FXML
    AnchorPane roomEditor;
    @FXML
    AnchorPane entryEditor;

    @FXML
    TextField roomName;

    @FXML
    TextField serviceSearch;

    @FXML
    Button addServiceButton;

    @FXML
    ListView<String> serviceOptions;

    @FXML
    ListView<String> currentServices;

    @FXML
    TextField entryName;

    @FXML
    TextField entryTitle;

    @FXML
    TextField entryRoomSearch;

    @FXML
    Button entryAddRoomButton;

    @FXML
    ListView<String> entryRoomOptions;

    @FXML
    Button entryDeleteRoom; 

    @FXML
    ListView<String> entryCurrentLocations;


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

    public void selectEntry(String entryName) {
        activeDirectoryEntry = map.getEntry(entryName);
        if(activeDirectoryEntry == null){
            System.out.println("No entry :( ");
        }

        else{
            displayEntry(activeDirectoryEntry);
        }


        System.out.println("Selected: " + entryName);
    }

    public boolean deleteEntry(DirectoryEntry entry) {
        return false;
    }

    /**
     * create a new entry
     */
    public void createEntry() {
        activeDirectoryEntry = new DirectoryEntry("", "", new LinkedList<Room>());
        displayEntry(activeDirectoryEntry);
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

    public void entryCancel () {
        if(activeDirectoryEntry != null) {
            displayEntry(activeDirectoryEntry);
        }
    }

    /**
     * save entry
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

        saveEntry(name,title,rooms);
        serviceSearch.setText("");
        filterAllEntries();
        activeDirectoryEntry = null;
    }

    /**
     * activeDirectoryEntry must have the entry that is being edited
     * Throws error IllegalStateException if entry is not selected
     * and throws IllegalArgumentException if entry already exits
     * @param name name of the new entry
     * @param title title of the new entry
     * @param rooms list of room associated with the new entry
     */
    public void saveEntry(String name, String title, List<Room> rooms) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, rooms);

        if( map.getEntry(name) != null && map.getEntry(name).equals(newEntry) ) {
            throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
            + " already in the directory");
        }

        System.out.print(activeDirectoryEntry.getName());
        map.deleteEntry(activeDirectoryEntry.getName());
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

    public void displayEntry (DirectoryEntry activeEntry) {
        entryEditor.setVisible(true);
        entryName.setText(activeEntry.getName());
        entryTitle.setText(activeEntry.getTitle());
        List<Room> locs = activeEntry.getLocation();
        List<String> locsAsString = locs
            .stream()
            .map(room -> room.name)
            .collect(Collectors.toList());
        ObservableList<String> roomNames = FXCollections.observableList(locsAsString);
        entryCurrentLocations.setItems(roomNames);




    }
    /**
     * filter rooms for entries
     */
    public void filterRooms () {
        System.out.println("FILTER Room");
        String searchText = entryRoomSearch.getText();
        if (searchText.isEmpty()) {
            System.out.println("nothing entered");
        }
        else {
            System.out.println("search text" + searchText);
            List<String> results = map.subStringSearchRoom(searchText);
            results.stream().forEach(System.out::println);
            ObservableList<String> allEntries = FXCollections.observableList(results);
            entryRoomOptions.setVisible(true);
            entryRoomOptions.setItems(allEntries);
        }
    }

    public void entryDeleteSelectedRoom () {
        if (activeEntryRoomSelected != null) {
            entryRemoveRoom(activeEntryRoomSelected);
            entryDeleteRoom.setVisible(false); 
        }
        else {
            System.out.println("no room selected"); 
            entryDeleteRoom.setVisible(false); 
        }

    }


    /**
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
    /**
     *
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
     * go back to patient display
     */
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
        helpLabel.setText("Welcome to the directory entry editor.\n You're an admin, you don't need help" );

        // get both entries then rooms
        List<String> entryList = map.getAllEntries();
        entryList.sort(String::compareTo);
//        entryList.admindAll(map.getAllRooms());
        ObservableList<String> allEntries = FXCollections.observableList(entryList);
        listEntries.setItems(allEntries);
        listEntries.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = listEntries.getSelectionModel().getSelectedItem();
                selectEntry(selectedString);
            }
        });

        entryRoomOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = entryRoomOptions.getSelectionModel().getSelectedItem();
                entryAddRoom(selectedString);
            }
        });
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

}

