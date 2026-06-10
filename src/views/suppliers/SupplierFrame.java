package views.suppliers;

import controllers.SupplierController;
import models.Supplier;
import views.components.AppButton;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class SupplierFrame extends AppFrame {

    private final AppTable table;
    private AppButton btnManageCategories;

    public SupplierFrame() {
        table = new AppTable(new String[]{"CUIT", "Razón Social", "Nombre Fantasía", "Condición IVA", "Tope Crédito"});
        table.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) btnManageCategories.setEnabled(table.getSelectedRow() >= 0);
        });
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        btnManageCategories = new AppButton("Gestionar Rubros", this::openManageCategories);
        btnManageCategories.setEnabled(false);

        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Proveedor", this::openCreateDialog));
        toolbar.add(btnManageCategories);
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateSupplierDialog dialog = new CreateSupplierDialog();
        dialog.setVisible(true);
        refresh();
    }

    private void openManageCategories() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        if (row >= suppliers.size()) return;
        Supplier supplier = suppliers.get(row);
        ManageCategoriesDialog dialog = new ManageCategoriesDialog(supplier);
        dialog.setVisible(true);
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
