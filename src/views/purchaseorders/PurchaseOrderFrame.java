package views.purchaseorders;

import controllers.PurchaseOrderController;
import models.PurchaseOrder;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PurchaseOrderFrame extends JPanel {

    private final AppTable table;

    public PurchaseOrderFrame() {
        setLayout(new BorderLayout());
        table = new AppTable(new String[]{"N°", "Fecha", "Proveedor", "Total"});
        initToolbar();
        add(table, BorderLayout.CENTER);
        refresh();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nueva Orden de Compra", this::openCreateDialog));
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreatePurchaseOrderDialog dialog = new CreatePurchaseOrderDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        table.clearRows();
        List<PurchaseOrder> orders = PurchaseOrderController.getInstance().findAll();
        for (PurchaseOrder o : orders) {
            table.addRow(new Object[]{
                o.getNumber(),
                o.getIssueDate(),
                o.getSupplier().getRazonSocial(),
                String.format("$ %.2f", o.getTotal())
            });
        }
    }
}
