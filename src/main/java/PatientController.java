import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * controls all interaction with the patient display
 */
public class PatientController extends DisplayController implements Initializable {

    //what type/state of display the Patient Display is currently displaying
    private enum state{
        PATIENT_DEFAULT,
        PATIENT_SEARCH,
        DISPLAYING_TEXT_DIRECTION,
        SHOWING_MENU,
        HIDING_MENU
    }

    state displayState;
    // kiosk location
    GraphNode startNode;
    // list of shapes that have been drawn on the screen
    List<Node> drawnObjects;
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
    @FXML private Button TextDirection;
    @FXML private Button miniMenuButton;

    private List<SubPath> currentPath;
    private int currentSubPath;
    private LinkedList<Label> roomLabels = new LinkedList<>();
    private LinkedList<Minimap> minimaps = new LinkedList<>();

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
     * display the image on the main patient screen
     */
    public void displayImage() {
        Image floor = applicationController.getImage(currentMap);
        if (displayState == state.PATIENT_DEFAULT){
            imageView.setImage(floor);
        }
//        if (displayState == state.PATIENT_SEARCH){d
//            patientImageView.setImage(floor);
//        }
    }

    /**
     * shows the patient search interface (the dark one)
     */
    public void startSearch(){
        if (this.displayState == state.PATIENT_DEFAULT){//switch state
            searchAnchorPane.setVisible(true);
            patientImageView.setImage(imageView.getImage());
            this.displayState = state.PATIENT_SEARCH;
            miniMenuButton.setVisible(true);
            displayImage();
        }
    }

    /**
     * hides the patient search interface and returns to the default patient interface
     */
    public void exitSearch(){
        System.out.println("Exit button works");
        if (this.displayState == state.PATIENT_SEARCH || this.displayState == state.DISPLAYING_TEXT_DIRECTION ){//switch state
            searchAnchorPane.setVisible(false);
            this.displayState = state.PATIENT_DEFAULT;
            clearSearchDisplay();
            miniMenuButton.setVisible(false);
            hideMultiMapAnimation();
            displayImage();//display the original image
        }
    }

