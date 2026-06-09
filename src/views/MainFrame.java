package views;

import controllers.UserController;
import java.awt.*;
import javax.swing.*;
import views.paymentorders.PaymentOrderFrame;
import views.products.ProductFrame;
import views.purchaseorders.PurchaseOrderFrame;
import views.suppliers.SupplierFrame;
import views.users.SelectUserDialog;

public class MainFrame extends JFrame {

    private JLabel lblCurrentUser;

    public MainFrame() {
        setTitle("Farmared - Sistema de Gestión de Compras");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public boolean requestLogin() {
        SelectUserDialog dialog = new SelectUserDialog(this);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }

    public void init() {
        initStatusBar();
        initTabs();
    }

    private void initStatusBar() {
        lblCurrentUser = new JLabel("  Usuario: " + UserController.getInstance().getCurrentUser().toString());
        lblCurrentUser.setFont(lblCurrentUser.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusBar.add(lblCurrentUser);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void initTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Proveedores", new SupplierFrame());
        tabs.addTab("Productos", new ProductFrame());
        tabs.addTab("Órdenes de Compra", new PurchaseOrderFrame());
        tabs.addTab("Órdenes de Pago", new PaymentOrderFrame());
        add(tabs, BorderLayout.CENTER);
    }
}
