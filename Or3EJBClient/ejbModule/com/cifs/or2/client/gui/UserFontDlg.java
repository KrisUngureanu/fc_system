package com.cifs.or2.client.gui;

import java.awt.*;
import javax.swing.*;

public class UserFontDlg extends JDialog {
    JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JComboBox jComboBox1 = new JComboBox();
    JComboBox jComboBox2 = new JComboBox();
    JPanel jPanel2 = new JPanel();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    GridLayout gridLayout1 = new GridLayout();
    JPanel jPanel3 = new JPanel();
    GridLayout gridLayout3 = new GridLayout();

    public UserFontDlg(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UserFontDlg() {
        this(null, "", false);
    }

    void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        jLabel1.setText("Шрифт по умолчанию:");
        jLabel2.setText("Фон по умолчанию:");
        jButton1.setText("ОК");
        jButton2.setText("Отмена");
        jPanel2.setLayout(gridLayout1);
        gridLayout1.setHgap(10);
        gridLayout1.setVgap(10);
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jPanel3.setLayout(gridLayout3);
        gridLayout3.setRows(5);
        gridLayout3.setColumns(10);
        getContentPane().add(panel1);
        panel1.add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(jPanel3, null);
        jPanel3.add(jLabel1, null);
        jPanel3.add(jComboBox1, null);
        jPanel3.add(jLabel2, null);
        jPanel3.add(jComboBox2, null);
        jPanel3.add(jPanel2, null);
        jPanel2.add(jButton1, null);
        jPanel2.add(jButton2, null);
    }
}