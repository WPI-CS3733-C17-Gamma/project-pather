import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * controls all interaction with the patient display
 */
public class PatientController extends DisplayController implements Initializable {

    private enum state{
        PATIENT_DEFAULT,
        PATIENT_SEARCH
    }

    state displayState;
    // kiosk location
    GraphNode startNode;
    // list of shapes that have been drawn on the screen
    List<Shape> drawnObjects;
    // FXML Things
    @FXML private TextField searchBar;
    @FXML private ListView<String> options;
    @FXML private ImageView imageView;
    @FXML private Label textDirectionsTextBox;
    @FXML private AnchorPane anchorPane;
    @FXML private Label helpLabel;

    @FXML private AnchorPane searchAnchorPane;
    @FXML private Button help;
    @FXML private ImageView patientImageView;
    @FXML private Button exitButton;
    @FXML private HBox multiMapDisplayMenu;
    @FXML private Button adminButton;
    @FXML private Button directoryAdminButton;
    @FXML private Button mapAdminButton;
    @FXML private AnchorPane adminPane;
    @FXML private Button patientDisplayButton;
    @FXML private Button login;

    private List<SubPath> currentPath;

    /**
     *
     * @param map
     * @param applicationController
     * @param currentMap
     */
    public PatientController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        super(map,applicationController, currentMap);

