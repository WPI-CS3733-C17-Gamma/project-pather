package app.display;

import app.CustomMenus.CircularContextMenu;
import app.applicationControl.ApplicationController;
import app.dataPrimitives.*;
import app.pathfinding.PathNotFoundException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * controls all interaction with the patient display
 */
public class PatientController extends DisplayController {
    final static Logger logger = LoggerFactory.getLogger(PatientController.class);

    //what type/state of display the Patient Display is currently displaying
    private enum state{
        PATIENT_DEFAULT,
        PATIENT_SEARCH,
    }

    state displayState = state.PATIENT_DEFAULT;
    // list of shapes that have been drawn on the screen
    List<Node> drawnObjects = new ArrayList<>();
    // FXML Things
    @FXML private ComboBox<String> searchBar;
    @FXML private HBox multiMapDisplayMenu;
    @FXML private ImageView imageView;
    @FXML private Pane imagePane;
    @FXML private ToggleGroup floorPicker;
    @FXML private TabPane sidebar;

    @FXML private ListView<String> textDirectionsListView;

    private List<SubPath> currentPath;
    private int currentSubPath;

    private LinkedList<Pair<Integer, String>> currentTextDirections;
    private Shape drawnTextShape;

    private LinkedList<Label> roomLabels = new LinkedList<>();
    private LinkedList<Minimap> minimaps = new LinkedList<>();

    private final Rectangle highlight = new Rectangle(133, 75, Color.GRAY);

    private String currentMap;

    private boolean togStairs;

    CircularContextMenu menu = new CircularContextMenu();

    /**
     *
     * @param map
     * @param applicationController
     * @param currentMap
     */
    public void init(app.datastore.Map map,
                ApplicationController applicationController,
                Stage stage,
                String currentMap){
        super.init(map,applicationController, stage);
        this.currentMap = currentMap;
    }

    /**
     * hides the patient search interface and returns to the default patient interface
     */
    public void exitSearch(){
        logger.debug("Exiting search in {}", this.getClass().getSimpleName());
        hideMinipaths();
        this.displayState = state.PATIENT_DEFAULT;
        clearSearchDisplay();
        drawRoomLabels();
        searchBar.getEditor().clear();
        searchBar.getSelectionModel().select(null);
        //Select floor selection pane
        sidebar.getSelectionModel().select(0);
    }

    /**
     * perform search; get text from the textfield
     * @param search the string to filter by
     */
    public void filterResults(String search) {
        final String selected = searchBar.getSelectionModel().getSelectedItem();
        logger.info("Searching : {}, Selected : {}", search, selected);
        if (selected == null || selected == "" || selected.equals(search)) {
            List<String> results = search(search);
            displayResults(results);
        }
    }

    /**
     * get rooms / entries related to entry
     * @param searchTerm
     * @return
     */
    public List<String> search(String searchTerm) {
        if (searchTerm.length() == 0) {
            return new LinkedList<>();
        }
        if (((int)searchTerm.charAt(0)) < 58 && ((int)searchTerm.charAt(0)) > 47){
            return map.searchRoom(searchTerm);
        }
        // if the first letter is not a number, search for entries first, then add all the rooms
        // <></>o the bottom of the list
        else{
            String lowerCaseSearch = searchTerm.toLowerCase();
            List<String> results = map.searchEntry(lowerCaseSearch) ;
            results.addAll(map.searchRoom(searchTerm));
            return results;
        }
    }

    /**
     * display set of result strings
     * @param results
     */
    public void displayResults (List<String> results) {
        if(!results.isEmpty()) {
            ObservableList<String> items = FXCollections.observableArrayList(results);
            // Create a FilteredList wrapping the ObservableList.
            FilteredList<String> filteredItems = new FilteredList<String>(items, p -> true);

            searchBar.setItems(filteredItems);
            searchBar.show();
        }
        else {
            searchBar.hide();
        }
    }

