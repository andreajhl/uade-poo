package views.components;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {

    public StatusBar(String text) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        add(label);
    }
}
