
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Elevator {
    private int id;
    private boolean isGoingUp;
    private boolean isGoingDown;
    private int maxPeople;
    private int currentFloor;
    private int defaultFloor;
    private List<Person> peopleOnBoard;

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
    
    public Elevator(int elId, int defFloor, int max){
        id = elId;
        isGoingUp = false;
        isGoingDown = false;
        maxPeople = max;
        currentFloor = defFloor;
        defaultFloor = defFloor;
        peopleOnBoard = Collections.synchronizedList(new ArrayList<Person>(maxPeople));
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDefaultFloor() {
        return defaultFloor;
    }

    public void setDefaultFloor(int defaultFloor) {
        this.defaultFloor = defaultFloor;
    }

    public boolean isGoingUp() {
        return isGoingUp;
    }

    public void setIsGoingUp(boolean up) {
        isGoingUp = up;
    }

    public boolean isGoingDown() {
        return isGoingDown;
    }

    public void setIsGoingDown(boolean down) {
        isGoingDown = down;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxNr) {
        maxPeople = maxNr;
    }

    public List<Person> getPeopleOnBoard() {
        return peopleOnBoard;
    }

    public void setPeopleOnBoard(List<Person> onBoard) {
        peopleOnBoard = onBoard;
    }
}
