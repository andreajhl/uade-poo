package views.users;

import controllers.UserController;
import models.User;
import views.components.ButtonBar;

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

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        center.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        center.add(cmbUser, gbc);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(ButtonBar.primary("Ingresar", this::confirm));

        setLayout(new BorderLayout());
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
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
