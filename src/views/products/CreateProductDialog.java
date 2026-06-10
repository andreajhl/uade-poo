package views.products;

import controllers.ProductController;
import models.Category;
import models.enums.TaxType;
import models.enums.UnitOfMeasure;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTextField;
import views.components.ButtonBar;
import views.components.FormPanel;

public class CreateProductDialog extends AppDialog {

    private AppTextField txtCode;
    private AppTextField txtDescription;
    private AppComboBox<UnitOfMeasure> cmbUnitOfMeasure;
    private AppComboBox<TaxType> cmbTaxType;
    private AppTextField txtCategory;

    public CreateProductDialog() {
        super("Nuevo Producto", 400, 300);
        initForm();
        initButtons();
    }

    private void initForm() {
        txtCode = new AppTextField();
        txtDescription = new AppTextField();
        cmbUnitOfMeasure = new AppComboBox<>(UnitOfMeasure.values());
        cmbTaxType = new AppComboBox<>(TaxType.values());
        txtCategory = new AppTextField();

        FormPanel form = new FormPanel();
        form.addRow("Código *", txtCode);
        form.addRow("Descripción *", txtDescription);
        form.addRow("Unidad de Medida *", cmbUnitOfMeasure);
        form.addRow("Tipo de IVA *", cmbTaxType);
        form.addRow("Rubro *", txtCategory);

        addCenter(form);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        addSouth(bar);
    }

    private void save() {
        String code = txtCode.getText().trim();
        String description = txtDescription.getText().trim();
        String categoryName = txtCategory.getText().trim();

        if (code.isEmpty() || description.isEmpty() || categoryName.isEmpty()) {
            Alerts.warn(this, "Complete todos los campos obligatorios (*).");
            return;
        }

        UnitOfMeasure unitOfMeasure = cmbUnitOfMeasure.getSelected();
        TaxType taxType = cmbTaxType.getSelected();

        ProductController.getInstance().create(code, description, unitOfMeasure, taxType, new Category(categoryName));
        Alerts.info(this, "Producto creado correctamente.");
        dispose();
    }
}
