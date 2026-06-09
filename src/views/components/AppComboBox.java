package views.components;

import javax.swing.*;

public class AppComboBox<T> extends JComboBox<T> {

    public AppComboBox() {}

    public AppComboBox(T[] items) {
        super(items);
    }

    @SuppressWarnings("unchecked")
    public T getSelected() {
        return (T) getSelectedItem();
    }

    public void onSelectionChanged(Runnable callback) {
        addActionListener(e -> callback.run());
    }
}
