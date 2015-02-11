/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package leaptest;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class LeapTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create a sample listener and controller
        Listener listener = new Listener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        while (true) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                Logger.getLogger(LeapTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            Frame frame = controller.frame();
            if (!frame.hands().isEmpty()) {
                // Get the first hand
                Hand hand0 = frame.hands().frontmost();
                double roll = Math.toDegrees(hand0.palmNormal().roll());
                double pitch = Math.toDegrees(hand0.palmNormal().pitch());
                double yaw = Math.toDegrees(hand0.direction().yaw());
                double x;
                double y;
                double z;
                x = -Math.pow(roll / 60,3);
                if (x > 1) {
                    if (x > 2) {
                        x = 0;
                    } else {
                        x = 1;
                    }
                } else if (x < -1) {
                    if (x < -2) {
                        x = 0;
                    } else {
                        x = -1;
                    }
                }
                y = -Math.pow((pitch + 90) / 50,3);
                if (y > 1) {
                    if (y > 2) {
                        y = 0;
                    } else {
                        y = 1;
                    }
                } else if (y < -1) {
                    if (y < -2) {
                        y = 0;
                    } else {
                         y = -1;
                    }
                }
                z = Math.pow(yaw / 60, 3);
                if (z > 1) {
                    if (z > 2) {
                        z = 0;
                    } else {
                        z = 1;
                    }
                } else if (z < -1) {
                    if (z < -2) {
                        z = 0;
                    } else {
                        z = -1;
                    }
                }
                if (hand0.isValid() && hand0.fingers().count() == 5 && hand0.palmVelocity().magnitude() < 300 && frame.hands().count() == 1 && hand0.palmPosition().getY() < 330) {
                System.out.println(", X: " + x + ", Y: " + y + ", Rot: " + z);
            
                } else {
                    System.out.println("Locking on...");
                }
            }
        }
    }
}
