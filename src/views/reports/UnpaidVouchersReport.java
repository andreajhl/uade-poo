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
import views.components.SectionPanel;

import java.util.List;

public class UnpaidVouchersReport extends AppFrame {

    private AppComboBox<Supplier> cmbSupplier;
    private AppTable table;
    private InfoLabel lblTotal;

    public UnpaidVouchersReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        cmbSupplier = new AppComboBox<>();
        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Proveedor:", cmbSupplier);
        form.addFullRow(ButtonBar.primary("Consultar", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{"N°", "Tipo", "Fecha", "Neto", "IVA", "Total", "Estado"});
        lblTotal = InfoLabel.highlight("Total impago: $ 0.00");

        BorderPanel south = new BorderPanel();
        south.addEast(lblTotal);

        SectionPanel section = new SectionPanel("Documentos Impagos");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    @Override
    public void refresh() {
        Supplier selected = cmbSupplier.getSelected();
        cmbSupplier.removeAllItems();

        for (Supplier s : SupplierController.getInstance().findAll()) cmbSupplier.addItem(s);

        if (selected != null) cmbSupplier.setSelectedItem(selected);
    }

    private void query() {
        Supplier supplier = cmbSupplier.getSelected();
        if (supplier == null) return;

        List<Voucher> unpaid = VoucherController.getInstance().findUnpaid(supplier.getId());

        table.clearRows();
        float total = 0f;

        for (Voucher v : unpaid) {
            table.addRow(new Object[]{
                v.getNumber(),
                v.getType().name(),
                v.getIssueDate(),
                String.format("$ %.2f", v.getNetTotal()),
                String.format("$ %.2f", v.getVatTotal()),
                String.format("$ %.2f", v.getGrossTotal()),
                v.getStatus().name()
            });

            total += v.getGrossTotal();
        }

        lblTotal.setText(String.format("Total impago: $ %.2f  (%d documentos)", total, unpaid.size()));
    }
}
