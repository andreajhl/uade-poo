package views.components;

import javax.swing.*;
import java.awt.*;

public class ButtonBar extends JPanel {

    public ButtonBar() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
    }

    private ButtonBar(int alignment) {
        setLayout(new FlowLayout(alignment));
    }

    public static ButtonBar centered() {
        return new ButtonBar(FlowLayout.CENTER);
    }

    public JButton addButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        add(button);
        return button;
    }

    public static JButton primary(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }

    public static JButton danger(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(200, 60, 60));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
}
