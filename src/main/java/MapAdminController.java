import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MapAdminController extends DisplayController implements Initializable {
    //    MapAdminDisplay display;
    GraphNode selectedNode;
    GraphNode secondaryNode;
    Room activeRoom ;
    boolean chainAdd;
    boolean addNode;
    boolean addConnection;


    HashMap<GraphNode, Shape> drawnNodes = new HashMap<>();
    List<Shape> drawnLines = new ArrayList<>();

    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;
    @FXML
    private ToggleButton togglebuttonAddNode;
    @FXML
    private ToggleButton togglebuttonAddConnections;
    @FXML
    private ToggleButton togglebuttonChainAdd;
    @FXML
    private ImageView imageviewMap;
    @FXML
    private AnchorPane anchorpaneMap;

    @FXML
    private TextField roomName;

    private GraphNode tempNode ;

    String mapName;



    /**
     *  Construct map admin controller
     * @param map all the data for the program
     * @param applicationController main controller
     * @param currentMap
     */
    public MapAdminController(Map map, ApplicationController applicationController, String currentMap) {
        super(map, applicationController, currentMap);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMap(location.toString());
        drawMap();
    }

    void update(){
        //needs to update the database with any changes

    }

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
        map.graph.getGraphNodesOnFloor("floor3")
            .stream()
            .forEach(node -> drawNode(node));
        highlightSelected();
    }


    public void drawNode (GraphNode node ) {
        drawNode(node.location);
        for (GraphNode adj : node.adjacent) {
            drawConnection(node.location, adj.location);
        }
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * toggle chain add function
     */
    public void toggleChainAdd () {
        chainAdd = !chainAdd;
        if(chainAdd){
            addNode = false;
            addConnection = false;

            togglebuttonAddNode.setSelected(false);
            togglebuttonAddConnections.setSelected(false);

        }
    }

    /**
     * handle toggle button
     */
    public void toggleAddNode() {
        addNode = !addNode;
        if(addNode){
            addConnection = false;
            chainAdd = false;

            togglebuttonAddConnections.setSelected(false);
            togglebuttonChainAdd.setSelected(false);
        }
        System.out.println(addNode);
    }
    public void toggleAddConnection() {
        addConnection = !addConnection;
        if(addConnection){
            addNode = false;
            chainAdd = false;
            togglebuttonAddNode.setSelected(false);
            togglebuttonChainAdd.setSelected(false);
        }
    }
    /**
     * Create node from given location. Make new GraphNode
     * @param location location to create a point at
     */
    public void addNode(FloorPoint location){
        System.out.println("add node @ " + location);
        GraphNode newNode = new GraphNode(location);
        map.addNode(newNode);
        secondaryNode = selectedNode;
        selectedNode = newNode;
        drawMap();
    }


    /**
     *
     * @return
     */
    public GraphNode nearbyNode (MouseEvent n) {
        FloorPoint graphPoint = mouseToGraph(n);
        System.out.println(graphPoint);

        tempNode = map.getGraphNode(graphPoint);
        System.out.println(tempNode);
        if (tempNode != null && tempNode.location.distance(graphPoint) > 50 ){
            tempNode = null;
            System.out.println("no close node");
        }
        return tempNode;
    }

    public void addRoom () {
        System.out.println("Add/Change room");
        if(activeRoom != null) {
            System.out.println("there is a room");
            String newName = roomName.getText();
            if(newName == activeRoom.name) {
                System.out.println("no change");
            }
            else if(newName.isEmpty()){
                System.out.println("DELETE room");
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

    private void drawNode(FloorPoint loc){
        FloorPoint imagePoint = graphToImage(loc);
        Circle circ = new Circle(imagePoint.x, imagePoint.y, 4, Color.BLUE);
        circ.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(circ);
        GraphNode graphNodeAttatched = map.getGraphNode(loc);
        drawnNodes.put(graphNodeAttatched, circ);

    }

    public void deleteConnection() {
        if(selectedNode != null && secondaryNode != null) {
            System.out.println("delete con");
            map.deleteConnection(selectedNode, secondaryNode);
            drawMap();
        }
    }

    //
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



//----------------------------------------------------------------------------------------------------------------------

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

    public void deleteConnection(GraphNode nodeA, GraphNode nodeB){



    }


    /**
     *
     * @param x1
     * @param x2
     */
    public void drawConnection(FloorPoint x1, FloorPoint x2){
        FloorPoint imagePoint1 = graphToImage(x1);
        FloorPoint imagePoint2 = graphToImage(x2);

        Line line = new Line(imagePoint1.x, imagePoint1.y, imagePoint2.x, imagePoint2.y);
        line.setFill(Color.BLACK);
        line.setMouseTransparent(true);
        anchorpaneMap.getChildren().add(line);
        drawnLines.add(line);
    }



//----------------------------------------------------------------------------------------------------------------------

    /**
     * convert to map coords
     * @param m
     * @return
     */
    private FloorPoint mouseToGraph(MouseEvent m){
//        double imageWidth = imageviewMap.getFitWidth();
//        double imageHeight = imageviewMap.getFitHeight();
        double imageWidth = imageviewMap.getBoundsInLocal().getWidth();
        double imageHeight = imageviewMap.getBoundsInLocal().getHeight();

        System.out.println("layout.getx"  + imageviewMap.getLayoutX());

        int newX = (int) ( m.getX()  * 1000. / imageWidth );
        int newY = (int) ( m.getY() * 1000. / imageHeight);

        return new FloorPoint(newX, newY, "floor3");
    }

    /**
     * take graph point and make image point
     * @param graphPoint
     * @return
     */
    private FloorPoint graphToImage(FloorPoint graphPoint){
//        double imageWidth = imageviewMap.getFitWidth();
//        double imageHeight = imageviewMap.getFitHeight();
        double imageWidth = imageviewMap.getBoundsInLocal().getWidth();
        double imageHeight = imageviewMap.getBoundsInLocal().getHeight();

        double layoutX = imageviewMap.getLayoutX();
        double layoutY = imageviewMap.getLayoutY();

        int newX = (int) ( graphPoint.getX() * imageWidth / 1000. + layoutX);
        int newY = (int) ( graphPoint.getY() * imageHeight / 1000. + layoutY);

        return new FloorPoint(newX, newY, graphPoint.getFloor());
    }

    public void displayRoom (GraphNode selected) {
        Room room = map.getRoomFromNode(selectedNode);
        if (room != null) {
            roomName.setText(room.name);
            activeRoom = room;
        }
        else {
            System.out.println("no romo found");
            activeRoom  = new Room(selected, "");
            roomName.setText("");
        }
    }

    /**
     * Highlights the primary node in red and secondary node in blue
     */
    public void highlightSelected () {
        drawnLines.forEach(shape -> shape.setFill(Color.BLUE));
        drawnNodes.values().forEach(shape -> shape.setFill(Color.BLUE));
        if(selectedNode != null) {
            Shape selected1 = drawnNodes.get(selectedNode);
            if(selected1 != null) {
                selected1.setFill(Color.RED);
            }
        }
        if (secondaryNode != null) {
            Shape selected2 = drawnNodes.get(secondaryNode);
            if(selected2 != null){
                selected2.setFill(Color.PURPLE);
            }
        }
    }
    /**
     * add node to graph
     * @param m
     */
    public void isPressed(MouseEvent m) {
        GraphNode nearby = nearbyNode(m);
        System.out.println("new sout");
        System.out.println("press");
        System.out.println("post near node");
        if (addConnection || (!addConnection && !chainAdd && !addNode)) {
            if (nearby != null) {
                secondaryNode = selectedNode;
                selectedNode = nearby;
                nearby = nearbyNode(m);
                if (addConnection && selectedNode != null && nearby != null) {
                    System.out.println("addConnection");
                    addConnection(secondaryNode, selectedNode);
                }
            }
        }
        FloorPoint graphPoint = mouseToGraph(m);
        System.out.println("Mouse to graph" + graphPoint);
        if (chainAdd) {
            addNode(graphPoint);
            if (secondaryNode != null) {
                addConnection(secondaryNode, selectedNode);
            }
        }
        else if (addNode) {
            addNode(graphPoint);
        }
        displayRoom(selectedNode);
        drawMap();
    }

//        if(nearby != null && nearby.equals(selectedNode)) {
//            // pass
//        }
//        else {
//            secondaryNode = selectedNode;
//            selectedNode = nearby;
//        }
//        displayRoom(selectedNode);

    // room stuff

    public void isReleased(MouseEvent m){
//        FloorPoint graphPoint = mouseToGraph(m);
//        System.out.println("release");
//        if (chainAdd) {
//            addNode(graphPoint);
//        }
//        else if (togglebuttonAddNode.isSelected()) {
//            addNode(graphPoint);
//        }
//        if (togglebuttonAddConnections.isSelected()){
//            GraphNode nearby = nearbyNode(m);
//            if(selectedNode != null && nearby != null) {
//                addConnection(nearby, selectedNode);
//            }
//        }
    }

    public void noMoreConnections(){ //checks if connections button is pushed
        if (togglebuttonAddConnections.isSelected()){
            togglebuttonAddConnections.setSelected(false);
        }
    }

    public void noMoreNodes(){      //checks if nodes button is pushed
        if (togglebuttonAddNode.isSelected()){
            togglebuttonAddNode.setSelected(false);
        }
    }

    /**
     * save and exit the application
     */
    public void done(){

        applicationController.logout();
    }


    /**
     * create patient display without saving to the database
     *
     */
    public void preview () {
        System.out.println("Preview");
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


//----------------------------------------------------------------------------------------------------------------------

    private void setMap(String loc){
        //System.out.println(loc);
        Image floor3 = new Image("Maps/floor3.png");
        imageviewMap.setImage(floor3);
        this.mapName = loc;
    }






}
