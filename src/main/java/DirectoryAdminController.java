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

    /**
     * activeDirectoryEntry must have the entry that is being edited
     * Throws error IllegalStateException if entry is not selected
     * and throws IllegalArgumentException if entry already exits
     * @param name name of the new entry
     * @param title title of the new entry
     * @param room list of room associated with the new entry
     */
    public void saveEntry(String name, String title, List<Room> room) throws IllegalStateException, IllegalArgumentException{
        if( activeDirectoryEntry == null ) {
            throw new IllegalStateException("Tried to save entry when none was selected\n"
                + "Please make sure 'activeDirectoryEntry in DirectoryAdminController is not null");
        }

        DirectoryEntry newEntry = new DirectoryEntry(name, title, room);

        if( map.getEntry(name) != null && map.getEntry(name).equals(newEntry) ) {
            throw new IllegalArgumentException("Tried to save entry that would be a duplicate of one"
            + " already in the directory");
        }

        System.out.print(activeDirectoryEntry.getName());
        map.deleteEntry(activeDirectoryEntry.getName());
        map.addEntry(newEntry);
        return;

    }

    void update() {
    }
}
