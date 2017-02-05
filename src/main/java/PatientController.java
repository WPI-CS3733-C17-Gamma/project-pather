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

    public void search(String room) {
        if (((int)room.charAt(0)) < 58 && ((int)room.charAt(0)) > 47){
            map.searchRoom(room);
        }
        else{
            String room2 = room.toLowerCase();
            map.searchEntry(room2);
        }
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
