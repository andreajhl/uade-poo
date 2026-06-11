package views.paymentorders;

import controllers.PaymentOrderController;
import models.PaymentOrder;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class PaymentOrderFrame extends AppFrame {

    private final AppTable table;

    public PaymentOrderFrame() {
        table = new AppTable(new String[]{
            "N°", "Fecha", "Proveedor", "Total Facturas", "Retenciones", "Neto", "Estado"
        });
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nueva Orden de Pago", this::openCreateDialog));
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreatePaymentOrderDialog dialog = new CreatePaymentOrderDialog();
        dialog.setVisible(true);
        refresh();
    }

    @Override
    public void refresh() {
        table.clearRows();
        List<PaymentOrder> orders = PaymentOrderController.getInstance().findAll();
        for (PaymentOrder order : orders) {
            table.addRow(new Object[]{
                order.getNumber(),
                order.getIssueDate(),
                order.getSupplier().getRazonSocial(),
                String.format("$ %.2f", order.getTotalVouchersAmount()),
                String.format("$ %.2f", order.getTotalRetained()),
                String.format("$ %.2f", order.getNetAmount()),
                order.getStatus().name()
            });
        }
    }
}
