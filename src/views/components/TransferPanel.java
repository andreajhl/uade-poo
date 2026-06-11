package views.components;

import java.awt.*;
import javax.swing.*;

public class TransferPanel extends JPanel {

    public TransferPanel(Component left, Component center, Component right) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        add(left, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(center, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(right, gbc);
    }
}
