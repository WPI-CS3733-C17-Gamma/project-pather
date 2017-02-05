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
        String room2 = room.toLowerCase();

        if (((int)room2.charAt(0)) < 58 && ((int)room2.charAt(0)) > 47){
            map.searchRoom(room2);
        }
        else{
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
