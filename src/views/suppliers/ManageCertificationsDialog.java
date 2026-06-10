package views.suppliers;

import controllers.SupplierController;
import models.CertificationRetention;
import models.Supplier;
import models.enums.TaxType;
import views.components.Alerts;
import views.components.AppComboBox;
import views.components.AppDialog;
import views.components.AppTable;
import views.components.BorderPanel;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.PlaceholderTextField;
import views.components.SectionPanel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ManageCertificationsDialog extends AppDialog {

    private final Supplier supplier;
    private AppTable table;
    private AppComboBox<TaxType> cmbTaxType;
    private PlaceholderTextField txtIssueDate;
    private PlaceholderTextField txtExpirationDate;

    public ManageCertificationsDialog(Supplier supplier) {
        super("Certificados de No Retención — " + supplier.getRazonSocial(), 620, 480);
        this.supplier = supplier;
        initTable();
        initForm();
        populateTable();
    }

    private void initTable() {
        table = new AppTable(new String[]{"Impuesto", "Fecha Emisión", "Fecha Vencimiento", "Estado"});

        SectionPanel section = new SectionPanel("Certificados registrados");
        section.addCenter(table);
        addCenter(section);
    }

    private void initForm() {
        cmbTaxType = new AppComboBox<>();
        for (TaxType t : TaxType.values()) cmbTaxType.addItem(t);

        txtIssueDate = new PlaceholderTextField("yyyy-MM-dd");
        txtExpirationDate = new PlaceholderTextField("yyyy-MM-dd");

        FormPanel form = new FormPanel("Agregar certificado");
        form.addRow("Impuesto:", cmbTaxType);
        form.addRow("Fecha emisión:", txtIssueDate);
        form.addRow("Fecha vencimiento:", txtExpirationDate);

        BorderPanel south = new BorderPanel();
        south.addCenter(form);
        south.addSouth(ButtonBar.primary("Agregar", this::addCertification));
        addSouth(south);
    }

    private void populateTable() {
        table.clearRows();
        LocalDate today = LocalDate.now();

        for (CertificationRetention c : supplier.getCertifications()) {
            String status = c.isValid(today) ? "Vigente" : "Vencido";

            table.addRow(new Object[]{
                c.getTaxType().name(),
                c.getIssueDate(),
                c.getExpirationDate(),
                status
            });
        }
    }

    private void addCertification() {
        TaxType taxType = cmbTaxType.getSelected();
        LocalDate issueDate = parseDate(txtIssueDate.getValue());
        LocalDate expirationDate = parseDate(txtExpirationDate.getValue());

        if (taxType == null) {
            Alerts.warn(this, "Seleccioná un tipo de impuesto.");
            return;
        }

        if (issueDate == null) {
            Alerts.warn(this, "La fecha de emisión es inválida. Formato esperado: yyyy-MM-dd");
            return;
        }

        if (expirationDate == null) {
            Alerts.warn(this, "La fecha de vencimiento es inválida. Formato esperado: yyyy-MM-dd");
            return;
        }

        if (!expirationDate.isAfter(issueDate)) {
            Alerts.warn(this, "La fecha de vencimiento debe ser posterior a la fecha de emisión.");
            return;
        }

        try {
            SupplierController.getInstance().addCertification(
                supplier.getId(),
                new CertificationRetention(taxType, issueDate, expirationDate)
            );

            txtIssueDate.setText("");
            txtExpirationDate.setText("");
            populateTable();
        } catch (Exception e) {
            Alerts.error(this, "No se pudo agregar el certificado: " + e.getMessage());
        }
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;
        try { return LocalDate.parse(text); } catch (DateTimeParseException e) { return null; }
    }
}
