package views.purchaseorders;

import controllers.PurchaseOrderController;
import models.PurchaseOrder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PurchaseOrderFrame extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public PurchaseOrderFrame() {
        setLayout(new BorderLayout());
        initTable();
        initToolbar();
        refresh();
    }

    private void initTable() {
        String[] columns = {"N°", "Fecha", "Proveedor", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Nueva Orden de Compra");

        btnNew.addActionListener(e -> openCreateDialog());
        toolbar.add(btnNew);
        
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreatePurchaseOrderDialog dialog = new CreatePurchaseOrderDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<PurchaseOrder> orders = PurchaseOrderController.getInstance().findAll();
        for (PurchaseOrder o : orders) {
            tableModel.addRow(new Object[]{
                o.getNumber(),
                o.getIssueDate(),
                o.getSupplier().getRazonSocial(),
                String.format("$ %.2f", o.getTotal())
            });
        }
    }
}
