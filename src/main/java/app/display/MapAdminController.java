package app.display;
import app.CustomMenus.CircularContextMenu;
import app.applicationControl.ApplicationController;
import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MapAdminController extends DisplayController {

    /**
     * Keep track of the state of the system
     */
    public enum State {
        NONE, // the user is selecting
        ADD_NODES, // the user is adding nodes
        CHAIN_ADD, // the user is adding nodes in a chain
        ADD_CONNECTION, // the user is adding connections
        ADD_ELEVATOR,
        DRAG_NODE,
    }

    State currentState = State.NONE;
    //    MapAdminDisplay display;
    GraphNode selectedNode;
    GraphNode secondaryNode;
    Room activeRoom ;
    List<String> elevatorFloors = new ArrayList<>();

    // keep track of the objects that have been drawn on the screen
    HashMap<Long, Shape> drawnNodes = new HashMap<>();
    CircularContextMenu nodeMenu = new CircularContextMenu();
    CircularContextMenu screenMenu = new CircularContextMenu();
    List<Node> miscDrawnObjects = new ArrayList<>();
    Stage stage;

    //For the context menu (right click menu)
    //ContextMenu contextMenu;

    @FXML private Button buttonSave;
    @FXML private Button buttonCancel;
    @FXML private ToggleButton togglebuttonAddNode;
    @FXML private ToggleButton togglebuttonAddConnections;
    @FXML private ToggleButton togglebuttonChainAdd;
    @FXML private ToggleButton togglebuttonAddElevator;
    @FXML private ToggleGroup toggleTools;
    @FXML private ImageView imageviewMap;
    @FXML private AnchorPane anchorpaneMap;
    @FXML private ComboBox<String> roomName;

    @FXML private ListView<String> elevatorFloorOptions;
    @FXML private ListView<String> changeFloorOptions;
    @FXML private ToggleButton togglebuttonChangeFloor;

    @FXML private Button defaultKioskButton;

    private GraphNode tempNode ;
    private String currentMap;

    /**
     *  Construct map admin controller
     * @param map all the data for the program
     * @param applicationController main controller
     */
    public void init(app.datastore.Map map, ApplicationController applicationController, Stage stage) {
        super.init(map, applicationController, stage);

        // Add states to toggles
        togglebuttonAddNode.setUserData(State.ADD_NODES);
        togglebuttonAddConnections.setUserData(State.ADD_CONNECTION);
        togglebuttonChainAdd.setUserData(State.CHAIN_ADD);
        togglebuttonAddElevator.setUserData(State.ADD_ELEVATOR);

        /**
         * Add click listener to the list of floor options
         */
        elevatorFloorOptions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        elevatorFloorOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = elevatorFloorOptions.getSelectionModel().getSelectedItem();
                selectFloor(selectedString);
            }
        });
        elevatorFloorOptions.toFront();
        changeFloorOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedString = changeFloorOptions.getSelectionModel().getSelectedItem();
                setMap(selectedString);
            }
        });
        imageviewMap.toBack();

        setMap("floor3");
        drawMap();
    }

    /**
     * draw state of graph
     * and clear
     */
    public void drawMap(){
        anchorpaneMap.getChildren().removeAll(drawnNodes.values());
        drawnNodes.clear();
        anchorpaneMap.getChildren().removeAll(miscDrawnObjects);
        miscDrawnObjects.clear();
        anchorpaneMap.getChildren().removeAll(
            anchorpaneMap.getChildren().stream().filter(node -> node instanceof Shape).collect(Collectors.toList())
        );
        map.getGraphNodesOnFloor(currentMap)
            .stream()
            .forEach(node -> drawNode(node, imageviewMap));
        highlightSelected();
        addFloorLabel(this.currentMap);
//---------------------------------------------------------------------------------------------------------------------
        //Setup Contest Menu here

    }

    /**
     * Add an on the map label for each room
     * @param room
     */
    public void addRoomLabel (Room room) {
        Label label = new Label(room.getName());
        label.setFont(Font.font ("Georgia", 10));
        FloorPoint temp = graphToImage(room.getLocation().getLocation(),  imageviewMap);
        label.setLayoutX(temp.getX() + 5);
        label.setLayoutY(temp.getY() + 5);
        label.setTextFill(Color.rgb(27, 68, 156));
        label.setStyle("-fx-background-color: #E1F0F5; -fx-border-color: darkblue;-fx-padding: 2;");
        anchorpaneMap.getChildren().add(label);
        this.miscDrawnObjects.add(label);
    }

    /**
     * Add floor label to the current map
     * @param floor
     */
    public void addFloorLabel (String floor) {
        Label label = new Label(floor);
        label.setFont(Font.font ("Georgia", 20));
        FloorPoint temp = graphToImage(new FloorPoint(50,30, floor), imageviewMap);
        label.setLayoutX(temp.getX());
        label.setLayoutY(temp.getY());
        label.setTextFill(Color.rgb(27, 68, 156));
        anchorpaneMap.getChildren().add(label);
        this.miscDrawnObjects.add(label);
    }

    /**
     * Draw the node at the given location.
     * NOTE: the imageview passed in my be the direct child of
     * the container the circle is being added to
     * @param node already scaled to 1000 X 1000
     * @param imageToDrawOver the image the point must cover
     */
    public void drawNode (GraphNode node, ImageView imageToDrawOver) {
        if (node.isElevator()) {
            drawElevator(node.getLocation(), imageToDrawOver);
        }
        else {
            drawPoint(node.getLocation(), imageToDrawOver);

            Room nodeRoom = map.getRoomFromNode(node);
            if (nodeRoom != null) {
                System.out.println("drawing room " +  nodeRoom);
                addRoomLabel(nodeRoom);
            }

        }
        for (GraphNode adj : node.getAdjacent()) {
            if(adj.getLocation().getFloor().equals(currentMap)){
                drawConnection(node.getLocation(), adj.getLocation());
            }
        }
    }

    /**
     * Draw elevator in the same way as a point
     * @param loc
     * @param imageToDrawOn
     */
    private void drawElevator(FloorPoint loc, ImageView imageToDrawOn) {
        FloorPoint imagePoint = graphToImage(loc, imageToDrawOn);
        int width = 8, height = 8;
        Rectangle rect = new Rectangle(imagePoint.getX() - width / 2, imagePoint.getY() - height / 2 , width,height);
        rect.setFill(Color.BLACK);
        rect.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(rect);
        GraphNode graphNodeAttatched = map.getGraphNode(loc);
        drawnNodes.put(graphNodeAttatched.id, rect);
    }

    /**
     * Handles the change of the tool toggles (add node,
     * add connection, etc)
     */
    public void handleToggleTools() {
        if(toggleTools.getSelectedToggle() == null) {
            changeState(State.NONE);
        }
        else {
            changeState((State) toggleTools.getSelectedToggle().getUserData());
        }
    }

    /**
     * Open the menu to switch between floors
     */
    public void toggleSwitchMap () {
        toggleSwitchMap(togglebuttonChangeFloor.isSelected());
    }

    public void toggleSwitchMap (boolean selected) {
        togglebuttonChangeFloor.setSelected(selected);
        changeFloorOptions.setVisible(selected);
        List<String> options = applicationController.getAllFloors();
        ObservableList<String> observOptions= FXCollections.observableArrayList(options);
        changeFloorOptions.setItems(observOptions);
        changeFloorOptions.toFront();
    }

    /**
     * Create node from given location. Make new app.dataPrimitives.GraphNode
     * @param location location to create a point at
     */
    public void addNode(FloorPoint location){
        GraphNode newNode = new GraphNode(location);
        map.addNode(newNode);
        secondaryNode = selectedNode;
        selectedNode = newNode;
        drawMap();
    }

    /**
     * Create an elevator on the given floors
     * @param location
     */
    public void addElevator(FloorPoint location, List<String> floors){
        map.addElevator(location, floors);
        secondaryNode = selectedNode;
        selectedNode = map.getGraphNode(location);
        drawMap();
    }

    /**
     *
     * @return
     */
    public GraphNode nearbyNode (MouseEvent n) {
        FloorPoint graphPoint = mouseToGraph(n);

        tempNode = map.getGraphNode(graphPoint);
        if (tempNode != null && tempNode.getLocation().distance(graphPoint) > 20 ){
            tempNode = null;
        }
        return tempNode;
    }

    /**
     * Add a room to the map.
     * Take the text from the roomName text field
     */
    public void addRoom () {
        String newName = roomName.getValue();
        Room existingRoom = map.getRoomFromName(newName);
        // Entered name is empty, so delete room
        // TODO: maybe make this more explicit, like a separate button,
        // and ignore this case
        if (newName.isEmpty()) {
            if (activeRoom != null) {
                map.deleteRoom(activeRoom);
            }
        }
        // Already a room of that name, so change room location
        else if (existingRoom != null) {
            // if there is also an active room, remove this node from it
            if (activeRoom != null) {
                activeRoom.setLocation(null);
            }
            existingRoom.setLocation(selectedNode);
        }
        // Node already has a room, so rename room
        else if (activeRoom != null) {
            map.changeRoomName(activeRoom, newName);
        }
        // No room either existed at this node or had the new name, so
        // add a new room
        else {
            map.addRoom(new Room(selectedNode, newName));
        }
    }

    /**
     * Draw point at given location.
     * That location is given as a 1000 X 1000 coordinate
     * and coverted to the appropriate size in this function
     * @param loc
     * @param imageToDrawOn image view that the point will cover
     */
    private void drawPoint (FloorPoint loc, ImageView imageToDrawOn ){
        FloorPoint imagePoint = graphToImage(loc, imageToDrawOn);
        // draw  point with layout x and y, not actual x and y
        Circle circ = new Circle(4);
        circ.setLayoutX(imagePoint.getX());
        circ.setLayoutY(imagePoint.getY());
        circ.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(circ);
        GraphNode graphNodeAttatched = map.getGraphNode(loc);
        drawnNodes.put(graphNodeAttatched.id, circ);
    }

    /**
     * Deletes the connection between the two selected nodes,
     * if one exists
     */
    public void deleteConnection() {
        if(selectedNode != null && secondaryNode != null) {
            map.deleteConnection(selectedNode, secondaryNode);
            drawMap();
        }
    }

    /**
     * delete the selected node
     */
    public void deleteSelected () {
        if(selectedNode != null) {
            map.deleteNode(selectedNode);
            selectedNode = null;
            drawMap();
        }
    }

    /**
     * Delete the selected node if it is an elevator
     */
    public void deleteElevator () {
        if (selectedNode != null ) {
            if(selectedNode.isElevator()) {
                boolean isEl = map.deleteElevator(selectedNode);
                selectedNode = null;
                drawMap();
            }
        }
    }

    /**
     * Delete a node from graph and delete the node from the adjacent nodes
     * @param node
     */
    public void deleteNode(GraphNode node){
        map.deleteNode(node);
    }

    /**
     * Add connection from nodeA to nodeB and
     * from nodeB to nodeA
     * Does not add a connection if it already exists
     * @param nodeA
     * @param nodeB
     */
    public void addConnection(GraphNode nodeA, GraphNode nodeB){
        if(nodeA != null && nodeB != null){
            map.addConnection(nodeA, nodeB);
            drawMap();
        }
    }

    /**
     *
     * @param x1
     * @param x2
     */
    public void drawConnection(FloorPoint x1, FloorPoint x2){
        FloorPoint imagePoint1 = graphToImage(x1, imageviewMap);
        FloorPoint imagePoint2 = graphToImage(x2, imageviewMap);

        // it is okay for line to use hard set layout x and y because this doesn't need to change
        Line line = new Line(imagePoint1.getX(), imagePoint1.getY(), imagePoint2.getX(), imagePoint2.getY());
        line.setFill(Color.BLACK);
        line.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(line);
        miscDrawnObjects.add(line);
    }

    /**
     * convert to map coords
     * @param m
     * @return
     */
    private FloorPoint mouseToGraph(MouseEvent m){
        Node producer = null;
        // Find the container for the image
        if (m.getSource() instanceof Node) {
            producer = (Node) m.getSource();
        }
        else {
            return null;
        }
        double imageWidth = producer.getBoundsInLocal().getWidth();
        double imageHeight = producer.getBoundsInLocal().getHeight();

        int newX = (int) ( m.getX()  * 1000. / imageWidth );
        int newY = (int) ( m.getY() * 1000. / imageHeight);

        return new FloorPoint(newX, newY, currentMap);
    }

    /**
     * take 1000 x 1000 graph point and scale up to image size
     * @param graphPoint
     * @param imageToDrawOn image that the point will be placed on
     * @return
     */
    private FloorPoint graphToImage(FloorPoint graphPoint, ImageView imageToDrawOn){
        double imageWidth = imageToDrawOn.getBoundsInLocal().getWidth();
        double imageHeight = imageToDrawOn.getBoundsInLocal().getHeight();
        double layoutX = imageToDrawOn.getLayoutX();
        double layoutY = imageToDrawOn.getLayoutY();

        // convert the 1000 X 1000 graph point to the image dimensions
        int newX = (int) ( graphPoint.getX() * imageWidth / 1000. + layoutX);
        int newY = (int) ( graphPoint.getY() * imageHeight / 1000. + layoutY);

        return new FloorPoint(newX, newY, graphPoint.getFloor());
    }

    /**
     * If the selected node has a room attached to it, display the name of that room
     * @param selected
     */
    public void displayRoom (GraphNode selected) {
        Room room = map.getRoomFromNode(selectedNode);
        if (room != null) {
            roomName.setValue(room.getName());
            activeRoom = room;
            defaultKioskButton.setStyle("-fx-background-color: green;");
        }
        else {
            roomName.setValue("");
            activeRoom = null;
            defaultKioskButton.setStyle("-fx-background-color: gray;");
        }
    }

    /**
     * Change the default location of the kiosk
     */
    public void setDefaultKiosk () {
        if (activeRoom != null) {
            map.setKiosk(activeRoom.getName());
        }
    }

    /**
     * Highlight the primary node in red and secondary node in purple
     */
    public void highlightSelected () {
//        miscDrawnObjects.forEach(shape -> shape.setFill(Color.BLUE));
        drawnNodes.values().forEach(shape -> shape.setFill(Color.BLUE));
        if(selectedNode != null) {
            Shape selected1 = drawnNodes.get(selectedNode.id);
            if(selected1 != null) {
                selected1.setFill(Color.RED);
            }
        }
        if (secondaryNode != null) {
            Shape selected2 = drawnNodes.get(secondaryNode.id);
            if(selected2 != null){
                selected2.setFill(Color.PURPLE);
            }
        }
    }

    /**
     * switch state and reset values of all state objects (maybe this is optional)
     * You cannot switch to a different state when you are in a state
     * (we could implement a major mode and minor mode like emacs. But,
     * one mode at a time is the most simple for now)
     *
     * @param state
     */
    public void changeState (State state) {
        this.currentState = state;

        if (currentState != State.NONE) {
            selectedNode = null;
            secondaryNode = null;
        }

        if (currentState == State.ADD_ELEVATOR) {
            elevatorFloorOptions.setVisible(true);
            displayElevatorOptions();
        }
        else {
            elevatorFloorOptions.setVisible(false);
        }

        toggleSwitchMap(false);

        drawMap();
    }

    /**
     * When in add elevator, display the elevatorFloor options
     */
    public void displayElevatorOptions () {
       List<String> options = applicationController.getAllFloors();
       ObservableList<String> observOptions= FXCollections.observableArrayList(options);
       elevatorFloorOptions.setItems(observOptions);
       elevatorFloorOptions.getSelectionModel().clearSelection();
        for (String floor : elevatorFloors) {
            elevatorFloorOptions.getSelectionModel().select(floor);
        }
    }

    public void selectFloor (String floor) {
        if (!elevatorFloors.contains(floor)){
            elevatorFloors.add(floor);
        }
        else {
            elevatorFloors.remove(floor);
        }
        displayElevatorOptions();
    }

    /**
     * add node to graph
     * @param m
     */
    public void isPressed(MouseEvent m) {
        screenMenu.hide();
        if (roomName.isFocused()){
            anchorpaneMap.requestFocus(); //deselects textbox if click outside
        }
        switch (currentState){
            case NONE:
                // Possible state changing logic goes here
                handleMouseEventDefault(m);
                break;
            case ADD_NODES:
                handleMouseEventAddNode(m);
                break;
            case CHAIN_ADD:
                handleMouseEventChainAdd(m);
                break;
            case ADD_CONNECTION:
                handlePressAddConnection(m);
                break;
            case ADD_ELEVATOR:
                handleMouseEventAddElevator(m);
                break;
        }
        displayRoom(selectedNode);
        drawMap();
    }

    /**
     * Add an elevator at the given location
     * Create a menu to select what rooms
     * @param e
     */
    public void handleMouseEventAddElevator (MouseEvent e) {
        FloorPoint graphPoint = mouseToGraph(e);
        addElevator(graphPoint, elevatorFloors);
    }


    /**
     * handle the mouse event when no states are selected
     * @param e Mouse event generated the container
     */
    public void handleMouseEventDefault (MouseEvent e) {
        GraphNode nearby = nearbyNode(e);
        if (nearby != null) {
            secondaryNode = selectedNode;
            selectedNode = nearby;
        }
        else {
            selectedNode = null;
        }
    }

    /**
     * handle drag event
     * @param e
     */
    public void handleDragEvent (MouseEvent e) {
        switch (currentState) {
            case NONE:
                if ((selectedNode != null) && e.isPrimaryButtonDown()) {
                    currentState = State.DRAG_NODE;
                }
                break;
            case DRAG_NODE:
                // convert from image --> graph --> anchor pane and draw circle there
                FloorPoint mousePoint = mouseToGraph(e);
                FloorPoint imagePoint = graphToImage(mousePoint, imageviewMap);


                // Do not  points that would fall outside of the map
                if (mousePoint.getX() > 999 || mousePoint.getX() < 1 ||
                    mousePoint.getY() > 999 || mousePoint.getY() < 1) {
                    return;
                }

                if (selectedNode != null) {
                    selectedNode.setLocation(mouseToGraph(e));
                }
                // just move the point every drag event
                drawMap();
        }
    }

    /**
     * handle drop event from fxml
     * @param e
     */
    public void handleDragDropEvent (MouseEvent e) {
        changeState(State.NONE);
    }

    /**
     * Handle mouse event for chain add nodes
     * @param e
     */
    public void handleMouseEventChainAdd (MouseEvent e) {
        FloorPoint graphPoint = mouseToGraph(e);
        addNode(graphPoint);
        if (secondaryNode != null) {
            addConnection(secondaryNode, selectedNode);
        }
    }

    /**
     * Deselects all buttons when textbox is selected
     */
    public void isFocused(){
        System.out.println("getting unlocated rooms");
        ObservableList<String> unlocatedRoomOptions = FXCollections.observableArrayList(map.getRoomsWithoutLocations());
        roomName.setItems(unlocatedRoomOptions);
        changeState(State.NONE);
    }


    /**
     * handles the mouse click event on the unlocated rooms drop down menu
     * sets the romo name to the selected location
     */
    public void selectUnlocatedRoom () {
        String selectedString = roomName.getSelectionModel().getSelectedItem();
        roomName.setValue(selectedString);
        map.setRoomLocation(selectedString, selectedNode);
    }

    /**
     * Implements Key handling
     * To add key, add new case statement
     * @param key
     */
    @FXML
    public void handleKey(KeyEvent key){
        if (!roomName.isFocused()) {
            switch (key.getCode()) {
                case DELETE:
                    deleteSelected();
                    break;
                case N:
                    changeState(State.ADD_NODES);
                    break;
                case C:
                    changeState(State.ADD_CONNECTION);
                    break;
                case E:
                    changeState(State.ADD_ELEVATOR);
                    break;
                case A:
                    changeState(State.CHAIN_ADD);
                    break;
            }
        }
    }

    /**
     * Handle the mouse event for chain add node
     * @param e
     */
    public void handleMouseEventAddNode (MouseEvent e) {
        if (nearbyNode(e) == null) {
            FloorPoint graphPoint = mouseToGraph(e);
            addNode(graphPoint);
        } else {
            selectedNode = nearbyNode(e);
        }
    }

    /**
     * Handle the mouse event during the add connection state
     * @param e
     */
    public void handlePressAddConnection (MouseEvent e) {
        GraphNode nearby = nearbyNode(e);
        if (nearby != null) {
            secondaryNode = selectedNode;
            selectedNode = nearby;
            if (selectedNode != null && nearby != null) {
                addConnection(secondaryNode, selectedNode);
            }
        }
    }

    /**
     * Handle the release event
     * Each state can implement a release event
     * @param m
     */
    public void isReleased(MouseEvent m){
        switch (currentState){
            case NONE:
                break;
            case ADD_NODES:
                break;
            case CHAIN_ADD:
                break;
            case ADD_CONNECTION:
                break;
            case DRAG_NODE:
                handleDragDropEvent(m);
                changeState(State.NONE);
                break;
        }
        displayRoom(selectedNode);
        //drawMap();
    }

    /**
     * save and exit the application
     */
    public void done(){
        applicationController.logout();
    }

    /**
     * reload the database and reset the state of the gui
     */
    public void undo () {
        super.undo();
        activeRoom = null;
        selectedNode = null;
        secondaryNode = null;
        drawMap();
    }

    /**
     * Change the main display map.
     * @param loc
     */
    private void setMap(String loc){
        Image floorImage = applicationController.getImage(loc);
        imageviewMap.setImage(floorImage);
        currentMap = loc;
        // must make elevator on current floor
        if(! elevatorFloors.contains(loc)) {
            elevatorFloors.add(loc);
        }
        drawMap();
    }

    /**
     * Opens ContextMenu
     */
