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
import views.components.Alerts;
import views.components.AppTable;
import views.components.ButtonBar;

public class CreatePaymentOrderDialog extends JDialog {

    private JComboBox<Supplier> cmbSupplier;
    private AppTable vouchersTable;
    private AppTable selectedVouchersTable;
    private JLabel lblTotalVouchers;
    private JLabel lblTotalRetentions;
    private JLabel lblNetAmount;
    private List<VoucherPayment> selectedPayments;

    public CreatePaymentOrderDialog(JFrame parent) {
        super(parent, "Nueva Orden de Pago", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        selectedPayments = new ArrayList<>();

        initSupplierPanel();
        initVouchersPanel();
        initSummaryPanel();
        initButtons();
    }

    private void initSupplierPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Proveedor"));

        cmbSupplier = new JComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) {
            cmbSupplier.addItem(s);
        }

        cmbSupplier.addActionListener(e -> refreshPendingVouchers());
        cmbSupplier.setPreferredSize(new Dimension(300, 25));

        panel.add(new JLabel("Proveedor:"));
        panel.add(cmbSupplier);

        add(panel, BorderLayout.NORTH);
    }

    private void initVouchersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Comprobantes"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        vouchersTable = new AppTable(new String[]{"Número", "Fecha", "Monto", "Estado"});
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(vouchersTable), gbc);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonsPanel.add(ButtonBar.primary(">>", this::moveToSelected));
        buttonsPanel.add(ButtonBar.danger("<<", this::removeSelected));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(buttonsPanel, gbc);

        selectedVouchersTable = new AppTable(new String[]{"Número", "Monto Original", "Monto a Pagar"});
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(new JScrollPane(selectedVouchersTable), gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void initSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblTotalVouchers = new JLabel("Total Comprobantes: $ 0.00");
        lblTotalVouchers.setFont(lblTotalVouchers.getFont().deriveFont(Font.BOLD, 12f));

        lblTotalRetentions = new JLabel("Total Retenciones: $ 0.00");
        lblTotalRetentions.setFont(lblTotalRetentions.getFont().deriveFont(Font.BOLD, 12f));

        lblNetAmount = new JLabel("Neto a Pagar: $ 0.00");
        lblNetAmount.setFont(lblNetAmount.getFont().deriveFont(Font.BOLD, 14f));
        lblNetAmount.setForeground(new Color(0, 100, 0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTotalVouchers, gbc);

        gbc.gridy = 1;
        panel.add(lblTotalRetentions, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(15, 16, 8, 16);
        panel.add(lblNetAmount, gbc);

        add(panel, BorderLayout.SOUTH);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Pago", this::confirm);
        getContentPane().add(bar, BorderLayout.SOUTH);
    }

    private void refreshPendingVouchers() {
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();
        if (supplier == null) return;

        vouchersTable.clearRows();

        try {
            List<Voucher> pending = PaymentOrderController.getInstance()
                .getPendingVouchersBySupplier(supplier.getId());

            for (Voucher v : pending) {
                vouchersTable.addRow(new Object[]{
                    v.getNumber(),
                    v.getIssueDate(),
                    String.format("$ %.2f", v.getGrossTotal()),

                    v.getStatus().name()
                });
            }
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        Object monto = vouchersTable.getValueAt(row, 3);

        selectedVouchersTable.addRow(new Object[]{
            nro,
            monto,
            monto
        });

        vouchersTable.removeRow(row);
        updateSummary();
    }

    private void removeSelected() {
        int row = selectedVouchersTable.getSelectedRow();
        if (row < 0) return;

        Object nro = selectedVouchersTable.getValueAt(row, 0);
        Object monto = selectedVouchersTable.getValueAt(row, 1);

        vouchersTable.addRow(new Object[]{
            nro,
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
            String monto = (String) selectedVouchersTable.getValueAt(i, 2);
            float valor = Float.parseFloat(monto.replaceAll("[^0-9.]", ""));
            totalVouchers += valor;
        }

        lblTotalVouchers.setText(String.format("Total Comprobantes: $ %.2f", totalVouchers));

        float totalRetentions = totalVouchers * 0.03f;
        lblTotalRetentions.setText(String.format("Total Retenciones: $ %.2f", totalRetentions));

        float netAmount = totalVouchers - totalRetentions;
        lblNetAmount.setText(String.format("Neto a Pagar: $ %.2f", netAmount));
    }

    private void confirm() {
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();

        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedVouchersTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos un comprobante.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User currentUser = UserController.getInstance().getCurrentUser();

        List<VoucherPayment> payments = new ArrayList<>();
        for (int i = 0; i < selectedVouchersTable.getRowCount(); i++) {
            String montoStr = (String) selectedVouchersTable.getValueAt(i, 2);
            float monto = Float.parseFloat(montoStr.replaceAll("[^0-9.]", ""));

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

            JOptionPane.showMessageDialog(this, "Orden de pago creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}