    /**
     * select the clicked option
     * @param option
     * @return
     */
    public GraphNode select(String option) {
        logger.info("Select {}", option);
        DirectoryEntry entry = map.getEntry(option);
        // if the selected entry is an entry not a room
        if (entry != null) {
            logger.debug("Found Entry!");
            List<Room> locs = entry.getLocation();

            // if no location, should (probably) throw error
            if(locs.isEmpty()) {
                return null;
            }
            // take first option if only one
            else if (locs.size() == 1) {
                logger.debug("Found desired graph node at: {}",
                    locs.get(0).getLocation().toString());
                getPath(map.getKioskLocation(), locs.get(0).getLocation(), false);
                //Select text directions pane
                sidebar.getSelectionModel().select(1);
                resetSearch(option);
                return locs.get(0).getLocation();
            }
            // too many options, redisplay options
            else {
               List<String> serviceLocations = locs.stream()
                    .map(room -> room.getName())
                    .collect(Collectors.toList());
               displayResults(serviceLocations);
               return null;
            }
        }
        else {
            Room room =  map.getRoomFromName(option);
            if (room != null) {
                logger.debug("FOUND ROOM! : " + room);
                getPath(map.getKioskLocation(), room.getLocation(), false);
                //Select text directions pane
                sidebar.getSelectionModel().select(1);
                resetSearch(option);
                return room.getLocation();
            }
            else {
                logger.debug("No entry found");
            }
        }

        return null;
    }

    /**
     * Reset the searchBar, setting the selected string as the prompt text
     * @param selected the new prompt text
     */
    private void resetSearch(String selected) {
        searchBar.setPromptText(selected);
        searchBar.getEditor().clear();
        searchBar.getSelectionModel().clearSelection();
        searchBar.setItems(null);
        imageView.requestFocus();
    }

    /**
     * remove search result
     */
    public void clearSearchDisplay(){
        hideMinipaths();//hide the hBox thingy
        multiMapDisplayMenu.getChildren().clear();//clear the hBox menu thingy
        clearDisplay();
    }


    /**
     * Remove all the points and labels that have been drawn on the map
     */
    public void clearDisplay () {
        if(drawnObjects == null) {
            logger.debug("No objects were drawn");
            return;
        }
        imagePane.getChildren().removeAll(drawnObjects);
        imagePane.getChildren().removeAll(roomLabels);
    }

    /**
     * Sets the current floor to the map
     * @param floor the name of the floor
     */
    public void setFloor(String floor) {
        currentMap = floor;
        imageView.setImage(applicationController.getFloorImage(currentMap));
        clearDisplay();
        drawRoomLabels();
    }

    /**
     * convert the graph 1000 * 1000 coordinate to the size of the image view
     * The point must be added to the direct parent of the image view for this to work
     * OR all points must be added ot the scene
     * @param node
     * @param imageToBeDrawnOver image the the coordinate must be scaled to
     * @return
     */
    private static FloorPoint graphPointToImage (GraphNode node,
                                                 Node imageToBeDrawnOver) {
        double imageWidth = imageToBeDrawnOver.getBoundsInLocal().getWidth();
        double imageHeight = imageToBeDrawnOver.getBoundsInLocal().getHeight();

        int newX = (int)(node.getLocation().getX() * imageWidth / 1000.);
        int newY = (int)(node.getLocation().getY() * imageHeight / 1000.);
        logger.debug("Image width : {}, Image Height : {}", imageWidth, imageHeight);

        return new FloorPoint(newX, newY, node.getLocation().getFloor());
    }

    /**
     * get paths across multiple floor and display different resulted floors on the search interface (the large ImageView and the hBox)
     * @param start the starting location
     * @param end the ending location
     * @param useStairs true if the stairs are to be forced into use
     */
    public void getPath (GraphNode start, GraphNode end, boolean useStairs) {
        hideMinipaths();
        displayState = state.PATIENT_SEARCH;
        minimaps = new LinkedList<>();
        if (start == null || end == null) {
            logger.error("Cannot path, start or end is null!");
        }
        try {
            currentPath = map.getPathByFloor(start, end, useStairs);
            changeSubpath(0); // show first subPath
            for (int x = 0; x <currentPath.size(); x++){
                SubPath p = currentPath.get(x);
                Pane pane = new Pane();
                ImageView currentImageView = new ImageView();

                currentImageView.setPreserveRatio(true);
                currentImageView.setFitHeight(75);
                currentImageView.setFitWidth(133);
                currentImageView.setOnMousePressed(e -> mapChoice(e));
                currentImageView.setImage(applicationController.getFloorImage(p.getFloor()));
                currentImageView.setId(x + "floor in list");
                logger.debug("Current image view id: {}", currentImageView.getId());
                pane.getChildren().add(currentImageView);
               multiMapDisplayMenu.getChildren().add(pane);
               minimaps.add(new Minimap(currentImageView, pane, p));
            }
            initializeMinimaps();
        } catch (PathNotFoundException e) {
            logger.error("No path can be drawn");
        }
    }

