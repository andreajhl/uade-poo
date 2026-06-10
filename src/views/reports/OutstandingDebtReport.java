package views.reports;

import controllers.SupplierController;
import models.Supplier;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.InfoLabel;
import views.components.SectionPanel;

import java.util.List;

public class OutstandingDebtReport extends AppFrame {

    private AppTable table;
    private InfoLabel lblTotal;

    public OutstandingDebtReport() {
        initTable();
        refresh();
    }

    private void initTable() {
        table = new AppTable(new String[]{"Proveedor", "CUIT", "Tope Crédito", "Deuda Total", "Disponible"});
        lblTotal = InfoLabel.highlight("Deuda total: $ 0.00");

        BorderPanel south = new BorderPanel();
        south.addWest(ButtonBar.primary("Actualizar", this::refresh));
        south.addEast(lblTotal);

        SectionPanel section = new SectionPanel("Deuda Vigente por Proveedor");
        section.addCenter(table);
        section.addSouth(south);

        addCenter(section);
    }

    public void refresh() {
        SupplierController ctrl = SupplierController.getInstance();
        List<Supplier> suppliers = ctrl.findAll();

        table.clearRows();
        float grandTotal = 0f;

        for (Supplier s : suppliers) {
            float debt = ctrl.getTotalDebt(s.getId());
            float available = s.getCreditLimit() - debt;

            table.addRow(new Object[]{
                s.getRazonSocial(),
                s.getCuit(),
                String.format("$ %.2f", s.getCreditLimit()),
                String.format("$ %.2f", debt),
                String.format("$ %.2f", available)
            });

            grandTotal += debt;
        }

        lblTotal.setText(String.format("Deuda total: $ %.2f", grandTotal));
    }
}
