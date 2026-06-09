package views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PlaceholderTextField extends JTextField {

    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        applyPlaceholder();
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    applyPlaceholder();
                }
            }
        });
    }

    private void applyPlaceholder() {
        setText(placeholder);
        setForeground(Color.GRAY);
    }

    public boolean hasValue() {
        return !getText().isEmpty() && !getText().equals(placeholder);
    }

    public String getValue() {
        return hasValue() ? getText().trim() : "";
    }
}
