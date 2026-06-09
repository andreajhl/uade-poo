package views.components;

import javax.swing.*;
import java.awt.*;

public class AppList<T> extends JPanel {

    private final DefaultListModel<T> model;
    private final JList<T> list;

    public AppList() {
        setLayout(new BorderLayout());
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void addElement(T element) {
        model.addElement(element);
    }

    public void removeElement(T element) {
        model.removeElement(element);
    }

    public T getSelectedValue() {
        return list.getSelectedValue();
    }
}
