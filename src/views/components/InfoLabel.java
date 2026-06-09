package views.components;

import javax.swing.*;
import java.awt.*;

public class InfoLabel extends JLabel {

    public InfoLabel(String text) {
        super(text);
        setFont(getFont().deriveFont(Font.BOLD));
    }

    public InfoLabel() {
        this("—");
    }

    public static InfoLabel highlight(String text) {
        InfoLabel label = new InfoLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }
}
