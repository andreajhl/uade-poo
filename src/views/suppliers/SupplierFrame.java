package views.suppliers;

import controllers.SupplierController;
import models.Supplier;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SupplierFrame extends JPanel {

    private final AppTable table;
    private JButton btnManageCategories;

    public SupplierFrame() {
        setLayout(new BorderLayout());
        table = new AppTable(new String[]{"CUIT", "Razón Social", "Nombre Fantasía", "Condición IVA", "Tope Crédito"});
        table.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) btnManageCategories.setEnabled(table.getSelectedRow() >= 0);
        });
        initToolbar();
        add(table, BorderLayout.CENTER);
        refresh();
    }

    private void initToolbar() {
        btnManageCategories = new JButton("Gestionar Rubros");
        btnManageCategories.setEnabled(false);
        btnManageCategories.addActionListener(e -> openManageCategories());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nuevo Proveedor", this::openCreateDialog));
        toolbar.add(btnManageCategories);
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreateSupplierDialog dialog = new CreateSupplierDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    private void openManageCategories() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        if (row >= suppliers.size()) return;
        Supplier supplier = suppliers.get(row);
        ManageCategoriesDialog dialog = new ManageCategoriesDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), supplier);
        dialog.setVisible(true);
    }

    public void refresh() {
        table.clearRows();
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        for (Supplier s : suppliers) {
            table.addRow(new Object[]{
                s.getCuit(),
                s.getRazonSocial(),
                s.getFantasyName(),
                s.getIvaCondition(),
                String.format("$ %.2f", s.getCreditLimit())
            });
        }
    }
}
