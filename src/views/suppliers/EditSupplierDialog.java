package views.suppliers;

import controllers.SupplierController;
import models.Supplier;
import models.enums.Category;
import models.enums.IVACondition;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppList;
import views.components.AppScrollPane;
import views.components.AppTextField;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.PlaceholderTextField;
import views.components.SectionPanel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EditSupplierDialog extends AppDialog {

    private final Supplier supplier;

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
    private AppComboBox<Category> cmbCategory;
    private AppList<Category> lstCategories;
    private List<Category> selectedCategories;

    public EditSupplierDialog(Supplier supplier) {
        super("Editar Proveedor — " + supplier.getRazonSocial(), 480, 560);
        this.supplier = supplier;
        this.selectedCategories = new ArrayList<>(supplier.getCategories());
        initForm();
        initCategoryPanel();
        initButtons();
    }

    private void initForm() {
        txtCuit = new AppTextField(supplier.getCuit());
        txtRazonSocial = new AppTextField(supplier.getRazonSocial());
        txtFantasyName = new AppTextField(supplier.getFantasyName());
        txtAddress = new AppTextField(supplier.getAddress());
        txtPhone = new AppTextField(supplier.getPhone());
        txtEmail = new AppTextField(supplier.getEmail() != null ? supplier.getEmail() : "");
        cmbIvaCondition = new AppComboBox<>(IVACondition.values());
        cmbIvaCondition.setSelectedItem(supplier.getIvaCondition());
        txtIngresosBrutos = new AppTextField(supplier.getIngresosBrutos());
        txtActivityStartDate = new PlaceholderTextField("yyyy-MM-dd");
        txtActivityStartDate.setText(supplier.getActivityStartDate().toString());
        txtCreditLimit = new AppTextField(String.format("%.2f", supplier.getCreditLimit()));

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

        addNorth(new AppScrollPane(form));
    }

    private void initCategoryPanel() {
        cmbCategory = new AppComboBox<>(Category.values());
        lstCategories = new AppList<>();
        for (Category cat : selectedCategories) lstCategories.addElement(cat);

        BorderPanel addRow = new BorderPanel();
        addRow.addCenter(cmbCategory);
        addRow.addEast(ButtonBar.primary("Agregar", this::addCategory));

        SectionPanel panel = new SectionPanel("Rubros *");
        panel.addNorth(addRow);
        panel.addCenter(lstCategories);
        panel.addSouth(ButtonBar.danger("Quitar seleccionado", this::removeCategory));

        addCenter(panel);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        addSouth(bar);
    }

    private void addCategory() {
        Category cat = cmbCategory.getSelected();
        if (cat == null) return;
        if (selectedCategories.contains(cat)) {
            Alerts.warn(this, "El rubro ya fue agregado.");
            return;
        }
        selectedCategories.add(cat);
        lstCategories.addElement(cat);
    }

    private void removeCategory() {
        Category selected = lstCategories.getSelectedValue();
        if (selected == null) {
            Alerts.warn(this, "Seleccioná un rubro para quitar.");
            return;
        }
        selectedCategories.remove(selected);
        lstCategories.removeElement(selected);
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
        if (!cuit.matches("^\\d{2}-\\d{8}-\\d{1}$")) {
            Alerts.warn(this, "El CUIT debe tener el formato XX-XXXXXXXX-X (ej: 20-12345678-3).");
            return;
        }
        if (razonSocial.length() < 3 || fantasyName.length() < 3 || address.length() < 3) {
            Alerts.warn(this, "Razón Social, Nombre Fantasía y Domicilio deben tener al menos 3 caracteres.");
            return;
        }
        if (!phone.matches("[0-9-]+") || phone.replaceAll("-", "").length() < 11) {
            Alerts.warn(this, "El Teléfono debe contener solo dígitos y tener al menos 11 dígitos.");
            return;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            Alerts.warn(this, "El Email debe tener el formato test@gmail.com.");
            return;
        }
        if (selectedCategories.isEmpty()) {
            Alerts.warn(this, "Agregá al menos un rubro.");
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
            creditLimit = Float.parseFloat(txtCreditLimit.getText().trim().replace(",", "."));
            if (creditLimit < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El tope de crédito debe ser un número mayor o igual a 0.");
            return;
        }

        try {
            SupplierController.getInstance().update(
                    supplier.getId(), cuit, razonSocial, fantasyName, address, phone,
                    email, cmbIvaCondition.getSelected(), ingresosBrutos,
                    activityStartDate, creditLimit, selectedCategories);
            Alerts.info(this, "Proveedor actualizado correctamente.");
            dispose();
        } catch (Exception ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return false;
        int dotIndex = email.lastIndexOf('.');
        return dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }
}
