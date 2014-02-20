
import java.awt.EventQueue;
import javax.swing.JFrame;

public class ElevatorSimulator {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JFrame frame = new HotelWindow();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
