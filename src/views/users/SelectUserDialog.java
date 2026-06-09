package views.users;

import controllers.UserController;
import models.User;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.Alerts;
import views.components.ButtonBar;
import views.components.FormPanel;

import java.util.List;

public class SelectUserDialog extends AppDialog {

    private AppComboBox<User> cmbUser;
    private boolean confirmed = false;

    public SelectUserDialog() {
        super("Iniciar sesión", 380, 160);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        List<User> users = UserController.getInstance().findAll();
        cmbUser = new AppComboBox<>(users.toArray(new User[0]));

        FormPanel form = new FormPanel();
        form.addRow("Usuario:", cmbUser);

        ButtonBar bar = ButtonBar.centered();
        bar.addButton("Ingresar", this::confirm);

        addCenter(form);
        addSouth(bar);
    }

    private void confirm() {
        User selected = cmbUser.getSelected();
        if (selected == null) {
            Alerts.warn(this, "Seleccioná un usuario.");
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
