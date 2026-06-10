package views.reports;

import controllers.SupplierController;
import controllers.VoucherController;
import models.Supplier;
import models.Voucher;
import views.components.AppComboBox;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.InfoLabel;
import views.components.PlaceholderTextField;
import views.components.SectionPanel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class InvoicesByDateReport extends AppFrame {

    private AppComboBox<Supplier> cmbSupplier;
    private PlaceholderTextField txtFrom;
    private PlaceholderTextField txtTo;
    private AppTable table;
    private InfoLabel lblTotal;

    public InvoicesByDateReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        cmbSupplier = new AppComboBox<>();
        cmbSupplier.addItem(null);

        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);

        txtFrom = new PlaceholderTextField("yyyy-MM-dd");
        txtTo   = new PlaceholderTextField("yyyy-MM-dd");

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Proveedor:", cmbSupplier);
        form.addRow("Desde:", txtFrom);
        form.addRow("Hasta:", txtTo);
        form.addFullRow(ButtonBar.primary("Consultar", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{"Fecha", "Proveedor", "N°", "Tipo", "Neto", "IVA", "Total"});
        lblTotal = InfoLabel.highlight("Total: $ 0.00");

        BorderPanel south = new BorderPanel();
        south.addEast(lblTotal);

        SectionPanel section = new SectionPanel("Resultados");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    @Override
    public void refresh() {
        Supplier selected = cmbSupplier.getSelected();
        cmbSupplier.removeAllItems();
        cmbSupplier.addItem(null);

        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);

        if (selected != null) cmbSupplier.setSelectedItem(selected);
    }

    private void query() {
        LocalDate from = parseDate(txtFrom.getValue());
        LocalDate to   = parseDate(txtTo.getValue());
        Supplier supplier = cmbSupplier.getSelected();

        List<Voucher> invoices = VoucherController.getInstance()
                .findInvoices(supplier != null ? supplier.getId() : null, from, to);

        table.clearRows();
        float grandTotal = 0f;
        for (Voucher v : invoices) {
            table.addRow(new Object[]{
                v.getIssueDate(),
                v.getSupplier().getRazonSocial(),
                v.getNumber(),
                v.getType().name(),
                String.format("$ %.2f", v.getNetTotal()),
                String.format("$ %.2f", v.getVatTotal()),
                String.format("$ %.2f", v.getGrossTotal())
            });
            grandTotal += v.getGrossTotal();
        }
        lblTotal.setText(String.format("Total: $ %.2f  (%d facturas)", grandTotal, invoices.size()));
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) return null;
        try { return LocalDate.parse(text); } catch (DateTimeParseException e) { return null; }
    }
}
