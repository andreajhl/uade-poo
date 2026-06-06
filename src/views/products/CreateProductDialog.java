package views.products;

import controllers.ProductController;
import models.Category;

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
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCode = new JTextField();
        txtDescription = new JTextField();
        txtUnitOfMeasure = new JTextField();
        txtIvaRate = new JTextField("21");
        txtCategory = new JTextField();

        Object[][] rows = {
            {"Código *", txtCode},
            {"Descripción *", txtDescription},
            {"Unidad de Medida *", txtUnitOfMeasure},
            {"IVA % *", txtIvaRate},
            {"Rubro *", txtCategory}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            form.add(new JLabel((String) rows[i][0]), gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add((Component) rows[i][1], gbc);
        }

        add(form, BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        buttons.add(btnCancel);
        buttons.add(btnSave);
        
        add(buttons, BorderLayout.SOUTH);
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

        Category category = new Category(categoryName);
        ProductController.getInstance().create(code, description, unitOfMeasure, ivaRate, category);

        JOptionPane.showMessageDialog(this, "Producto creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
