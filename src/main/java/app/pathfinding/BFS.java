package app.pathfinding;

import app.dataPrimitives.GraphNode;

import java.util.*;

/**
 * Created by zht on 2/19/2017.
 */
public class BFS implements IPathFindingAlgorithm {

    Queue<BFSNode> todo;
    List<BFSNode> checked;
    LinkedList<GraphNode> path;

    public String name = "BFS";

    /**
     * finds a path from the start and the end node using breath first search algorithm
     * @param start
     * @param end
     * @param useStairs
     * @return
     * @throws PathNotFoundException
     */
    @Override
    public LinkedList<GraphNode> findPath(GraphNode start, GraphNode end, boolean useStairs) throws PathNotFoundException {
        todo = new LinkedList<>();
        checked = new ArrayList<>();
        path = new LinkedList<>();

        //if the path starts and ends at the same place, the path contains that one node (path is found tho)
        if (start.equals(end)) {
            path.add(start);
            return path;
        }

        todo.add(new BFSNode(null, start));
        BFSNode current = new BFSNode(null, start);
        //while there are more nodes to check
        while(!todo.isEmpty()){
            current = todo.poll();
            checked.add(current);
            //if the next level contains the target
            if (current.current.getAdjacent().contains(end)){
                path.add(end);
                break;
            }
            //if not, add all of the unchecked neighbors into the todo list
            for (GraphNode neighbor : current.current.getAdjacent()){
                if (!BFScontains(checked, neighbor) && !BFScontains(todo, neighbor)){
                    todo.add(new BFSNode(current, neighbor));
                }
            }
        }
        //if no path is found, throw exception
        if (path.isEmpty()){
            throw new PathNotFoundException(start, end);
        }
        //trace back the path
        while(current != null){
            path.add(0, current.current);
            current = current.comeFrom;
        }
        return path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * check if a given list contains a certain graphnode
     * @param list
     * @param node
     * @return
     */
    private boolean BFScontains(Collection<BFSNode> list, GraphNode node){
        for (BFSNode n : list){
            if (n.current.equals(node)){
                return true;
            }
        }
        return false;
    }

    private class BFSNode{
        BFSNode comeFrom;
        GraphNode current;

        BFSNode(BFSNode from, GraphNode current){
            comeFrom = from;
            this.current = current;
        }

    }
}
