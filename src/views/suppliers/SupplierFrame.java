package views.suppliers;

import controllers.SupplierController;
import models.Supplier;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SupplierFrame extends JPanel {

    private final AppTable table;

    public SupplierFrame() {
        setLayout(new BorderLayout());
        table = new AppTable(new String[]{"CUIT", "Razón Social", "Nombre Fantasía", "Condición IVA", "Tope Crédito"});
        initToolbar();
        add(table, BorderLayout.CENTER);
        refresh();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nuevo Proveedor", this::openCreateDialog));
        add(toolbar, BorderLayout.NORTH);
    }

    private void openCreateDialog() {
        CreateSupplierDialog dialog = new CreateSupplierDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        table.clearRows();
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        for (Supplier s : suppliers) {
            table.addRow(new Object[]{
                s.getCuit(),
                s.getRazonSocial(),
                s.getFantasyName(),
                s.getIvaCondition(),
                String.format("$ %.2f", s.getCreditLimit())
            });
        }
    }
}
