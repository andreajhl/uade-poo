package views.paymentorders;

import controllers.PaymentOrderController;
import models.PaymentOrder;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PaymentOrderFrame extends JPanel {

    private final AppTable table;

    public PaymentOrderFrame() {
        setLayout(new BorderLayout());
        table = new AppTable(new String[]{
            "N°", "Fecha", "Proveedor", "Total Facturas", "Retenciones", "Neto", "Estado"
        });
        initToolbar();
        add(table, BorderLayout.CENTER);
        refresh();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nueva Orden de Pago", this::openCreateDialog));
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreatePaymentOrderDialog dialog = new CreatePaymentOrderDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this)
        );
        dialog.setVisible(true);
        refresh();
    }

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