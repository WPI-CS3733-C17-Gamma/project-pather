package app.display;

// this will hold the memento state
public class PatientMemento {
    String floor;

    /**
     * @param floor floor name for the default floor
     */
    public PatientMemento (String floor) {
	this.floor = floor; 
    }
}
