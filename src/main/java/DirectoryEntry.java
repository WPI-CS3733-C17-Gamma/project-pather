import java.util.List;
import java.util.Objects;

public class DirectoryEntry extends Ided{

    /**
     * the name of the entry (e.g. Dr. John or Restroom)
     */
    String name;

    /**
     * the type of entry (Doctor or Service)
     */
    String title;

    /**
     * the associated room locations
     */
    List<Room> location;

    DirectoryEntry(String name, String title, List<Room> location)
    {
        this.name = name;
        this.title = title;
        this.location = location;
    }

    /**
     * @return the name of this entry
     */
    public String getName(){
        return this.name;
    }

    /**
     * @param name the name that the entry will be set to
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * @return the type/title of this entry
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * @return the list of locations associated with this entry
     */
    public List<Room> getLocation() {
        return location;
    }

    /**
     * @param location the new location that this entry is associated with
     */
    public void addLocatoin(Room location){
        this.location.add(location);
    }

    /**
     * @param loc the location that this entry is no longer associated with
     */
    public void deleteLocatoin(Room loc){
        this.location.remove(loc);
    }

    /**
     * @param title the new type that this entry is set to
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
