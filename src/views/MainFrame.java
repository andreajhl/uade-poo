package views;

import views.products.ProductFrame;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Farmared - Sistema de Gestión de Compras");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initTabs();
    }

    private void initTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Productos", new ProductFrame());
        add(tabs);
    }
}
