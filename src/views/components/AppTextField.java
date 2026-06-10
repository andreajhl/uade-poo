package views.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AppTextField extends JTextField {

    public AppTextField() {
        addSelectAllOnFocus();
    }

    public AppTextField(String defaultText) {
        super(defaultText);
        addSelectAllOnFocus();
    }

    private void addSelectAllOnFocus() {
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { selectAll(); }
        });
    }

    public void onTextChanged(Runnable callback) {
        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { callback.run(); }
            public void removeUpdate(DocumentEvent e) { callback.run(); }
            public void changedUpdate(DocumentEvent e) { callback.run(); }
        });
    }
}
