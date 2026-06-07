package views.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AppTable extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    public AppTable(String[] columns) {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void addRow(Object[] rowData) {
        model.addRow(rowData);
    }

    public void removeRow(int rowIndex) {
        model.removeRow(rowIndex);
    }

    public void clearRows() {
        model.setRowCount(0);
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public Object getValueAt(int row, int col) {
        return model.getValueAt(row, col);
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}
