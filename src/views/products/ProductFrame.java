package views.products;

import controllers.ProductController;
import models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductFrame extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public ProductFrame() {
        setLayout(new BorderLayout());
        initTable();
        initToolbar();
        refresh();
    }

    private void initTable() {
        String[] columns = {"Código", "Descripción", "UDM", "IVA %", "Rubro"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Nuevo Producto");
        btnNew.addActionListener(e -> openCreateDialog());
        toolbar.add(btnNew);
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreateProductDialog dialog = new CreateProductDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Product> products = ProductController.getInstance().findAll();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getCode(),
                p.getDescription(),
                p.getUnitOfMeasure(),
                String.format("%.1f%%", p.getIvaRate()),
                p.getCategory().getName()
            });
        }
    }
}
