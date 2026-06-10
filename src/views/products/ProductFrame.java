package views.products;

import controllers.ProductController;
import models.Product;
import views.components.AppButton;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class ProductFrame extends AppFrame {

    private final AppTable table;
    private AppButton btnEdit;

    public ProductFrame() {
        table = new AppTable(new String[]{"Código", "Descripción", "UDM", "Tipo IVA", "Rubro"});
        table.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) btnEdit.setEnabled(table.getSelectedRow() >= 0);
        });
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        btnEdit = new AppButton("Editar", this::openEditDialog);
        btnEdit.setEnabled(false);

        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Producto", this::openCreateDialog));
        toolbar.add(btnEdit);
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateProductDialog dialog = new CreateProductDialog();
        dialog.setVisible(true);
        refresh();
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
