package views.purchaseorders;

import controllers.PurchaseOrderController;
import models.PurchaseOrder;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class PurchaseOrderFrame extends AppFrame {

    private final AppTable table;

    public PurchaseOrderFrame() {
        table = new AppTable(new String[]{"N°", "Fecha", "Proveedor", "Total"});
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nueva Orden de Compra", this::openCreateDialog));
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreatePurchaseOrderDialog dialog = new CreatePurchaseOrderDialog();
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
