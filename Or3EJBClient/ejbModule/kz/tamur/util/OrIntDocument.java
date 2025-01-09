package kz.tamur.util;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 25.07.2006
 * Time: 12:09:46
 */
public class OrIntDocument extends PlainDocument {


    private JComponent comp_;
    private int charsLimit_;

    public OrIntDocument(JComponent comp, int charsLimit) {
        comp_ = comp;
        charsLimit_ = charsLimit;
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null)
            return;

        if (charsLimit_ > 0 && charsLimit_ < getLength() + str.length()) {
            comp_.getToolkit().beep();
            return;
        }

        char[] upper = str.toCharArray();
        for (int i = 0; i < upper.length; i++) {
            if (!Character.isDigit(upper[i]) && upper[i] != '-') {
                comp_.getToolkit().beep();
                return;
            }
        }
        super.insertString(offs, str, a);
    }
}

