package app.dataPrimitives;

import app.Ided;

import java.util.List;

public class DirectoryEntry extends Ided {
    String name;

    String title;

    List<Room> location;

    String icon;

    public DirectoryEntry(String name, String title, List<Room> location)
    {
        this.name = name;
        this.title = title;
        this.location = location;
    }

    /**
     * the name of the entry (e.g. Dr. John or Restroom)
     */ /**
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
     * the type of entry (Doctor or Service)
     */ /**
     * @return the type/title of this entry
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * the associated room locations
     */ /**
     * @return the list of locations associated with this entry
     */
    public List<Room> getLocation() {
        return location;
    }

    /**
     *
     * @return the name of the icon associated with the entry
     */
    public String getIcon(){
        return this.icon;
    }

    /**
     * @param location the new location that this entry is associated with
     */
    public void addLocation(Room location){
        this.location.add(location);
    }

    /**
     * @param loc the location that this entry is no longer associated with
     */
    public void deleteLocation(Room loc){
        this.location.remove(loc);
    }

    /**
     * @param title the new type that this entry is set to
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param icon the name of the icon in the extra image hashmap
     */
    public void setIcon(String icon){
        this.icon = icon;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DirectoryEntry)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        DirectoryEntry rhs = (DirectoryEntry) obj;
        return this.name.equals(rhs.name) &&
            this.title.equals(rhs.title) &&
            this.location.equals(rhs.location);
    }

    public void setLocation(List<Room> location) {
        this.location = location;
    }
}
