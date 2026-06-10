package views.reports;

import controllers.PaymentOrderController;
import controllers.SupplierController;
import models.PaymentOrder;
import models.Supplier;
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

public class PaymentOrdersSummaryReport extends AppFrame {

    private AppComboBox<Supplier> cmbSupplier;
    private PlaceholderTextField txtFrom;
    private PlaceholderTextField txtTo;
    private AppTable table;
    private InfoLabel lblTotals;

    public PaymentOrdersSummaryReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        cmbSupplier = new AppComboBox<>();
        cmbSupplier.addItem(null);

        for (Supplier s : SupplierController.getInstance().findAll()) {
            cmbSupplier.addItem(s);
        }

        txtFrom = new PlaceholderTextField("yyyy-MM-dd");
        txtTo = new PlaceholderTextField("yyyy-MM-dd");

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Proveedor:", cmbSupplier);
        form.addRow("Desde:", txtFrom);
        form.addRow("Hasta:", txtTo);
        form.addFullRow(ButtonBar.primary("Consultar", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{
            "N° Orden", "Fecha", "Proveedor", "Total Facturas", "Retenciones", "Neto Pagado", "Estado"
        });
        lblTotals = InfoLabel.highlight("Total: $ 0.00  |  Retenciones: $ 0.00");

        BorderPanel south = new BorderPanel();
        south.addEast(lblTotals);

        SectionPanel section = new SectionPanel("Órdenes de Pago");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    @Override
    public void refresh() {
        Supplier selected = cmbSupplier.getSelected();
        cmbSupplier.removeAllItems();
        cmbSupplier.addItem(null);

        for (Supplier s : SupplierController.getInstance().findAll()) {
            cmbSupplier.addItem(s);
        }

        if (selected != null) {
            cmbSupplier.setSelectedItem(selected);
        }
    }

    private void query() {
        LocalDate from = parseDate(txtFrom.getValue());
        LocalDate to = parseDate(txtTo.getValue());
        Supplier supplier = cmbSupplier.getSelected();

        List<PaymentOrder> orders = PaymentOrderController.getInstance().findAll();

        table.clearRows();
        float totalPayments = 0f;
        float totalRetentions = 0f;
        int count = 0;

        for (PaymentOrder order : orders) {
            // Filtrar por proveedor si está seleccionado
            if (supplier != null && !order.getSupplier().getId().equals(supplier.getId())) {
                continue;
            }

            // Filtrar por fecha
            if (from != null && order.getIssueDate().isBefore(from)) {
                continue;
            }
            if (to != null && order.getIssueDate().isAfter(to)) {
                continue;
            }

            table.addRow(new Object[]{
                order.getNumber(),
                order.getIssueDate(),
                order.getSupplier().getRazonSocial(),
                String.format("$ %.2f", order.getTotalVouchersAmount()),
                String.format("$ %.2f", order.getTotalRetained()),
                String.format("$ %.2f", order.getNetAmount()),
                order.getStatus().name()
            });

            totalPayments += order.getTotalVouchersAmount();
            totalRetentions += order.getTotalRetained();
            count++;
        }

        lblTotals.setText(String.format(
            "Total: $ %.2f  |  Retenciones: $ %.2f  (%d órdenes)",
            totalPayments, totalRetentions, count));
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