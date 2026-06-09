package views.components;

import javax.swing.*;
import java.awt.*;

public class AppMainFrame extends JFrame {

    public AppMainFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    public void addNorth(Component c) { add(c, BorderLayout.NORTH); }
    public void addCenter(Component c) { add(c, BorderLayout.CENTER); }
    public void addSouth(Component c) { add(c, BorderLayout.SOUTH); }
}
