package views.components;

import javax.swing.*;
import java.awt.*;

public class BorderPanel extends JPanel {

    public BorderPanel() {
        setLayout(new BorderLayout());
    }

    public BorderPanel(int hgap, int vgap) {
        setLayout(new BorderLayout(hgap, vgap));
    }

    public void addNorth(Component c) { add(c, BorderLayout.NORTH); }
    public void addCenter(Component c) { add(c, BorderLayout.CENTER); }
    public void addSouth(Component c) { add(c, BorderLayout.SOUTH); }
    public void addEast(Component c) { add(c, BorderLayout.EAST); }
    public void addWest(Component c) { add(c, BorderLayout.WEST); }
}
