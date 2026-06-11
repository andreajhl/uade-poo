package views.paymentorders;

import controllers.PaymentOrderController;
import controllers.SupplierController;
import controllers.TaxRuleController;
import controllers.UserController;
import controllers.VoucherController;
import exceptions.EntityNotFoundException;
import exceptions.InvalidVoucherStatusException;
import models.Supplier;
import models.TaxRule;
import models.User;
import models.Voucher;
import models.VoucherPayment;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SectionPanel;

import java.util.ArrayList;
import java.util.List;

public class CreatePaymentOrderDialog extends AppDialog {

    private AppComboBox<Supplier> cmbSupplier;
    private AppTable vouchersTable;
    private AppTable selectedVouchersTable;
    private InfoLabel lblTotalVouchers;
    private InfoLabel lblTotalRetentions;
    private InfoLabel lblNetAmount;

    public CreatePaymentOrderDialog() {
        super("Nueva Orden de Pago", 900, 650);
        initSupplierPanel();
        initVouchersPanel();
        initSouthPanel();
    }

    private void initSupplierPanel() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);
        cmbSupplier.onSelectionChanged(this::refreshPendingVouchers);

        FormPanel form = new FormPanel("Proveedor");
        form.addRow("Seleccionar *", cmbSupplier);
        addNorth(form);
    }

    private void initVouchersPanel() {
        vouchersTable = new AppTable(new String[]{"Número", "Tipo", "Fecha", "Neto", "IVA", "Total", "Estado"});
        selectedVouchersTable = new AppTable(new String[]{"Número", "Tipo", "Monto Original", "Monto a Pagar"});

        BorderPanel buttons = new BorderPanel(5, 5);
        buttons.addNorth(ButtonBar.primary(">>", this::moveToSelected));
        buttons.addSouth(ButtonBar.danger("<<", this::removeFromSelected));

        BorderPanel center = new BorderPanel(5, 5);
        center.addWest(vouchersTable);
        center.addCenter(buttons);
        center.addEast(selectedVouchersTable);

        SectionPanel section = new SectionPanel("Comprobantes Disponibles (Facturas y ND)");
        section.addCenter(center);
        addCenter(section);
    }

    private void initSouthPanel() {
        lblTotalVouchers = new InfoLabel("$ 0.00");
        lblTotalRetentions = new InfoLabel("$ 0.00");
        lblNetAmount = InfoLabel.highlight("$ 0.00");

        FormPanel summary = new FormPanel();
        summary.addRow("Total Comprobantes:", lblTotalVouchers);
        summary.addRow("Total Retenciones:", lblTotalRetentions);
        summary.addRow("Neto a Pagar:", lblNetAmount);

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Pago", this::confirm);

        BorderPanel south = new BorderPanel();
        south.addCenter(summary);
        south.addSouth(bar);
        addSouth(south);
    }

    private void refreshPendingVouchers() {
        Supplier supplier = cmbSupplier.getSelected();
        vouchersTable.clearRows();
        if (supplier == null) return;

        try {
            List<Voucher> vouchers = PaymentOrderController.getInstance()
                    .getPendingPayableVouchersBySupplier(supplier.getId());
            for (Voucher v : vouchers) {
                vouchersTable.addRow(new Object[]{
                    v.getNumber(),
                    v.getType().name(),
                    v.getIssueDate(),
                    String.format("$ %.2f", v.getNetTotal()),
                    String.format("$ %.2f", v.getVatTotal()),
                    String.format("$ %.2f", v.getGrossTotal()),
                    v.getStatus().name()
                });
            }
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private void moveToSelected() {
        int row = vouchersTable.getSelectedRow();
        if (row < 0) { Alerts.warn(this, "Seleccioná un comprobante."); return; }

        Object number = vouchersTable.getValueAt(row, 0);
        Object type   = vouchersTable.getValueAt(row, 1);
        Object total  = vouchersTable.getValueAt(row, 5);

        selectedVouchersTable.addRow(new Object[]{ number, type, total, total });
        vouchersTable.removeRow(row);
        updateSummary();
    }

    private void removeFromSelected() {
        int row = selectedVouchersTable.getSelectedRow();
        if (row < 0) { Alerts.warn(this, "Seleccioná un comprobante para quitar."); return; }

        Object number  = selectedVouchersTable.getValueAt(row, 0);
        Object type    = selectedVouchersTable.getValueAt(row, 1);
        Object amount  = selectedVouchersTable.getValueAt(row, 2);

        vouchersTable.addRow(new Object[]{ number, type, "", "", "", amount, "PENDING" });
        selectedVouchersTable.removeRow(row);
        updateSummary();
    }

    private void updateSummary() {
        float totalVouchers = 0f;
        for (int i = 0; i < selectedVouchersTable.getRowCount(); i++) {
            totalVouchers += parseDecimal((String) selectedVouchersTable.getValueAt(i, 3));
        }

        float totalRetentions = 0f;
        for (TaxRule rule : TaxRuleController.getInstance().getAllTaxRules()) {
            totalRetentions += rule.calculateRetention(totalVouchers);
        }

        lblTotalVouchers.setText(String.format("$ %.2f", totalVouchers));
        lblTotalRetentions.setText(String.format("$ %.2f", totalRetentions));
        lblNetAmount.setText(String.format("$ %.2f", totalVouchers - totalRetentions));
    }

    private void confirm() {
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) { Alerts.warn(this, "Seleccioná un proveedor."); return; }
        if (selectedVouchersTable.getRowCount() == 0) { Alerts.warn(this, "Seleccioná al menos un comprobante."); return; }

        User currentUser = UserController.getInstance().getCurrentUser();
        if (currentUser == null) { Alerts.error(this, "No hay usuario conectado."); return; }

        if (!validateSupplierCertifications(supplier)) {
            if (!Alerts.confirm(this,
                    "El proveedor no tiene certificaciones de retención vigentes.\n¿Desea continuar de todas formas?",
                    "Advertencia: sin certificaciones")) return;
        }

        List<VoucherPayment> payments = buildPayments(supplier);
        if (payments.isEmpty()) { Alerts.error(this, "No se pudo identificar ningún comprobante."); return; }

        try {
            PaymentOrderController.getInstance().createPaymentOrder(supplier.getId(), payments, currentUser.getId());
            Alerts.info(this, "Orden de pago creada correctamente.");
            dispose();
        } catch (EntityNotFoundException | InvalidVoucherStatusException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private List<VoucherPayment> buildPayments(Supplier supplier) {
        List<VoucherPayment> payments = new ArrayList<>();
        List<Voucher> allVouchers = VoucherController.getInstance().findBySupplier(supplier.getId());

        for (int i = 0; i < selectedVouchersTable.getRowCount(); i++) {
            int number = Integer.parseInt(selectedVouchersTable.getValueAt(i, 0).toString());
            float amount = parseDecimal((String) selectedVouchersTable.getValueAt(i, 3));

            for (Voucher v : allVouchers) {
                if (v.getNumber() == number) {
                    payments.add(new VoucherPayment(v.getId(), amount));
                    break;
                }
            }
        }
        return payments;
    }

    private boolean validateSupplierCertifications(Supplier supplier) {
        if (supplier.getCertifications() == null || supplier.getCertifications().isEmpty()) return false;

        java.time.LocalDate today = java.time.LocalDate.now();

        for (var cert : supplier.getCertifications()) {
            if (cert.isValid(today)) return true;
        }

        return false;
    }

    private float parseDecimal(String value) {
        try {
            return Float.parseFloat(value.replaceAll("[^0-9,.]", "").replace(",", "."));
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }
}
