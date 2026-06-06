package views.purchaseorders;

import controllers.ProductController;
import controllers.PurchaseOrderController;
import controllers.SupplierController;
import exceptions.CreditLimitExceededException;
import exceptions.EntityNotFoundException;
import models.Product;
import models.PurchaseOrderDetail;
import models.Supplier;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePurchaseOrderDialog extends JDialog {

    private JComboBox<Supplier> cmbSupplier;
    private JComboBox<Product> cmbProduct;
    private JTextField txtQuantity;
    private JLabel lblUnitPrice;
    private JLabel lblItemSubtotal;
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private List<PurchaseOrderDetail> details;
    private JLabel lblTotal;

    private float currentUnitPrice = 0f;

    public CreatePurchaseOrderDialog(JFrame parent) {
        super(parent, "Nueva Orden de Compra", true);
        setSize(620, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));
        details = new ArrayList<>();
        initSupplierPanel();
        initItemPanel();
        initDetailTable();
        initButtons();
    }

    private void initSupplierPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Proveedor"));
        cmbSupplier = new JComboBox<>();

        for (Supplier s : SupplierController.getInstance().findAll())  cmbSupplier.addItem(s);

        cmbSupplier.setPreferredSize(new Dimension(300, 25));
        cmbSupplier.addActionListener(e -> refreshUnitPrice());

        panel.add(new JLabel("Proveedor:"));
        panel.add(cmbSupplier);

        add(panel, BorderLayout.NORTH);
    }

    private void initItemPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Agregar ítem"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbProduct = new JComboBox<>();
        for (Product p : ProductController.getInstance().findAll()) cmbProduct.addItem(p);
        cmbProduct.addActionListener(e -> refreshUnitPrice());

        txtQuantity = new JTextField("1", 6);
        lblUnitPrice = new JLabel("$ 0.00");
        lblUnitPrice.setFont(lblUnitPrice.getFont().deriveFont(Font.BOLD));
        lblItemSubtotal = new JLabel("$ 0.00");
        lblItemSubtotal.setFont(lblItemSubtotal.getFont().deriveFont(Font.BOLD));

        txtQuantity.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshItemSubtotal(); }
            public void removeUpdate(DocumentEvent e) { refreshItemSubtotal(); }
            public void changedUpdate(DocumentEvent e) { refreshItemSubtotal(); }
        });

        JButton btnAdd = new JButton("Agregar ítem");
        btnAdd.setBackground(new Color(70, 130, 180));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> addDetail());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cmbProduct, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0;
        panel.add(txtQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Precio unitario:"), gbc);
        gbc.gridx = 1;
        panel.add(lblUnitPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Subtotal ítem:"), gbc);
        gbc.gridx = 1;
        panel.add(lblItemSubtotal, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnAdd, gbc);

        add(panel, BorderLayout.CENTER);
        refreshUnitPrice();
    }

    private void initDetailTable() {
        String[] cols = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        detailModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Ítems agregados"));

        lblTotal = new JLabel("Total OC: $ 0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 13f));

        JButton btnRemove = new JButton("Quitar seleccionado");
        btnRemove.addActionListener(e -> removeSelected());

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(btnRemove, BorderLayout.WEST);
        bottomRow.add(lblTotal, BorderLayout.EAST);

        tablePanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        tablePanel.add(bottomRow, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.SOUTH);
    }

    private void initButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirm = new JButton("Confirmar Orden");
        JButton btnCancel = new JButton("Cancelar");

        btnConfirm.addActionListener(e -> confirm());
        btnCancel.addActionListener(e -> dispose());

        buttons.add(btnCancel);
        buttons.add(btnConfirm);

        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void refreshUnitPrice() {
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();
        Product product = (Product) cmbProduct.getSelectedItem();
        if (supplier == null || product == null) currentUnitPrice = 0f;
        else currentUnitPrice = product.getPriceForSupplier(supplier.getId());

        lblUnitPrice.setText(String.format("$ %.2f", currentUnitPrice));
        refreshItemSubtotal();
    }

    private void refreshItemSubtotal() {
        try {
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            lblItemSubtotal.setText(String.format("$ %.2f", qty * currentUnitPrice));
        } catch (NumberFormatException e) {
            lblItemSubtotal.setText("$ -");
        }
    }

    private void addDetail() {
        Product product = (Product) cmbProduct.getSelectedItem();
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderDetail detail = new PurchaseOrderDetail(product, quantity, currentUnitPrice);
        details.add(detail);
        detailModel.addRow(new Object[]{
            product.getDescription(),
            quantity,
            String.format("$ %.2f", currentUnitPrice),
            String.format("$ %.2f", detail.getSubtotal())
        });
        refreshOrderTotal();
        txtQuantity.setText("1");
    }

    private void removeSelected() {
        int row = detailTable.getSelectedRow();

        if (row < 0) return;

        details.remove(row);
        detailModel.removeRow(row);
        
        refreshOrderTotal();
    }

    private void refreshOrderTotal() {
        float total = 0f;
        for (PurchaseOrderDetail d : details) total += d.getSubtotal();
        lblTotal.setText(String.format("Total OC: $ %.2f", total));
    }

    private void confirm() {
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();
        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (details.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un ítem.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UUID dummyUserId = UUID.randomUUID();

        try {
            PurchaseOrderController.getInstance().createPurchaseOrder(supplier.getId(), details, dummyUserId);
            JOptionPane.showMessageDialog(this, "Orden de compra creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (CreditLimitExceededException ex) {
            int option = JOptionPane.showConfirmDialog(this,
                ex.getMessage() + "\n\n¿Desea solicitar autorización de supervisor para continuar?",
                "Tope de Crédito Excedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Funcionalidad de autorización pendiente de implementación.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
