package views.products;

import controllers.ProductController;
import models.Product;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductFrame extends JPanel {

    private final AppTable table;

    public ProductFrame() {
        setLayout(new BorderLayout());
        table = new AppTable(new String[]{"Código", "Descripción", "UDM", "IVA %", "Rubro"});
        initToolbar();
        add(table, BorderLayout.CENTER);
        refresh();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nuevo Producto", this::openCreateDialog));
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreateProductDialog dialog = new CreateProductDialog((JFrame) SwingUtilities.getWindowAncestor(this));
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
                p.getUnitOfMeasure(),
                String.format("%.1f%%", p.getIvaRate()),
                p.getCategory().getName()
            });
        }
    }
}
