
import java.util.Random;

public class Person {
    
    private int goingTo;
    private int currentFloor;
    boolean goingUp;
    
    public Person(int start, boolean up){
        currentFloor = start;
        goingUp = up;
        setGoingTo();
    }
    
    public void setGoingTo(){
        //Picks a random number between 0-19 to indicate what floor this person is going to.
        Random generator = new Random();
        goingTo = generator.nextInt(20);
        if(goingUp){
            while(goingTo <= currentFloor){
                goingTo = generator.nextInt(20);
            }
        }
        else{
            while(goingTo >= currentFloor){
                goingTo = generator.nextInt(20);
            }
        }
    }
    
    public int getGoingTo(){
        return goingTo;
    }
}
