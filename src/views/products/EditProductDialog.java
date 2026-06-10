package views.products;

import controllers.ProductController;
import models.Product;
import models.enums.Category;
import models.enums.TaxType;
import models.enums.UnitOfMeasure;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTextField;
import views.components.ButtonBar;
import views.components.FormPanel;

public class EditProductDialog extends AppDialog {

    private final Product product;

    private AppTextField txtCode;
    private AppTextField txtDescription;
    private AppComboBox<UnitOfMeasure> cmbUnitOfMeasure;
    private AppComboBox<TaxType> cmbTaxType;
    private AppComboBox<Category> cmbCategory;

    public EditProductDialog(Product product) {
        super("Editar Producto — " + product.getDescription(), 400, 300);
        this.product = product;
        initForm();
        initButtons();
    }

    private void initForm() {
        txtCode = new AppTextField(product.getCode());
        txtDescription = new AppTextField(product.getDescription());
        cmbUnitOfMeasure = new AppComboBox<>(UnitOfMeasure.values());
        cmbUnitOfMeasure.setSelectedItem(product.getUnitOfMeasure());
        cmbTaxType = new AppComboBox<>(TaxType.values());
        cmbTaxType.setSelectedItem(product.getTaxType());
        cmbCategory = new AppComboBox<>(Category.values());
        cmbCategory.setSelectedItem(product.getCategory());

        FormPanel form = new FormPanel();
        form.addRow("Código *", txtCode);
        form.addRow("Descripción *", txtDescription);
        form.addRow("Unidad de Medida *", cmbUnitOfMeasure);
        form.addRow("Tipo de IVA *", cmbTaxType);
        form.addRow("Rubro *", cmbCategory);

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

        if (code.isEmpty() || description.isEmpty()) {
            Alerts.warn(this, "Complete todos los campos obligatorios (*).");
            return;
        }

        try {
            ProductController.getInstance().update(
                    product.getId(), code, description,
                    cmbUnitOfMeasure.getSelected(),
                    cmbTaxType.getSelected(),
                    cmbCategory.getSelected());
            Alerts.info(this, "Producto actualizado correctamente.");
            dispose();
        } catch (Exception ex) {
            Alerts.error(this, ex.getMessage());
        }
    }
}
