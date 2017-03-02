package app.display;

import app.CustomMenus.CircularContextMenu;
import app.applicationControl.ApplicationController;
import app.applicationControl.email.EmailController;
import app.dataPrimitives.*;
import app.pathfinding.PathNotFoundException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static app.applicationControl.email.EmailController.phoneCompanies.*;

/**
 * controls all interaction with the patient display
 */
public class PatientController extends DisplayController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(PatientController.class);

    //what type/state of display the Patient Display is currently displaying
    private enum state{
        PATIENT_DEFAULT,
        PATIENT_SEARCH,
        DISPLAYING_TEXT_DIRECTION,
        SHOWING_MENU,
        HIDING_MENU
    }

    state displayState;
    // list of shapes that have been drawn on the screen
    List<Node> drawnObjects = new ArrayList<>();
    // FXML Things
    @FXML private TextField searchBar;
    @FXML private ListView<String> options;
    @FXML private ImageView imageView;
    @FXML private ListView<String> textDirectionsListView;
    @FXML private AnchorPane anchorPane;
    @FXML private TabPane mapTabs;
    @FXML private Tab main;
    @FXML private Tab belkin;
    @FXML private Tab campusTab;
    @FXML private ChoiceBox selectPhoneOrEmail;
    @FXML private TextField phoneOrEmail;

    @FXML private AnchorPane searchAnchorPane;
    @FXML private Button help;
    @FXML private Button exitButton;
    @FXML private HBox multiMapDisplayMenu;
    @FXML private HBox mapHbox;
    @FXML private Button adminButton;
    @FXML private Button directoryAdminButton;
    @FXML private Button mapAdminButton;
    @FXML private Button patientDisplayButton;
    @FXML private Button login;
    @FXML private Button TextDirection;
    @FXML private Button floor1;
    @FXML private Button floor3;
    @FXML private Button belkin1;
    @FXML private Button campus;
    @FXML private Button sendTextButton;
    @FXML private Line line;
    @FXML private ImageView logo;
    @FXML private ToggleButton togStairs;
    @FXML private ListView providersList;
    @FXML private VBox creditsPane;
    //Colors for patient Display
    //------------------------------------------------------------------------------------------------------------------
    private Color lightGrey = Color.rgb(211, 211, 211);
    private Color darkBlue = Color.rgb(42, 45 , 56);
    private Color orange = Color.rgb(232,144,20);




    //------------------------------------------------------------------------------------------------------------------

    private List<SubPath> currentPath;
    private int currentSubPath;

    private LinkedList<Pair<Integer, String>> currentTextDirections;
    private Shape drawnTextShape;

    private LinkedList<Label> roomLabels = new LinkedList<>();
    private LinkedList<Minimap> minimaps = new LinkedList<>();

    private String currentMap;

    boolean selected = false;
    CircularContextMenu menu = new CircularContextMenu();

    private Button previousButton;
    String defaultFloor;

    PatientMemento memento;

    //------------------------------------------------------------------------------------------------------------------
    //For Texting/Email

    EmailController.phoneCompanies carrierPicked = EMAIL;
    GraphNode lastStart;
    GraphNode lastEnd;
    boolean lastUseStairs;

    //------------------------------------------------------------------------------------------------------------------

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
        defaultFloor = currentMap;

        displayState = state.PATIENT_DEFAULT;
        /* Memento Pattern */
        // add event filter
        EventHandler passAllEventsToTimer = new EventHandler() {
                @Override
                public void handle(Event event) {
                    IdleTimer timer = IdleTimer.getInstance();
                    if (map.getSetting("idleTime") != null) {
                        timer.setTime(Double.parseDouble(map.getSetting("idleTime")));
                    }
                    GraphNode kioskLoc = map.getKioskLocation();
                    String floorname;
                    if (kioskLoc != null) {
                        floorname = kioskLoc.getLocation().getFloor();
                    }
                    else {
                        floorname = defaultFloor;
                    }
                    timer.resetTimer(new PatientMemento(floorname));
                }
            };
        this.stage.addEventFilter(MouseEvent.ANY, passAllEventsToTimer);
        this.stage.addEventFilter(KeyEvent.ANY, passAllEventsToTimer);
        this.memento = new PatientMemento(currentMap);
    }


    /**
     * display the image on the main patient screen
     */
    public void displayImage() {
        Image floor = applicationController.getFloorImage(currentMap);
        if (displayState == state.PATIENT_DEFAULT){
            imageView.setImage(floor);
        }
    }

    /**
     * shows the patient search interface (the dark one)
     */
    public void startSearch(){
        if (this.displayState == state.PATIENT_DEFAULT){//switch state
            clearDisplay();
            imageView.setImage(imageView.getImage());
            this.displayState = state.PATIENT_SEARCH;
            displayImage();
        }
    }

    /**
     * hides the patient search interface and returns to the default patient interface
     */
    public void exitSearch(){
        logger.debug("Exiting search in {}", this.getClass().getSimpleName());
        if (this.displayState == state.PATIENT_SEARCH || this.displayState == state.DISPLAYING_TEXT_DIRECTION ){//switch state
            hideMultiMapAnimation();
            hideMapAnimation();
            selectPhoneOrEmail.setVisible(false);
            sendTextButton.setVisible(false);
            phoneOrEmail.setVisible(false);
            mapTabs.setVisible(true);
            this.displayState = state.PATIENT_DEFAULT;
            clearSearchDisplay();
            displayImage();//display the original image
            searchBar.setPromptText("Search");
            drawRoomLabel(currentMap, imageView);
        }
    }

    /**
     * Initalises the Map
     */
    public void initializeMap(){
         displaySubPath(imageView, currentPath.get(0), true,10, 1,20);
    }
    /**
     * show the HBox from the bottom
     */
    public void showMapAnimation(){
        if(displayState == state.PATIENT_SEARCH || displayState == state.DISPLAYING_TEXT_DIRECTION){
            state s = displayState;
            displayState = state.SHOWING_MENU;
            final Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.setAutoReverse(true);
            timeline.setOnFinished(e -> initializeMap());

            line.setVisible(false);
            logo.setVisible(false);
            final KeyValue kv = new KeyValue(mapHbox.layoutYProperty(), 70);
            final KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            displayState  = s;
            TextDirection.setVisible(true);
            textDirection();
            TextDirection.setText("Hide Text Directions");
            mapTabs.setVisible(false);
        }
    }

    /**
     * hide the HBox to the bottom
     */
    public void hideMapAnimation(){
        if(displayState == state.PATIENT_SEARCH || displayState == state.DISPLAYING_TEXT_DIRECTION){
            state s = displayState;
            hideMinipaths();
            final Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.setAutoReverse(true);

            line.setVisible(true);
            logo.setVisible(true);
            timeline.setOnFinished(e->refreshDisplay());
            final KeyValue kv = new KeyValue(mapHbox.layoutYProperty(), 130);
            final KeyFrame kf = new KeyFrame(Duration.millis(100), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            displayState = s;
            mapTabs.setVisible(true);
            textDirectionsListView.setVisible(false);
            TextDirection.setVisible(false);
            TextDirection.setText("Show Text Directions");
        }
    }
    /**
     *
     * perform search; get text from the textfield
     */
    public void search () {
        System.out.println("Doing search...");
        if (displayState != state.PATIENT_SEARCH){
            displayState = state.PATIENT_SEARCH;
            startSearch();
            clearSearchDisplay();
            System.out.println("Starting display");
        }

        currentPath = null;
        String search = searchBar.getText();
        if (!search.isEmpty()) {
            options.setVisible(true);
        }
        else {
            options.setVisible(false);
            return;
        }
        logger.info("Searching : {}", search);
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
     * @param searchTerm
     * @return
     */
    public List<String> search(String searchTerm) {
        if (((int)searchTerm.charAt(0)) < 58 && ((int)searchTerm.charAt(0)) > 47){
            return map.searchRoom(searchTerm);
        }
	// if the first letter is not a number, search for entries first, then add all the rooms
	// to the bottom of the list
        else{
            String lowerCaseSearch = searchTerm.toLowerCase();
            List<String> results = map.searchEntry(lowerCaseSearch) ;
            results.addAll(map.searchRoom(searchTerm));
            return results;
        }
        //(update) the display the list of room
    }

    /**
     * selectPhoneOrEmail the clicked option
     * @param option
     * @return
     */
    public GraphNode select(String option) {
        searchBar.setPromptText(option);
        searchBar.setText("");
        logger.info("Select {}", option);
        DirectoryEntry entry = map.getEntry(option);
        // if the selected entry is an entry not a room
        if (entry != null) {
            logger.debug("Found Entry!");
            List<Room> locs = entry.getLocation();

            // if no location, should (probably) throwerror
            if(locs.isEmpty()) {
                return null;
            }
            // take first option if only one
            else if (locs.size() == 1) {
                logger.debug("Found desired graph node at: {}",
                    locs.get(0).getLocation().toString());
                displayResults(new LinkedList<>());
                getPath(map.getKioskLocation(), locs.get(0).getLocation());
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
                displayResults(new LinkedList<>());
                getPath(map.getKioskLocation(), room.getLocation());
                return room.getLocation();
            }
            else {
                logger.debug("No entry found");
            }
        }

        return null;
    }

    /**
     * change the patient image display depending on the button being clicked on using the id of the button
     * @param e
     */
    public void selectPatientImage(MouseEvent e){
        if (e.getSource() instanceof Button) {
            Button temp = (Button) e.getSource();
            currentMap = temp.getId();
	        displayPatientMap(currentMap, temp);
        }
    }

    /**
     * Display first floor of Belkin
     */
    public void displayBelkin(Event e){
        clearDisplay();
        imageView.setImage(applicationController.getFloorImage("belkin1"));
        currentMap = "belkin1";
        if (previousButton != null){
            //return to default image color
            previousButton.setStyle("-fx-background-color: #F7F7F7");
        }
        previousButton = belkin1;
        //selected color
        previousButton.setStyle("-fx-background-color: #898b95");
        drawRoomLabel(currentMap, imageView);
    }
    /**
     * Display 3rd floor of main
     */
    public void displayMain(Event e){
        clearDisplay();
        imageView.setImage(applicationController.getFloorImage("floor3"));
        currentMap = "floor3";
        if (previousButton != null){
            //return to default image color
            previousButton.setStyle("-fx-background-color: #F7F7F7");
        }
        previousButton = floor3;
        //selected color
        previousButton.setStyle("-fx-background-color: #898b95");
        drawRoomLabel(currentMap, imageView);
    }

    /**
     * Display campus
     */
    public void displayCampus(Event e){
        clearDisplay();
        imageView.setImage(applicationController.getFloorImage("campus"));
        currentMap = "campus";
        if (previousButton != null){
            //return to default image color
            previousButton.setStyle("-fx-background-color: #F7F7F7");
        }
        previousButton = campus;
        //selected color
        previousButton.setStyle("-fx-background-color: #898b95");
        drawRoomLabel(currentMap, imageView);
    }

    public void displayPatientMap(String floorname, Button currentButton){
        clearDisplay();
        imageView.setImage(applicationController.getFloorImage(floorname));
        if (previousButton != null){
            //return to default image color
            previousButton.setStyle("-fx-background-color: #F7F7F7");
        }
        previousButton = currentButton;
        //selected color
        previousButton.setStyle("-fx-background-color: #898b95");

        drawRoomLabel(currentMap, imageView);
    }

    /**
     * refresh the display
     */
    public void refreshDisplay () {
        System.out.println("Clearing display");
        clearDisplay();
        drawRoomLabel(currentMap, imageView);
    }

    /**
     * remove search result
     */
    public void clearSearchDisplay(){
        hideMultiMapAnimation();//hide the hBox thingy
        hideMapAnimation();
        multiMapDisplayMenu.getChildren().clear();//clear the hBox menu thingy
        clearDisplay();
    }

    /**
     * Remove all the points and labels that have been drawn on the map
     */
    public void clearDisplay () {
        GraphNode kioskNode = map.getKioskLocation();
        if (kioskNode != null) {
            defaultFloor = kioskNode.getLocation().getFloor();
        }
        imageView.setImage(applicationController.getFloorImage(defaultFloor));
        if(drawnObjects == null) {
            return;
        }
        for (Node shape : drawnObjects) {
            anchorPane.getChildren().remove(shape);
        }
        for (Label label :roomLabels){
            anchorPane.getChildren().remove(label);
        }
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
        Parent currentParent = imageToBeDrawnOver.getParent();

        double imageWidth = imageToBeDrawnOver.getBoundsInLocal().getWidth()*imageToBeDrawnOver.getScaleX();;
        double imageHeight = imageToBeDrawnOver.getBoundsInLocal().getHeight()*imageToBeDrawnOver.getScaleY();
        double offsetX = imageToBeDrawnOver.getLayoutX() - (imageToBeDrawnOver.getScaleX() - 1) * imageToBeDrawnOver.getBoundsInLocal().getWidth()/2;
        double offsetY = imageToBeDrawnOver.getLayoutY() - (imageToBeDrawnOver.getScaleY() - 1) * imageToBeDrawnOver.getBoundsInLocal().getHeight()/2;

        while(!(currentParent instanceof AnchorPane)){
            offsetX += currentParent.getLayoutX();
            offsetY += currentParent.getLayoutY();
            currentParent = currentParent.getParent();
        }

        logger.debug("Offsets: off x {}, off y {}", offsetX, offsetY);

        int newX = (int)(node.getLocation().getX() * imageWidth / 1000. + offsetX );
        int newY = (int)(node.getLocation().getY() * imageHeight / 1000. + offsetY );
        logger.debug("Image width : {}, Image Height : {}", imageWidth, imageHeight);

        return new FloorPoint(newX, newY, node.getLocation().getFloor());
    }

    /**
     * get paths across multiple floor and display different resulted floors on the search interface (the large ImageView and the hBox)
     * @param start the starting location
     * @param end the ending location
     */
    public void getPath (GraphNode start, GraphNode end) {
        lastStart = start;
        lastEnd = end;
        lastUseStairs = togStairs.isSelected();
        minimaps = new LinkedList<>();
        if (start == null || end == null) {
            logger.error("Cannot path, start or end is null!");
            return;
        }
        try {
            currentPath = map.getPathByFloor(start, end, lastUseStairs);
            TextDirection.setVisible(true);
            clearDisplay();
            //displaySubPath(imageView, currentPath.get(0), true,10, 1,20);
            currentSubPath = 0;
            for (int x = 0; x <currentPath.size(); x++){
                SubPath p = currentPath.get(x);
                ImageView currentImageView = new ImageView();

                currentImageView.setPreserveRatio(true);
                currentImageView.setFitHeight(75);
                currentImageView.setFitWidth(133);
                currentImageView.setOnMousePressed(e -> mapChoice(e));
                currentImageView.setImage(applicationController.getFloorImage(p.getFloor()));
                currentImageView.setId(x + "floor in list");
                logger.debug("Current image view id: {}", currentImageView.getId());
                multiMapDisplayMenu.getChildren().add(currentImageView);
                minimaps.add(new Minimap(currentImageView,p));
            }
            showMultiMapAnimation();
            showMapAnimation();
            textDirectionsListView.setVisible(true);
            mapTabs.setVisible(false);
            selectPhoneOrEmail.setVisible(true);
            sendTextButton.setVisible(true);
            phoneOrEmail.setVisible(true);
        } catch (PathNotFoundException e) {
            logger.error("No path can be drawn");
        }
    }

    /**
     * toggle between resulted maps when clicking on the corresponding imageView in the HBox
     * @param e
     */
    public void mapChoice(MouseEvent e){
        try {
            textDirectionsListView.getSelectionModel().select(null);

            ImageView iv = (ImageView) e.getSource();
            for(Node child :iv.getParent().getChildrenUnmodifiable()){
                ImageView buffer = (ImageView) child;
                buffer.setEffect(null);
                buffer.setScaleX(1);
                buffer.setScaleY(1);
            }
            logger.debug("Image view ID: {}", iv.getId());
            currentSubPath = (int) iv.getId().charAt(0) - 48;
            SubPath path = currentPath.get(currentSubPath);//ascii conversion
            clearDisplay();
            iv.setScaleX(1.3);//setFitWidth(iv.getFitWidth()*1.3);
            iv.setScaleY(1.3);//FitHeight(iv.getFitHeight()*1.3);
            clearDisplay();
            displaySubPath(imageView, path, true,10,1, 20);
            displayMinipaths();
            iv.setEffect(new DropShadow(30, Color.rgb(42, 57, 86)));
            if (displayState == state.DISPLAYING_TEXT_DIRECTION){
                String nextFloor = null;
                if(currentPath.size() > currentSubPath+1) {
                    nextFloor = currentPath.get(currentSubPath + 1).getFloor();
                }
                displayTextDirections(path.getPath(), nextFloor);
            }
        }catch(ClassCastException cc){
            logger.error("This method, mapChoice(MouseEvent e), is implemented incorrectly");
        }
    }

    /**
     * show the HBox from the bottom
     */
    public void showMultiMapAnimation(){
        if(displayState == state.PATIENT_SEARCH || displayState == state.DISPLAYING_TEXT_DIRECTION){
            state s = displayState;
            displayState = state.SHOWING_MENU;
            final Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.setAutoReverse(true);
            timeline.setOnFinished(e -> initializeMinimaps());
            final KeyValue kv = new KeyValue(multiMapDisplayMenu.layoutYProperty(), 510);
            final KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            displayState  = s;
        }
    }

    /**
     * hide the HBox to the bottom
     */
    public void hideMultiMapAnimation(){
        if(displayState == state.PATIENT_SEARCH || displayState == state.DISPLAYING_TEXT_DIRECTION){
            state s = displayState;
            hideMinipaths();
            final Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.setAutoReverse(true);
            final KeyValue kv = new KeyValue(multiMapDisplayMenu.layoutYProperty(), 600);
            final KeyFrame kf = new KeyFrame(Duration.millis(100), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            displayState = s;
        }
    }

    /**
     * Display the given sub path over the given image view
     * Addds all the drawn objects to a list of drawn objects
     * Changes the image view to the image of the floor the sub path covers
     * @param mapImage
     * @param subPath subpath to be drawn
     */
    public void displaySubPath (ImageView mapImage, SubPath subPath, boolean drawLabels,int nodeRadius, double arrowHeadSize, int lableFontSize) {
        GraphNode prev = null;
        mapImage.setImage(applicationController.getFloorImage(subPath.getFloor()));
        List<Shape> listToDraw = new ArrayList<>();
        Shape startPoint = new Shape() {
            @Override
            public com.sun.javafx.geom.Shape impl_configShape() {
                return null;
            }
        };
        Shape endPoint = new Shape() {
            @Override
            public com.sun.javafx.geom.Shape impl_configShape() {
                return null;
            }
        };

        Iterator<GraphNode> iterator = subPath.getPath().iterator();
        if (iterator.hasNext()){
            prev = iterator.next();
            FloorPoint localPoint = graphPointToImage(prev, mapImage);
            startPoint = drawStartPoint(localPoint,nodeRadius);
            listToDraw.add(startPoint);
        }
        // draw path, and all connections from previous
        while(iterator.hasNext()){
            GraphNode node = iterator.next();
            FloorPoint localPoint = graphPointToImage(node, mapImage);
            if (!iterator.hasNext()){
                endPoint = drawEndPoint(localPoint,nodeRadius);
                listToDraw.add(endPoint);
            }
            // draw connection
            listToDraw.add(drawConnection(prev, node, mapImage));
            listToDraw.add(drawArrowHeads(prev,node,mapImage,arrowHeadSize));
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
        drawFloorLabel(mapImage, subPath, lableFontSize);
        if(drawLabels) {
            roomLabels = getRoomLabels(subPath);
            displayRoomLabels(roomLabels);
        }
    }

    /**
     * given an image view and a subpath, draw a floor label
     * @param mapImage
     * @param subPath
     * @param labelFontSize
     */
    public void drawFloorLabel(ImageView mapImage, SubPath subPath, int labelFontSize){
        Label label = new Label(subPath.getFloor());
        label.setFont(Font.font ("Georgia", labelFontSize));
        FloorPoint temp = graphPointToImage(new GraphNode(50, 30, "one"), mapImage);
        label.setLayoutX(temp.getX());
        label.setLayoutY(temp.getY());
        label.setTextFill(Color.rgb(27, 68, 156));
        anchorPane.getChildren().add(label);
        this.drawnObjects.add(label);
    }


    /**
     * Display labels on the patient map and clickable
     * @param floorName
     * @param imageView
     */
    public void drawRoomLabel (String floorName, ImageView imageView) {
        List<String> roomNames= map.getAllRooms();
        String kiosk = map.getKiosk();
        for (String roomName : roomNames) {
            Room
cur = map.getRoomFromName(roomName);
            GraphNode loc = cur.getLocation();
            // skip rooms without locations
            if (loc == null || ! loc.getLocation().getFloor().equals(floorName)) {
                continue;
            }

            FloorPoint imageLoc = graphPointToImage(loc, imageView);
            String labelName = cur.getName();
            String curImage = "";
            for (DirectoryEntry entry : cur.getEntries()){
                if (entry.getIcon() != ""){
                    curImage = entry.getIcon();
                }
            }
            Label label = new Label(labelName);
            label.setLayoutX(imageLoc.getX() + 3);
            label.setLayoutY(imageLoc.getY() + 3);
            label.setFont(Font.font ("Calibri", 10));
            label.setStyle("-fx-background-color: #424556; -fx-padding: 2; -fx-background-radius: 1px; -fx-text-fill: #d3d3d3;");
            //set the labels clickable
            label.setOnMousePressed((e) -> goToSelectedRoom(e, labelName));
            label.setOnMouseEntered(e -> setMouseToHand(e));
            label.setOnMouseExited(e -> setMouseToNormal(e));
            anchorPane.getChildren().add(label);
            logger.debug("Adding Label {}", labelName);
            drawnObjects.add(label);

            if (kiosk != null && kiosk.equals(roomName)){
                curImage = "Star";
            }
            //if the room has a directory associated with it that contains an icon
            if (!curImage.equals("")){
                ImageView image = new ImageView();
                image.setImage(applicationController.getIconImage(curImage));
                image.setPreserveRatio(true);
                image.setFitWidth(20);
                image.setFitHeight(20);
                Label iconLabel = new Label();
                iconLabel.setGraphic(image);
                iconLabel.setLayoutX(imageLoc.getX() - 10);
                iconLabel.setLayoutY(imageLoc.getY() - 10);
                iconLabel.setOnMousePressed((e) -> goToSelectedRoom(e, labelName));
                iconLabel.setOnMouseEntered(e -> setMouseToHand(e));
                iconLabel.setOnMouseExited(e -> setMouseToNormal(e));
                anchorPane.getChildren().add(iconLabel);
                drawnObjects.add(iconLabel);
            }
            else{
                Circle circ = new Circle(2, Color.BLACK);
                circ.setLayoutX(imageLoc.getX());
                circ.setLayoutY(imageLoc.getY());
                anchorPane.getChildren().add(circ);
                drawnObjects.add(circ);
            }
        }
    }


    /**
     * display the path to the room when clicked on the patient display map
     * @param e
     */
    public void goToSelectedRoom(MouseEvent e, String roomname){
        if (e.getSource() instanceof Label){
            searchBar.setPromptText(roomname);
            searchBar.setText("");
            startSearch();
            getPath(map.getKioskLocation(),map.getRoomFromName(roomname).getLocation());
        }
    }

    /**
     * given local point, draw the starting point of a sub path
     * @param localPoint
     */
    public Shape drawStartPoint (FloorPoint localPoint, int radius) {
        Circle c = new Circle(localPoint.getX(), localPoint.getY(), radius);
        c.setFill(Color.GREEN);
        c.setMouseTransparent(true);
        anchorPane.getChildren().add(c);
        return c;
    }

    /**
     * given local point, draw the ending point of a sub path
     * @param localPoint
     */
    public Shape drawEndPoint (FloorPoint localPoint, int radius) {
        return drawPoint(localPoint, Color.RED, radius);
    }

    /**
     * given local point, draw the the point
     * @param localPoint
     */
    public Shape drawPoint (FloorPoint localPoint, Color color, int radius) {
        Circle c = new Circle(localPoint.getX(), localPoint.getY(), radius);
        c.setFill(color);
        c.setMouseTransparent(true);
        anchorPane.getChildren().add(c);
        return c;
    }


    /**
     * draw the name of the nodes that the subpath goes through
     * @param subPath
     * @param lableFontSize
     * @return
     */
    private Label drawFloorLabel(SubPath subPath, int lableFontSize){
        Label label = new Label(subPath.getFloor());
        label.setFont(Font.font ("Verdana", lableFontSize));
        label.setLayoutX(imageView.getFitWidth() - 50);
        label.setLayoutY(10);
        return label;
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

        Line line = new Line(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
        line.setStrokeWidth(4);
        line.setMouseTransparent(true);
        line.setStroke(darkBlue);

        anchorPane.getChildren().add(line);

        return line;
    }
        /**
     * draw the arrow head for line between two nodes
     * @param nodeA
     * @param nodeB
     * @return arrow head to be drawn
     */
    public Shape drawArrowHeads (GraphNode nodeA, GraphNode nodeB, ImageView imageView, double arrowSize) {
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
        triangle.setFill(darkBlue);

        anchorPane.getChildren().add(triangle);

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
                current.setBorder(new Border(new BorderStroke(Color.rgb(66,64,86),BorderStrokeStyle.SOLID,new CornerRadii(1),BorderWidths.DEFAULT)));
                current.setBackground(new Background(new BackgroundFill(Color.rgb(66,64,86), new CornerRadii(1),
                    new Insets(0.0,0.0,0.0,0.0))));
                current.setFont(Font.font ("Calibri", 10));
                current.setStyle("-fx-background-color: #424556; -fx-border-color: #424556; -fx-padding: 2; -fx-border-radius: 1 1 1 1");
                current.setTextFill(lightGrey);
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
        ImageView imageView = minimaps.get(0).map;
        imageView.setScaleY(1.3);
        imageView.setScaleX(1.3);
        displayMinipaths();
        imageView.setEffect(new DropShadow(30, Color.rgb(42, 57, 86)));
    }


    /**
     * Display labels on app.datastore.Map
     * @param labels
     */
    public void displayRoomLabels(LinkedList<Label> labels) {
        for (Label label : labels) {
            if (!anchorPane.getChildren().contains(label)) {
                anchorPane.getChildren().add(label);
            }
        }
    }

    public void textDirection() {
        if (textDirectionsListView.isVisible()){
            displayState = state.PATIENT_SEARCH;
            textDirectionsListView.setVisible(false);
            TextDirection.setText("Show Text Direction");
        }else {
            displayState = state.DISPLAYING_TEXT_DIRECTION;
            TextDirection.setText("Hide Text Direction");
            String nextFloor = null;
            if(currentPath.size() > currentSubPath +1) {
                nextFloor = currentPath.get(currentSubPath + 1).getFloor();
            }
            displayTextDirections(currentPath.get(currentSubPath).getPath(), nextFloor);
        }
    }

    private List<String> directions;
    /**
     * Function to get textual directions and print it on screen
     * @param path the path to be converted to text
     * @param nextFloor the next floor to be pathed to, can be null
     */
    public void displayTextDirections(List<GraphNode> path, String nextFloor) {
        textDirectionsListView.setVisible(true);
        LinkedList<Pair<Integer, String>> textDirections = map.getTextualDirections(path, nextFloor, true);
        directions = textDirections.stream().map(p -> p.getValue()).collect(Collectors.toList());
        textDirectionsListView.setItems(FXCollections.observableList(directions));
        currentTextDirections = textDirections;
        return;
    }


    public void logIn () {
        applicationController.createLoginAdmin();
    }

    /**
     * initialize the fxml components etc
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("INIT PatientController");
        displayImage();
//        displayPatientMap(String floorname, Button currentButton)

        imageView.setMouseTransparent(true);

        options.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = options.getSelectionModel().getSelectedItem();
                logger.debug("clicked on {}", selectedString);
                select(selectedString);
            }
        });

        //When the text directions box is clicked
        textDirectionsListView.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> {
                logger.debug("Selected {} in text directions", newValue);
                if(newValue != null
                    && (int) newValue >= 0
                    && (int) newValue < currentTextDirections.size()) {

                    if (drawnTextShape != null) {
                        anchorPane.getChildren().remove(drawnTextShape);
                    }
                    GraphNode node = currentPath.get(currentSubPath).getPath().get(
                        currentTextDirections.get((int) newValue).getKey());
                    drawnTextShape = drawPoint(graphPointToImage(node, imageView), Color.BLUE, 5);
                    drawnObjects.add(drawnTextShape);
                }
            }
        );

        // the image view should be the bottom pane so circles can be drawn over it
        imageView.toBack();
//        imageView.setPreserveRatio(false);
        helpLabel.setText("Hello! Thanks for using project-pather." +
            "\n\nTo get started, start typing into the search bar. " +
            "\n Then, selectPhoneOrEmail the option you would like to get a path to." +
            "\n\nTo close this menu, click on this");


        drawRoomLabel(currentMap, imageView);

        String options[] = {"Email", "Phone"};
        selectPhoneOrEmail.setItems(FXCollections.observableList(Arrays.asList(options)));
        selectPhoneOrEmail.setOnMouseClicked(e->{
            selectPhoneOrEmail.setValue(null);
            providersList.setVisible(false);
        });
        selectPhoneOrEmail.getSelectionModel().selectedIndexProperty().addListener(
            (e, a ,b)->{
                if(b.intValue() == 1){
                    providersList.setVisible(true);
                }else{
                    selectPhoneOrEmail.setValue("EMAIL");
                }
            }
        );
        String providers[] =   {"AT&T", "Sprint", "Verizon", "T-Mobile", "Virgin Mobile",
                                "Tracfone", "Metro PCS", "Boost Mobile", "Cricket", "Ptel",
                                "Republic Wireless", "Google Fi", "Suncom", "Ting",
                                "U.S. Cellular", "Consumer Cellular", "C-Spire", "Page Plus"};
        providersList.setItems(FXCollections.observableList(Arrays.asList(providers)));
        providersList.getSelectionModel().selectedIndexProperty().addListener(
            (e, a, b)->{
              switch (b.intValue()) {//TODO add interaction with text directions
                  case 0: //AT&T
                      System.out.println("AT&T");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("AT&T");
                      carrierPicked = ATT;
                      break;
                  case 1: //SPRINT
                      System.out.println("SPRINT");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Sprint");
                      carrierPicked = SPRINT;
                      break;
                  case 2: //Verizon
                      System.out.println("Verizon");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Verizon");
                      carrierPicked = VERIZON;
                      break;
                  case 3: //T-Mobile
                      System.out.println("T-Mobile");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("T-Mobile");
                      carrierPicked = TMOBILE;
                      break;
                  case 4: //Virgin Mobile
                      System.out.println("Virgin Mobile");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Virgin Mobile");
                      carrierPicked = VIRGIN;
                      break;
                  case 5: //Tracfone
                      System.out.println("Tracfone");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Tracfone");
                      carrierPicked = TRAC;
                      break;
                  case 6: //Metro PCS
                      System.out.println("Metro PCS");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Metro PCS");
                      carrierPicked = METRO;
                      break;
                  case 7: //Boost Mobile
                      System.out.println("Boost Mobile");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Boost Mobile");
                      carrierPicked = BOOST;
                      break;
                  case 8: //Cricket
                      System.out.println("Cricket");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Cricket");
                      carrierPicked = CRICKET;
                      break;
                  case 9: //Ptel
                      System.out.println("Ptel");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Ptel");
                      carrierPicked = PTEL;
                      break;
                  case 10: //Republic Wireless
                      System.out.println("Republic Wireless");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Republic Wireless");
                      carrierPicked = REPUBLIC;
                      break;
                  case 11: //Google Fi
                      System.out.println("Google Fi");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Google Fi");
                      carrierPicked = GOOGLE;
                      break;
                  case 12: //Suncom
                      System.out.println("Suncom");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Suncom");
                      carrierPicked = SUNCOM;
                      break;
                  case 13: //Ting
                      System.out.println("Ting");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Ting");
                      carrierPicked = TING;
                      break;
                  case 14: //U.S. Cellular
                      System.out.println("US Cellular");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("US Cellular");
                      carrierPicked = US;
                      break;
                  case 15: //Consumer Cellular
                      System.out.println("Consumer Cellular");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Consumer Cellular");
                      carrierPicked = CONSUMER;
                      break;
                  case 16: //C-Spire
                      System.out.println("C-Spire");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("C-Spire");
                      carrierPicked = CSPIRE;
                      break;
                  case 17: //Page Plus
                      System.out.println("Page Plus");
                      providersList.setVisible(false);
                      selectPhoneOrEmail.setValue("Page Plus");
                      carrierPicked = PAGE;
                      break;
              }


            }
        );
        phoneOrEmail.setOnAction(
            e->{
            }
        );
        sendTextButton.setOnAction(e->{
            if (carrierPicked.equals(EMAIL)){
                sendEmail(phoneOrEmail.getText());
            } else {
                sendText(phoneOrEmail.getText(), carrierPicked);
                System.out.println("Send message");
            }
            phoneOrEmail.clear();
        });
    }

    private void sendEmail(String email){
        //GraphNode start = map.getKioskLocation();
        //GraphNode end = map.getRoomFromName(searchBar.getText()).getLocation();
        applicationController.sendEmail(email, lastStart, lastEnd, togStairs.isSelected());
    }

    private void sendText(String number, EmailController.phoneCompanies carrier){
        //GraphNode start = map.getKioskLocation();
        //GraphNode end = map.getRoomFromName(searchBar.getText()).getLocation();
        applicationController.sendText(number, carrier, lastStart, lastEnd, togStairs.isSelected());       //number, carrier, directions, destination
    }


    /**
     * Resizes Window's Width
     * @param oldSceneWidth
     * @param newSceneWidth
     */
    public void scaleWidth(Number oldSceneWidth, Number newSceneWidth){
        anchorPane.setScaleX(anchorPane.getScaleX()*newSceneWidth.doubleValue()/oldSceneWidth.doubleValue());
        clearDisplay();
        drawRoomLabel(currentMap, imageView);
        //imageView.setScaleX(imageView.getScaleX()*newSceneWidth.doubleValue()/oldSceneWidth.doubleValue());
    }

    /**
     * Resizes Window's Height
     * @param oldSceneHeight
     * @param newSceneHeight
     */
    public void scaleHeight(Number oldSceneHeight, Number newSceneHeight){
        anchorPane.setScaleY(anchorPane.getScaleY()*newSceneHeight.doubleValue()/oldSceneHeight.doubleValue());
        clearDisplay();
        drawRoomLabel(currentMap, imageView);
        //imageView.setScaleX(imageView.getScaleY()*newSceneHeight.doubleValue()/oldSceneHeight.doubleValue());
    }

    /**
     * Displays paths on minimaps
     */
    public void displayMinipaths(){
        for(Minimap minimap: minimaps){
            displaySubPath(minimap.map, minimap.path, false,3,0, 10);
        }
    }

    /**
     * Displays paths on minimaps
     */
    public void hideMinipaths() {
        if (currentPath != null) {
            clearDisplay();
            displaySubPath(imageView, currentPath.get(currentSubPath), true, 10,0, 20);
        }
    }

    public void hideOptions(){
        options.setVisible(false);
        providersList.setVisible(false);
    }

    // revert to previous state
    public void revertState (PatientMemento memento) {
        logger.info("Reverting State");
        exitSearch();
        currentMap = memento.floor;
        displayImage();
        refreshDisplay();
        phoneOrEmail.clear();
        creditsPane.setVisible(false);
    }

    public void exitCredits() {
        displayImage();
        creditsPane.setVisible(false);
        anchorPane.setVisible(true);
    }


    public void showCredits() {
        anchorPane.setVisible(false);
        creditsPane.setVisible(true);
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
}

/**
 * Minimaps contain a map and a path
 */
class Minimap{
    ImageView map;
    SubPath path;

    Minimap(ImageView map, SubPath path){
        this.path = path;
        this. map = map;
    }
}
