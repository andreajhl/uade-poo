package views.vouchers;

import controllers.VoucherController;
import models.Voucher;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.ToolbarPanel;

import java.util.List;

public class VoucherFrame extends AppFrame {

    private final AppTable table;

    public VoucherFrame() {
        table = new AppTable(new String[]{"N°", "Tipo", "Proveedor", "Fecha", "Neto", "IVA", "Total", "Estado"});
        initToolbar();
        addCenter(table);
        refresh();
    }

    private void initToolbar() {
        ToolbarPanel toolbar = new ToolbarPanel();
        toolbar.add(ButtonBar.primary("Nuevo Comprobante", this::openCreateDialog));
        addNorth(toolbar);
    }

    private void openCreateDialog() {
        CreateVoucherDialog dialog = new CreateVoucherDialog();
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        table.clearRows();
        List<Voucher> vouchers = VoucherController.getInstance().findAll();
        for (Voucher v : vouchers) {
            table.addRow(new Object[]{
                v.getNumber(),
                v.getType().name(),
                v.getSupplier().getRazonSocial(),
                v.getIssueDate(),
                String.format("$ %.2f", v.getNetTotal()),
                String.format("$ %.2f", v.getVatTotal()),
                String.format("$ %.2f", v.getGrossTotal()),
                v.getStatus().name()
            });
        }
    }
}
