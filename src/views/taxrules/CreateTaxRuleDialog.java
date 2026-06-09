package views.taxrules;

import controllers.TaxRuleController;
import models.TaxRule;
import models.TaxScale;
import models.enums.TaxType;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.AppTextField;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;

public class CreateTaxRuleDialog extends AppDialog {

    private AppComboBox<TaxType> cmbTaxType;
    private AppTextField txtDefaultPercentage;
    private AppTextField txtMinimumTaxableAmount;
    private AppTextField txtFromAmount;
    private AppTextField txtToAmount;
    private AppTextField txtScalePercentage;
    private AppTable scaleTable;
    private TaxRule createdRule;

    public CreateTaxRuleDialog() {
        super("Nueva Regla Impositiva", 500, 480);
        initRuleForm();
        initScalePanel();
        initButtons();
    }

    private void initRuleForm() {
        cmbTaxType = new AppComboBox<>(TaxType.values());
        txtDefaultPercentage = new AppTextField();
        txtMinimumTaxableAmount = new AppTextField("0");

        FormPanel form = new FormPanel("Regla");
        form.addRow("Tipo de impuesto *", cmbTaxType);
        form.addRow("Porcentaje por defecto *", txtDefaultPercentage);
        form.addRow("Monto mínimo imponible", txtMinimumTaxableAmount);

        addNorth(form);
    }

    private void initScalePanel() {
        txtFromAmount = new AppTextField();
        txtToAmount = new AppTextField();
        txtScalePercentage = new AppTextField();

        FormPanel scaleForm = new FormPanel("Escalas (opcional)");
        scaleForm.addRow("Desde $", txtFromAmount);
        scaleForm.addRow("Hasta $", txtToAmount);
        scaleForm.addRow("Porcentaje %", txtScalePercentage);
        scaleForm.addFullRow(ButtonBar.primary("Agregar escala", this::addScale));

        scaleTable = new AppTable(new String[]{"Desde $", "Hasta $", "Porcentaje %"});

        BorderPanel scalePanel = new BorderPanel(5, 5);
        scalePanel.addNorth(scaleForm);
        scalePanel.addCenter(scaleTable);

        addCenter(scalePanel);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        addSouth(bar);
    }

    private void addScale() {
        float from, to, percentage;
        try {
            from = Float.parseFloat(txtFromAmount.getText().trim());
            to = Float.parseFloat(txtToAmount.getText().trim());
            percentage = Float.parseFloat(txtScalePercentage.getText().trim());
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "Los valores de escala deben ser números válidos.");
            return;
        }
        if (from >= to) {
            Alerts.warn(this, "El monto 'Desde' debe ser menor al monto 'Hasta'.");
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
        TaxType taxType = cmbTaxType.getSelected();
        float defaultPercentage, minimumTaxableAmount;
        try {
            defaultPercentage = Float.parseFloat(txtDefaultPercentage.getText().trim());
            minimumTaxableAmount = Float.parseFloat(txtMinimumTaxableAmount.getText().trim());
        } catch (NumberFormatException ex) {
            Alerts.warn(this, "Los porcentajes y montos deben ser números válidos.");
            return;
        }

        createdRule = TaxRuleController.getInstance().createTaxRule(taxType, defaultPercentage, minimumTaxableAmount);

        for (int i = 0; i < scaleTable.getModel().getRowCount(); i++) {
            float from = parseAmount((String) scaleTable.getValueAt(i, 0));
            float to = parseAmount((String) scaleTable.getValueAt(i, 1));
            float pct = parsePercentage((String) scaleTable.getValueAt(i, 2));
            createdRule.addScale(new TaxScale(from, to, pct));
        }

        Alerts.info(this, "Regla impositiva creada correctamente.");
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
