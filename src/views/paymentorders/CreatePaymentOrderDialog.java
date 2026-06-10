package views.paymentorders;

import controllers.PaymentOrderController;
import controllers.SupplierController;
import controllers.UserController;
import controllers.VoucherController;
import exceptions.EntityNotFoundException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.*;
import models.Supplier;
import models.User;
import models.Voucher;
import models.VoucherPayment;
import models.enums.VoucherType;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SectionPanel;

public class CreatePaymentOrderDialog extends AppDialog {

    private AppComboBox<Supplier> cmbSupplier;
    private AppTable vouchersTable;
    private AppTable selectedVouchersTable;
    private InfoLabel lblTotalVouchers;
    private InfoLabel lblTotalRetentions;
    private InfoLabel lblNetAmount;
    private List<VoucherPayment> selectedPayments;

    public CreatePaymentOrderDialog() {
        super("Nueva Orden de Pago", 900, 650);
        selectedPayments = new ArrayList<>();
        initSupplierPanel();
        initVouchersPanel();
        initSummaryPanel();
        initButtons();
    }

    private void initSupplierPanel() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) {
            cmbSupplier.addItem(s);
        }
        cmbSupplier.onSelectionChanged(this::refreshPendingVouchers);

        FormPanel form = new FormPanel("Proveedor");
        form.addRow("Seleccionar *", cmbSupplier);

        addNorth(form);
    }

    private void initVouchersPanel() {
    vouchersTable = new AppTable(new String[]{"Número", "Tipo", "Fecha", "Monto Neto", "IVA", "Total", "Estado"});
    selectedVouchersTable = new AppTable(new String[]{"Número", "Tipo", "Monto Original", "Monto a Pagar"});

    JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 5, 50, 5));
    buttonsPanel.add(ButtonBar.primary(">>", this::moveToSelected));
    buttonsPanel.add(ButtonBar.danger("<<", this::removeSelected));

    JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
    centerPanel.add(vouchersTable, BorderLayout.WEST);
    centerPanel.add(buttonsPanel, BorderLayout.CENTER);
    centerPanel.add(selectedVouchersTable, BorderLayout.EAST);

    SectionPanel vouchersPanel = new SectionPanel("Comprobantes Disponibles (Facturas y ND)");
    vouchersPanel.addCenter(centerPanel);

    addCenter(vouchersPanel);
}

    private void initSummaryPanel() {
        lblTotalVouchers = new InfoLabel("$ 0.00");
        lblTotalRetentions = new InfoLabel("$ 0.00");
        lblNetAmount = InfoLabel.highlight("$ 0.00");

        FormPanel summaryForm = new FormPanel();
        summaryForm.addRow("Total Comprobantes:", lblTotalVouchers);
        summaryForm.addRow("Total Retenciones:", lblTotalRetentions);
        summaryForm.addRow("Neto a Pagar:", lblNetAmount);

        addSouth(summaryForm);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Pago", this::confirm);
        getContentPane().add(bar, BorderLayout.SOUTH);
    }

    private void refreshPendingVouchers() {
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) {
            vouchersTable.clearRows();
            return;
        }

        vouchersTable.clearRows();

        try {
            List<Voucher> allVouchers = VoucherController.getInstance().findBySupplier(supplier.getId());
            
            for (Voucher v : allVouchers) {
                // Solo mostrar Facturas (A, B, C) y Notas de Débito que están PENDING
                boolean isPayableType = v.getType() == VoucherType.FACTURA_A
                        || v.getType() == VoucherType.FACTURA_B
                        || v.getType() == VoucherType.FACTURA_C
                        || v.getType() == VoucherType.NOTA_DEBITO;
                
                if (isPayableType && v.getStatus().name().equals("PENDING")) {
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
            }
        } catch (Exception ex) {
            Alerts.error(this, "Error al cargar comprobantes: " + ex.getMessage());
        }
    }

    private void moveToSelected() {
        int row = vouchersTable.getSelectedRow();
        if (row < 0) {
            Alerts.warn(this, "Seleccioná un comprobante.");
            return;
        }

        Object nro = vouchersTable.getValueAt(row, 0);
        Object tipo = vouchersTable.getValueAt(row, 1);
        Object monto = vouchersTable.getValueAt(row, 5);

        selectedVouchersTable.addRow(new Object[]{
            nro,
            tipo,
            monto,
            monto
        });

        vouchersTable.removeRow(row);
        updateSummary();
    }

    private void removeSelected() {
        int row = selectedVouchersTable.getSelectedRow();
        if (row < 0) {
            Alerts.warn(this, "Seleccioná un comprobante para remover.");
            return;
        }

        Object nro = selectedVouchersTable.getValueAt(row, 0);
        Object tipo = selectedVouchersTable.getValueAt(row, 1);
        Object monto = selectedVouchersTable.getValueAt(row, 2);

        vouchersTable.addRow(new Object[]{
            nro,
            tipo,
            "",
            "",
            "",
            monto,
            "PENDING"
        });

        selectedVouchersTable.removeRow(row);
        updateSummary();
    }

    private void updateSummary() {
        float totalVouchers = 0f;

        for (int i = 0; i < selectedVouchersTable.getRowCount(); i++) {
            String monto = (String) selectedVouchersTable.getValueAt(i, 3);
            float valor = parseDecimal(monto);
            totalVouchers += valor;
        }

        lblTotalVouchers.setText(String.format("$ %.2f", totalVouchers));

        float totalRetentions = totalVouchers * 0.03f;
        lblTotalRetentions.setText(String.format("$ %.2f", totalRetentions));

        float netAmount = totalVouchers - totalRetentions;
        lblNetAmount.setText(String.format("$ %.2f", netAmount));
    }

    private float parseDecimal(String value) {
        try {
            // Aceptar coma como separador decimal (como en órdenes de compra)
            String cleaned = value.replaceAll("[^0-9,.]", "").replace(",", ".");
            return Float.parseFloat(cleaned);
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }

  private void confirm() {
        Supplier supplier = cmbSupplier.getSelected();

        if (supplier == null) {
            Alerts.warn(this, "Seleccioná un proveedor.");
            return;
        }

        if (selectedVouchersTable.getRowCount() == 0) {
            Alerts.warn(this, "Seleccioná al menos un comprobante.");
            return;
        }

        User currentUser = UserController.getInstance().getCurrentUser();
        if (currentUser == null) {
            Alerts.error(this, "No hay usuario conectado.");
            return;
        }

        
        boolean hasCertifications = validateSupplierCertifications(supplier);
        if (!hasCertifications) {
            boolean continueAnyway = Alerts.confirm(this,
                "El proveedor no tiene certificaciones de retención vigentes.\n" +
                "¿Desea continuar con la orden de pago de todas formas?",
                "Advertencia: Certificaciones Vencidas");
            if (!continueAnyway) {
                return;
            }
        }

        List<VoucherPayment> payments = new ArrayList<>();
        for (int i = 0; i < selectedVouchersTable.getRowCount(); i++) {
            String montoStr = (String) selectedVouchersTable.getValueAt(i, 3);
            float monto = parseDecimal(montoStr);

            Object nroObj = selectedVouchersTable.getValueAt(i, 0);
            int nro = Integer.parseInt(nroObj.toString());

            List<Voucher> allVouchers = VoucherController.getInstance().findBySupplier(supplier.getId());
            UUID voucherId = null;
            for (Voucher v : allVouchers) {
                if (v.getNumber() == nro) {
                    voucherId = v.getId();
                    break;
                }
            }

            if (voucherId != null) {
                payments.add(new VoucherPayment(voucherId, monto));
            }
        }

        try {
            PaymentOrderController.getInstance().createPaymentOrder(
                supplier.getId(),
                payments,
                currentUser.getId()
            );

            Alerts.info(this, "Orden de pago creada correctamente.");
            dispose();
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }

    private boolean validateSupplierCertifications(Supplier supplier) {
        if (supplier.getCertifications() == null || supplier.getCertifications().isEmpty()) {
            return false;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        int activeCertifications = 0;

        for (var cert : supplier.getCertifications()) {
            if (cert.isValid(today)) {
                activeCertifications++;
            }
        }

        return activeCertifications > 0;
    }
}