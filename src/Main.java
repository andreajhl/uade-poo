import views.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataLoader.load();
            MainFrame frame = new MainFrame();
            if (!frame.requestLogin()) {
                System.exit(0);
            }
            frame.init();
            frame.setVisible(true);
        });
    }
}