    /**
     * perform search; get text from the textfield
     */
    public void search () {
        clearSearchDisplay();
        currentPath = null;
        String search = searchBar.getText();
        if (!search.isEmpty()) {
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
            if(locs.isEmpty()) {
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

    /**
     * remove search result
     */
    public void clearSearchDisplay(){
        TextDirection.setVisible(false);
        hideMultiMapAnimation();//hide the hBox thingy
        multiMapDisplayMenu.getChildren().clear();//clear the hBox menu thingy
        textDirectionsTextBox.setVisible(false);
        clearDisplay();
    }

    /**
     * Remove all the points and labels that have been drawn on the map
     */
    public void clearDisplay () {
        patientImageView.setImage(imageView.getImage());
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

        double imageWidth = imageToBeDrawnOver.getBoundsInLocal().getWidth();
        double imageHeight = imageToBeDrawnOver.getBoundsInLocal().getHeight();
        double offsetX = imageToBeDrawnOver.getLayoutX();
        double offsetY = imageToBeDrawnOver.getLayoutY();

        while(!(currentParent instanceof AnchorPane)){
            offsetX += currentParent.getLayoutX();
            offsetY += currentParent.getLayoutY();
            currentParent = currentParent.getParent();
        }

        System.out.println("off x " + offsetX + "  off y "  + offsetY);

        int newX = (int)(node.location.x * imageWidth / 1000. + offsetX );
        int newY = (int)(node.location.y * imageHeight / 1000. + offsetY );
        System.out.printf("image width : %f \nimage Height : %f\n", imageWidth, imageHeight);

        return new FloorPoint(newX, newY, node.location.floor);
    }

    /**
     * get paths across multiple floor and display different resulted floors on the search interface (the large ImageView and the hBox)
     * @param start the starting location
     * @param end the ending location
     */
    public void getPath (GraphNode start, GraphNode end) {
        minimaps = new LinkedList<>();
        if (start == null || end == null) {
            System.out.println("Start or end is null!");
        }
        try {
            currentPath = map.getPathByFloor(start, end);
            TextDirection.setVisible(true);
            clearDisplay();
            displaySubPath(patientImageView, currentPath.get(0), true,10, 20);
            currentSubPath = 0;
            for (int x = 0; x <currentPath.size(); x++){
                SubPath p = currentPath.get(x);
                ImageView currentImageView = new ImageView();

                currentImageView.setPreserveRatio(true);
                currentImageView.setFitHeight(95);
                currentImageView.setFitWidth(165);
                currentImageView.setOnMousePressed(e -> mapChoice(e));
                currentImageView.setImage(applicationController.getImage(p.floor));
                currentImageView.setId(x + "floor in list");
                System.out.println(currentImageView.getId());
               multiMapDisplayMenu.getChildren().add(currentImageView);
               minimaps.add(new Minimap(currentImageView,p));
            }
            showMultiMapAnimation();
        } catch (PathNotFoundException e) {
            System.out.println("No path can be drawn");
        }
    }

    /**
     * toggle between resulted maps when clicking on the corresponding imageView in the HBox
     * @param e
     */
    public void mapChoice(MouseEvent e){
        try {
            ImageView iv = (ImageView) e.getSource();
            for(Node child :iv.getParent().getChildrenUnmodifiable()){
                child.setEffect(null);
            }
            System.out.println(iv.getId() + "*******");
            currentSubPath = (int) iv.getId().charAt(0) - 48;
            SubPath path = currentPath.get(currentSubPath);//ascii conversion
            clearDisplay();
            displaySubPath(patientImageView, path, true,10, 20);
            displayMinipaths();
            iv.setEffect(new DropShadow());
            if (displayState == state.DISPLAYING_TEXT_DIRECTION){
                String nextFloor = null;
                if(currentPath.size() > currentSubPath+1) {
                    nextFloor = currentPath.get(currentSubPath + 1).floor;
                }
                displayTextDirections(path.path, nextFloor);
            }
        }catch(ClassCastException cc){
            System.err.println("you are implementing this method in a wrong place");
        }
    }

    public void menuControl(){
        if (miniMenuButton.getText() == "^"){
            showMultiMapAnimation();
        } else if (miniMenuButton.getText() == "X") {
            hideMultiMapAnimation();
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
            final KeyValue kv = new KeyValue(multiMapDisplayMenu.layoutYProperty(), 490);
            final KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            miniMenuButton.setLayoutY(491);
            miniMenuButton.setText("X");
            displayState  = s;
        }
    }

    /**
     * hide the HBox
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
            miniMenuButton.setLayoutY(568);
            miniMenuButton.setText("^");
            displayState = s;
        }
    }

    /**
     * Initialises the minimaps after animation
     */
    public void initializeMinimaps(){
        displayMinipaths();
        minimaps.getFirst().map.setEffect(new DropShadow());
    }
    /**
     * Display the given sub path over the given image view
     * Addds all the drawn objects to a list of drawn objects
     * Changes the image view to the image of the floor the sub path covers
     * @param mapImage
     * @param subPath subpath to be drawn
     */
    public void displaySubPath (ImageView mapImage, SubPath subPath, boolean drawLabels,int nodeRadius, int lableFontSize) {

        System.out.println("SubPath");
        GraphNode prev = null;
        mapImage.setImage(applicationController.getImage(subPath.floor));
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

        Iterator<GraphNode> iterator = subPath.path.iterator();
        if (iterator.hasNext()){
            prev = iterator.next();
            FloorPoint localPoint = graphPointToImage(prev, mapImage);
            System.out.println(localPoint);
            startPoint = drawStartPoint(localPoint,nodeRadius);
            listToDraw.add(startPoint);
        }
        // draw path, and all connections from previous
        while(iterator.hasNext()){
            GraphNode node = iterator.next();
            FloorPoint localPoint = graphPointToImage(node, mapImage);
            System.out.println(localPoint);
            if (!iterator.hasNext()){
                endPoint = drawEndPoint(localPoint,nodeRadius);
                listToDraw.add(endPoint);
            }
            // draw connection
            listToDraw.add(drawConnection(prev, node, mapImage));
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
        Label label = new Label(subPath.floor);
        label.setFont(Font.font ("Georgia", labelFontSize));
        FloorPoint temp = graphPointToImage(new GraphNode(50, 30, "one"), mapImage);
        label.setLayoutX(temp.getX());
        label.setLayoutY(temp.getY());
        label.setTextFill(Color.rgb(27, 68, 156));
        anchorPane.getChildren().add(label);
        this.drawnObjects.add(label);
    }
    /**
     * given local point, draw the starting point of a sub path
     * @param localPoint
     */
    public Shape drawStartPoint (FloorPoint localPoint, int radius) {
        Circle c = new Circle(localPoint.x, localPoint.y, radius);
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
        Circle c = new Circle(localPoint.x, localPoint.y, radius);
        c.setFill(Color.RED);
        c.setMouseTransparent(true);
        anchorPane.getChildren().add(c);
        return c;
    }

    private Label drawFloorLabel(SubPath subPath, int lableFontSize){
        Label label = new Label(subPath.floor);
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

        Line line = new Line(pointA.x, pointA.y, pointB.x, pointB.y);
        line.setStrokeWidth(4);
        line.setMouseTransparent(true);
        line.setStroke(Color.rgb(88, 169, 196));

        anchorPane.getChildren().add(line);

        return line;
    }


    public void textDirection(){
        if (textDirectionsTextBox.isVisible()){
            displayState = state.PATIENT_SEARCH;
            textDirectionsTextBox.setVisible(false);
            TextDirection.setText("Show Text Direction");
        }
        else {
            displayState = state.DISPLAYING_TEXT_DIRECTION;
            TextDirection.setText("Hide Text Direction");
            String nextFloor = null;
            if(currentPath.size() > currentSubPath +1) {
                nextFloor = currentPath.get(currentSubPath + 1).floor;
            }
            displayTextDirections(currentPath.get(currentSubPath).path, nextFloor);
        }
    }


    /**
     * Function to get textual directions and print it on screen
     * @param path the path to be converted to text
     * @param floor
     */
    public void displayTextDirections(List<GraphNode> path, String floor) {
        textDirectionsTextBox.setVisible(true);
        List<String> directions = map.getTextualDirections(path);
        String dir = "";
        for(String line : directions) {
            dir += (line + "\n");
        }
        if(floor != null) {
            dir += ("Take elevator to " + floor);
        }
        textDirectionsTextBox.setText(dir);
        return;
    }

    boolean selected = false;
    CircularContextMenu menu = new CircularContextMenu();
    public void logIn () {
        menu.addOption(Color.RED);
        menu.addOption(Color.BLACK);
        //applicationController.createLoginAdmin();
        if(selected)
            try {
                menu.show(anchorPane, 300, 300, 10);
            }catch(InvalidPaneException e){

            }
            selected = !selected;
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

    /**
     * Resizes Window's Width
     * @param oldSceneWidth
     * @param newSceneWidth
     */
    public void scaleWidth(Number oldSceneWidth, Number newSceneWidth){
        anchorPane.setScaleX(anchorPane.getScaleX()*newSceneWidth.doubleValue()/oldSceneWidth.doubleValue());
        //imageView.setScaleX(imageView.getScaleX()*newSceneWidth.doubleValue()/oldSceneWidth.doubleValue());
    }
    /**
     * Resizes Window's Height
     * @param oldSceneHeight
     * @param newSceneHeight
     */
    public void scaleHeight(Number oldSceneHeight, Number newSceneHeight){
        anchorPane.setScaleY(anchorPane.getScaleY()*newSceneHeight.doubleValue()/oldSceneHeight.doubleValue());
        //imageView.setScaleX(imageView.getScaleY()*newSceneHeight.doubleValue()/oldSceneHeight.doubleValue());
    }

    /**
     * gets Room description on screen
     * @param subpath
     */
    public LinkedList<Label> getRoomLabels(SubPath subpath){
        LinkedList<Label> labels = new LinkedList<>();
        Room room;

        List<GraphNode> path = subpath.path;

        for (GraphNode node: path){
            Label current = new Label();
            room = map.getRoomFromNode(node);
            FloorPoint point;
            int roomx;
            int roomy;

            if(room != null) {
                point = graphPointToImage(room.location, patientImageView);
                roomx = point.x + 5;
                roomy = point.y;

                current.setText(room.name);
                current.setLayoutX(roomx);
                current.setLayoutY(roomy);
                current.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,new CornerRadii(4),BorderWidths.DEFAULT)));
                current.setBackground(new Background(new BackgroundFill(Color.rgb(124,231,247), new CornerRadii(4),
                    new Insets(0.0,0.0,0.0,0.0))));
                current.setMouseTransparent(true);
                labels.add(current);
            }


        }

        return labels;
    }

    /**
     * Display labels on Map
     * @param labels
     */
    public void displayRoomLabels(LinkedList<Label> labels){
        for(Label label: labels){
            anchorPane.getChildren().add(label);
        }
    }

    /**
     * Displays paths on minimaps
     */
    public void displayMinipaths(){
        for(Minimap minimap: minimaps){
            displaySubPath(minimap.map, minimap.path, false,3, 10);
        }
    }

    /**
     * Displays paths on minimaps
     */
    public void hideMinipaths() {
        if (currentPath != null) {
            clearDisplay();
            displaySubPath(patientImageView, currentPath.get(currentSubPath), true, 10, 20);
        }
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
