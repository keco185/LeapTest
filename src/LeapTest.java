/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Kevin
 */
class DSWindow extends JComponent {
    private static final long serialVersionUID = 1L;
    public static int windowWidth = 500;
    public static int windowHeight = 300;
    double x = 0.0;
    double y = 0.0;
    double rot = 0.0;
    int status = 0; //0 = red 1 = yellow 2 = green
    boolean allowRot = true;
    public DSWindow(double x, double y, double rot, int status, boolean allowRot) {
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.status = status;
        this.allowRot = allowRot;
    }
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (this.allowRot || this.status == 2 && !(this.status == 0)) {
            this.allowRot = true;
        } else {
            this.allowRot = false;
        }
        int xPoints[] = {0,-10,0,10,0};
        int yPoints[] = {0,20,14,20,0};
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, windowWidth, windowHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(20, 20, 260, 260); //driving
        g2.drawRect(300, 20, 180, 260); //status
        if (status == 0) {
            g2.setColor(Color.red);
        } else if (status == 1) {
            g2.setColor(Color.YELLOW);
        } else if (status == 2) {
            g2.setColor(Color.GREEN);
        }
        g2.fillRect(301, 21, 179, 259);
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(150, 20, 150, 280); //crosshair
        g2.drawLine(20, 150, 280, 150); //crosshair
        g2.translate(150 + (130*x), 150 - (130*y));
        g2.rotate(rot*Math.PI/2);
        if (this.allowRot) {
        g2.setColor(Color.red);
        } else {
            g2.setColor(Color.GRAY);
        }
        g2.fillPolygon(xPoints, yPoints, 5);
    }
}
public class LeapTest {
    static GraphicsDevice device = GraphicsEnvironment
	.getLocalGraphicsEnvironment().getScreenDevices()[0];
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.revalidate ();
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setTitle("Hand Control");
	window.setResizable(false);
	window.setName("Hand Control");
	window.setBounds(0, 0, 500, 330);
        window.getContentPane().removeAll();
        window.getContentPane().add(new DSWindow(0.0, 0.0, 0.0, 0, false));
        window.setVisible(true);
        System.out.println("Setting Motion Listener");
        Listener listener = new Listener();
        Controller controller = new Controller();

        controller.addListener(listener);
        System.out.println("Setting to Client Mode");
        NetworkTable.setClientMode();
        System.out.println("Connecting to robot");
        NetworkTable.setTeam(484);
        NetworkTable.setIPAddress("10.4.84.2");
        NetworkTable table = NetworkTable.getTable("SmartDashboard");
        int speedWait = 0;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        int status = 0;
        boolean allowRot = false;
        while (true) {
            double xOld = x;
            double yOld = y;
            status = 0;
            x = 0.0;
            y = 0.0;
            z = 0.0;
            allowRot = false;
            
            try {
                Thread.sleep(15);
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
                if (roll > 0) {
                    x = -Math.pow(roll / 60,2);
                } else {
                    x = Math.pow(roll / 60,2);
                }
                if (x > 1) {
                    if (x > 2) {
                        x = 0;
                        status = 1;
                        speedWait = 20;
                        allowRot = true;
                    } else {
                        x = 1;
                    }
                } else if (x < -1) {
                    if (x < -2) {
                        x = 0;
                        status = 1;
                        speedWait = 20;
                        allowRot = true;
                    } else {
                        x = -1;
                    }
                }
                if (pitch + 90 > 0) {
                    y = -Math.pow((pitch + 90) / 50,2);
                } else {
                    y = Math.pow((pitch + 90) / 50,2);
                }
                if (y > 1) {
                    if (y > 2) {
                        y = 0;
                        status = 1;
                        speedWait = 20;
                        allowRot = true;
                    } else {
                        y = 1;
                    }
                } else if (y < -1) {
                    if (y < -2) {
                        y = 0;
                        status = 1;
                        speedWait = 20;
                        allowRot = true;
                    } else {
                         y = -1;
                    }
                }
                if (yaw > 0) {
                    z = Math.pow(yaw / 60, 2);
                } else {
                    z = -Math.pow(yaw / 60, 2);
                }
                if (z > 1) {
                    if (z > 2) {
                        z = 0;
                        status = 1;
                    } else {
                        z = 1;
                    }
                } else if (z < -1) {
                    if (z < -2) {
                        z = 0;
                        status = 1;
                    } else {
                        z = -1;
                    }
                }
                if (hand0.palmVelocity().magnitude() > 300) {
                    speedWait = 20;
                } else if (speedWait > 0) {
                    speedWait--;
                }
                if (Math.abs(xOld - x) > 0.1 || Math.abs(yOld - y) > 0.15) {
                    allowRot = false;
                    status = 1;
                }
                if (hand0.isValid() && hand0.fingers().count() == 5 && hand0.palmVelocity().magnitude() < 300 && frame.hands().count() == 1 && speedWait == 0 && hand0.palmPosition().getY() < 330) {
                System.out.println("X: " + xOld + ", Y: " + yOld + ", Rot: " + z);
                    table.putNumber("DriveX", x);
                    table.putNumber("DriveY", y);
                    table.putNumber("Rot", z);
                    table.putBoolean("AllowRot", allowRot);
                    if (status == 0) {
                        status = 2;
                    }
                } else {
                    if (hand0.isValid() && hand0.fingers().count() == 5 && hand0.palmVelocity().magnitude() < 300 && frame.hands().count() == 1 && hand0.palmPosition().getY() < 330) {
                        allowRot = true;
                    } else {
                        allowRot = false;
                    }
                    table.putNumber("DriveX", 0);
                    table.putNumber("DriveY", 0);
                    table.putNumber("Rot", 0);
                    table.putBoolean("AllowRot", allowRot);
                    System.out.println("Locking on...");
                    status = 1;
                }
            } else {
                table.putNumber("DriveX", 0);
                table.putNumber("DriveY", 0);
                table.putNumber("Rot", 0);
                table.putBoolean("AllowRot", allowRot);
                System.out.println("No Hands");
            }
            table.putNumber("status", status);
            window.getContentPane().removeAll();
            window.getContentPane().add(new DSWindow(x, y, z, status, allowRot));
            window.setVisible(true);
            allowRot = false;
        }
    }
}
