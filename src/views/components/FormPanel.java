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

    public FormPanel(String title) {
        this();
        setBorder(BorderFactory.createTitledBorder(title));
    }

    public void addRow(String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.weightx = 0.3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(field, gbc);

        currentRow++;
    }

    public void addRow(String label, Component field, boolean fillVertical) {
        if (fillVertical) {
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
        }
        addRow(label, field);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
    }

    public void addFullRow(Component component) {
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(component, gbc);
        gbc.gridwidth = 1;
        currentRow++;
    }
}
