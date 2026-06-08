package views.products;

import controllers.ProductController;
import models.Category;
import models.enums.TaxType;
import models.enums.UnitOfMeasure;
import views.components.ButtonBar;
import views.components.FormPanel;

import javax.swing.*;
import java.awt.*;

public class CreateProductDialog extends JDialog {

    private JTextField txtCode;
    private JTextField txtDescription;
    private JComboBox<UnitOfMeasure> cmbUnitOfMeasure;
    private JComboBox<TaxType> cmbTaxType;
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
        cmbUnitOfMeasure = new JComboBox<>(UnitOfMeasure.values());
        cmbTaxType = new JComboBox<>(TaxType.values());
        txtCategory = new JTextField();

        FormPanel form = new FormPanel();
        form.addRow("Código *", txtCode);
        form.addRow("Descripción *", txtDescription);
        form.addRow("Unidad de Medida *", cmbUnitOfMeasure);
        form.addRow("Tipo de IVA *", cmbTaxType);
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
        String categoryName = txtCategory.getText().trim();

        if (code.isEmpty() || description.isEmpty() || categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (*).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UnitOfMeasure unitOfMeasure = (UnitOfMeasure) cmbUnitOfMeasure.getSelectedItem();
        TaxType taxType = (TaxType) cmbTaxType.getSelectedItem();

        ProductController.getInstance().create(code, description, unitOfMeasure, taxType, new Category(categoryName));
        JOptionPane.showMessageDialog(this, "Producto creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
