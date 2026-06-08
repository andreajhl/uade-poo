package views.components;

import controllers.UserController;
import models.Authorization;
import models.User;
import models.enums.Permission;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SupervisorApprovalDialog extends JDialog {

    private JComboBox<User> cmbSupervisor;
    private JTextArea txtObservation;
    private Authorization authorization;

    public SupervisorApprovalDialog(JFrame parent, String reason) {
        super(parent, "Autorización de Supervisor", true);
        initComponents(reason);
        setSize(420, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents(String reason) {
        List<User> supervisors = UserController.getInstance().findByPermission(Permission.AUTHORIZE_PURCHASE_ORDER);
        cmbSupervisor = new JComboBox<>(supervisors.toArray(new User[0]));
        txtObservation = new JTextArea(4, 20);
        txtObservation.setLineWrap(true);
        txtObservation.setWrapStyleWord(true);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        center.add(new JLabel("<html><b>Motivo:</b> " + reason + "</html>"), gbc);

        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Supervisor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        center.add(cmbSupervisor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        center.add(new JLabel("Observación:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
        center.add(new JScrollPane(txtObservation), gbc);

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Autorizar", this::approve);

        setLayout(new BorderLayout());
        add(center, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
    }

    private void approve() {
        User supervisor = (User) cmbSupervisor.getSelectedItem();
        if (supervisor == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un supervisor.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        authorization = new Authorization(supervisor, txtObservation.getText().trim());
        dispose();
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public boolean isApproved() {
        return authorization != null && authorization.isValid();
    }
}
