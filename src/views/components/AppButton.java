package views.components;

import javax.swing.*;
import java.awt.*;

public class AppButton extends JButton {

    public AppButton(String text) {
        super(text);
    }

    public AppButton(String text, Runnable action) {
        super(text);
        addActionListener(e -> action.run());
    }

    public static AppButton primary(String text, Runnable action) {
        AppButton button = new AppButton(text, action);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    public static AppButton danger(String text, Runnable action) {
        AppButton button = new AppButton(text, action);
        button.setBackground(new Color(200, 60, 60));
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setFocusPainted(false);
        return button;
    }
}
