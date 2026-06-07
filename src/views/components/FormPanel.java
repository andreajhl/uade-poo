package views.components;

import javax.swing.*;
import java.awt.*;

public class FormPanel extends JPanel {

    private final GridBagConstraints gbc;
    private int currentRow = 0;

    public FormPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    public void addRow(String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.weightx = 0.3;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(field, gbc);

        currentRow++;
    }
}
