package views.suppliers;

import controllers.SupplierController;
import models.enums.IVACondition;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppScrollPane;
import views.components.AppTextField;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.PlaceholderTextField;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateSupplierDialog extends AppDialog {

    private AppTextField txtCuit;
    private AppTextField txtRazonSocial;
    private AppTextField txtFantasyName;
    private AppTextField txtAddress;
    private AppTextField txtPhone;
    private AppTextField txtEmail;
    private AppComboBox<IVACondition> cmbIvaCondition;
    private AppTextField txtIngresosBrutos;
    private PlaceholderTextField txtActivityStartDate;
    private AppTextField txtCreditLimit;

    public CreateSupplierDialog() {
        super("Nuevo Proveedor", 450, 420);
        initForm();
        initButtons();
    }

    private void initForm() {
        txtCuit = new AppTextField();
        txtRazonSocial = new AppTextField();
        txtFantasyName = new AppTextField();
        txtAddress = new AppTextField();
        txtPhone = new AppTextField();
        txtEmail = new AppTextField();
        cmbIvaCondition = new AppComboBox<>(IVACondition.values());
        txtIngresosBrutos = new AppTextField();
        txtActivityStartDate = new PlaceholderTextField("yyyy-MM-dd");
        txtCreditLimit = new AppTextField("0");

        FormPanel form = new FormPanel();
        form.addRow("CUIT *", txtCuit);
        form.addRow("Razón Social *", txtRazonSocial);
        form.addRow("Nombre Fantasía *", txtFantasyName);
        form.addRow("Domicilio *", txtAddress);
        form.addRow("Teléfono *", txtPhone);
        form.addRow("Email", txtEmail);
        form.addRow("Condición IVA *", cmbIvaCondition);
        form.addRow("Ingresos Brutos *", txtIngresosBrutos);
        form.addRow("Inicio Actividades *", txtActivityStartDate);
        form.addRow("Tope Crédito *", txtCreditLimit);

        addCenter(new AppScrollPane(form));
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        addSouth(bar);
    }

    private void save() {
        String cuit           = txtCuit.getText().trim();
        String razonSocial    = txtRazonSocial.getText().trim();
        String fantasyName    = txtFantasyName.getText().trim();
        String address        = txtAddress.getText().trim();
        String phone          = txtPhone.getText().trim();
        String email          = txtEmail.getText().trim();
        String ingresosBrutos = txtIngresosBrutos.getText().trim();

        if (cuit.isEmpty() || razonSocial.isEmpty() || fantasyName.isEmpty()
                || address.isEmpty() || phone.isEmpty() || ingresosBrutos.isEmpty()) {
            Alerts.warn(this, "Complete todos los campos obligatorios (*).");
            return;
        }

        if (cuit.length() < 3 || !cuit.matches("^[0-9-]+$")) {
            Alerts.warn(this, "El CUIT debe tener al menos 3 caracteres.");
            return;
        }
        if (razonSocial.length() < 3) {
            Alerts.warn(this, "La Razón Social debe tener al menos 3 caracteres.");
            return;
        }
        if (fantasyName.length() < 3) {
            Alerts.warn(this, "El Nombre Fantasía debe tener al menos 3 caracteres.");
            return;
        }
        if (address.length() < 3) {
            Alerts.warn(this, "El Domicilio debe tener al menos 3 caracteres.");
            return;
        }
        if (!phone.matches("[0-9]+") || phone.length() < 11) {
            Alerts.warn(this, "El Teléfono debe contener solo dígitos y tener al menos 11 caracteres.");
            return;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            Alerts.warn(this, "El Email debe tener el formato test@gmail.com.");
            return;
        }
        if (ingresosBrutos.length() < 3) {
            Alerts.warn(this, "El número de Ingresos Brutos debe tener al menos 3 caracteres.");
            return;
        }

        LocalDate activityStartDate;
        try {
            String dateText = txtActivityStartDate.getValue();
            if (dateText.isEmpty()) throw new DateTimeParseException("", "", 0);
            activityStartDate = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            Alerts.warn(this, "Fecha de inicio inválida. Use el formato yyyy-MM-dd.");
            return;
        }

        float creditLimit;
        try {
            creditLimit = Float.parseFloat(txtCreditLimit.getText().trim());
            if (creditLimit < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El tope de crédito debe ser un número mayor o igual a 0.");
            return;
        }

        SupplierController.getInstance().create(
                cuit, razonSocial, fantasyName, address, phone,
                email,
                cmbIvaCondition.getSelected(),
                ingresosBrutos, activityStartDate, creditLimit
        );

        Alerts.info(this, "Proveedor creado correctamente.");
        dispose();
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return false;
        int dotIndex = email.lastIndexOf('.');
        return dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }
}
