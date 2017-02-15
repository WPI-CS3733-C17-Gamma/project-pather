import javafx.scene.image.Image;

import java.util.*;

public class Map {
    Directory directory;
    GraphNetwork graph;
    HashMap<String, Image> mapImages;

    public Map(Directory directory,
               GraphNetwork graph,
               HashMap<String, Image> mapImages) {
        this.directory = directory;
        this.graph = graph;
        this.mapImages = mapImages;
    }

    /** See method {@link Directory#searchRooms(String)} */
    public List<String> searchRoom(String term){
        return directory.searchRooms(term);
    }

    /** See method {@Link Directory#subStringSearchRoom} */
    public List<String> subStringSearchRoom(String term) {
        return directory.subStringSearchRooms(term);
    }

    /**
     * Function to get all entries as a list
     * @return get all entries in the directory by name
     */
    public List<String> getAllEntries () {
        return directory.getAllEntries();
    }

    /**
     * Funcition to get all rooms as a list
     * @return get all entries in the directory by name
     */
    public List<String> getAllRooms () {
        return directory.getAllRooms();
    }

    /** See method {@link Directory#searchEntries(String)} */
    public List<String> searchEntry(String term){
        return directory.searchEntries(term);
    }

    /** See method {@link Directory#getRoom(String)} */
    public Room getRoomFromName(String roomName){
        return directory.getRoom(roomName);
    }

    /** See method {@link Directory#getRoom(GraphNode)} */
    public Room getRoomFromNode(GraphNode node){
        return directory.getRoom(node);
    }

    /**
     *
     * @param room
     * @return true if the room is added, false if it is a duplicate
     */
    /** See method {@link Directory#addRoom(Room)} */
    public boolean addRoom(Room room){
        return directory.addRoom(room);
    }

    /** See method {@link Directory#deleteRoom(Room)} */
    public boolean deleteRoom(Room room){
        return directory.deleteRoom(room);
    }

    /**
     * Function to delete a room by its node
     * @param node location of the room to delete
     * @return true if deleted, else false
     */
    public boolean deleteRoom(GraphNode node) {
        Room roomAtNode = directory.getRoom(node);
        return directory.deleteRoom(roomAtNode);
    }

    /**
     * Change the name of an existing room.
     * Returns true if the room is alredy contained in the directory
     * @param room
     * @param newName
     * @return
     */
    public boolean changeRoomName (Room room, String newName) {
        return directory.changeRoomName(room, newName);
    }

    /** See method {@link Directory#addEntry(DirectoryEntry)} */
    public boolean addEntry(DirectoryEntry entry){
        return directory.addEntry(entry);
    }

    /** See method {@link Directory#deleteEntry(DirectoryEntry)} */
    public boolean deleteEntry(DirectoryEntry entry){
        return directory.deleteEntry(entry);
    }

    /** See method {@link Directory#getEntry(String)} */
    public DirectoryEntry getEntry(String name){
        return directory.getEntry(name);
    }

    /**
     * Add an elevator at the given Point to all floors given
     * @param point x,y location
     * @param floors list of floors to connect the elevator through
     */
    public void addElevator (FloorPoint point, List<String> floors) {
        System.out.println("In map create elevator @ : " + point);
        List<GraphNode> elevators = new ArrayList<>();
        // make elevators
        for (String floor : floors) {
            FloorPoint currentFloor = new FloorPoint(point.x, point.y, floor);
            GraphNode newNode = new GraphNode(currentFloor);
            graph.addNode(newNode);
            elevators.add(newNode);
        }

        // connect elevators
        for (GraphNode elevatorA : elevators) {
            for (GraphNode elevatorB : elevators) {
                graph.addConnection(elevatorA, elevatorB);
            }
        }
    }

    /**
     * Delete an elevator and all other elevator nodes
     * it is connected to
     * @param elevator
     */
    public boolean deleteElevator (GraphNode elevator) {
        if (elevator.isElevator()) {
            //  copy adjacent list because it will be modified by deleting!
            List<GraphNode> copy = new ArrayList<GraphNode>();
            copy.addAll(elevator.getConnectedElevators());

            // delete each adjacent elevator
            for (GraphNode adjacentElevator : copy) {
                graph.deleteNode(adjacentElevator);
            }

            // delete the elevator that was marked for deletion
            this.deleteNode(elevator);
            return true;
        }
        return false;
    }

    /** See method {@link GraphNetwork#addNode(GraphNode)} */
    public boolean addNode(GraphNode node){
        return graph.addNode(node);
    }

    /**
     * Delete node from graph and delete the node from adjacent nodes
     * @param node node to be deleted
     * @return true
     */
    public boolean deleteNode(GraphNode node){
        graph.deleteNode(node);
        Room roomOnNode = this.getRoomFromNode(node);
        if(roomOnNode != null) {
            deleteRoom(node);
        }
        return true;
    }

    /**
     * return the graph node closest to the location given
     * ignores points on different floors
     * @param point point to get the node for
     * @return the graph node at the point
     */
    public GraphNode getGraphNode(FloorPoint point){
        return graph.getGraphNode(point);
    }

    /**
     * Returns a list of SubPaths.
     * Each subpath contains a string floor name
     * and a list of nodes to draw.
     * @param start
     * @param end
     * @return
     * @throws PathNotFoundException
     */
    public List<SubPath> getPathByFloor(GraphNode start, GraphNode end) throws PathNotFoundException {
        List<GraphNode> fullPath = graph.getPath(start, end);
        if(fullPath.isEmpty()) {
            return new ArrayList<>();
        }
        Collections.reverse(fullPath);
        List<SubPath> subPaths = new ArrayList<>();
        SubPath currentPath = new SubPath(fullPath.get(0).location.floor);
        for (GraphNode node : fullPath) {
            if (! node.getLocation().floor.equals(currentPath.floor)) {
                subPaths.add(currentPath);
                currentPath = new SubPath(node.location.floor) ;
            }
            currentPath.path.add(node);
        }
        subPaths.add(currentPath);

        return subPaths;
    }

    /** See method {@Link GraphNetwork#getPath(startNode, goalNode)} */
    public List<GraphNode> getPath(GraphNode start, GraphNode end){
        try {
            return graph.getPath(start, end);
        } catch( PathNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return new LinkedList<>();
    }

    /**
     * adds a connection between two nodes
     * @param nodeA First node
     * @param nodeB Second node
     * @return true if the point is  added to both nodes
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        return graph.addConnection(nodeA, nodeB);
    }

    /**
     * Deletes the connection between two nodes
     * @param nodeA first node
     * @param nodeB second node
     * @return true if successful
     */
    public boolean deleteConnection(GraphNode nodeA, GraphNode nodeB) {
        return this.graph.deleteConnection(nodeA, nodeB);
    }

    /**
     * @return the images stored in the Map object
     */
    public HashMap<String, Image> getImages(){
        return this.mapImages;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Map rhs = (Map) obj;
        return this.directory.equals(rhs.directory) &&
            this.graph.equals(rhs.graph) &&
            this.mapImages.equals(rhs.mapImages);
    }

}
