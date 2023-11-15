package run.runnable.commontool.util;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 * Clipboard tools
 *
 * @author asher
 * @date 2023/10/21
 */
public interface ClipboardUtil {

    /**
     * Get clipboard contents
     *
     * @return {@link String}
     */
    @SneakyThrows
    static String getClipboardStringData() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }

    /**
     * Save text to clipboard
     *
     * @param content content
     */
    static void saveClipboardStringData(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(content), null);
    }
}
