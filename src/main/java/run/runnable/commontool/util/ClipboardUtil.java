package run.runnable.commontool.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 剪贴板实用程序
 *
 * @author asher
 * @date 2023/10/21
 */
public interface ClipboardUtil {

    static String getClipboardStringData() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String data = null;
        try {
            data = (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    static void saveClipboardStringData(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(content), null);
    }
}