//    public void showContextMenu(ContextMenuEvent event){

//        ContextMenu contextMenu = new ContextMenu();
//
//        contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
//            public void handle(WindowEvent e) {
//                System.out.println("showing");
//            }
//        });
//        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
//            public void handle(WindowEvent e) {
//                System.out.println("shown");
//            }
//        });
//
//        MenuItem item1 = new MenuItem("About");
//        item1.setStyle("MapAdminContextMenu");
//        item1.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                System.out.println("About");
//            }
//        });
//        MenuItem item2 = new MenuItem("Preferences");
//        item2.setStyle("fx-background-image: red");
//        item2.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                System.out.println("Preferences");
//            }
//        });
//        contextMenu.getItems().addAll(item1, item2);
//        contextMenu.setId("MapAdminContextMenu");
//        contextMenu.show(circle, event.getScreenX(), event.getScreenY());
//        contextMenu.setStyle("-fx-shape:Circle ");
//        contextMenu.setStyle("fx-background-image: red");

    public void showContextMenu(ContextMenuEvent event){
        if(selectedNode == null){
            //nodeMenu.hide();
            Shape circle = new Circle(event.getX(), event.getY(), 10);
            anchorpaneMap.getChildren().add(circle);
            ImagePattern deleteRoom = new ImagePattern(new Image("/Radial Icons/Delete_Room.png"));
            ImagePattern addRoom = new ImagePattern(new Image("/Radial Icons/Add_Room2.png"));
            ImagePattern deleteNode = new ImagePattern(new Image("/Radial Icons/Delete_Node.png"));

            nodeMenu.addOption(deleteRoom);
            nodeMenu.addOption(addRoom);
            nodeMenu.addOption(deleteNode);
            nodeMenu.show(circle,event.getScreenX(), event.getScreenY());
        }else{

            Shape circle = new Circle(event.getX(), event.getY(), 10);
            anchorpaneMap.getChildren().add(circle);
            screenMenu.addOption(Color.WHITE);
            screenMenu.addOption(Color.RED);
            screenMenu.addOption(Color.BLUE);
            screenMenu.show(circle,event.getScreenX(), event.getScreenY());
        }


//        stage.getScene().setOnDragDetected(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                stage.getScene().startFullDrag();
//            }
//        });
//        stage.getScene().setOnMouseReleased(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
////                Event.fireEvent(menu.getOwnerWindow(), new MouseEvent(MouseEvent.MOUSE_RELEASED,
////                    event.getX(), event.getY(), event.getX(), event.getY(), MouseButton.PRIMARY, 1,
////                    true, true, true, true, true, true, true, true, true, true, null));
//                nodeMenu.fireEvent2(event.copyFor(nodeMenu,nodeMenu.getOwnerNode()));
//            }
//        });
//        contextMenu.getItems().addAll(item1, item2);
//        Shape circle = new Circle(event.getX(), event.getY(), 10);
//        anchorpaneMap.getChildren().add(circle);
//        contextMenu.setId("MapAdminContextMenu");
//        contextMenu.show(circle, event.getScreenX(), event.getScreenY());
//        contextMenu.setStyle("-fx-shape:Circle ");
//        contextMenu.setStyle("fx-background-image: red");
    }
}

