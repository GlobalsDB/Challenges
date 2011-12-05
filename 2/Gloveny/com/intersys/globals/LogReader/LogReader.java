import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LogReader extends Frame {
    JButton Find;
    JTextArea textarea_1;
    JScrollPane sp_textarea_1;
    JButton ReadLog;
    JPanel mainPanel;

    public LogReader() {
        LogReaderLayout customLayout = new LogReaderLayout();

        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setLayout(customLayout);

        Find = new JButton("Find");
        add(Find);

        textarea_1 = new JTextArea("textarea_1");
        sp_textarea_1 = new JScrollPane(textarea_1);
        add(sp_textarea_1);

        ReadLog = new JButton("Read log");
        add(ReadLog);

        mainPanel = new JPanel();
        add(mainPanel);

        setSize(getPreferredSize());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String args[]) {
        LogReader window = new LogReader();

        window.setTitle("LogReader");
        window.pack();
        window.show();
    }
}

class LogReaderLayout implements LayoutManager {

    public LogReaderLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = 830 + insets.left + insets.right;
        dim.height = 566 + insets.top + insets.bottom;

        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        return dim;
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        Component c;
        c = parent.getComponent(0);
        if (c.isVisible()) {c.setBounds(insets.left+432,insets.top+32,176,56);}
        c = parent.getComponent(1);
        if (c.isVisible()) {c.setBounds(insets.left+16,insets.top+328,776,192);}
        c = parent.getComponent(2);
        if (c.isVisible()) {c.setBounds(insets.left+624,insets.top+32,168,56);}
        c = parent.getComponent(3);
        if (c.isVisible()) {c.setBounds(insets.left+8,insets.top+8,800,536);}
    }
}
