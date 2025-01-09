package kz.tamur.rt;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class UserAbout extends JDialog implements ActionListener {

    JPanel contentPanel = new JPanel(new BorderLayout());
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    ImageIcon im;
    JLabel imLabel = new JLabel();
    JPanel anime= new JPanel();
    JButton okBtn = new JButton("Ok");

    public UserAbout(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        pack();
    }
    /**Component initialization*/
    private void jbInit() throws Exception {
        this.setTitle("О программе");
        setResizable(false);
        okBtn.addActionListener(this);
        buttonPanel.add(anime);
        buttonPanel.add(okBtn);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        URL url = MainFrame.class.getResource("/HelpMenu/about.jpg");
        if (url != null) {
            im = new ImageIcon(url);
            imLabel.setIcon(im);
        }
        contentPanel.add(imLabel, BorderLayout.CENTER);
        this.getContentPane().add(contentPanel, null);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    void cancel() {
        dispose();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okBtn) {
            cancel();
        }
    }
}