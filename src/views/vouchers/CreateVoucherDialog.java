package views.vouchers;

import controllers.PurchaseOrderController;
import controllers.SupplierController;
import controllers.ProductController;
import controllers.VoucherController;
import exceptions.EntityNotFoundException;
import exceptions.VoucherDeviationException;
import models.Product;
import models.PurchaseOrder;
import models.PurchaseOrderDetail;
import models.Supplier;
import models.Voucher;
import models.VoucherDetail;
import models.enums.VoucherType;
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
import views.components.SupervisorApprovalDialog;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateVoucherDialog extends AppDialog {

    private AppComboBox<Supplier> cmbSupplier;
    private AppComboBox<VoucherType> cmbType;
    private PlaceholderTextField txtDate;

    private BorderPanel pnlOCRef;
    private BorderPanel pnlNDRef;
    private AppComboBox<PurchaseOrder> cmbRelatedOC;
    private AppComboBox<Voucher> cmbRelatedND;

    private AppComboBox<Product> cmbProduct;
    private AppTextField txtQuantity;
    private AppTextField txtUnitPrice;

    private InfoLabel lblNetTotal;
    private InfoLabel lblVatTotal;
    private InfoLabel lblGrossTotal;

    private AppTable itemsTable;
    private final List<VoucherDetail> details = new ArrayList<>();

    public CreateVoucherDialog() {
        super("Nuevo Comprobante", 660, 640);
        initHeaderPanel();
        initItemsPanel();
        initButtons();
        onTypeChanged();
    }

    private void initHeaderPanel() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);
        cmbSupplier.onSelectionChanged(this::onSupplierChanged);

        cmbType = new AppComboBox<>(VoucherType.values());
        cmbType.onSelectionChanged(this::onTypeChanged);

        txtDate = new PlaceholderTextField("yyyy-MM-dd");

        cmbRelatedOC = new AppComboBox<>();
        cmbRelatedOC.onSelectionChanged(this::refreshPriceHint);
        pnlOCRef = new BorderPanel();
        pnlOCRef.addCenter(cmbRelatedOC);

        cmbRelatedND = new AppComboBox<>();
        pnlNDRef = new BorderPanel();
        pnlNDRef.addCenter(cmbRelatedND);

        FormPanel form = new FormPanel("Datos del comprobante");
        form.addRow("Proveedor *", cmbSupplier);
        form.addRow("Tipo *", cmbType);
        form.addRow("Fecha *", txtDate);
        form.addRow("OC asociada *", pnlOCRef);
        form.addRow("Factura asociada *", pnlNDRef);

        refreshOCCombo();
        refreshNDCombo();

        addNorth(form);
    }

    private void initItemsPanel() {
        cmbProduct = new AppComboBox<>();
        for (Product p : ProductController.getInstance().findAll()) cmbProduct.addItem(p);
        cmbProduct.onSelectionChanged(this::refreshPriceHint);

        txtQuantity = new AppTextField("1");
        txtUnitPrice = new AppTextField("0.00");

        FormPanel addForm = new FormPanel();
        addForm.addRow("Producto *", cmbProduct);
        addForm.addRow("Cantidad *", txtQuantity);
        addForm.addRow("Precio unitario *", txtUnitPrice);
        addForm.addFullRow(ButtonBar.primary("Agregar ítem", this::addItem));

        itemsTable = new AppTable(
                new String[]{"Producto", "Cant.", "P. Unit.", "Neto", "IVA %", "IVA $", "Total"});

        lblNetTotal   = new InfoLabel("$ 0.00");
        lblVatTotal   = new InfoLabel("$ 0.00");
        lblGrossTotal = InfoLabel.highlight("$ 0.00");

        FormPanel totalsForm = new FormPanel();
        totalsForm.addRow("Total Neto:", lblNetTotal);
        totalsForm.addRow("Total IVA:", lblVatTotal);
        totalsForm.addRow("Total:", lblGrossTotal);

        BorderPanel bottomRow = new BorderPanel();
        bottomRow.addWest(ButtonBar.danger("Quitar seleccionado", this::removeItem));
        bottomRow.addEast(totalsForm);

        SectionPanel itemsPanel = new SectionPanel("Ítems del comprobante");
        itemsPanel.addNorth(addForm);
        itemsPanel.addCenter(itemsTable);
        itemsPanel.addSouth(bottomRow);

        addCenter(itemsPanel);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        addSouth(bar);
    }

    private void onTypeChanged() {
        if (pnlOCRef == null || pnlNDRef == null) return;
        VoucherType type = cmbType.getSelected();
        boolean isInvoice = type == VoucherType.FACTURA_A
                || type == VoucherType.FACTURA_B
                || type == VoucherType.FACTURA_C;
        boolean referencesInvoice = type == VoucherType.NOTA_DEBITO
                || type == VoucherType.NOTA_CREDITO;

        pnlOCRef.setVisible(isInvoice);
        pnlNDRef.setVisible(referencesInvoice);
    }

    private void onSupplierChanged() {
        refreshOCCombo();
        refreshNDCombo();
        refreshPriceHint();
    }

    private void refreshOCCombo() {
        Supplier supplier = cmbSupplier.getSelected();
        cmbRelatedOC.removeAllItems();
        if (supplier == null) return;
        for (PurchaseOrder oc : PurchaseOrderController.getInstance().findBySupplier(supplier.getId())) {
            cmbRelatedOC.addItem(oc);
        }
    }

    private void refreshNDCombo() {
        Supplier supplier = cmbSupplier.getSelected();
        cmbRelatedND.removeAllItems();
        if (supplier == null) return;
        for (Voucher invoice : VoucherController.getInstance().findInvoices(supplier.getId(), null, null)) {
            cmbRelatedND.addItem(invoice);
        }
    }

    private void refreshPriceHint() {
        if (cmbProduct == null) return;
        Product product = cmbProduct.getSelected();
        if (product == null) return;

        PurchaseOrder oc = cmbRelatedOC.getSelected();
        if (oc != null) {
            for (PurchaseOrderDetail d : oc.getDetails()) {
                if (d.getProduct().getId().equals(product.getId())) {
                    txtUnitPrice.setText(String.format("%.2f", d.getAgreedUnitPrice()));
                    return;
                }
            }
        }

        Supplier supplier = cmbSupplier.getSelected();
        if (supplier != null) {
            float price = product.getPriceForSupplier(supplier.getId());
            txtUnitPrice.setText(String.format("%.2f", price));
        }
    }

    private void addItem() {
        Product product = cmbProduct.getSelected();
        if (product == null) {
            Alerts.warn(this, "Seleccioná un producto.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "La cantidad debe ser un número entero mayor a 0.");
            return;
        }

        float unitPrice;
        try {
            unitPrice = Float.parseFloat(txtUnitPrice.getText().trim().replace(",", "."));
            if (unitPrice < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "El precio unitario debe ser un número válido mayor o igual a 0.");
            return;
        }

        VoucherDetail detail = new VoucherDetail(product, quantity, unitPrice);
        details.add(detail);

        float ivaRate = VoucherDetail.ivaRate(product.getTaxType());
        itemsTable.addRow(new Object[]{
            product.getDescription(),
            quantity,
            String.format("$ %.2f", unitPrice),
            String.format("$ %.2f", detail.getNetAmount()),
            String.format("%.1f%%", ivaRate),
            String.format("$ %.2f", detail.getVatAmount()),
            String.format("$ %.2f", detail.getGrossAmount())
        });

        refreshTotals();
        txtQuantity.setText("1");
        refreshPriceHint();
    }

    private void removeItem() {
        int row = itemsTable.getSelectedRow();
        if (row < 0) return;
        details.remove(row);
        itemsTable.removeRow(row);
        refreshTotals();
    }

    private void refreshTotals() {
        float net = 0f, vat = 0f;
        for (VoucherDetail d : details) {
            net += d.getNetAmount();
            vat += d.getVatAmount();
        }
        lblNetTotal.setText(String.format("$ %.2f", net));
        lblVatTotal.setText(String.format("$ %.2f", vat));
        lblGrossTotal.setText(String.format("$ %.2f", net + vat));
    }

    private void save() {
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) { Alerts.warn(this, "Seleccioná un proveedor."); return; }

        VoucherType type = cmbType.getSelected();


        LocalDate issueDate;
        try {
            String dateText = txtDate.getValue();
            if (dateText.isEmpty()) throw new DateTimeParseException("", "", 0);
            issueDate = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            Alerts.warn(this, "Fecha inválida. Usá el formato yyyy-MM-dd.");
            return;
        }

        if (details.isEmpty()) { Alerts.warn(this, "Agregá al menos un ítem."); return; }

        boolean isInvoice = type == VoucherType.FACTURA_A
                || type == VoucherType.FACTURA_B
                || type == VoucherType.FACTURA_C;

        if (type == VoucherType.NOTA_DEBITO || type == VoucherType.NOTA_CREDITO) {
            Voucher invoice = cmbRelatedND.getSelected();
            if (invoice == null) {
                String label = type == VoucherType.NOTA_DEBITO ? "Nota de Débito" : "Nota de Crédito";
                Alerts.warn(this, "La " + label + " debe estar asociada a una Factura.");
                return;
            }
            try {
                if (type == VoucherType.NOTA_DEBITO) {
                    VoucherController.getInstance().registerDebitNote(
                            supplier.getId(), issueDate, details, invoice.getId());
                    Alerts.info(this, "Nota de Débito registrada correctamente.");
                } else {
                    VoucherController.getInstance().registerCreditNote(
                            supplier.getId(), issueDate, details, invoice.getId());
                    Alerts.info(this, "Nota de Crédito registrada correctamente.");
                }
                dispose();
            } catch (EntityNotFoundException ex) {
                Alerts.error(this, ex.getMessage());
            }
            return;
        }

        if (isInvoice) {
            PurchaseOrder oc = cmbRelatedOC.getSelected();
            if (oc == null) {
                Alerts.warn(this, "La Factura debe estar asociada a una Orden de Compra.");
                return;
            }
            UUID ocId = oc.getId();
            try {
                VoucherController.getInstance().registerInvoice(
                        supplier.getId(), type, issueDate, details, ocId);
                Alerts.info(this, "Factura registrada correctamente.");
                dispose();

            } catch (VoucherDeviationException ex) {
                boolean wantsAuth = Alerts.confirm(this,
                        ex.getMessage() + "\n\n¿Desea solicitar autorización de supervisor?",
                        "Requiere Autorización");
                if (!wantsAuth) return;

                SupervisorApprovalDialog approval = new SupervisorApprovalDialog(ex.getMessage());
                approval.setVisible(true);
                if (!approval.isApproved()) return;

                try {
                    VoucherController.getInstance().registerInvoiceWithAuthorization(
                            supplier.getId(), type, issueDate, details, ocId, approval.getAuthorization());
                    Alerts.info(this, "Factura registrada con autorización.");
                    dispose();
                } catch (EntityNotFoundException e) {
                    Alerts.error(this, e.getMessage());
                }

            } catch (EntityNotFoundException ex) {
                Alerts.error(this, ex.getMessage());
            }
        }
    }
}
