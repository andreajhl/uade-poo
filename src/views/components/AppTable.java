package views.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
    
    public int getRowCount() {
    return table.getRowCount();
    }

    public Object getValueAt(int row, int col) {
        return model.getValueAt(row, col);
    }

    public void onRowSelected(Runnable callback) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) callback.run();
        });
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getModel() {
        return model;
    }
    
    
}

