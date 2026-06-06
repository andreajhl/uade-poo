package views.suppliers;

import controllers.SupplierController;
import models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierFrame extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public SupplierFrame() {
        setLayout(new BorderLayout());
        initTable();
        initToolbar();
        refresh();
    }

    private void initTable() {
        String[] columns = {"CUIT", "Razón Social", "Nombre Fantasía", "Condición IVA", "Tope Crédito"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Nuevo Proveedor");
        btnNew.addActionListener(e -> openCreateDialog());
        toolbar.add(btnNew);
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreateSupplierDialog dialog = new CreateSupplierDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                s.getCuit(),
                s.getRazonSocial(),
                s.getFantasyName(),
                s.getIvaCondition(),
                String.format("$ %.2f", s.getCreditLimit())
            });
        }
    }
}
