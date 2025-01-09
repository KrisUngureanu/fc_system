package kz.tamur.guidesigner;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 06.05.2004
 * Time: 10:30:32
 * To change this template use File | Settings | File Templates.
 */
public class PopupTextSearchWindow extends JWindow {

    private JList searchSource;
    private JTextField textFld = new JTextField();

    private static final JLabel FIND_LBL = new JLabel("Поиск: ");

    public PopupTextSearchWindow(Window owner, JList source, String startChar) {
        super(owner);
        textFld.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                PopupTextSearchWindow.this.dispose();
            }
        });
        searchSource = source;
        if (startChar != null) {
            textFld.setText(startChar);
        }

        JPanel panel = new JPanel();
        panel.setBorder(
                BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        panel.setBackground(Utils.getLightSysColor());
        ((FlowLayout)panel.getLayout()).setHgap(1);
        ((FlowLayout)panel.getLayout()).setVgap(1);
        textFld.setPreferredSize(new Dimension(150, 20));
        textFld.setBackground(Utils.getLightSysColor());
        textFld.setFont(Utils.getDefaultFont());
        textFld.addKeyListener(new TextKeyAdapter());
        textFld.setBorder(null);
        FIND_LBL.setFont(Utils.getDefaultFont());
        panel.add(FIND_LBL);
        panel.add(textFld);
        getContentPane().add(panel);
        pack();
    }

    public class TextKeyAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            if (e.getSource() instanceof JTextField) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    PopupTextSearchWindow.this.dispose();
                    searchSource.requestFocus();
                } else if (Character.isLetter(e.getKeyChar())
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
                        e.getKeyCode() == KeyEvent.VK_DELETE) {
                    String searchStr = textFld.getText();
                    if (!exists(searchStr)) {
                        textFld.setForeground(Color.red);
                    } else {
                        textFld.setForeground(Color.black);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    PopupTextSearchWindow.this.dispose();
                    searchSource.requestFocus();
                } else {
                    super.keyReleased(e);
                }
            } else {

            }
        }

        public boolean exists(String str) {
            ListModel lm = searchSource.getModel();
            for (int i = 0; i < lm.getSize(); i++) {
                if (lm.getElementAt(i).toString().toLowerCase(Constants.OK).startsWith(
                        str.toLowerCase(Constants.OK))) {
                    searchSource.setSelectedIndex(i);
                    setViewPos();
                    return true;
                }
            }
            return false;
        }

        private void setViewPos() {
            JViewport v = (JViewport)searchSource.getParent();
            Point pt = v.getViewPosition();
            FontMetrics fm = searchSource.getFontMetrics(searchSource.getFont());
            pt.y = fm.getHeight() * searchSource.getSelectedIndex();
            int maxYExt = v.getView().getHeight() - v.getHeight();
            pt.y = Math.max(0, pt.y);
            pt.y = Math.min(maxYExt, pt.y);
            v.setViewPosition(pt);
        }

    }



}
