import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MapAdminController extends DisplayController implements Initializable {

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


    // keep track of the objects that have been drawn on the screen
    HashMap<Long, Shape> drawnNodes = new HashMap<>();
    List<Shape> drawnLines = new ArrayList<>();

    @FXML private Button buttonSave;
    @FXML private Button buttonCancel;
    @FXML private ToggleButton togglebuttonAddNode;
    @FXML private ToggleButton togglebuttonAddConnections;
    @FXML private ToggleButton togglebuttonChainAdd;
    @FXML private ToggleButton togglebuttonAddElevator;
    @FXML private ImageView imageviewMap;
    @FXML private AnchorPane anchorpaneMap;
    @FXML private TextField roomName;

    @FXML private ListView<String> floorOptions;

    private GraphNode tempNode ;


    /**
     *  Construct map admin controller
     * @param map all the data for the program
     * @param applicationController main controller
     * @param currentMap
     */
    public MapAdminController(Map map, ApplicationController applicationController, String currentMap) {
        super(map, applicationController, currentMap);
    }

    /**
     * Called when the javaFX pane finishes loading
     * put startup stuff in here, not in the constructor
     * (The FXML elements are null in the constructor until this method loads)
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMap("floor3");
        drawMap();
    }

    /**
     * TODO
     * handle login with a string password
     * @param credentials
     */
    public void login(String credentials){
        //no need to worry about this for this iteration
    }

    /**
     * draw state of graph
     * and clear
     */
    public void drawMap(){
        anchorpaneMap.getChildren().removeAll(drawnNodes.values());
        drawnNodes.clear();
        anchorpaneMap.getChildren().removeAll(drawnLines);
        drawnLines.clear();
        anchorpaneMap.getChildren().removeAll(
            anchorpaneMap.getChildren().stream().filter(node -> node instanceof Shape).collect(Collectors.toList())
        );
        map.graph.getGraphNodesOnFloor(currentMap)
            .stream()
            .forEach(node -> drawNode(node, imageviewMap));
        highlightSelected();
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
            drawElevator(node.location, imageToDrawOver);
        }
        else {
            drawPoint(node.location, imageToDrawOver);
        }
        for (GraphNode adj : node.adjacent) {
            if(adj.location.floor.equals(currentMap)){
                drawConnection(node.location, adj.location);
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
        Rectangle rect = new Rectangle(imagePoint.x, imagePoint.y, 8,8);
        rect.setFill(Color.BLACK);
        rect.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(rect);
        GraphNode graphNodeAttatched = map.getGraphNode(loc);
        drawnNodes.put(graphNodeAttatched.id, rect);
    }

    public void toggleAddElevator () {
        switch (currentState){
            case ADD_ELEVATOR:
                changeState(State.NONE);
                break;
            default:
                changeState(State.ADD_ELEVATOR);
                break;
        }

    }

    /**
     * toggle chain add function
     * Does nothing if the user is in a different state alerady
     */
    public void toggleChainAdd () {
        switch (currentState){
            case CHAIN_ADD:
                changeState(State.NONE);
                break;
            default:
                changeState(State.CHAIN_ADD);
                break;
        }
    }

    /**
     * toggle add node function
     * Does nothing if the user is in a different state already
     */
    public void toggleAddNode() {
        switch (currentState){
            case ADD_NODES:
                changeState(State.NONE);
                break;
            default:
                changeState(State.ADD_NODES);
                break ;
        }
    }

    /**
     * Called when the user clicks the toggleAddConnectionButton
     * Does nothing if the user is in a different state already
     */
    public void toggleAddConnection() {
        switch (currentState){
            case ADD_CONNECTION:
                changeState(State.NONE);
                break;
            default:
                changeState(State.ADD_CONNECTION);
                break;

        }
    }
    /**
     * Create node from given location. Make new GraphNode
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
        System.out.println("Add elevator @ " + location);
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
        if (tempNode != null && tempNode.location.distance(graphPoint) > 50 ){
            tempNode = null;
        }
        return tempNode;
    }

    /**
     * Add a room to the map.
     * Take the text from the roomName text field
     */
    public void addRoom () {
        if(activeRoom != null) {
            String newName = roomName.getText();
            if(newName == activeRoom.name) {
            }
            else if(newName.isEmpty()){
                map.deleteRoom(activeRoom);
            }
            // if room is already there
            else if (map.getRoomFromName(activeRoom.name) == null){
                map.addRoom(new Room (selectedNode, newName));
            }
            else {
                map.changeRoomName(activeRoom, newName);
            }
            // change room
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
        circ.setLayoutX(imagePoint.x);
        circ.setLayoutY(imagePoint.y);
        circ.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(circ);
        GraphNode graphNodeAttatched = map.getGraphNode(loc);
        drawnNodes.put(graphNodeAttatched.id, circ);

    }

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
     * Delete a node from graph and delete the node from the adjacent nodes
     * @param node
     */
    public void deleteNode(GraphNode node){
        map.deleteNode(node);
    }

    public void deleteRoomFromNode(GraphNode node) {
        boolean successfulDelete = map.deleteRoom(node);    //why?
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
        Line line = new Line(imagePoint1.x, imagePoint1.y, imagePoint2.x, imagePoint2.y);
        line.setFill(Color.BLACK);
        line.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(line);
        drawnLines.add(line);
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
            roomName.setText(room.name);
            activeRoom = room;
        }
        else {
            activeRoom  = new Room(selected, "");
            roomName.setText("");
        }
    }

    /**
     * Highlight the primary node in red and secondary node in purple
     */
    public void highlightSelected () {
//        drawnLines.forEach(shape -> shape.setFill(Color.BLUE));
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
        selectedNode = null;
        secondaryNode = null;
        this.currentState = state;

        switch (state) {
            case ADD_CONNECTION:
                togglebuttonChainAdd.setSelected(false);
                togglebuttonAddConnections.setSelected(true);
                togglebuttonAddNode.setSelected(false);
                togglebuttonAddElevator.setSelected(false);
                break;
            case ADD_NODES:
                togglebuttonChainAdd.setSelected(false);
                togglebuttonAddConnections.setSelected(false);
                togglebuttonAddNode.setSelected(true);
                togglebuttonAddElevator.setSelected(false);
                break;
            case CHAIN_ADD:
                togglebuttonChainAdd.setSelected(true);
                togglebuttonAddConnections.setSelected(false);
                togglebuttonAddNode.setSelected(false);
                togglebuttonAddElevator.setSelected(false);
                break;
            case ADD_ELEVATOR:
                togglebuttonChainAdd.setSelected(false);
                togglebuttonAddConnections.setSelected(false);
                togglebuttonAddNode.setSelected(false);
                togglebuttonAddElevator.setSelected(true);
                break;
            case NONE:
                togglebuttonChainAdd.setSelected(false);
                togglebuttonAddConnections.setSelected(false);
                togglebuttonAddNode.setSelected(false);
                togglebuttonAddElevator.setSelected(false);
                break;

        }

        drawMap();
    }


    /**
     * add node to graph
     * @param m
     */
    public void isPressed(MouseEvent m) {
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

        List<String> floors = new ArrayList<>();
        floors.add("floor3");
        floors.add("floor4");

        addElevator(graphPoint, floors);
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
    }

    /**
     * handle drag event
     * @param e
     */
    public void handleDragEvent (MouseEvent e) {
        switch (currentState) {
            case NONE:
                if(selectedNode != null) {
                    currentState = State.DRAG_NODE;
                }
                break;
        }

        // convert from image --> gapph --> anchor pane and draw circle there
        FloorPoint mousePoint = mouseToGraph(e);
        FloorPoint imagePoint = graphToImage(mousePoint, imageviewMap);

        // Do not  points that would fall outside of the map
        if(mousePoint.x > 999 || mousePoint.x < 1 ||
            mousePoint.y > 999 || mousePoint.y < 1) {
            return ;
        }

        if(selectedNode != null) {
            selectedNode.location = mouseToGraph(e);
        }
        // just move the point every drag event
        drawMap();
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
     * Handle the mouse event for chain add node
     * @param e
     */
    public void handleMouseEventAddNode (MouseEvent e) {
        FloorPoint graphPoint = mouseToGraph(e);
        addNode(graphPoint);
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
        drawMap();
    }

    /**
     * save and exit the application
     */
    public void done(){
        applicationController.logout();
    }


    /**
     * create patient display without saving to the database
     */
    public void preview () {
        applicationController.createPatientDisplay();
    }

    /**
     * reload the database and reset the state of the gui
     */
    public void undo () {
        map = applicationController.reload();
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
        super.currentMap = loc;
    }
}
