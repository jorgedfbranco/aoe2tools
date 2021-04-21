package infra;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class WindowsService {
    public static void setClipboard(String str) {
        Toolkit
            .getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(str), null);
    }

    public static boolean runWindowsCommand(String command) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + command);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
