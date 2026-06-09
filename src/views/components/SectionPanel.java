package views.components;

import javax.swing.*;
import java.awt.*;

public class SectionPanel extends JPanel {

    public SectionPanel(String title) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(title));
    }

    public void addNorth(Component c) { add(c, BorderLayout.NORTH); }
    public void addCenter(Component c) { add(c, BorderLayout.CENTER); }
    public void addSouth(Component c) { add(c, BorderLayout.SOUTH); }
    public void addEast(Component c) { add(c, BorderLayout.EAST); }
    public void addWest(Component c) { add(c, BorderLayout.WEST); }
}
