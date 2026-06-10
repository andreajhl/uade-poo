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
    private AppButton btnEdit;

    public SupplierFrame() {
        table = new AppTable(new String[]{"CUIT", "Razón Social", "Condición IVA", "Tope Crédito", "Deuda Total", "Cant. OCs", "Rubros"});
        table.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) btnEdit.setEnabled(table.getSelectedRow() >= 0);
        });
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        btnEdit = new AppButton("Editar", this::openEditDialog);
        btnEdit.setEnabled(false);

        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Proveedor", this::openCreateDialog));
        toolbar.add(btnEdit);
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateSupplierDialog dialog = new CreateSupplierDialog();
        dialog.setVisible(true);
        refresh();
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        if (row >= suppliers.size()) return;
        EditSupplierDialog dialog = new EditSupplierDialog(suppliers.get(row));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        table.clearRows();
        List<Supplier> suppliers = SupplierController.getInstance().findAll();
        SupplierController ctrl = SupplierController.getInstance();
        for (Supplier s : suppliers) {
            table.addRow(new Object[]{
                s.getCuit(),
                s.getRazonSocial(),
                s.getIvaCondition(),
                String.format("$ %.2f", s.getCreditLimit()),
                String.format("$ %.2f", ctrl.getTotalDebt(s.getId())),
                ctrl.getPurchaseCount(s.getId()),
                s.getCategories().toString()
            });
        }
    }
}
