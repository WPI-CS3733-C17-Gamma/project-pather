package app.datastore;

import app.pathfinding.*;
import app.dataPrimitives.FloorPoint;
import app.dataPrimitives.GraphNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphNetwork {
    final Logger logger = LoggerFactory.getLogger(GraphNetwork.class);

    LinkedList<GraphNode> graphNodes = new LinkedList<>();
    private HashMap<String, IPathFindingAlgorithm> pathingAlgorithm = new HashMap<>();
    private IPathFindingAlgorithm currentPathingAlgorithm;

    public GraphNetwork(){
        setUpPathFindingAlgorithms();
        currentPathingAlgorithm = pathingAlgorithm.get("A*");
    }

    public GraphNetwork(LinkedList<GraphNode> graphNodes) {
        setUpPathFindingAlgorithms();
        currentPathingAlgorithm = pathingAlgorithm.get("A*");
        this.graphNodes = graphNodes;
    }

    private void setUpPathFindingAlgorithms(){
        pathingAlgorithm.put("DFS", new DFS());
        pathingAlgorithm.put("BFS", new BFS());
        pathingAlgorithm.put("A*", new AStar());
    }

    /**
     * given the name of the algorithm in String ("AStar", "DFS", "BFS"), set the search method
     * to the corresponding algorithm
     * @param algo
     */
    public void changeAlgorithm(String algo){
        logger.info(algo);
        currentPathingAlgorithm = pathingAlgorithm.get(algo);
    }

    public Set<String> getPathingAlgorithmList(){
        return pathingAlgorithm.keySet();
    }

    public String getPathingAlgorithm(){
        return currentPathingAlgorithm.getName();
    }

    /**
     * returns a path from the startNode to the goalNode
     * @param startNode
     * @param goalNode
     * @return
     * @throws PathNotFoundException
     */
    public LinkedList<GraphNode> getPath(GraphNode startNode, GraphNode goalNode) throws PathNotFoundException {
        return currentPathingAlgorithm.findPath(startNode, goalNode);
    }

    /**
     * Generates a list of textual directions for the path
     * @param path the path to get directions for
     * @return a List of string directions
     */
    public static List<String> getDirections(List<GraphNode> path) {
        LinkedList<String>  directions = new LinkedList<>();

        int nodeNum = -1;
        for (GraphNode node : path) {
            nodeNum++;
            // No directions for first and last node
            if (nodeNum == 0 || nodeNum >= path.size() - 1) {
                continue;
            }
            //TODO decide what we should do about this
            // No directions if there is only one path option
            /*if (node.getAdjacent().size() <= 2) {
                continue;
            }*/
            // Get a direction from the angle
            double angle = node.getAngle(path.get(nodeNum - 1), path.get(nodeNum + 1));
            if (angle < 80) {
                // Sharp Right
                directions.add("Take a sharp left");
            }
            else if (angle >= 80 && angle <= 170) {
                // Right
                directions.add("Take a left");
            }
            else if (angle > 170 && angle < 190) {
                // Straight
                directions.add("Continue going straight");
            }
            else if (angle >= 190 && angle <= 280) {
                // Left
                directions.add("Take a right");
            }
            else if (angle >= 280) {
                // Sharp Left
                directions.add("Take a sharp right");
            }
        }
        return directions;
    }

    /**
     * return the node closest to the point on the same floor
     * Does not reject points based off distance, this must be handled
     * by the caller
     * @param loc
     * @return
     */
    public GraphNode getGraphNode(FloorPoint loc){


        List<GraphNode> sameFloor = getGraphNodesOnFloor(loc.getFloor());
        if(sameFloor.isEmpty()) {
            return null;
        }
        // get the closest point to the node
        GraphNode closestNode = sameFloor.get(0);
        double minDistance = closestNode.getLocation().distance(loc);

        // find the minimum distance on the same floor
        for (int i = 0 ; i < sameFloor.size(); i++) {
            GraphNode currentNode = sameFloor.get(i);
            double currentDistance = currentNode.getLocation().distance(loc);
            if(currentDistance < minDistance){
                minDistance = currentDistance;
                closestNode = currentNode;
            }
        }
        return closestNode;
    }

    /**
     * Get a list of all nodes on a specified floor
     * @param floor string floor name
     * @return
     */
    public List<GraphNode> getGraphNodesOnFloor(String floor) {
      List<GraphNode> sameFloor = graphNodes.stream()
              .filter(node -> node.getLocation().getFloor().equals(floor))
              .collect(Collectors.toList());

      return sameFloor;
    }

    /**
     * Add node to graph if it is not already there
     * @param node
     */
    public boolean addNode(GraphNode node){
        if(!graphNodes.contains(node)){
            graphNodes.add(node);
            return true;
        }
        return false;
    }

    /**
     * Delete a node from graph and delete the node from the adjacent nodes
     * @param node
     */
    public void deleteNode(GraphNode node){
        // 1. remove node from list
        graphNodes.remove(node);
        // 2. remove node from all adjacent nodes
       for(GraphNode s: node.getAdjacent()){
           s.removeAdjacent(node);
       }
    }

    /**
     * Adds a connection between two nodes
     * @param nodeA
     * @param nodeB
     * @return true if connection was successful
     */
    public boolean addConnection(GraphNode nodeA, GraphNode nodeB){
        nodeA.addAdjacent(nodeB);
        nodeB.addAdjacent(nodeA);
        if(nodeA.getAdjacent().contains(nodeB) && nodeB.getAdjacent().contains(nodeA))
            return true;
        else
            return false;
    }

    /**
     * Deletes the connection between two nodes
     * @param nodeA
     * @param nodeB
     * @return true if successful
     */
    public boolean deleteConnection(GraphNode nodeA, GraphNode nodeB) {
        if (nodeB.getAdjacent().contains(nodeA)) {
            //do the thing
            nodeA.removeAdjacent(nodeB);
            nodeB.removeAdjacent(nodeA);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphNetwork)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        GraphNetwork rhs = (GraphNetwork) obj;
        return this.graphNodes.equals(rhs.graphNodes);
    }
}