    /**
     * toggle between resulted maps when clicking on the corresponding imageView in the HBox
     * @param e
     */
    public void mapChoice(MouseEvent e){
        Pane pane = (Pane) e.getSource();

        logger.debug("Image view ID: {}", pane.getId());
        changeSubpath((int) pane.getId().charAt(0) - 48); //ascii conversion
        pane.getChildren().add(highlight);
        highlight.toBack();
    }

    /**
     * Handles changing subPaths
     * @param id the subPath to change to
     */
    private void changeSubpath(int id) {
        textDirectionsListView.getSelectionModel().select(null);
        currentSubPath = id;
        SubPath path = currentPath.get(currentSubPath);
        clearDisplay();
        displaySubPath(imageView, imagePane, path, true,10, 1,20);

        // Directions stuff
        String nextFloor = null;
        if(currentPath.size() > currentSubPath+1) {
            nextFloor = currentPath.get(currentSubPath + 1).getFloor();
        }
        setTextDirections(path.getPath(), nextFloor);
    }

    /**
     * Display the given sub path over the given image view
     * Addds all the drawn objects to a list of drawn objects
     * Changes the image view to the image of the floor the sub path covers
     * @param image
     * @param subPath subpath to be drawn
     */
    public void displaySubPath (ImageView image, Pane drawPane,
                                SubPath subPath, boolean drawLabels,
                                int nodeRadius, double arrowHeadSize, int labelFontSize) {
        GraphNode prev = null;
        image.setImage(applicationController.getFloorImage(subPath.getFloor()));
        List<Shape> listToDraw = new ArrayList<>();
        Shape startPoint = new Circle();
        Shape endPoint = new Circle();

        Iterator<GraphNode> iterator = subPath.getPath().iterator();
        if (iterator.hasNext()){
            prev = iterator.next();
            FloorPoint localPoint = graphPointToImage(prev, image);
            startPoint = drawPoint(drawPane, localPoint, nodeRadius, Color.GREEN);
            listToDraw.add(startPoint);
        }
        // draw path, and all connections from previous
        while(iterator.hasNext()){
            GraphNode node = iterator.next();
            FloorPoint localPoint = graphPointToImage(node, image);
            if (!iterator.hasNext()){
                endPoint = drawPoint(drawPane, localPoint, nodeRadius, Color.RED);
                listToDraw.add(endPoint);
            }
            // draw connection
            listToDraw.add(drawConnection(drawPane, image, prev, node));
            listToDraw.add(drawArrowHeads(drawPane, image, prev, node, arrowHeadSize));
            startPoint.toFront();
            endPoint.toFront();
            prev = node;
        }

        startPoint.toFront();
        endPoint.toFront();
        if(drawnObjects == null) {
            drawnObjects = new ArrayList<>();
        }
        drawnObjects.addAll(listToDraw);
        drawFloorLabel(drawPane, subPath, labelFontSize);
        if(drawLabels) {
            roomLabels = getRoomLabels(subPath);
            displayRoomLabels(roomLabels);
        }
    }

    /**
     * Draw a point of a given color and radius
     * @param pane
     * @param localPoint
     * @param radius
     * @param color
     */
    public static Shape drawPoint (Pane pane, FloorPoint localPoint, int radius, Color color) {
        Circle c = new Circle(localPoint.getX(), localPoint.getY(), radius);
        c.setFill(color);
        c.setMouseTransparent(true);
        pane.getChildren().add(c);
        return c;
    }

