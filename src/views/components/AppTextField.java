package views.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AppTextField extends JTextField {

    public AppTextField() {}

    public AppTextField(String defaultText) {
        super(defaultText);
    }

    public void onTextChanged(Runnable callback) {
        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { callback.run(); }
            public void removeUpdate(DocumentEvent e) { callback.run(); }
            public void changedUpdate(DocumentEvent e) { callback.run(); }
        });
    }
}
