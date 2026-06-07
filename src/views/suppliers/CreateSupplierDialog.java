package views.suppliers;

import controllers.SupplierController;
import models.enums.IVACondition;
import views.components.ButtonBar;
import views.components.FormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateSupplierDialog extends JDialog {

    private JTextField txtCuit;
    private JTextField txtRazonSocial;
    private JTextField txtFantasyName;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<IVACondition> cmbIvaCondition;
    private JTextField txtIngresosBrutos;
    private JTextField txtActivityStartDate;
    private JTextField txtCreditLimit;

    public CreateSupplierDialog(JFrame parent) {
        super(parent, "Nuevo Proveedor", true);
        setSize(450, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        initForm();
        initButtons();
    }

    private void initForm() {
        txtCuit = new JTextField();
        txtRazonSocial = new JTextField();
        txtFantasyName = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        cmbIvaCondition = new JComboBox<>(IVACondition.values());
        txtIngresosBrutos = new JTextField();
        txtActivityStartDate = new JTextField();
        applyPlaceholder(txtActivityStartDate, "yyyy-MM-dd");
        txtCreditLimit = new JTextField("0");

        FormPanel form = new FormPanel();
        form.addRow("CUIT *", txtCuit);
        form.addRow("Razón Social *", txtRazonSocial);
        form.addRow("Nombre Fantasía *", txtFantasyName);
        form.addRow("Domicilio *", txtAddress);
        form.addRow("Teléfono *", txtPhone);
        form.addRow("Email", txtEmail);
        form.addRow("Condición IVA *", cmbIvaCondition);
        form.addRow("Ingresos Brutos *", txtIngresosBrutos);
        form.addRow("Inicio Actividades *", txtActivityStartDate);
        form.addRow("Tope Crédito *", txtCreditLimit);

        add(new JScrollPane(form), BorderLayout.CENTER);
    }

    private void initButtons() {
        ButtonBar bar = new ButtonBar();
        bar.addButton("Cancelar", this::dispose);
        bar.addButton("Guardar", this::save);
        add(bar, BorderLayout.SOUTH);
    }

    private void save() {
        String cuit = txtCuit.getText().trim();
        String razonSocial = txtRazonSocial.getText().trim();
        String fantasyName = txtFantasyName.getText().trim();
        String address = txtAddress.getText().trim();
        String phone = txtPhone.getText().trim();
        String ingresosBrutos = txtIngresosBrutos.getText().trim();

        if (cuit.isEmpty() || razonSocial.isEmpty() || fantasyName.isEmpty()
                || address.isEmpty() || phone.isEmpty() || ingresosBrutos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (*).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate activityStartDate;
        try {
            String dateText = txtActivityStartDate.getText().trim();
            if (dateText.equals("yyyy-MM-dd") || dateText.isEmpty()) throw new DateTimeParseException("", "", 0);
            activityStartDate = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha de inicio inválida. Use el formato yyyy-MM-dd.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float creditLimit;
        try {
            creditLimit = Float.parseFloat(txtCreditLimit.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El tope de crédito debe ser un número válido.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SupplierController.getInstance().create(
                cuit, razonSocial, fantasyName, address, phone,
                txtEmail.getText().trim(),
                (IVACondition) cmbIvaCondition.getSelectedItem(),
                ingresosBrutos, activityStartDate, creditLimit
        );

        JOptionPane.showMessageDialog(this, "Proveedor creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void applyPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}
