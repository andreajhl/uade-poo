package views.purchaseorders;

import controllers.ProductController;
import controllers.PurchaseOrderController;
import controllers.SupplierController;
import controllers.UserController;
import exceptions.CreditLimitExceededException;
import exceptions.EntityNotFoundException;
import models.Product;
import models.PurchaseOrderDetail;
import models.Supplier;
import models.User;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SupervisorApprovalDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreatePurchaseOrderDialog extends JDialog {

    private JComboBox<Supplier> cmbSupplier;
    private JComboBox<Product> cmbProduct;
    private JTextField txtQuantity;
    private InfoLabel lblUnitPrice;
    private InfoLabel lblItemSubtotal;
    private InfoLabel lblTotal;
    private AppTable detailTable;
    private List<PurchaseOrderDetail> details;

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
        cmbSupplier = new JComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);
        cmbSupplier.addActionListener(e -> refreshUnitPrice());

        FormPanel form = new FormPanel("Proveedor");
        form.addRow("Proveedor:", cmbSupplier);

        add(form, BorderLayout.NORTH);
    }

    private void initItemPanel() {
        cmbProduct = new JComboBox<>();
        for (Product p : ProductController.getInstance().findAll()) cmbProduct.addItem(p);
        cmbProduct.addActionListener(e -> refreshUnitPrice());

        txtQuantity = new JTextField("1");
        lblUnitPrice = new InfoLabel("$ 0.00");
        lblItemSubtotal = new InfoLabel("$ 0.00");

        txtQuantity.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshItemSubtotal(); }
            public void removeUpdate(DocumentEvent e) { refreshItemSubtotal(); }
            public void changedUpdate(DocumentEvent e) { refreshItemSubtotal(); }
        });

        FormPanel form = new FormPanel("Agregar ítem");
        form.addRow("Producto:", cmbProduct);
        form.addRow("Cantidad:", txtQuantity);
        form.addRow("Precio unitario:", lblUnitPrice);
        form.addRow("Subtotal ítem:", lblItemSubtotal);
        form.addFullRow(ButtonBar.primary("Agregar ítem", this::addDetail));

        add(form, BorderLayout.CENTER);
        refreshUnitPrice();
    }

    private void initDetailTable() {
        detailTable = new AppTable(new String[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal"});
        lblTotal = InfoLabel.highlight("Total OC: $ 0.00");

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(ButtonBar.danger("Quitar seleccionado", this::removeSelected), BorderLayout.WEST);
        bottomRow.add(lblTotal, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Ítems agregados"));
        tablePanel.add(detailTable, BorderLayout.CENTER);
        tablePanel.add(bottomRow, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.SOUTH);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Orden", this::confirm);
        getContentPane().add(bar, BorderLayout.SOUTH);
    }

    private void refreshUnitPrice() {
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();
        Product product = (Product) cmbProduct.getSelectedItem();
        currentUnitPrice = (supplier == null || product == null) ? 0f : product.getPriceForSupplier(supplier.getId());
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
        detailTable.addRow(new Object[]{
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
        detailTable.removeRow(row);
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

        User currentUser = UserController.getInstance().getCurrentUser();

        try {
            PurchaseOrderController.getInstance().createPurchaseOrder(supplier.getId(), details, currentUser.getId());
            JOptionPane.showMessageDialog(this, "Orden de compra creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (CreditLimitExceededException ex) {
            int option = JOptionPane.showConfirmDialog(this,
                ex.getMessage() + "\n\n¿Desea solicitar autorización de supervisor para continuar?",
                "Tope de Crédito Excedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                SupervisorApprovalDialog approvalDialog = new SupervisorApprovalDialog(
                        (JFrame) getParent(), "Límite de crédito excedido");
                approvalDialog.setVisible(true);
                if (approvalDialog.isApproved()) {
                    try {
                        PurchaseOrderController.getInstance().createPurchaseOrderWithAuthorization(
                                supplier.getId(), details, currentUser.getId(), approvalDialog.getAuthorization());
                        JOptionPane.showMessageDialog(this, "Orden de compra creada con autorización.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (EntityNotFoundException ex2) {
                        JOptionPane.showMessageDialog(this, ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
