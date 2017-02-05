import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;

public class MapAdminController {

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

    String mapName;

    void update(){
    }

    public void login(String credentials){
    }

    public void addNode(FloorPoint location){
        //need to add database save code
    }

    public void addNodeGraphically(){
        addNode(getMouseLocation());
    }

    public GraphNode selectNode(FloorPoint loc){
        return  null;
    }

    public void deleteNode(GraphNode node){
    }

    public void addConnection(GraphNode nodeA, GraphNode nodeB){
    }

    public void deleteConnection(GraphNode nodeA, GraphNode nodeB){
    }

    private void setMap(String loc, String mapName){
        File file = new File(loc);
        Image map = new Image(file.toURI().toString());
        imageviewMap.setImage(map);
        this.mapName = mapName;
    }

    private void isClicked(){
        if (togglebuttonAddNode.isSelected()){
            addNodeGraphically();
        } else if (togglebuttonAddConnections.isSelected()){
            //add in AddConnections stuff
        }
    }

    private FloorPoint getMouseLocation(){
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();

        return new FloorPoint(((int) b.getX()),(int) b.getY(),mapName);
    }

}
