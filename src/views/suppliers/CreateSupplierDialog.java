package views.suppliers;

import controllers.SupplierController;
import models.enums.IVACondition;

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
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCuit = new JTextField();
        txtRazonSocial = new JTextField();
        txtFantasyName = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtIngresosBrutos = new JTextField();
        txtActivityStartDate = new JTextField();

        cmbIvaCondition = new JComboBox<>(IVACondition.values());
        applyPlaceholder(txtActivityStartDate, "yyyy-MM-dd");
        txtCreditLimit = new JTextField("0");

        Object[][] rows = {
            {"CUIT *", txtCuit},
            {"Razón Social *", txtRazonSocial},
            {"Nombre Fantasía *", txtFantasyName},
            {"Domicilio *", txtAddress},
            {"Teléfono *", txtPhone},
            {"Email", txtEmail},
            {"Condición IVA *", cmbIvaCondition},
            {"Ingresos Brutos *", txtIngresosBrutos},
            {"Inicio Actividades *", txtActivityStartDate},
            {"Tope Crédito *", txtCreditLimit}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(new JLabel((String) rows[i][0]), gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add((Component) rows[i][1], gbc);
        }

        add(new JScrollPane(form), BorderLayout.CENTER);
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
