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

        FormPanel form = new FormPanel();
        form.addFullRow(new JLabel("<html><b>Motivo:</b> " + reason + "</html>"));
        form.addRow("Supervisor:", cmbSupervisor);
        form.addRow("Observación:", new JScrollPane(txtObservation), true);

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Autorizar", this::approve);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
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
