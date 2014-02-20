
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

public class ElevatorController {
    
    public static final int MAX_LOAD = 4;
    
    private HotelWindow window;
    private HotelWindow.SimulateElevatorMovement simulator[];
    private List<Elevator> elevatorList;
    private List<List<Person>> goingUpList;
    private List<List<Person>> goingDownList;
    private int nrOfElevators;
    
    public ElevatorController(HotelWindow caller, int nr_of_elevators, List<List<Person>> up, List<List<Person>> down){
        window = caller;
        nrOfElevators = nr_of_elevators;
        elevatorList = new ArrayList<>(nrOfElevators);
        goingUpList = up;
        goingDownList = down;
        simulator = new HotelWindow.SimulateElevatorMovement[nrOfElevators];
        installElevators();
        
        startSimulator();
    }
    
    private void installElevators(){
        List<List<JLabel>> eLList;
        List<List<Person>> upLine;
        List<List<Person>> downLine;
        for(int i = 0; i < nrOfElevators; i++){
            elevatorList.add(new Elevator(i, (i*5), MAX_LOAD));
            eLList = window.getElevatorLabelList();
            upLine = window.getGoingUpLine();
            downLine = window.getGoingUpLine();
            eLList.get(i).get(i*5).setBackground(Color.green);            
        }
    }
    
    public void startSimulator(){
        goToFloor(1, true);
    }
    
    public void goToFloor(int floor, boolean Up){
        //Finds all elevators distance to calling floor.
        int distances[] = new int[elevatorList.size()];
        Elevator el;
        
        //If calling floor is first floor.
        if(floor == 0){
            for(int i = 0; i < elevatorList.size(); i++){
                el = elevatorList.get(i);
                //if elevator is going down or it's standing still
                if(el.isGoingDown() || (!el.isGoingUp() && !el.isGoingDown())){
                    distances[i] = Math.abs(elevatorList.get(i).getCurrentFloor() - floor);
                }
                else{
                    distances[i] = 9999;
                }
            }
        }
        //if calling floor is top-floor.
        else if(floor == HotelWindow.NR_OF_FLOORS - 1){
            for(int i = 0; i < elevatorList.size(); i++){
                el = elevatorList.get(i);
                //if elevator is going up or it's standing still
                if(el.isGoingUp() || (!el.isGoingUp() && !el.isGoingDown())){
                    distances[i] = Math.abs(elevatorList.get(i).getCurrentFloor() - floor);
                }
                else{
                    distances[i] = 9999;
                }
            }
        }
        else{
            for(int i = 0; i < elevatorList.size(); i++){
                el = elevatorList.get(i);
                if(Up){
                    if((el.isGoingUp() && el.getCurrentFloor() < (floor - 1)) || (!el.isGoingUp() && !el.isGoingDown())){
                        distances[i] = Math.abs(elevatorList.get(i).getCurrentFloor() - floor);
                    }
                    else{
                        distances[i] = 9999;
                    }
                }
                else{
                    if((el.isGoingDown() && el.getCurrentFloor() > (floor + 1)) || (!el.isGoingUp() && !el.isGoingDown())){
                        distances[i] = Math.abs(elevatorList.get(i).getCurrentFloor() - floor);
                    }
                    else{
                        distances[i] = 9999;
                    }
                }
            }
        }
        
        int best = distances[0];
        int bestIndex = 0;
        for(int i = 1; i < distances.length; i++){
            if(distances[i] < best){
                best = distances[i];
                bestIndex = i;
            }
        }
        if(best != 9999){
            el = elevatorList.get(bestIndex);

            //if elevator is finished with last work, and available.
            if(!el.isGoingUp() && !el.isGoingDown()){
                simulator[bestIndex] = window.new SimulateElevatorMovement(el, floor);
                simulator[bestIndex].execute();
            }
            else{
                simulator[bestIndex].addStop(floor);
            }
        }
    }
    
    // Is called when an Elevator is leaving from a floor
    public void elevatorIsLeaving(int atFloor, boolean isGoingUp){
        // If elevator got full before line got empty, we 'push the button' once more.
        if(isGoingUp){
            if(!goingUpList.get(atFloor).isEmpty()){
                goToFloor(atFloor, true);
            }
        }
        else{
            if(!goingDownList.get(atFloor).isEmpty()){                
                goToFloor(atFloor, false);
            }
        }
    }
}