        displayState = state.PATIENT_DEFAULT;
    }

    /**
     * display the image
     */
    public void displayImage() {
        Image floor = super.applicationController.getImage(currentMap);
        if (displayState == state.PATIENT_DEFAULT){
            imageView.setImage(floor);
        }
        if (displayState == state.PATIENT_SEARCH){
//            patientImageView.setImage(floor);
        }
    }

    /**
     *
     */
    public void startSearch(){
        if (this.displayState == state.PATIENT_DEFAULT){
//            patientImageView.setVisible(true);
            searchAnchorPane.setVisible(true);
//            exitButton.setVisible(true);
            this.displayState = state.PATIENT_SEARCH;
            displayImage();
        }
    }

    public void exitSearch(){
        System.out.println("Exit button works");
        if (this.displayState == state.PATIENT_SEARCH){
//            patientImageView.setVisible(false);
            searchAnchorPane.setVisible(false);
//            exitButton.setVisible(false);
            this.displayState = state.PATIENT_DEFAULT;
            clearSearchDisplay();
            displayImage();
        }
    }

    /**
     * perform search
     */
    public void search () {
        clearSearchDisplay();
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
        displayResults(results);
    }

    /**
     * display set of result strings
     * @param results
     */
    public void displayResults (List<String> results) {
        if(!results.isEmpty()) {
            ObservableList<String> filter = FXCollections.observableArrayList(results);
            options.setItems(filter);
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
        //(update) the display the list of room
    }

    /**
     * Toggles admin display
     */
    public void toggleAdminWindow(){
        adminPane.setVisible(!adminPane.isVisible());
    }

    /**
     * swtich to map admin
     */
    public void switchToMapAdmin() {
        applicationController.createMapAdminDisplay(new Login());//*********************************
    }


    /**
     * swtich to directory admin
     */
    public void switchToDirectoryAdmin () {
        System.out.println("SWITCHING TO DIR ADMIN");
        applicationController.createDirectoryAdminDisplay(new Login());//************************
    }

    /**
     * select the clicked option
     * @param option
     * @return
     */
    public GraphNode select(String option) {
        searchBar.setText(option);
        System.out.println("select");
        DirectoryEntry entry = map.getEntry(option);
        // if the selected entry is an entry not a room
        if (entry != null) {
            System.out.println("Found Entry!");
            List<Room> locs = entry.getLocation();

            // if no location, should (probably) throw error
            if(locs.size() == 0) {
                System.out.println("No location");
                return null;
            }
            // take first option if only one
            else if (locs.size() == 1) {
                System.out.println(locs.get(0).location);
                displayResults(new LinkedList<>());
                getPath(startNode, locs.get(0).location);
                return locs.get(0).location;
            }
            // too many options, redisplay options
            else {
               List<String> serviceLocations = locs.stream()
                    .map(room -> room.name)
                    .collect(Collectors.toList());
               displayResults(serviceLocations);
               return null;
            }
        }
        else {
            Room room =  map.getRoomFromName(option);
            if (room != null) {
                System.out.println("FOUND ROOM! : " + room);
                displayResults(new LinkedList<>());
                getPath(startNode, room.location);
                return room.location;
            }
            else {
                System.out.println("no entry :( ");
            }
        }

        return null;
    }

    public void clearSearchDisplay(){
        hideMultiMapAnimation();
//        patientImageView.setImage(null);
        multiMapDisplayMenu.getChildren().clear();
        clearDisplay();
    }

    /**
     * Remove all the points that have been drawn on the map
     */
    public void clearDisplay () {
        patientImageView.setImage(null);
        if(drawnObjects == null) {
            return;
        }
        for (Shape shape : drawnObjects) {
            anchorPane.getChildren().remove(shape);
        }
        textDirectionsTextBox.setVisible(false);
    }

    /**
     * convert the graph 1000 * 1000 coordinate to the size of the image view
     * The point must be added to the direct parent of the image view for this to work
     * OR all points must be added ot the scene
     * @param node
     * @param imageToBeDrawnOver image the the coordinate must be scaled to
     * @return
     */
    FloorPoint graphPointToImage (GraphNode node, ImageView imageToBeDrawnOver) {
        double imageWidth = imageToBeDrawnOver.getBoundsInLocal().getWidth();
        double imageHeight = imageToBeDrawnOver.getBoundsInLocal().getHeight();
        double offsetX = imageToBeDrawnOver.getLayoutX();
        double offsetY = imageToBeDrawnOver.getLayoutY();

        System.out.println("off x " + offsetX + "  off y "  + offsetY);

        int newX = (int)(node.location.x * imageWidth / 1000. + offsetX );
        int newY = (int)(node.location.y * imageHeight / 1000. + offsetY );
        System.out.printf("image width : %f \nimage Height : %f\n", imageWidth, imageHeight);

        return new FloorPoint(newX, newY, node.location.floor);
    }



    /**
     *
     * @param start
     * @param end
     */
    public void getPath (GraphNode start, GraphNode end) {
        try {
            currentPath = map.getPathByFloor(start, end);
            displaySubPath(patientImageView, currentPath.get(0));
            for (int x = 0; x < currentPath.size(); x++){
                SubPath p = currentPath.get(x);
                ImageView i = new ImageView();
                i.setPreserveRatio(true);
                i.setFitHeight(95);
                i.setFitWidth(165);
                i.setOnMousePressed(e -> mapChoice(e));
                i.setImage(applicationController.getImage(p.floor));
                i.setId(x + "floor in list");
//                i.getStyleClass().add("tinyMapMenu");
//                i.applyCss();
                System.out.println(i.getId());
               multiMapDisplayMenu.getChildren().add(i);
            }
            showMultiMapAnimation();
        } catch (PathNotFoundException e) {
            System.out.println("No path can be drawn");
        }
    }

    public void mapChoice(MouseEvent e){
        ImageView iv = (ImageView) e.getSource();
        System.out.println(iv.getId() + "*******");
        SubPath path = currentPath.get((int)iv.getId().charAt(0) - 48);//ascii conversion
        displaySubPath(patientImageView, path);
    }

    public void showMultiMapAnimation(){
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv = new KeyValue(multiMapDisplayMenu.layoutYProperty(), 475);
        final KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        System.out.println("hello2");
    }

    public void hideMultiMapAnimation(){
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv = new KeyValue(multiMapDisplayMenu.layoutYProperty(), 600);
        final KeyFrame kf = new KeyFrame(Duration.millis(100), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        System.out.println("hello3");
    }


    /**
     * Display the given sub path over the given image view
     * Addds all the drawn objects to a list of drawn objects
     * Changes the image view to the image of the floor the sub path covers
     * @param mapImage
     * @param subPath subpath to be drawn
     */
    public void displaySubPath (ImageView mapImage, SubPath subPath) {
        clearDisplay();
        System.out.println("SubPath");
        GraphNode prev = null;
        mapImage.setImage(applicationController.getImage(subPath.floor));
        List<Shape> listToDraw = new ArrayList<>();
        // draw path, and all connections from previous
        for (GraphNode node : subPath.path) {
            FloorPoint localPoint = graphPointToImage(node, mapImage);
            System.out.println(localPoint);
            listToDraw.add(drawPoint(localPoint));
            // draw connection
            if (prev != null) {
                listToDraw.add(drawConnection(prev, node, mapImage));
            }
            prev = node;
        }
        if(drawnObjects == null) {
            drawnObjects = new ArrayList<>();
        }
        drawnObjects.addAll(listToDraw);
    }

    /**
     * draw path on screen
     * @param path
     */
    public void displayPath(List<GraphNode> path) {
        GraphNode prev = null;
        List<Shape> listToDraw = new ArrayList<>();
        // draw path, and all connections from previous
        for (GraphNode node : path) {
            FloorPoint localPoint = graphPointToImage(node, imageView);
            listToDraw.add(drawPoint(localPoint));
            // draw connection
            if (prev != null) {
                listToDraw.add(drawConnection(prev, node, imageView));
            }
            prev = node;
        }
        drawnObjects = listToDraw;
    }


    /**
     * Function to get textual directions and print it on screen
     * @param path the path to be converted to text
     */
    public void displayTextDirections(List<GraphNode> path) {
        textDirectionsTextBox.setVisible(true);
        List<String> directions = map.getTextualDirections(path);
        String dir = "";
        for(String line : directions) {
            dir += (line + "\n");
        }
        textDirectionsTextBox.setText(dir);
        return;
    }

    /**
     * given local point, draw the point
     * @param localPoint
     */
    public Shape drawPoint (FloorPoint localPoint) {
        Circle c = new Circle(localPoint.x, localPoint.y, 5);
        c.setFill(Color.BLUE);
        anchorPane.getChildren().add(c);
        return c;
    }

    /**
     * draw the line between two nodes
     * @param nodeA
     * @param nodeB
     * @return
     */
    public Shape drawConnection (GraphNode nodeA, GraphNode nodeB, ImageView imageToBeDrawnOver) {
        FloorPoint pointA = graphPointToImage(nodeA, imageToBeDrawnOver);
        FloorPoint pointB = graphPointToImage(nodeB, imageToBeDrawnOver);

        Line line = new Line(pointA.x, pointA.y, pointB.x, pointB.y);
        line.setStrokeWidth(4);
        line.setFill(Color.BLUE);
        line.setStrokeWidth(1);

        anchorPane.getChildren().add(line);

        return line;
    }


    public void logIn () {
        applicationController.createLoginAdmin();
    }

    public void help () {
        System.out.println("Here is how to use this...");
        if (helpLabel.isVisible()) {
            helpLabel.setVisible(false);
        }
        else {
           helpLabel.setVisible(true);
        }
    }
    /**
     * initialize the fxml components etc
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("INIT");
        displayImage();

        // TODO will need to be changed to kiosk
        this.startNode = new GraphNode(100, 100, "");
        try {
            this.startNode = map.getRoomFromName("Kiosk").location;
        }
        catch (Exception e) {
            System.out.println("No Kiosk");
        }


        options.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = options.getSelectionModel().getSelectedItem();
                System.out.println("clicked on " + selectedString);
                select(selectedString);
            }
        });
        // the image view should be the bottom pane so circles can be drawn over it
        imageView.toBack();
//        imageView.setPreserveRatio(false);
        helpLabel.setText("Hello! Thanks for using project-pather." +
            "\n\nTo get started, start typing into the search bar. " +
            "\n Then, select the option you would like to get a path to." +
            "\n\nTo close this menu, click on this");
    }


}
