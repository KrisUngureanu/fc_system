package com.cifs.or2.client;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 20.02.2004
 * Time: 17:23:04
 * To change this template use Options | File Templates.
 */
public class SearchAndReplaceDialog extends JDialog implements ActionListener,
        DocumentListener {

    public static int REPLACE_RESULT = 0;
    public static int REPLACE_ALL_RESULT = 1;
    public static int CANCEL_RESULT = 2;

    public int result;
    public String find;
    public String replace;

    ImageIcon imOk =
            new ImageIcon(
                    SearchAndReplaceDialog.class.getResource(
                            "gui/images/replace.gif"));
    ImageIcon imAll =
            new ImageIcon(
                    SearchAndReplaceDialog.class.getResource(
                            "gui/images/replaceAll.gif"));

    ImageIcon imCancel =
            new ImageIcon(
                    SearchAndReplaceDialog.class.getResource(
                            "gui/images/cancel_.gif"));

    ImageIcon imLab =
            new ImageIcon(
                    SearchAndReplaceDialog.class.getResource(
                            "gui/images/replaceBig.gif"));

    JButton replBut = new JButton("Заменить");
    JButton replAllBut = new JButton("Заменить всё");
    JButton cancelBut = new JButton("Отмена");
    JLabel sourceLab  = new JLabel("             Найти: ");
    JLabel replaceLab = new JLabel("Заменить на: ");
    JTextField sourceText = new JTextField();
    JTextField replaceText = new JTextField();
    JPanel buttonsPanel = new JPanel();
    JPanel contentPanel = new JPanel();
    JPanel textPanel = new JPanel(new BorderLayout());
    JPanel textPanel1 = new JPanel(new BorderLayout());
    JLabel imageLab = new JLabel(imLab);
    JLabel titleLab = new JLabel("Замена текста");
    JPanel mainTextPanel = new JPanel(new BoxLayout(this, BoxLayout.Y_AXIS));
    JComboBox cb_ = new JComboBox();

    private ArrayList reportattrs_;
    private JTable table_;
    private int col_;

    public SearchAndReplaceDialog(Frame owner, JTable table,
                                  ArrayList attrs, int col) {
        super(owner, "Замена", true);
        reportattrs_ = attrs;
        table_ = table;
        col_ = col;
        init();
    }


    void init() {
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(sz.width / 3 * 2, sz.height / 5);
        setLocation(Utils.centerOnScreen(getWidth(), getHeight()));
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        replBut.addActionListener(this);
        replBut.setIcon(imOk);
        replAllBut.addActionListener(this);
        replAllBut.setIcon(imAll);
        cancelBut.setIcon(imCancel);
        cancelBut.addActionListener(this);
        buttonsPanel.add(replBut);
        buttonsPanel.add(replAllBut);
        buttonsPanel.add(cancelBut);
        //textPanel.setLayout(new GridLayout(2, 2, 10, 20));
        textPanel.add(sourceLab, BorderLayout.WEST);

        sourceText.getDocument().addDocumentListener(this);

        textPanel.add(sourceText,  BorderLayout.CENTER);

        textPanel1.add(replaceLab,  BorderLayout.WEST);
        textPanel1.add(replaceText,  BorderLayout.CENTER);
        mainTextPanel.add(textPanel);
        mainTextPanel.add(textPanel1);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(mainTextPanel, BorderLayout.CENTER);
        imageLab.setBorder(BorderFactory.createEtchedBorder());
        contentPanel.add(imageLab, BorderLayout.WEST);
        JPanel decor = new JPanel();
        decor.setPreferredSize(new Dimension(getWidth(), 20));
        //contentPanel.add(titleLab, BorderLayout.NORTH);
        contentPanel.add(decor, BorderLayout.SOUTH);
        content.add(contentPanel, BorderLayout.CENTER);
        mainTextPanel.setBorder(BorderFactory.createEtchedBorder());
        content.add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == replBut) {
            result = REPLACE_RESULT;
            find = sourceText.getText();
            replace = replaceText.getText();
            dispose();
        } else if (src == replAllBut) {
            result = REPLACE_ALL_RESULT;
            find = sourceText.getText();
            replace = replaceText.getText();
            dispose();
        } else if (src == cancelBut) {
            result = CANCEL_RESULT;
            dispose();
        }
    }

    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        String searchText = sourceText.getText();
        for (int i = 0; i < reportattrs_.size(); i++) {
            QRAttr attr = (QRAttr) reportattrs_.get(i);
            String entry = "";
            if (col_ == 0)
                entry = attr.getName();
            else if (col_ == 1)
                entry = attr.getPath();
            if (entry.indexOf(searchText) > -1) {
                table_.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
}