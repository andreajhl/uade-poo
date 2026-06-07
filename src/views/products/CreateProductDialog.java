package views.products;

import controllers.ProductController;
import models.Category;
import views.components.ButtonBar;
import views.components.FormPanel;

import javax.swing.*;
import java.awt.*;

public class CreateProductDialog extends JDialog {

    private JTextField txtCode;
    private JTextField txtDescription;
    private JTextField txtUnitOfMeasure;
    private JTextField txtIvaRate;
    private JTextField txtCategory;

    public CreateProductDialog(JFrame parent) {
        super(parent, "Nuevo Producto", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        initForm();
        initButtons();
    }

    private void initForm() {
        txtCode = new JTextField();
        txtDescription = new JTextField();
        txtUnitOfMeasure = new JTextField();
        txtIvaRate = new JTextField("21");
        txtCategory = new JTextField();

        FormPanel form = new FormPanel();
        form.addRow("Código *", txtCode);
        form.addRow("Descripción *", txtDescription);
        form.addRow("Unidad de Medida *", txtUnitOfMeasure);
        form.addRow("IVA % *", txtIvaRate);
        form.addRow("Rubro *", txtCategory);

        add(form, BorderLayout.CENTER);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        add(bar, BorderLayout.SOUTH);
    }

    private void save() {
        String code = txtCode.getText().trim();
        String description = txtDescription.getText().trim();
        String unitOfMeasure = txtUnitOfMeasure.getText().trim();
        String categoryName = txtCategory.getText().trim();

        if (code.isEmpty() || description.isEmpty() || unitOfMeasure.isEmpty() || categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (*).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float ivaRate;
        try {
            ivaRate = Float.parseFloat(txtIvaRate.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El IVA debe ser un número válido.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProductController.getInstance().create(code, description, unitOfMeasure, ivaRate, new Category(categoryName));
        JOptionPane.showMessageDialog(this, "Producto creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
