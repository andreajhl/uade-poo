package views.purchaseorders;

import controllers.ProductController;
import controllers.PurchaseOrderController;
import controllers.SupplierController;
import controllers.UserController;
import exceptions.CreditLimitExceededException;
import exceptions.EntityNotFoundException;
import models.Product;
import models.ProductSupplier;
import models.PurchaseOrderDetail;
import models.Supplier;
import models.User;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.AppTextField;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.SectionPanel;
import views.components.SupervisorApprovalDialog;

import java.util.ArrayList;
import java.util.List;

public class CreatePurchaseOrderDialog extends AppDialog {

    private AppComboBox<Supplier> cmbSupplier;
    private AppComboBox<Product> cmbProduct;
    private AppTextField txtQuantity;
    private AppTextField txtUnitPrice;
    private InfoLabel lblItemSubtotal;
    private InfoLabel lblTotal;
    private AppTable detailTable;
    private List<PurchaseOrderDetail> details;

    public CreatePurchaseOrderDialog() {
        super("Nueva Orden de Compra", 620, 520);
        details = new ArrayList<>();
        initSupplierPanel();
        initItemPanel();
        initDetailTable();
        initButtons();
    }

    private void initSupplierPanel() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);
        cmbSupplier.onSelectionChanged(this::onSupplierChanged);

        FormPanel form = new FormPanel("Proveedor");
        form.addRow("Proveedor:", cmbSupplier);
        addNorth(form);
    }

    private void onSupplierChanged() {
        refreshProductList();
        refreshUnitPrice();
    }

    private void refreshProductList() {
        cmbProduct.removeAllItems();
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) return;
        for (Product p : ProductController.getInstance().findAll()) {
            if (supplier.getCategories().contains(p.getCategory())) {
                cmbProduct.addItem(p);
            }
        }
    }

    private void initItemPanel() {
        cmbProduct = new AppComboBox<>();
        cmbProduct.onSelectionChanged(this::refreshUnitPrice);

        txtQuantity = new AppTextField("1");
        txtUnitPrice = new AppTextField("0.00");
        lblItemSubtotal = new InfoLabel("$ 0.00");

        txtQuantity.onTextChanged(this::refreshItemSubtotal);
        txtUnitPrice.onTextChanged(this::refreshItemSubtotal);

        FormPanel form = new FormPanel("Agregar ítem");
        form.addRow("Producto:", cmbProduct);
        form.addRow("Cantidad:", txtQuantity);
        form.addRow("Precio unitario:", txtUnitPrice);
        form.addRow("Subtotal ítem:", lblItemSubtotal);
        form.addFullRow(ButtonBar.primary("Agregar ítem", this::addDetail));

        addCenter(form);
        refreshProductList();
        refreshUnitPrice();
    }

    private void initDetailTable() {
        detailTable = new AppTable(new String[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal"});
        lblTotal = InfoLabel.highlight("Total OC: $ 0.00");

        BorderPanel bottomRow = new BorderPanel();
        bottomRow.addWest(ButtonBar.danger("Quitar seleccionado", this::removeSelected));
        bottomRow.addEast(lblTotal);

        SectionPanel tablePanel = new SectionPanel("Ítems agregados");
        tablePanel.addCenter(detailTable);
        tablePanel.addSouth(bottomRow);

        addSouth(tablePanel);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Confirmar Orden", this::confirm);
        addSouth(bar);
    }

    private void refreshUnitPrice() {
        Supplier supplier = cmbSupplier.getSelected();
        Product product = cmbProduct.getSelected();
        float known = (supplier == null || product == null) ? 0f : product.getPriceForSupplier(supplier.getId());
        txtUnitPrice.setText(String.format("%.2f", known));
        refreshItemSubtotal();
    }

    private void refreshItemSubtotal() {
        try {
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            float price = Float.parseFloat(txtUnitPrice.getText().trim().replace(",", "."));
            lblItemSubtotal.setText(String.format("$ %.2f", qty * price));
        } catch (NumberFormatException e) {
            lblItemSubtotal.setText("$ -");
        }
    }

    private void addDetail() {
        Supplier supplier = cmbSupplier.getSelected();
        Product product = cmbProduct.getSelected();
        if (product == null) {
            Alerts.warn(this, "Seleccione un producto.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "La cantidad debe ser un número entero.");
            return;
        }
        if (quantity <= 0) {
            Alerts.warn(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        float unitPrice;
        try {
            unitPrice = Float.parseFloat(txtUnitPrice.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El precio unitario debe ser un número válido.");
            return;
        }
        if (unitPrice < 0) {
            Alerts.warn(this, "El precio unitario no puede ser negativo.");
            return;
        }

        if (supplier != null) {
            product.addSupplierPrice(new ProductSupplier(supplier.getId(), unitPrice, product.getCategory()));
        }

        PurchaseOrderDetail detail = new PurchaseOrderDetail(product, quantity, unitPrice);
        details.add(detail);
        detailTable.addRow(new Object[]{
            product.getDescription(),
            quantity,
            String.format("$ %.2f", unitPrice),
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
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) {
            Alerts.warn(this, "Seleccione un proveedor.");
            return;
        }
        if (details.isEmpty()) {
            Alerts.warn(this, "Agregue al menos un ítem.");
            return;
        }

        User currentUser = UserController.getInstance().getCurrentUser();

        try {
            PurchaseOrderController.getInstance().createPurchaseOrder(supplier.getId(), details, currentUser.getId());
            Alerts.info(this, "Orden de compra creada correctamente.");
            dispose();
        } catch (CreditLimitExceededException ex) {
            boolean wantsAuth = Alerts.confirm(this,
                ex.getMessage() + "\n\n¿Desea solicitar autorización de supervisor para continuar?",
                "Tope de Crédito Excedido");
            if (wantsAuth) {
                SupervisorApprovalDialog approvalDialog = new SupervisorApprovalDialog("Límite de crédito excedido");
                approvalDialog.setVisible(true);
                if (approvalDialog.isApproved()) {
                    try {
                        PurchaseOrderController.getInstance().createPurchaseOrderWithAuthorization(
                                supplier.getId(), details, currentUser.getId(), approvalDialog.getAuthorization());
                        Alerts.info(this, "Orden de compra creada con autorización.");
                        dispose();
                    } catch (EntityNotFoundException ex2) {
                        Alerts.error(this, ex2.getMessage());
                    }
                }
            }
        } catch (EntityNotFoundException ex) {
            Alerts.error(this, ex.getMessage());
        }
    }
}
