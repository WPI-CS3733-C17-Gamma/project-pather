import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import java.util.List;
import java.util.ResourceBundle;

public class MapAdminController extends DisplayController implements Initializable {
//    MapAdminDisplay display;
    GraphNode selectedNode;
    GraphNode secondaryNode;

    List<Shape> drawnShapes = new ArrayList<>();

    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;
    @FXML
    private ToggleButton togglebuttonAddNode;
    @FXML
    private ToggleButton togglebuttonAddConnections;
    @FXML
    private ImageView imageviewMap;
    @FXML
    private AnchorPane anchorpaneMap;
    @FXML
    private AnchorPane anchorpaneWindow;

    private GraphNode tempNode ;

    String mapName;

    double yDrop;


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
        yDrop = anchorpaneWindow.getLayoutY();
    }

    void update(){
        //needs to update the database with any changes

    }

    public void login(String credentials){
        //no need to worry about this for this iteration
    }

    /**
     * draw state of graph
     */
    public void drawMap(){
        anchorpaneMap.getChildren().removeAll(drawnShapes);
        drawnShapes.clear();
       map.graph.getGraphNodesOnFloor("floor3")
       .stream()
       .forEach(node -> drawNode(node));
    }


    public void drawNode (GraphNode node ) {
        drawNode(node.location);
        for (GraphNode adj : node.adjacent) {
            drawConnection(node.location, adj.location);
        }
    }
//----------------------------------------------------------------------------------------------------------------------

    /**
     * Create node from given location. Make new GraphNode
     * @param location location to create a point at
     */
    public void addNode(FloorPoint location){
        map.addNode(new GraphNode(location));
        drawNode(location);
    }

    public void addNodeGraphically(MouseEvent e){
        addNode(mouseToGraph(e));
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
        if (tempNode.location.distance(graphPoint) > 10 ){
            tempNode = null;
            System.out.println("no close node");
        }
        return tempNode;
    }

    private void drawNode(FloorPoint loc){
        FloorPoint imagePoint = graphToImage(loc);
        Circle circ = new Circle(imagePoint.x, imagePoint.y, 3, Color.BLUE);
        circ.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isClicked(event);
            }
        });
        anchorpaneMap.getChildren().add(circ);
        drawnShapes.add(circ);

    }


    /**
     * Delete a node from graph and delete the node from the adjacent nodes
     * @param node
     */
    public void deleteNode(GraphNode node){

        map.deleteNode(node);
    }
    /**
     * add a room to the selected node given a node and a name
     * @param node
     * @param roomName
     */
    public void addRoomToNode(GraphNode node, String roomName) {
        boolean successfulAdd = map.addRoom(new Room(node, roomName));
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
        map.addConnection(nodeA, nodeB);
        drawConnection(nodeA.location, nodeB.location);
    }

    public void deleteConnection(GraphNode nodeA, GraphNode nodeB){



    }

    public void addConnectionPressed(MouseEvent n){
            tempNode = nearbyNode(n);
        }


    public void addConnectionReleased(MouseEvent m) {

            GraphNode tempFinal = nearbyNode(m);
            if (tempNode == null || tempFinal == null) {
                //Add in error exception
                System.out.println("Missing node");
            } else if (tempFinal.equals(tempNode)) {
                //Add in error exception
                System.out.println("Too close");
            } else {
                addConnection(tempNode, tempFinal);
            }
        }


    public void drawConnection(FloorPoint x1, FloorPoint x2){
        FloorPoint imagePoint1 = graphToImage(x1);
        FloorPoint imagePoint2 = graphToImage(x2);

        Line line = new Line(imagePoint1.x, imagePoint1.y, imagePoint2.x, imagePoint2.y);
        line.setFill(Color.BLACK);
        anchorpaneMap.getChildren().add(line);
        drawnShapes.add(line);
    }



//----------------------------------------------------------------------------------------------------------------------

    /**
     * convert to map coords
     * @param m
     * @return
     */
    private FloorPoint mouseToGraph(MouseEvent m){
        double imageWidth = imageviewMap.getFitWidth();
        double imageHeight = imageviewMap.getFitHeight();

        int newX = (int) ( m.getX() / imageWidth * 1000);
        int newY = (int) ( m.getY() / imageHeight* 1000);

        return new FloorPoint(newX, newY, "floor3");
    }

    /**
     * take graph point and make image point
     * @param graphPoint
     * @return
     */
    private FloorPoint graphToImage(FloorPoint graphPoint){
        double imageWidth = imageviewMap.getFitWidth();
        double imageHeight = imageviewMap.getFitHeight();

        int newX = (int) ( graphPoint.getX() * imageWidth / 1000.);
        int newY = (int) ( graphPoint.getY() * imageHeight / 1000.);

        return new FloorPoint(newX, newY, graphPoint.getFloor());
    }

    /**
     * add node to graph
     * @param m
     */
    public void isClicked(MouseEvent m){
        System.out.println("click");
        if (togglebuttonAddNode.isSelected()) {
            addNodeGraphically(m);
        } else {
            GraphNode nearby = nearbyNode(m);
            if(selectedNode == null) {
                selectedNode = nearby;
                System.out.println("selected");
            }
            else {
                secondaryNode =selectedNode;
                selectedNode = nearby;
                System.out.println("replaced selected");
            }
        }
    }

    public void isPressed(MouseEvent m){

        if (togglebuttonAddConnections.isSelected()){
            addConnectionPressed(m);
        }
    }

    public void isReleased(MouseEvent m){
        if (togglebuttonAddConnections.isSelected()){
            addConnectionReleased(m);
        }
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

    public void done(){
        //Save function here
        togglebuttonAddNode.setSelected(false);
        togglebuttonAddConnections.setSelected(false);
        applicationController.createPatientDisplay();
    }

//----------------------------------------------------------------------------------------------------------------------

    private void setMap(String loc){
        //System.out.println(loc);
        Image floor3 = new Image("Maps/floor3.png");
        imageviewMap.setImage(floor3);
        this.mapName = loc;
    }






}
