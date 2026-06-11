package views.purchaseorders;

import models.PurchaseOrder;
import models.PurchaseOrderDetail;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SectionPanel;

public class PurchaseOrderDetailDialog extends AppDialog {

    public PurchaseOrderDetailDialog(PurchaseOrder order) {
        super("Detalle Orden de Compra N° " + order.getNumber(), 620, 480);

        FormPanel header = new FormPanel("Datos generales");
        header.addRow("Proveedor:", new InfoLabel(order.getSupplier().getRazonSocial()));
        header.addRow("Fecha:", new InfoLabel(order.getIssueDate().toString()));
        header.addRow("Estado:", new InfoLabel(order.getStatus().name()));

        if (order.getAuthorization() != null) {
            header.addRow("Autorizado por:", new InfoLabel(order.getAuthorization().getAuthorizedBy().toString()));
            header.addRow("Fecha autorización:", new InfoLabel(order.getAuthorization().getAuthorizationDate().toString()));
        }

        header.addRow("Total:", new InfoLabel(String.format("$ %.2f", order.getTotal())));
        addNorth(header);

        AppTable itemsTable = new AppTable(
                new String[]{"Producto", "Cantidad", "Precio Unitario", "Subtotal"});
        for (PurchaseOrderDetail d : order.getDetails()) {
            itemsTable.addRow(new Object[]{
                d.getProduct().getDescription(),
                d.getQuantity(),
                String.format("$ %.2f", d.getAgreedUnitPrice()),
                String.format("$ %.2f", d.getSubtotal())
            });
        }

        SectionPanel itemsSection = new SectionPanel("Ítems");
        itemsSection.addCenter(itemsTable);
        addCenter(itemsSection);
    }
}
