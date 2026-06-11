package views.paymentorders;

import models.PaymentItem;
import models.PaymentOrder;
import models.Retention;
import models.VoucherPayment;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SectionPanel;

public class PaymentOrderDetailDialog extends AppDialog {

    public PaymentOrderDetailDialog(PaymentOrder order) {
        super("Detalle Orden de Pago N° " + order.getNumber(), 660, 580);

        FormPanel header = new FormPanel("Datos generales");
        header.addRow("Proveedor:", new InfoLabel(order.getSupplier().getRazonSocial()));
        header.addRow("Fecha:", new InfoLabel(order.getIssueDate().toString()));
        header.addRow("Total comprobantes:", new InfoLabel(String.format("$ %.2f", order.getTotalVouchersAmount())));
        header.addRow("Total retenciones:", new InfoLabel(String.format("$ %.2f", order.getTotalRetained())));
        header.addRow("Neto pagado:", InfoLabel.highlight(String.format("$ %.2f", order.getNetAmount())));
        addNorth(header);

        AppTable vouchersTable = new AppTable(new String[]{"N° Comprobante", "Monto Aplicado"});
        for (VoucherPayment vp : order.getVoucherPayments()) {
            vouchersTable.addRow(new Object[]{
                vp.getVoucherId().toString().substring(0, 8) + "…",
                String.format("$ %.2f", vp.getAmount())
            });
        }

        AppTable retentionsTable = new AppTable(new String[]{"Tipo Impuesto", "Monto Retenido"});
        for (Retention r : order.getRetentions()) {
            retentionsTable.addRow(new Object[]{
                r.getTaxType().name(),
                String.format("$ %.2f", r.getAmount())
            });
        }

        AppTable paymentItemsTable = new AppTable(
                new String[]{"Medio de Pago", "Importe", "N° Cheque", "Firmante"});
        for (PaymentItem item : order.getPaymentItems()) {
            paymentItemsTable.addRow(new Object[]{
                item.getType().toString(),
                String.format("$ %.2f", item.getAmount()),
                item.isCheck() ? item.getCheckNumber() : "—",
                item.isCheck() ? item.getCheckSigner() : "—"
            });
        }

        SectionPanel vouchersSection = new SectionPanel("Comprobantes");
        vouchersSection.addCenter(vouchersTable);

        SectionPanel retentionsSection = new SectionPanel("Retenciones");
        retentionsSection.addCenter(retentionsTable);

        SectionPanel paymentSection = new SectionPanel("Medios de Pago");
        paymentSection.addCenter(paymentItemsTable);

        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 8, 0));
        bottom.add(retentionsSection);
        bottom.add(paymentSection);
        bottom.setPreferredSize(new java.awt.Dimension(1, 180));

        javax.swing.JPanel center = new javax.swing.JPanel(new java.awt.BorderLayout(0, 6));
        center.add(vouchersSection, java.awt.BorderLayout.CENTER);
        center.add(bottom, java.awt.BorderLayout.SOUTH);

        addCenter(center);
    }
}
