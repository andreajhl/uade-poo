package views.paymentorders;

import controllers.PaymentOrderController;
import controllers.SupplierController;
import controllers.TaxRuleController;
import controllers.UserController;
import controllers.VoucherController;
import exceptions.EntityNotFoundException;
import exceptions.InvalidVoucherStatusException;
import models.PaymentItem;
import models.Supplier;
import models.TaxRule;
import models.User;
import models.Voucher;
import models.VoucherPayment;
import models.enums.PaymentMethodType;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.AppTextField;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.PlaceholderTextField;
import views.components.SectionPanel;
import views.components.TransferPanel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CreatePaymentOrderDialog extends AppDialog {

    private AppComboBox<Supplier> cmbSupplier;
    private AppTable vouchersTable;
    private AppTable selectedVouchersTable;
    private AppTextField txtAmountToPay;
    private InfoLabel lblTotalVouchers;
    private InfoLabel lblTotalRetentions;
    private InfoLabel lblNetAmount;

    private AppComboBox<PaymentMethodType> cmbPaymentType;
    private AppTextField txtPaymentAmount;
    private FormPanel checkFieldsPanel;
    private AppTextField txtCheckNumber;
    private PlaceholderTextField txtCheckIssueDate;
    private PlaceholderTextField txtCheckDueDate;
    private AppTextField txtCheckSigner;
    private AppTable paymentItemsTable;
    private final List<PaymentItem> paymentItems = new ArrayList<>();

    public CreatePaymentOrderDialog() {
        super("Nueva Orden de Pago", 920, 820);
        initSupplierPanel();
        initVouchersPanel();
        initSouthPanel();
        cmbSupplier.onSelectionChanged(this::refreshPendingVouchers);
        refreshPendingVouchers();
    }

    private void initSupplierPanel() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);

        FormPanel form = new FormPanel("Proveedor");
        form.addRow("Seleccionar *", cmbSupplier);
        addNorth(form);
    }

    private void initVouchersPanel() {
        vouchersTable = new AppTable(
                new String[]{"Número", "Tipo", "Fecha", "Neto", "IVA", "Total"});
        selectedVouchersTable = new AppTable(
                new String[]{"Número", "Tipo", "Monto Original", "Monto a Pagar", "Cancelación"});

        txtAmountToPay = new AppTextField("0.00");
        vouchersTable.onRowSelected(this::prefillAmountToPay);

        FormPanel amountForm = new FormPanel();
        amountForm.addRow("Importe:", txtAmountToPay);

        BorderPanel buttons = new BorderPanel(5, 5);
        buttons.addNorth(ButtonBar.primary(">>", this::moveToSelected));
        buttons.addCenter(amountForm);
        buttons.addSouth(ButtonBar.danger("<<", this::removeFromSelected));

        SectionPanel section = new SectionPanel("Comprobantes Disponibles (Facturas)");
        section.addCenter(new TransferPanel(vouchersTable, buttons, selectedVouchersTable));
        addCenter(section);
    }

    private void prefillAmountToPay() {
        int row = vouchersTable.getSelectedRow();
        if (row < 0) return;
        Object total = vouchersTable.getValueAt(row, 5);
        if (total == null) return;
        float parsed = parseDecimal(total.toString());
        txtAmountToPay.setText(String.format("%.2f", parsed));
    }

    private void initSouthPanel() {
        lblTotalVouchers   = new InfoLabel("$ 0.00");
        lblTotalRetentions = new InfoLabel("$ 0.00");
        lblNetAmount       = InfoLabel.highlight("$ 0.00");

        FormPanel summaryForm = new FormPanel();
        summaryForm.addRow("Total Comprobantes:", lblTotalVouchers);
        summaryForm.addRow("Total Retenciones:", lblTotalRetentions);
        summaryForm.addRow("Neto a Pagar:", lblNetAmount);

        SectionPanel summarySection = new SectionPanel("Resumen");
        summarySection.addCenter(summaryForm);

        SectionPanel paymentSection = initPaymentMethodsSection();

        BorderPanel contentRow = new BorderPanel(8, 0);
        contentRow.addWest(summarySection);
        contentRow.addCenter(paymentSection);

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Pago", this::confirm);

        BorderPanel south = new BorderPanel(0, 4);
        south.addCenter(contentRow);
        south.addSouth(bar);
        addSouth(south);
    }

    private SectionPanel initPaymentMethodsSection() {
        cmbPaymentType    = new AppComboBox<>(PaymentMethodType.values());
        txtPaymentAmount  = new AppTextField("0.00");
        txtCheckNumber    = new AppTextField();
        txtCheckIssueDate = new PlaceholderTextField("yyyy-MM-dd");
        txtCheckDueDate   = new PlaceholderTextField("yyyy-MM-dd");
        txtCheckSigner    = new AppTextField();

        checkFieldsPanel = new FormPanel();
        checkFieldsPanel.addRow("N° Cheque *",       txtCheckNumber);
        checkFieldsPanel.addRow("Fecha emisión *",   txtCheckIssueDate);
        checkFieldsPanel.addRow("Fecha vcto. *",     txtCheckDueDate);
        checkFieldsPanel.addRow("Firmante *",        txtCheckSigner);
        checkFieldsPanel.setVisible(false);

        cmbPaymentType.onSelectionChanged(this::onPaymentTypeChanged);

        FormPanel addForm = new FormPanel();
        addForm.addRow("Tipo *",    cmbPaymentType);
        addForm.addRow("Importe *", txtPaymentAmount);

        BorderPanel inputArea = new BorderPanel(0, 2);
        inputArea.addNorth(addForm);
        inputArea.addCenter(checkFieldsPanel);
        inputArea.addSouth(ButtonBar.primary("Agregar medio de pago", this::addPaymentItem));

        paymentItemsTable = new AppTable(
                new String[]{"Tipo", "Importe", "N° Cheque", "Firmante"});

        SectionPanel section = new SectionPanel("Medios de Pago");
        section.addNorth(inputArea);
        section.addCenter(paymentItemsTable);
        section.addSouth(ButtonBar.danger("Quitar seleccionado", this::removePaymentItem));
        return section;
    }

    private void onPaymentTypeChanged() {
        PaymentMethodType type = cmbPaymentType.getSelected();
        boolean isCheck = type == PaymentMethodType.CHEQUE_PROPIO
                || type == PaymentMethodType.CHEQUE_DE_TERCEROS;
        checkFieldsPanel.setVisible(isCheck);
        revalidate();
        repaint();
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

        float amountToPay;
        try {
            amountToPay = Float.parseFloat(txtAmountToPay.getText().trim().replace(",", "."));
            if (amountToPay <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "Ingresá un importe válido mayor a 0.");
            return;
        }

        Object number    = vouchersTable.getValueAt(row, 0);
        Object type      = vouchersTable.getValueAt(row, 1);
        Object fullTotal = vouchersTable.getValueAt(row, 5);

        float fullAmount = parseDecimal((String) fullTotal);
        String cancelType = amountToPay >= fullAmount - 0.01f ? "Total" : "Parcial";

        selectedVouchersTable.addRow(new Object[]{
            number, type, fullTotal, String.format("$ %.2f", amountToPay), cancelType
        });
        vouchersTable.removeRow(row);
        txtAmountToPay.setText("0.00");
        updateSummary();
    }

    private void removeFromSelected() {
        int row = selectedVouchersTable.getSelectedRow();
        if (row < 0) { Alerts.warn(this, "Seleccioná un comprobante para quitar."); return; }

        Object number   = selectedVouchersTable.getValueAt(row, 0);
        Object type     = selectedVouchersTable.getValueAt(row, 1);
        Object original = selectedVouchersTable.getValueAt(row, 2);

        vouchersTable.addRow(new Object[]{ number, type, "", "", "", original });
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
        refreshPaymentAmountHint();
    }

    private void refreshPaymentAmountHint() {
        float remaining = parseDecimal(lblNetAmount.getText());
        for (PaymentItem item : paymentItems) remaining -= item.getAmount();
        txtPaymentAmount.setText(String.format("%.2f", Math.max(0f, remaining)));
    }

    private void addPaymentItem() {
        PaymentMethodType type = cmbPaymentType.getSelected();
        if (type == null) { Alerts.warn(this, "Seleccioná un tipo de pago."); return; }

        float amount;
        try {
            amount = Float.parseFloat(txtPaymentAmount.getText().trim().replace(",", "."));
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El importe debe ser un número mayor a 0.");
            return;
        }

        PaymentItem item;
        if (type == PaymentMethodType.CHEQUE_PROPIO || type == PaymentMethodType.CHEQUE_DE_TERCEROS) {
            String checkNumber = txtCheckNumber.getText().trim();
            String signer      = txtCheckSigner.getText().trim();
            if (checkNumber.isEmpty() || signer.isEmpty()) {
                Alerts.warn(this, "Completá los datos del cheque.");
                return;
            }
            LocalDate issueDate, dueDate;
            try {
                issueDate = LocalDate.parse(txtCheckIssueDate.getValue());
                dueDate   = LocalDate.parse(txtCheckDueDate.getValue());
            } catch (DateTimeParseException ex) {
                Alerts.warn(this, "Fechas del cheque inválidas. Usá el formato yyyy-MM-dd.");
                return;
            }
            item = new PaymentItem(type, amount, checkNumber, issueDate, dueDate, signer);
            paymentItemsTable.addRow(new Object[]{
                type.toString(),
                String.format("$ %.2f", amount),
                checkNumber,
                signer
            });
            txtCheckNumber.setText("");
            txtCheckIssueDate.setText("");
            txtCheckDueDate.setText("");
            txtCheckSigner.setText("");
        } else {
            item = new PaymentItem(type, amount);
            paymentItemsTable.addRow(new Object[]{
                type.toString(),
                String.format("$ %.2f", amount),
                "—",
                "—"
            });
        }

        paymentItems.add(item);
        refreshPaymentAmountHint();
    }

    private void removePaymentItem() {
        int row = paymentItemsTable.getSelectedRow();
        if (row < 0) { Alerts.warn(this, "Seleccioná un medio de pago para quitar."); return; }
        paymentItems.remove(row);
        paymentItemsTable.removeRow(row);
        refreshPaymentAmountHint();
    }

    private void confirm() {
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) { Alerts.warn(this, "Seleccioná un proveedor."); return; }
        if (selectedVouchersTable.getRowCount() == 0) {
            Alerts.warn(this, "Seleccioná al menos un comprobante."); return;
        }

        User currentUser = UserController.getInstance().getCurrentUser();
        if (currentUser == null) { Alerts.error(this, "No hay usuario conectado."); return; }

        if (paymentItems.isEmpty()) {
            Alerts.warn(this, "Agregá al menos un medio de pago."); return;
        }

        float netAmount = parseDecimal(lblNetAmount.getText());
        float totalPaid = 0f;
        for (PaymentItem item : paymentItems) totalPaid += item.getAmount();

        if (Math.abs(totalPaid - netAmount) > 0.01f) {
            Alerts.warn(this, String.format(
                    "El total de medios de pago ($ %.2f) no coincide con el neto a pagar ($ %.2f).",
                    totalPaid, netAmount));
            return;
        }

        if (!validateSupplierCertifications(supplier)) {
            if (!Alerts.confirm(this,
                    "El proveedor no tiene certificaciones de retención vigentes.\n¿Desea continuar de todas formas?",
                    "Advertencia: sin certificaciones")) return;
        }

        List<VoucherPayment> payments = buildPayments(supplier);
        if (payments.isEmpty()) { Alerts.error(this, "No se pudo identificar ningún comprobante."); return; }

        try {
            PaymentOrderController.getInstance().createPaymentOrder(
                    supplier.getId(), payments, currentUser.getId(), paymentItems);
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
            int number   = Integer.parseInt(selectedVouchersTable.getValueAt(i, 0).toString());
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
        LocalDate today = LocalDate.now();
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
