package views.reports;

import controllers.VoucherController;
import models.Voucher;
import models.VoucherDetail;
import models.enums.TaxType;
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

public class IVABookReport extends AppFrame {

    private PlaceholderTextField txtFrom;
    private PlaceholderTextField txtTo;
    private AppTable table;
    private InfoLabel lblTotals;

    public IVABookReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        txtFrom = new PlaceholderTextField("yyyy-MM-dd");
        txtTo   = new PlaceholderTextField("yyyy-MM-dd");

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Desde:", txtFrom);
        form.addRow("Hasta:", txtTo);
        form.addFullRow(ButtonBar.primary("Generar Libro", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{
            "CUIT", "Razón Social", "Fecha", "Tipo",
            "Base 21%", "IVA 21%", "Base 10.5%", "IVA 10.5%", "Exento", "Total"
        });
        lblTotals = InfoLabel.highlight("");

        BorderPanel south = new BorderPanel();
        south.addEast(lblTotals);

        SectionPanel section = new SectionPanel("Libro IVA Compras");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    private void query() {
        LocalDate from = parseDate(txtFrom.getValue());
        LocalDate to   = parseDate(txtTo.getValue());

        List<Voucher> invoices = VoucherController.getInstance().findInvoices(null, from, to);

        table.clearRows();
        float totalIva21 = 0f, totalIva105 = 0f, grandTotal = 0f;

        for (Voucher v : invoices) {
            float base21 = 0f, iva21 = 0f;
            float base105 = 0f, iva105 = 0f;
            float exento = 0f;

            for (VoucherDetail d : v.getDetails()) {
                TaxType tax = d.getProduct().getTaxType();
                if (tax == TaxType.IVA) {
                    base21 += d.getNetAmount();
                    iva21  += d.getVatAmount();
                } else if (tax == TaxType.INGRESOS_BRUTOS) {
                    base105 += d.getNetAmount();
                    iva105  += d.getVatAmount();
                } else {
                    exento += d.getNetAmount();
                }
            }

            float rowTotal = base21 + iva21 + base105 + iva105 + exento;

            table.addRow(new Object[]{
                v.getSupplier().getCuit(),
                v.getSupplier().getRazonSocial(),
                v.getIssueDate(),
                v.getType().name(),
                fmt(base21), fmt(iva21),
                fmt(base105), fmt(iva105),
                fmt(exento),
                fmt(rowTotal)
            });

            totalIva21  += iva21;
            totalIva105 += iva105;
            grandTotal  += rowTotal;
        }

        lblTotals.setText(String.format(
            "IVA 21%%: $ %.2f  |  IVA 10.5%%: $ %.2f  |  Total: $ %.2f",
            totalIva21, totalIva105, grandTotal));
    }

    private String fmt(float v) { return String.format("$ %.2f", v); }

    private LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) return null;
        try { return LocalDate.parse(text); } catch (DateTimeParseException e) { return null; }
    }
}
