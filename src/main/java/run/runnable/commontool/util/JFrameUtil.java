package run.runnable.commontool.util;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public interface JFrameUtil {


    static void buildNotify(String tips){
        JFrame frame = new JFrame("Tips");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel(tips);
        frame.add(label);

        frame.setLayout(new java.awt.FlowLayout());
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    static JTextArea messageBox(String title){
        JTextArea textArea = new JTextArea(10, 20);
        JFrame frame = new JFrame(title);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // 设置自动滚动
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 将滚动窗格添加到框架中
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);
        return textArea;
    }

}
