package views.reports;

import controllers.PaymentOrderController;
import models.PaymentOrder;
import models.Retention;
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

public class RetentionsDetailReport extends AppFrame {

    private PlaceholderTextField txtFrom;
    private PlaceholderTextField txtTo;
    private AppTable table;
    private InfoLabel lblTotals;

    public RetentionsDetailReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        txtFrom = new PlaceholderTextField("yyyy-MM-dd");
        txtTo = new PlaceholderTextField("yyyy-MM-dd");

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Desde:", txtFrom);
        form.addRow("Hasta:", txtTo);
        form.addFullRow(ButtonBar.primary("Generar Reporte", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{
            "Fecha", "N° Orden", "Proveedor", "Tipo Retención", "Monto Bruto", "% Retención", "Monto Retenido"
        });
        lblTotals = InfoLabel.highlight("");

        BorderPanel south = new BorderPanel();
        south.addEast(lblTotals);

        SectionPanel section = new SectionPanel("Detalle de Retenciones por Impuesto");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    private void query() {
        LocalDate from = parseDate(txtFrom.getValue());
        LocalDate to = parseDate(txtTo.getValue());

        List<PaymentOrder> orders = PaymentOrderController.getInstance().findAll();

        table.clearRows();
        float totalRetained = 0f;
        float totalIVA = 0f;
        float totalIngresosBrutos = 0f;
        float totalGanancias = 0f;

        for (PaymentOrder order : orders) {
            // Filtrar por fecha
            if (from != null && order.getIssueDate().isBefore(from)) {
                continue;
            }
            if (to != null && order.getIssueDate().isAfter(to)) {
                continue;
            }

            for (Retention retention : order.getRetentions()) {
                float percentage = calculatePercentage(order, retention);

                table.addRow(new Object[]{
                    order.getIssueDate(),
                    order.getNumber(),
                    order.getSupplier().getRazonSocial(),
                    retention.getTaxType().name(),
                    String.format("$ %.2f", order.getTotalVouchersAmount()),
                    String.format("%.2f%%", percentage),
                    String.format("$ %.2f", retention.getAmount())
                });

                totalRetained += retention.getAmount();

                if (retention.getTaxType() == TaxType.IVA) {
                    totalIVA += retention.getAmount();
                } else if (retention.getTaxType() == TaxType.INGRESOS_BRUTOS) {
                    totalIngresosBrutos += retention.getAmount();
                } else if (retention.getTaxType() == TaxType.GANANCIAS) {
                    totalGanancias += retention.getAmount();
                }
            }
        }

        lblTotals.setText(String.format(
            "IVA: $ %.2f  |  Ingresos Brutos: $ %.2f  |  Ganancias: $ %.2f  |  Total: $ %.2f",
            totalIVA, totalIngresosBrutos, totalGanancias, totalRetained));
    }

    private float calculatePercentage(PaymentOrder order, Retention retention) {
        if (order.getTotalVouchersAmount() == 0) return 0f;
        return (retention.getAmount() / order.getTotalVouchersAmount()) * 100f;
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) return null;
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}