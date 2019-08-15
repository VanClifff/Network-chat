package Old.Chat;

import javax.swing.*;
import java.awt.*;

public class TestFrontNative extends JFrame {

    public TestFrontNative() {
        super("Мое окно");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());


        JLabel label = new JLabel("<html>К этой метке применено \" +\n" +
                "\"HTML-форматирование, включая: <ul><li> <i>курсив</i>,\" +\n" +
                "\"<li><b>полужирный</b> <li><font size = +2> увеличение размера </font>\" +\n" +
                "\"<li>маркированный список </ul>");
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.add(new JButton("Кнопка с заголовком"), BorderLayout.NORTH);
        JPanel underground = new JPanel();
        mainPanel.add(underground, BorderLayout.SOUTH);
        underground.add(new JButton("hi"), BorderLayout.CENTER);

        setContentPane(mainPanel);
        setLocationRelativeTo(null);

        setSize(300, 200);
        setVisible(true);
    }
}
