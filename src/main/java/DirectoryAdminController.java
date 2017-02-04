import java.util.List;

public class DirectoryAdminController extends DisplayController {
/*    DirectoryAdminDisplay display;*/
    GraphNode activePoint;
    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;

    public DirectoryAdminController(Map map,
                                    /*Kiosk kiosk, */
                                    ApplicationController applicationController,
                                    String currentMap) {
        super(map,
              /*kiosk,*/
              applicationController,
              currentMap);
    }

    public List<String> searchEntry(String search) {
        return null;
    }

    public DirectoryEntry selectEntry(String entryName) {
        return null;
    }

    public boolean deleteEntry(DirectoryEntry entry) {
        return false;
    }

    public void createEntry(String name, String title, List<Room> room) {
    }


    public void addLocationToEntry(String room) {
    }

    public void deleteLocation(String room) {
    }

    public void changeTitle(String title) {
    }

    /* Functions for Rooms */
    public List<String> searchRoom(String search) {
        return null;
    }

    public void createRoom(String name, GraphNode node) {
    }

    public boolean deleteRoom(Room room) {
        return false;
    }

    public Room getRoom(FloorPoint location) {
        return null;
    }

    public Room getRoom(String name) {
        return null;
    }


    public GraphNode selectNode(FloorPoint loc) {
        return null;
    }

    public void saveEntry(String name, String title, List<Room> room) {
    }

    void update() {
    }
}
