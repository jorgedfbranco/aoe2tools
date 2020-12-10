package infra;

import java.net.URL;

public class Browser {
    public static void open(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
