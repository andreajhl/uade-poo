package views.users;

import controllers.UserController;
import models.User;
import views.components.ButtonBar;
import views.components.FormPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectUserDialog extends JDialog {

    private JComboBox<User> cmbUser;
    private boolean confirmed = false;

    public SelectUserDialog(Frame parent) {
        super(parent, "Iniciar sesión", true);
        initComponents();
        setSize(380, 160);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        List<User> users = UserController.getInstance().findAll();
        cmbUser = new JComboBox<>(users.toArray(new User[0]));

        FormPanel form = new FormPanel();
        form.addRow("Usuario:", cmbUser);

        ButtonBar bar = ButtonBar.centered();
        bar.addButton("Ingresar", this::confirm);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
    }

    private void confirm() {
        User selected = (User) cmbUser.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un usuario.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UserController.getInstance().login(selected);
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
