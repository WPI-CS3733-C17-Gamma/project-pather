import java.util.List;

public class PatientController extends DisplayController{
    //PatientDisplay display;
    GraphNode destination;
    ApplicationController appController;
//
//    public PatientController(/*PatientDisplay theDis, */GraphNode theDest, ApplicationController theAppCont) {
//        //this.display = display;
////        this.destination = destination;
////        this.appController = appController;
//    }

    public PatientController(Map map,
                             /*Kiosk kiosk, */
                             ApplicationController applicationController,
                             String currentMap){
        super(map,applicationController, currentMap);
    }

    /**
     * Search for a list of possible results given a string of input from the list of directory entries and list rooms
     * @param room the input string for searching for a room
     */
    public void search(String room) {
        List<String> result;
        if (((int)room.charAt(0)) < 58 && ((int)room.charAt(0)) > 47 && room.length() > 1){
            result = map.searchRoom(room);
        }
        else{
            String room2 = room.toLowerCase();
            result = map.searchEntry(room2);
        }
        //(update) the display the list of room
    }

    public GraphNode select(String option) {
        return null;
    }

    public void displayPath(GraphNode[] path) {
    }

    public void loginDirectoryAdmin(String login) {
    }

    void update() {
    }
}
