package views;

import controllers.UserController;
import views.components.AppMainFrame;
import views.components.AppTabs;
import views.components.StatusBar;
import views.products.ProductFrame;
import views.purchaseorders.PurchaseOrderFrame;
import views.suppliers.SupplierFrame;
import views.users.SelectUserDialog;
import views.vouchers.VoucherFrame;

public class MainFrame extends AppMainFrame {

    public MainFrame() {
        super("Farmared - Sistema de Gestión de Compras", 900, 600);
    }

    public boolean requestLogin() {
        SelectUserDialog dialog = new SelectUserDialog();
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }

    public void init() {
        addSouth(new StatusBar("  Usuario: " + UserController.getInstance().getCurrentUser().toString()));

        AppTabs tabs = new AppTabs();
        tabs.addTab("Proveedores", new SupplierFrame());
        tabs.addTab("Productos", new ProductFrame());
        tabs.addTab("Órdenes de Compra", new PurchaseOrderFrame());
        tabs.addTab("Comprobantes", new VoucherFrame());
        addCenter(tabs);
    }
}
