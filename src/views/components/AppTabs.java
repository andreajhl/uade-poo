package views.components;

import javax.swing.*;
import java.awt.*;

public class AppTabs extends JTabbedPane {

    public AppTabs() {
        addChangeListener(e -> {
            Component selected = getSelectedComponent();
            if (selected instanceof AppFrame) {
                ((AppFrame) selected).refresh();
            }
        });
    }

    public void addTab(String title, Component content) {
        super.addTab(title, content);
    }
}