    /**
     * draw the line between two nodes
     * @param nodeA
     * @param nodeB
     * @return
     */
    public static Shape drawConnection (Pane pane, ImageView image, GraphNode nodeA, GraphNode nodeB) {
        FloorPoint pointA = graphPointToImage(nodeA, image);
        FloorPoint pointB = graphPointToImage(nodeB, image);

        Line line = new Line(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
        line.setStrokeWidth(4);
        line.setMouseTransparent(true);
        line.setStroke(Color.rgb(88, 169, 196));

        pane.getChildren().add(line);

        return line;
    }
        /**
     * draw the arrow head for line between two nodes
     * @param nodeA
     * @param nodeB
     * @return arrow head to be drawn
     */
    public Shape drawArrowHeads (Pane pane, ImageView imageView, GraphNode nodeA, GraphNode nodeB, double arrowSize) {
        FloorPoint pointA = graphPointToImage(nodeA, imageView);
        FloorPoint pointB = graphPointToImage(nodeB, imageView);

        Polygon triangle = new Polygon();

        double delx = pointB.getX() - pointA.getX();
        double dely = pointB.getY() - pointA.getY();

        double angle = Math.toDegrees(Math.atan2(delx, dely));

        double dist = Math.sqrt(Math.pow(delx,2) + Math.pow(dely, 2))/2;

        Point2D point1 = new Point2D(7*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)), -7*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)));
        Point2D point2 = new Point2D(-7*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)), 7*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)));
        Point2D point3 = new Point2D(10*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.sin(arrowSize*Math.toRadians(angle)), 10*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)) - dist*arrowSize*Math.cos(arrowSize*Math.toRadians(angle)));


        triangle.getPoints().addAll(new Double [] {point3.getX(), point3.getY(), point2.getX(), point2.getY(), point1.getX(), point1.getY()});
        triangle.setLayoutX(pointB.getX());
        triangle.setLayoutY(pointB.getY());
        triangle.setFill(Color.rgb(88, 169, 196));

        pane.getChildren().add(triangle);

        return triangle;
    }

    /**
     * gets app.dataPrimitives.Room description on screen or display elevators
     * @param subpath
     */
    public LinkedList<Label> getRoomLabels(SubPath subpath){
        LinkedList<Label> labels = new LinkedList<>();
        Room room;

        List<GraphNode> path = subpath.getPath();

        for (GraphNode node: path){
            Label current = new Label();
            room = map.getRoomFromNode(node);
            FloorPoint point;
            int roomx;
            int roomy;

            //add elevator icon if applicable
            if(node.doesCrossFloor()){
                ImageView image = new ImageView();
                image.setImage(applicationController.getIconImage("elevator"));
                image.setPreserveRatio(true);
                image.setFitWidth(20);
                image.setFitHeight(20);
                current.setGraphic(image);
                logger.debug("node to get image: {}", node);
                point = graphPointToImage(node, imageView);
                roomx = point.getX() - 30;
                roomy = point.getY() - 5;
                current.setLayoutX(roomx);
                current.setLayoutY(roomy);
                labels.add(current);
            }
            if(room != null) {
                point = graphPointToImage(room.getLocation(), imageView);
                roomx = point.getX() + 5;
                roomy = point.getY();

                current.setText(room.getName());
                current.setLayoutX(roomx);
                current.setLayoutY(roomy);
                current.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,new CornerRadii(4),BorderWidths.DEFAULT)));
                current.setBackground(new Background(new BackgroundFill(Color.rgb(124,231,247), new CornerRadii(4),
                    new Insets(0.0,0.0,0.0,0.0))));
                current.setMouseTransparent(true);
                labels.add(current);
            }
        }
	drawnObjects.addAll(labels);
    return labels;
    }


    /**
     * Initialises the minimaps after animation
     */
    public void initializeMinimaps(){
        displayMinipaths();
        minimaps.get(currentSubPath).pane.getChildren().add(highlight);
        highlight.toBack();
    }

    /**
     * Displays paths on minimaps
     */
    public void displayMinipaths(){
        for(Minimap minimap: minimaps){
            displaySubPath(minimap.map, minimap.pane, minimap.path, false,3, 0,10);
        }
    }

    /**
     * Displays paths on minimaps
     */
    public void hideMinipaths() {
        multiMapDisplayMenu.getChildren().clear();
    }


    /**
     * given an image view and a subpath, draw a floor label
     * @param drawPane
     * @param subPath
     * @param labelFontSize
     */
    public void drawFloorLabel(Pane drawPane, SubPath subPath, int labelFontSize){
        Label label = new Label(subPath.getFloor());
        label.setFont(Font.font ("Georgia", labelFontSize));
        FloorPoint temp = graphPointToImage(new GraphNode(50, 30, "one"), drawPane);
        label.setLayoutX(temp.getX());
        label.setLayoutY(temp.getY());
        label.setTextFill(Color.rgb(27, 68, 156));
        drawPane.getChildren().add(label);
        this.drawnObjects.add(label);
    }

    /**
     * Display labels on app.datastore.Map
     * @param labels
     */
    public void displayRoomLabels(LinkedList<Label> labels){
        for(Label label: labels){
            if(! imagePane.getChildren().contains(label)){
                imagePane.getChildren().add(label);
            }
        }
    }

    public void drawRoomLabels() {
        List<String> roomNames= map.getAllRooms();
        for (String roomName : roomNames) {
            Room cur = map.getRoomFromName(roomName);
            GraphNode loc = cur.getLocation();
            // skip rooms without locations
            if (loc == null || ! loc.getLocation().getFloor().equals(currentMap)) {
                continue;
            }

            FloorPoint imageLoc = graphPointToImage(loc, imageView);
            String labelName = cur.getName();
            Label label = new Label(labelName);
            label.setLayoutX(imageLoc.getX() + 3);
            label.setLayoutY(imageLoc.getY() + 3);
            label.setFont(Font.font ("Georgia", 10));
            label.setStyle("-fx-background-color: #F0F4F5; -fx-border-color: darkblue;-fx-padding: 2;");
            Circle circ = new Circle(2, Color.BLACK);
            circ.setLayoutX(imageLoc.getX());
            circ.setLayoutY(imageLoc.getY());
            imagePane.getChildren().add(circ);
            imagePane.getChildren().add(label);
            logger.debug("Adding Label {}", labelName);
            drawnObjects.add(label);
            drawnObjects.add(circ);
        }
    }

    /**
     * Function to get textual directions and print it on screen
     * @param path the path to be converted to text
     * @param nextFloor the next floor to be pathed to, can be null
     */
    public void setTextDirections(List<GraphNode> path, String nextFloor) {
        LinkedList<Pair<Integer, String>> textDirections = map.getTextualDirections(path, nextFloor);
        List<String> directions = textDirections.stream().map(p -> p.getValue()).collect(Collectors.toList());
        textDirectionsListView.setItems(FXCollections.observableList(directions));
        currentTextDirections = textDirections;
        return;
    }


    public void logIn () {
        applicationController.createLoginAdmin();
    }

    private void updateSize() {
        clearDisplay();
        switch (displayState) {
            case PATIENT_DEFAULT:
                drawRoomLabels();
                break;
            case PATIENT_SEARCH:
                displaySubPath(imageView, imagePane,
                    currentPath.get(currentSubPath), true, 10, 1,20);
        }
    }

    /**
     * initialize the fxml components etc
     */
    @FXML
    public void initialize() {
        logger.info("INIT PatientController");

        imageView.setMouseTransparent(true);

        searchBar.getEditor().textProperty().addListener(
            (observable, oldValue, newValue) ->
                // Avoid wierd threading stuff
                Platform.runLater(() -> filterResults(newValue)));

        searchBar.getSelectionModel().selectedItemProperty().addListener(
            (observableValue, old_val, new_val) -> {
                logger.debug("clicked on {}", new_val);
                Platform.runLater(() -> select(new_val));
        });

        //When the text directions box is clicked
        textDirectionsListView.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> {
                logger.debug("Selected {} in text directions", newValue);
                if(newValue != null
                    && (int) newValue >= 0
                    && (int) newValue < currentTextDirections.size()) {

                    if (drawnTextShape != null) {
                        imagePane.getChildren().remove(drawnTextShape);
                    }
                    GraphNode node = currentPath.get(currentSubPath).getPath().get(
                        currentTextDirections.get((int) newValue).getKey());
                    drawnTextShape = drawPoint(imagePane, graphPointToImage(node, imageView), 5, Color.BLUE);
                }
            }
        );

        imagePane.widthProperty().addListener((observable, oldValue, newValue) -> {
            imageView.setFitWidth(imagePane.widthProperty().doubleValue());
            updateSize();
        });
        imagePane.heightProperty().addListener((observable, oldValue, newValue) -> {
            imageView.setFitHeight(imagePane.heightProperty().doubleValue());
            updateSize();
        });

        floorPicker.selectedToggleProperty().addListener(
            (observableValue, old_val, new_val) ->
                setFloor(((ToggleButton) new_val).getId()));

        setFloor("floor3");
        // the image view should be the bottom pane so circles can be drawn over it
        imageView.toBack();

        helpLabel.setText("Hello! Thanks for using project-pather." +
                          "\n\nTo get started, start typing into the search bar. " +
                          "\n Then, select the option you would like to get a path to." +
                          "\n\nTo close this menu, click on this");
    }


    /**
     * change the cursor to hand (like the one on top of buttons)
     * @param e
     */
    public void setMouseToHand(MouseEvent e){
        ((Label)e.getSource()).getScene().setCursor(Cursor.HAND);
    }

    /**
     * set the cursor to default
     * @param e
     */
    public void setMouseToNormal(MouseEvent e){
        ((Label)e.getSource()).getScene().setCursor(Cursor.DEFAULT);
    }

    public boolean toggleStairs(MouseEvent e) {
        togStairs = !togStairs;
        return togStairs;
    }
}

/**
 * Minimaps contain a map and a path
 */
class Minimap{
    ImageView map;
    Pane pane;
    SubPath path;

    Minimap(ImageView map, Pane pane, SubPath path){
        this.map = map;
        this.path = path;
        this.pane = pane;
    }
}
