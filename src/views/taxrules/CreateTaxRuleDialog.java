package views.taxrules;

import controllers.TaxRuleController;
import models.TaxRule;
import models.TaxScale;
import models.enums.TaxType;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.FormPanel;

import javax.swing.*;
import java.awt.*;

public class CreateTaxRuleDialog extends JDialog {

    private JComboBox<TaxType> cmbTaxType;
    private JTextField txtDefaultPercentage;
    private JTextField txtMinimumTaxableAmount;
    private JTextField txtFromAmount;
    private JTextField txtToAmount;
    private JTextField txtScalePercentage;
    private AppTable scaleTable;
    private TaxRule createdRule;

    public CreateTaxRuleDialog(JFrame parent) {
        super(parent, "Nueva Regla Impositiva", true);
        setSize(500, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));
        initRuleForm();
        initScalePanel();
        initButtons();
    }

    private void initRuleForm() {
        cmbTaxType = new JComboBox<>(TaxType.values());
        txtDefaultPercentage = new JTextField();
        txtMinimumTaxableAmount = new JTextField("0");

        FormPanel form = new FormPanel();
        form.addRow("Tipo de impuesto *", cmbTaxType);
        form.addRow("Porcentaje por defecto *", txtDefaultPercentage);
        form.addRow("Monto mínimo imponible", txtMinimumTaxableAmount);
        form.setBorder(BorderFactory.createTitledBorder("Regla"));

        add(form, BorderLayout.NORTH);
    }

    private void initScalePanel() {
        txtFromAmount = new JTextField();
        txtToAmount = new JTextField();
        txtScalePercentage = new JTextField();

        FormPanel scaleForm = new FormPanel();
        scaleForm.addRow("Desde $", txtFromAmount);
        scaleForm.addRow("Hasta $", txtToAmount);
        scaleForm.addRow("Porcentaje %", txtScalePercentage);

        JButton btnAddScale = ButtonBar.primary("Agregar escala", this::addScale);

        JPanel scaleInputPanel = new JPanel(new BorderLayout(5, 5));
        scaleInputPanel.add(scaleForm, BorderLayout.CENTER);
        scaleInputPanel.add(btnAddScale, BorderLayout.SOUTH);
        scaleInputPanel.setBorder(BorderFactory.createTitledBorder("Escalas (opcional)"));

        scaleTable = new AppTable(new String[]{"Desde $", "Hasta $", "Porcentaje %"});

        JPanel scalePanel = new JPanel(new BorderLayout(5, 5));
        scalePanel.add(scaleInputPanel, BorderLayout.NORTH);
        scalePanel.add(scaleTable, BorderLayout.CENTER);

        add(scalePanel, BorderLayout.CENTER);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();

        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);

        add(bar, BorderLayout.SOUTH);
    }

    private void addScale() {
        float from, to, percentage;

        try {
            from = Float.parseFloat(txtFromAmount.getText().trim());
            to = Float.parseFloat(txtToAmount.getText().trim());
            percentage = Float.parseFloat(txtScalePercentage.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los valores de escala deben ser números válidos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (from >= to) {
            JOptionPane.showMessageDialog(this, "El monto 'Desde' debe ser menor al monto 'Hasta'.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scaleTable.addRow(new Object[]{
            String.format("$ %.2f", from),
            String.format("$ %.2f", to),
            String.format("%.2f%%", percentage)
        });

        txtFromAmount.setText("");
        txtToAmount.setText("");
        txtScalePercentage.setText("");
    }

    private void save() {
        TaxType taxType = (TaxType) cmbTaxType.getSelectedItem();
        float defaultPercentage, minimumTaxableAmount;

        try {
            defaultPercentage = Float.parseFloat(txtDefaultPercentage.getText().trim());
            minimumTaxableAmount = Float.parseFloat(txtMinimumTaxableAmount.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los porcentajes y montos deben ser números válidos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        createdRule = TaxRuleController.getInstance().createTaxRule(taxType, defaultPercentage, minimumTaxableAmount);

        for (int i = 0; i < scaleTable.getModel().getRowCount(); i++) {
            float from = parseAmount((String) scaleTable.getValueAt(i, 0));
            float to = parseAmount((String) scaleTable.getValueAt(i, 1));
            float pct = parsePercentage((String) scaleTable.getValueAt(i, 2));
            createdRule.addScale(new TaxScale(from, to, pct));
        }

        JOptionPane.showMessageDialog(this, "Regla impositiva creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private float parseAmount(String value) {
        return Float.parseFloat(value.replace("$", "").replace(",", "").trim());
    }

    private float parsePercentage(String value) {
        return Float.parseFloat(value.replace("%", "").trim());
    }

    public TaxRule getCreatedRule() {
        return createdRule;
    }
}
