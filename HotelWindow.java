
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class HotelWindow extends JFrame{
    public static final int NR_OF_FLOORS = 20;
    public static final int NR_OF_ELEVATORS = 4;
    public static final int DEFAULTLENGTH_LINES = 4;
    
    ElevatorController controller;
    private Container con;
    private GridLayout grid;
    private JLabel floorLabels[];
    private JButton elevatorCallers[];
    private JLabel lineCounter[];
    private List<List<JLabel>> elevatorLabelList;
    
    private List<List<Person>> goingUpLine;
    private List<List<Person>> goingDownLine;
    //People going of the Elevator is set 'on while' in tempLine before they Randomly travel to another floor.
    private List<List<Person>> tempLine;
    
    public HotelWindow(){
        super("Heissimulator");
        setLocationByPlatform(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screensize = toolkit.getScreenSize();
        int width = screensize.width;
        int heigth = screensize.height;
        setSize(width/2, heigth/2);
        
        grid = new GridLayout(0, 6, 5,5);       
        con = getContentPane();
        con.setLayout(grid);
        
        //List for taking care of lines.
        goingUpLine = new LinkedList<>();
        goingDownLine = new LinkedList<>();
        tempLine = new ArrayList<>();        
        
        // Creating all the floors
        elevatorLabelList = new ArrayList<>(NR_OF_ELEVATORS);
        floorLabels = new JLabel[NR_OF_FLOORS];
        lineCounter = new JLabel[NR_OF_FLOORS];        
        
        for(int i = 0; i < NR_OF_FLOORS; i++){            
            // Create a line on each floor and populate it with Persons
            List<Person> syncListUp = Collections.synchronizedList(new LinkedList<Person>());
            List<Person> syncListDown = Collections.synchronizedList(new LinkedList<Person>());
            
            //in bottom- and top-floor we don't want a line the wrong way.
            for(int j = 0; j < DEFAULTLENGTH_LINES / 2; j++){
                if(i == 0)
                    syncListDown = new LinkedList<Person>();
                else
                    syncListDown.add(new Person(i, false));
                if(i == NR_OF_FLOORS-1)
                    syncListUp = new LinkedList<Person>();
                else
                    syncListUp.add(new Person(i, true));              
                
            }
            goingUpLine.add(syncListUp);
            goingDownLine.add(syncListDown);
            
            // Creates a label to name the floor, and a label to indicate how many people in line.
            floorLabels[i] = new JLabel(i + ".etg");
            lineCounter[i] = new JLabel(syncListUp.size() + syncListDown.size() + "");
            tempLine.add(new ArrayList<Person>());
        }
        
        //Create elevatordoors.
        for(int j = 0; j < NR_OF_ELEVATORS; j++){
            elevatorLabelList.add(new ArrayList<JLabel>(NR_OF_FLOORS));
            for(int i = 0; i < NR_OF_FLOORS; i++){
                JLabel jl = new JLabel();
                jl.setOpaque(true);
                jl.setBackground(Color.red);
                elevatorLabelList.get(j).add(jl);
            }
        }        
        for(int i = 0; i < NR_OF_FLOORS; i++){
            con.add(floorLabels[i]);
            con.add(elevatorLabelList.get(0).get(i));
            con.add(elevatorLabelList.get(1).get(i));
            con.add(elevatorLabelList.get(2).get(i));
            con.add(elevatorLabelList.get(3).get(i));
            con.add(lineCounter[i]);
        }
        controller = new ElevatorController(this, NR_OF_ELEVATORS, goingUpLine, goingDownLine);
    }
    
    public List<List<JLabel>> getElevatorLabelList(){
        return elevatorLabelList;
    }
    
    public List<List<Person>> getGoingUpLine(){
        return goingUpLine;
    }
    
    public List<List<Person>> getGoingDownLine(){
        return goingDownLine;
    }
    
    //Updates label with number of persons in line.
    public synchronized void updateNrInLine(JLabel label, int toAdd, int toSub){
        int text = Integer.parseInt(label.getText());
        if(toAdd > 0){
            text += toAdd;
        }
        else{
            text -= toSub;
        }
        label.setText(text + "");
    }
    
    
    //Class that simulate elevatormovement.
    public class SimulateElevatorMovement extends SwingWorker<Void, Void>{
        
        private Elevator elevator;
        private ArrayList<Integer> goingToFloor = new ArrayList<>();
        
        public SimulateElevatorMovement(Elevator elev, int fl){
            elevator = elev;
            goingToFloor.add(fl);
        }

        @Override
        protected Void doInBackground() throws Exception {
            int currentFloor = elevator.getCurrentFloor();
            int stops;
            if(goingToFloor.get(0) > currentFloor){
                elevator.setIsGoingUp(true);
                elevator.setIsGoingDown(false);
                SwingUtilities.invokeAndWait(new ElevatorLeaving(currentFloor, true));
                
                for(int i = 0; i < goingToFloor.size(); i++){
                    while(currentFloor < goingToFloor.get(i)){
                        Thread.sleep(1000);
                        SwingUtilities.invokeAndWait(new ChangeColorOnDoor(elevator, currentFloor, true));
                        currentFloor++;
                    }
                    if(currentFloor == goingToFloor.get(i)){
                        onElevatorStop(currentFloor, true, false);
                        goingToFloor.remove(i);
                        i--;
                        Thread.sleep(2000);
                    }
                }
                //Work is done.
                elevator.setIsGoingDown(false);
                elevator.setIsGoingUp(false);
            }
            else if(goingToFloor.get(0) < currentFloor){
                elevator.setIsGoingUp(false);
                elevator.setIsGoingDown(true);
                SwingUtilities.invokeAndWait(new ElevatorLeaving(currentFloor, false));
                
                for(int i = 0; i < goingToFloor.size(); i++){
                    while(currentFloor > goingToFloor.get(i)){
                        Thread.sleep(1000);
                        SwingUtilities.invokeAndWait(new ChangeColorOnDoor(elevator, currentFloor, false));
                        currentFloor--;
                    }
                    if(currentFloor == goingToFloor.get(i)){
                        onElevatorStop(currentFloor, false, true);
                        goingToFloor.remove(i);
                        i--;
                        Thread.sleep(2000);
                    }
                }
                //Work is done.
                elevator.setIsGoingDown(false);
                elevator.setIsGoingUp(false);
            }            
            return null;
        }
        
        //Adds a new floor to stop at.
        public void addStop(int stop){
            goingToFloor.add(stop);
            Collections.sort(goingToFloor);
        }
        
        //Called when elevator stops on a floor.
        public void onElevatorStop(int currentFloor, boolean up, boolean down){
            //Let people walk out of the Elevator
            boolean isGoingUp = up;
            boolean isGoingDown = down;
            List<Person> peopleOnBoard = elevator.getPeopleOnBoard();
            List<Person> peopleInUpLine = goingUpLine.get(currentFloor);
            List<Person> peopleInDownLine = goingDownLine.get(currentFloor);
            for(int i = 0; i < peopleOnBoard.size(); i++){
                if(peopleOnBoard.get(i).getGoingTo() == currentFloor){
                    tempLine.get(currentFloor).add(peopleOnBoard.get(i));
                    peopleOnBoard.remove(i);
                    i--;
                }
            }
            //Fill up the Elevator if room, and if anyone in line.
            
            //If we reached the ends of elevatorshaft.
            if(currentFloor == 0 || currentFloor == NR_OF_FLOORS - 1){
                elevator.setIsGoingDown(false);
                isGoingDown = false;
                elevator.setIsGoingUp(false);
                isGoingUp = false;
            }
            int available = ElevatorController.MAX_LOAD - peopleOnBoard.size();
            int addedPeople = 0;
            int elevatorId = elevator.getId();
            if(available != 0){
                //If Elevator is going upwards.
                if(isGoingUp && !isGoingDown && !peopleInUpLine.isEmpty()){
                    while(available != 0 && !peopleInUpLine.isEmpty()){
                        //If this Elevator is one of the leftmost elevators we load from start of the line.
                        if(elevatorId <= NR_OF_ELEVATORS / 2)
                            peopleOnBoard.add(peopleInUpLine.remove(0));
                        //If this Elevator is one of the rigthmost elevators we load from end of the line.
                        else{
                            peopleOnBoard.add(peopleInUpLine.remove(peopleInUpLine.size() - 1)); 
                        }
                        addedPeople++;
                        available--;
                    }
                }
                //If Elevator is going downwards.
                else if(!isGoingUp && isGoingDown && !peopleInDownLine.isEmpty()){
                    while(available != 0 && !peopleInDownLine.isEmpty()){
                        //If this Elevator is one of the leftmost elevators we load from start of the line.
                        if(elevatorId <= NR_OF_ELEVATORS / 2){
                            peopleOnBoard.add(peopleInDownLine.remove(0));
                        }
                        //If this Elevator is one of the rightmost elevators we load from end of the line.
                        else{
                            peopleOnBoard.add(peopleInUpLine.remove(peopleInDownLine.size() - 1)); 
                        }
                        addedPeople++;
                        available--;
                    }
                }
                //If the Elevator is finished with job.
                else if(!isGoingUp && !isGoingDown){
                    if(!goingUpLine.isEmpty()){
                        while(!peopleInUpLine.isEmpty()){
                            //If this Elevator is one of the leftmost elevators we load from start of the line.
                            if(elevatorId <= NR_OF_ELEVATORS / 2){
                                peopleOnBoard.add(peopleInUpLine.remove(0));
                            }
                            //If this Elevator is one of the rigthmost elevators we load from end of the line.
                            else{
                                peopleOnBoard.add(peopleInUpLine.remove(peopleInUpLine.size() - 1)); 
                            }
                            addedPeople++;
                            available--;
                        }
                        //Everyone in the Elevator push the buttons for their floor-to-go.
                        for(int i = 0; i < peopleOnBoard.size(); i++){
                            goingToFloor.add(peopleOnBoard.get(i).getGoingTo());
                            Collections.sort(goingToFloor);
                        }
                    }
                    else if(!goingDownLine.isEmpty()){
                        while(!peopleInDownLine.isEmpty()){
                            //If this Elevator is one of the leftmost elevators we load from start of the line.
                            if(elevatorId <= NR_OF_ELEVATORS / 2){
                                peopleOnBoard.add(peopleInDownLine.remove(0));
                            }
                            //If this Elevator is one of the rightmost elevators we load from end of the line.
                            else{
                                peopleOnBoard.add(peopleInUpLine.remove(peopleInDownLine.size() - 1)); 
                            }
                            addedPeople++;
                            available--;
                        }
                    }
                    else{
                        // Do something
                    }
                }
                //Everyone in the Elevator push the buttons for their floor-to-go if it's not allready pushed.
                for(int i = 0; i < peopleOnBoard.size(); i++){
                    if(!goingToFloor.contains(peopleOnBoard.get(i).getGoingTo())){
                        goingToFloor.add(peopleOnBoard.get(i).getGoingTo());
                    }
                }
                //If Elevator is going down we have to sort it in desc.
                if(elevator.isGoingDown()){
                    Collections.sort(goingToFloor);
                    Collections.reverse(goingToFloor);
                }
                else{
                    Collections.sort(goingToFloor);
                }
                JLabel toUpdate = lineCounter[currentFloor];
                SwingUtilities.invokeLater(new UpdateNrInLine(toUpdate, 0, addedPeople));
            }
        }
    }
    
    //Called when a elevator is leaving a floor.
    public class ElevatorLeaving implements Runnable{
        
        private int floor;
        private boolean isGoingUp;
        
        public ElevatorLeaving(int fl, boolean up){
            floor = fl;
            isGoingUp = up;
        }

        @Override
        public void run() {
            controller.elevatorIsLeaving(floor, isGoingUp);
        }
    }
    
    //To change color on elevatordoors.
    public class ChangeColorOnDoor implements Runnable{
        
        private Elevator elevator;
        private int floor;
        private boolean isGoingUp;
        
        public ChangeColorOnDoor(Elevator elev, int fl, boolean up){
            elevator = elev;
            floor = fl;
            isGoingUp = up;
        }

        @Override
        public void run() {
            if(isGoingUp){
                elevatorLabelList.get(elevator.getId()).get(floor).setBackground(Color.red);
                elevatorLabelList.get(elevator.getId()).get(floor+1).setBackground(Color.green);
            }
            else{
                elevatorLabelList.get(elevator.getId()).get(floor).setBackground(Color.red);
                elevatorLabelList.get(elevator.getId()).get(floor-1).setBackground(Color.green);
            }
        }        
    }
    
    //Updates number of persons in line.
    public class UpdateNrInLine implements Runnable{
        
        private JLabel lineLabel;
        private int toAdd;
        private int toSub;
        
        public UpdateNrInLine(JLabel label, int add, int sub){
            lineLabel = label;
            toAdd = add;
            toSub = sub;
        }

        @Override
        public void run() {
            updateNrInLine(lineLabel, toAdd, toSub);
        }        
    }
}
