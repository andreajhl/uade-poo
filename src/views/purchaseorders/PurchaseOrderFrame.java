package views.purchaseorders;

import controllers.ProductController;
import controllers.PurchaseOrderController;
import controllers.SupplierController;
import models.PurchaseOrder;
import views.components.AppButton;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ToolbarPanel;

import java.util.List;

public class PurchaseOrderFrame extends AppFrame {

    private final AppTable table;
    private AppButton btnNew;

    public PurchaseOrderFrame() {
        table = new AppTable(new String[]{"N°", "Fecha", "Proveedor", "Total", "Supervisor", "Fecha Auth.", "Estado Auth."});
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        btnNew = AppButton.primary("Nueva Orden de Compra", this::openCreateDialog);
        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(btnNew);
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreatePurchaseOrderDialog dialog = new CreatePurchaseOrderDialog();
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        btnNew.setEnabled(
            !SupplierController.getInstance().findAll().isEmpty()
            && !ProductController.getInstance().findAll().isEmpty()
        );
        table.clearRows();
        List<PurchaseOrder> orders = PurchaseOrderController.getInstance().findAll();
        for (PurchaseOrder o : orders) {
            boolean hasAuth = o.getAuthorization() != null;
            table.addRow(new Object[]{
                o.getNumber(),
                o.getIssueDate(),
                o.getSupplier().getRazonSocial(),
                String.format("$ %.2f", o.getTotal()),
                hasAuth ? o.getAuthorization().getAuthorizedBy().toString() : "—",
                hasAuth ? o.getAuthorization().getAuthorizationDate().toString() : "—",
                hasAuth ? "Aprobado" : "Sin autorización"
            });
        }
    }
}
