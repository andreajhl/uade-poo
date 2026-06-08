package views.suppliers;

import controllers.SupplierController;
import exceptions.EntityNotFoundException;
import models.Category;
import models.Supplier;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;

public class ManageCategoriesDialog extends JDialog {

    private final Supplier supplier;
    private DefaultListModel<Category> listModel;
    private JList<Category> categoryList;
    private JTextField txtNewCategory;

    public ManageCategoriesDialog(JFrame parent, Supplier supplier) {
        super(parent, "Rubros de " + supplier.getRazonSocial(), true);
        this.supplier = supplier;
        initComponents();
        setSize(380, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        listModel = new DefaultListModel<>();
        for (Category c : supplier.getCategories()) listModel.addElement(c);

        categoryList = new JList<>(listModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        txtNewCategory = new JTextField();

        JPanel addPanel = new JPanel(new BorderLayout(5, 0));
        addPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        addPanel.add(new JLabel("Nuevo rubro:"), BorderLayout.WEST);
        addPanel.add(txtNewCategory, BorderLayout.CENTER);
        addPanel.add(ButtonBar.primary("Agregar", this::addCategory), BorderLayout.EAST);

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBorder(BorderFactory.createTitledBorder("Rubros asociados"));
        listPanel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        listPanel.add(ButtonBar.danger("Quitar seleccionado", this::removeCategory), BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(5, 8));
        center.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        center.add(addPanel, BorderLayout.NORTH);
        center.add(listPanel, BorderLayout.CENTER);

        ButtonBar bar = new ButtonBar();
        bar.addButton("Cerrar", this::dispose);

        setLayout(new BorderLayout());
        add(center, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
    }

    private void addCategory() {
        String name = txtNewCategory.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresá el nombre del rubro.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category category = new Category(name);

        try {
            SupplierController.getInstance().addCategory(supplier.getId(), category);
            listModel.addElement(category);
            txtNewCategory.setText("");
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeCategory() {
        Category selected = categoryList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un rubro para quitar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SupplierController.getInstance().removeCategory(supplier.getId(), selected);
            listModel.removeElement(selected);
        } catch (EntityNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
