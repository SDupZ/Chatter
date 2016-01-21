import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Window extends JFrame {
    
    public Window(AJPanel aJ) {
        JFrame window = new JFrame("Application");
        window.setContentPane(aJ);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setSize(800, 500);
        window.setLocation(300, 150);
    }
}
        