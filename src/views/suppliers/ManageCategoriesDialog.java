package views.suppliers;

import controllers.SupplierController;
import exceptions.EntityNotFoundException;
import models.Category;
import models.Supplier;
import views.components.Alerts;
import views.components.AppDialog;
import views.components.AppList;
import views.components.AppTextField;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.SectionPanel;

public class ManageCategoriesDialog extends AppDialog {

    private final Supplier supplier;
    private AppList<Category> categoryList;
    private AppTextField txtNewCategory;

    public ManageCategoriesDialog(Supplier supplier) {
        super("Rubros de " + supplier.getRazonSocial(), 380, 380);
        this.supplier = supplier;
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        categoryList = new AppList<>();
        for (Category c : supplier.getCategories()) categoryList.addElement(c);

        txtNewCategory = new AppTextField();

        FormPanel addForm = new FormPanel("Agregar rubro");
        addForm.addRow("Nombre:", txtNewCategory);
        addForm.addFullRow(ButtonBar.primary("Agregar", this::addCategory));

        SectionPanel listPanel = new SectionPanel("Rubros asociados");
        listPanel.addCenter(categoryList);
        listPanel.addSouth(ButtonBar.danger("Quitar seleccionado", this::removeCategory));

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cerrar", this::dispose);

        addNorth(addForm);
        addCenter(listPanel);
        addSouth(bar);
    }

    private void addCategory() {
        String name = txtNewCategory.getText().trim();
        if (name.isEmpty()) {
            Alerts.warn(this, "Ingresá el nombre del rubro.");
            return;
        }
        Category category = new Category(name);
        try {
            SupplierController.getInstance().addCategory(supplier.getId(), category);
            categoryList.addElement(category);
            txtNewCategory.setText("");
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private void removeCategory() {
        Category selected = categoryList.getSelectedValue();
        if (selected == null) {
            Alerts.warn(this, "Seleccioná un rubro para quitar.");
            return;
        }
        try {
            SupplierController.getInstance().removeCategory(supplier.getId(), selected);
            categoryList.removeElement(selected);
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }
}
