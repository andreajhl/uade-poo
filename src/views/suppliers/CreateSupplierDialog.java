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
        String cuit = txtCuit.getText().trim();
        String razonSocial = txtRazonSocial.getText().trim();
        String fantasyName = txtFantasyName.getText().trim();
        String address = txtAddress.getText().trim();
        String phone = txtPhone.getText().trim();
        String ingresosBrutos = txtIngresosBrutos.getText().trim();

        if (cuit.isEmpty() || razonSocial.isEmpty() || fantasyName.isEmpty()
                || address.isEmpty() || phone.isEmpty() || ingresosBrutos.isEmpty()) {
            Alerts.warn(this, "Complete todos los campos obligatorios (*).");
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
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El tope de crédito debe ser un número válido.");
            return;
        }

        SupplierController.getInstance().create(
                cuit, razonSocial, fantasyName, address, phone,
                txtEmail.getText().trim(),
                cmbIvaCondition.getSelected(),
                ingresosBrutos, activityStartDate, creditLimit
        );

        Alerts.info(this, "Proveedor creado correctamente.");
        dispose();
    }
}
