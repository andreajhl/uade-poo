package views.components;

import controllers.UserController;
import models.Authorization;
import models.User;
import models.enums.Permission;

import javax.swing.*;
import java.util.List;

public class SupervisorApprovalDialog extends AppDialog {

    private JComboBox<User> cmbSupervisor;
    private JTextArea txtObservation;
    private Authorization authorization;

    public SupervisorApprovalDialog(String reason) {
        super("Autorización de Supervisor", 420, 300);
        setResizable(false);
        initComponents(reason);
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

        addCenter(form);
        addSouth(bar);
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
