package app.datastore;

import app.dataPrimitives.*;
import app.pathfinding.IPathFindingAlgorithm;
import app.pathfinding.PathNotFoundException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

public class Map {
    Directory directory;
    GraphNetwork graph;
    HashMap<String, String> settings;

    public Map(Directory directory, GraphNetwork graph) {
        this.directory = directory;
        this.graph = graph;
        this.settings = new HashMap<String, String>();
    }

    public Map(Directory directory,
               GraphNetwork graph,
               HashMap<String, String> settings) {
        this.directory = directory;
        this.graph = graph;
        this.settings = settings;
    }

    /** See method {@link Directory#searchRooms(String)} */
    public List<String> searchRoom(String term){
        return directory.searchRooms(term);
    }

    /** See method {@Link app.datastore.Directory#subStringSearchRoom} */
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

    /** See method {@link Directory#getKioskLocation()} */
    public GraphNode getKioskLocation(){
        return directory.getKioskLocation();
    }

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
            FloorPoint currentFloor = new FloorPoint(point.getX(), point.getY(), floor);
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
        List<SubPath> subPaths = new ArrayList<>();
        SubPath currentPath = new SubPath(fullPath.get(0).getLocation().getFloor());
        for (GraphNode node : fullPath) {
            if (! node.getLocation().getFloor().equals(currentPath.getFloor())) {
                subPaths.add(currentPath);
                currentPath = new SubPath(node.getLocation().getFloor()) ;
            }
            currentPath.getPath().add(node);
        }
        subPaths.add(currentPath);

        return subPaths;
    }

    /** See method {@Link app.datastore.GraphNetwork#getPath(startNode, goalNode)} */
    public List<GraphNode> getPath(GraphNode start, GraphNode end){
        try {
            return graph.getPath(start, end);
        } catch( PathNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return new LinkedList<>();
    }

    /** See method {@link GraphNetwork#getDirections(List)} */
    public List<String> getTextualDirections(List<GraphNode> path) {
        return graph.getDirections(path);
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
     * Gets a setting by name
     * @param name the setting to get
     * @return the value of the setting
     */
    public String getSetting(String name) {
        return settings.get(name);
    }

    /**
     * Sets a setting by name
     * @param name the setting to get
     * @return the value of the setting
     */
    public String setSetting(String name, String value) {
        return settings.put(name, value);
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
            this.graph.equals(rhs.graph);
    }

    public LinkedList<GraphNode> getGraphNodes() {
        return graph.graphNodes;
    }

    /** See method {@link app.datastore.Directory#getRooms()} */
    public HashMap<String, Room> getDirectoryRooms() {
        return directory.getRooms();
    }

    /** See method {@Link app.datastore.Directory#getEntries()} */
    public HashMap<String, DirectoryEntry> getDirectoryEntries() {
        return directory.getEntries();
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }

    public List<GraphNode> getGraphNodesOnFloor(String currentMap) {
        return graph.getGraphNodesOnFloor(currentMap);
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public GraphNetwork getGraph() {
        return graph;
    }

    public void setGraph(GraphNetwork graph) {
        this.graph = graph;
    }
}