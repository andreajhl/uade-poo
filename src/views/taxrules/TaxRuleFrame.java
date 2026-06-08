package views.taxrules;

import controllers.TaxRuleController;
import models.TaxRule;
import models.TaxScale;
import views.components.AppTable;
import views.components.ButtonBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TaxRuleFrame extends JPanel {

    private AppTable ruleTable;
    private AppTable scaleTable;

    public TaxRuleFrame() {
        setLayout(new BorderLayout(5, 5));
        initToolbar();
        initTables();
        refresh();
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(ButtonBar.primary("Nueva Regla", this::openCreateDialog));
        add(toolbar, BorderLayout.NORTH);
    }

    private void initTables() {
        ruleTable = new AppTable(new String[]{"Tipo Impuesto", "% Defecto", "Mínimo Imponible"});
        ruleTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshScales();
        });

        scaleTable = new AppTable(new String[]{"Desde $", "Hasta $", "Porcentaje %"});

        JPanel rulesPanel = new JPanel(new BorderLayout());
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Reglas impositivas"));
        rulesPanel.add(ruleTable, BorderLayout.CENTER);

        JPanel scalesPanel = new JPanel(new BorderLayout());
        scalesPanel.setBorder(BorderFactory.createTitledBorder("Escalas de la regla seleccionada"));
        scalesPanel.add(scaleTable, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rulesPanel, scalesPanel);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);
    }

    private void openCreateDialog() {
        CreateTaxRuleDialog dialog = new CreateTaxRuleDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        ruleTable.clearRows();
        scaleTable.clearRows();

        List<TaxRule> rules = TaxRuleController.getInstance().getAllTaxRules();
        for (TaxRule r : rules) {
            ruleTable.addRow(new Object[]{
                r.getTaxType().name(),
                String.format("%.2f%%", r.getDefaultPercentage()),
                String.format("$ %.2f", r.getMinimumTaxableAmount())
            });
        }
    }

    private void refreshScales() {
        scaleTable.clearRows();
        int row = ruleTable.getSelectedRow();

        if (row < 0) return;

        List<TaxRule> rules = TaxRuleController.getInstance().getAllTaxRules();

        if (row >= rules.size()) return;

        TaxRule selected = rules.get(row);
        for (TaxScale s : selected.getScales()) {
            scaleTable.addRow(new Object[]{
                String.format("$ %.2f", s.getFromAmount()),
                String.format("$ %.2f", s.getToAmount()),
                String.format("%.2f%%", s.getPercentage())
            });
        }
    }
}
