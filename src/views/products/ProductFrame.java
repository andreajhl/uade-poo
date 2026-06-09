package views.products;

import controllers.ProductController;
import models.Product;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class ProductFrame extends AppFrame {

    private final AppTable table;

    public ProductFrame() {
        table = new AppTable(new String[]{"Código", "Descripción", "UDM", "Tipo IVA", "Rubro"});
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Producto", this::openCreateDialog));
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateProductDialog dialog = new CreateProductDialog();
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
                p.getCategory().getName()
            });
        }
    }
}
