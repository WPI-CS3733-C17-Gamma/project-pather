import java.util.List;

/**
 * Created by Isaac on 2/3/2017.
 */
public class DirectoryAdminController extends DisplayContoller {
    DirectoryAdminDisplay display;
    GraphNode activePoint;
    DirectoryEntry activeDirectoryEntry;
    Room activeRoom;

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
        return;
    }

    public void saveEntry(String name, String title, List<Room> room) {
        return;
    }

    public void addLocationToEntry(String room) {
        return;
    }

    public void deleteLocation(String room) {
        return;
    }

    public void changeTitle(String title) {
        return;
    }

    /* Functions for Rooms */
    public List<String> searchRoom(String search) {
        return null;
    }

    public void createRoom(String name, GraphNode node) {
        return;
    }

    public boolean deleteRoom(Room room) {
        return false;
    }

    public Room getRoom(Point location) {
        return null;
    }

    public Room getRoom(String name) {
        return null;
    }


    public GraphNode selectNode(Point loc) {
        return null;
    }

    public void saveEntry(String name, String title, List<Room> room) {
        return;
    }

    void update() {
        return;
    }
}
