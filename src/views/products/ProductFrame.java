package views.products;

import controllers.ProductController;
import exceptions.EntityNotFoundException;
import models.Product;
import views.components.Alerts;
import views.components.AppButton;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class ProductFrame extends AppFrame {

    private final AppTable table;
    private AppButton btnEdit;
    private AppButton btnDelete;

    public ProductFrame() {
        table = new AppTable(new String[]{"Código", "Descripción", "UDM", "Tipo IVA", "Rubro"});
        table.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = table.getSelectedRow() >= 0;
                btnEdit.setEnabled(selected);
                btnDelete.setEnabled(selected);
            }
        });
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        btnEdit = new AppButton("Editar", this::openEditDialog);
        btnEdit.setEnabled(false);

        btnDelete = new AppButton("Eliminar", this::deleteSelected);
        btnDelete.setEnabled(false);

        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Producto", this::openCreateDialog));
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateProductDialog dialog = new CreateProductDialog();
        dialog.setVisible(true);
        refresh();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Product> products = ProductController.getInstance().findAll();
        if (row >= products.size()) return;
        Product product = products.get(row);
        if (!Alerts.confirm(this, "¿Eliminar el producto \"" + product.getDescription() + "\"?", "Confirmar eliminación")) return;
        try {
            ProductController.getInstance().delete(product.getId());
            refresh();
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Product> products = ProductController.getInstance().findAll();
        if (row >= products.size()) return;
        EditProductDialog dialog = new EditProductDialog(products.get(row));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        table.clearRows();
        List<Product> products = ProductController.getInstance().findAll();
        for (Product p : products) {
            table.addRow(new Object[]{
                p.getCode(),
                p.getDescription(),
                p.getUnitOfMeasure().name(),
                p.getTaxType().name(),
                p.getCategory().name()
            });
        }
    }
}
