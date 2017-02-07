import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MapAdminController extends DisplayController implements Initializable {
//    MapAdminDisplay display;
    GraphNode selectedNode;
    GraphNode secondaryNode;

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


    void update(){
        //needs to update the database with any changes

    }

    public void login(String credentials){
        //no need to worry about this for this iteration
    }

    /**
     * Create node from given location. Make new GraphNode
     * @param location location to create a point at
     */
    public void addNode(FloorPoint location){
        map.addNode(new GraphNode(location));
        System.out.println(location.getX() + " " + location.getY());
        Circle c = new Circle((double)location.getX(), (double)location.getY() + 150, 3, Color.BLUE); //Circle not in the right place bc cursor is entire frame while circle is within image frame.
        System.out.println(c.getCenterX() + " " + c.getCenterY());
        System.out.println();
        anchorpaneWindow.getChildren().addAll(c);

        //Everything needs to be adjusted in database
    }

    public void addNodeGraphically(MouseEvent e){
    addNode(getMouseLocation(e));
    }

    /**
     * Given the user mouse click, return the node they selected
     * If it is outside an acceptable distance, do not select the node
     * @param loc location generated by mouse click
     * @return selected GraphNode
     */
    public GraphNode selectNode(FloorPoint loc){
        GraphNode selected = map.getGraphNode(loc);
        int acceptableDistance = 100; // if farther than 100 units, don't select point

        if (selected != null && selected.location.distance(loc) < acceptableDistance) {
            return selected;
        }
        else {
            return null;
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
        map.addConnection(nodeA, nodeB);
        Line l = new Line(nodeA.location.getX(),nodeA.location.getY(), nodeB.location.getX(), nodeB.location.getY());
        l.setFill(Color.RED);

    }

    public void addConnectionGraphically(){



    }


    public void deleteConnection(GraphNode nodeA, GraphNode nodeB){
        //idk what to do about deleting this
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
        boolean successfulDelete = map.deleteRoom(node);
    }

    private void setMap(String loc){
        System.out.println(loc);
        Image floor3 = new Image("Maps/floor3.png");
        imageviewMap.setImage(floor3);
        this.mapName = loc;
    }

    public void isClicked(MouseEvent m){
        if (togglebuttonAddNode.isSelected()) {
            addNodeGraphically(m);
        }
    }

    private void isDragged(){
        if (togglebuttonAddConnections.isSelected()){

        }
    }

    private FloorPoint getMouseLocation(MouseEvent mouseEvent){

        return new FloorPoint((int)mouseEvent.getX(),(int)mouseEvent.getY(),mapName); //don't need to adjust for resolution because resolution is 1150x625
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMap(location.toString());
    }

}
