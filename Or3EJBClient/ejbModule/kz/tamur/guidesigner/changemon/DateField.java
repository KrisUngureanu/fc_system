package kz.tamur.guidesigner.changemon;

import javax.swing.JTextField;
import javax.swing.text.*;

import kz.tamur.util.Funcs;

import javax.swing.event.*;
import java.text.*;
import java.util.Date;
import java.awt.event.*;

public class DateField extends JTextField {
    private static final String MASK_ = "дд.мм.гггг";
    private static final CaretListener CARET_LISTENER_ = new DateCaretListener();
    private static final FocusListener FOCUS_LISTENER_ = new DateFocusListener();

    private int oldCaretPos_ = 0;

    public DateField() {
        super(6);
        setText(MASK_);
        addCaretListener(CARET_LISTENER_);
        addFocusListener(FOCUS_LISTENER_);
    }

    public Date getValue() {
        Date res = null;
        String str = getText();
        if (!str.equals(MASK_)) {
            try {
                res = Funcs.getDateFormat().parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public void setValue(Date value) {
        String str = (value == null) ?
                MASK_ : Funcs.getDateFormat().format(value);
        setText(str);
    }

    protected Document createDefaultModel() {
        return new DateDocument();
    }

    static class DateCaretListener implements CaretListener {
        private boolean selfChange_ = false;

        public void caretUpdate(CaretEvent e) {
            if (!selfChange_) {
                try {
                    selfChange_ = true;
                    DateField comp = (DateField) e.getSource();
                    int oldPos = comp.oldCaretPos_;
                    int pos_ = e.getDot();
                    int pos_m = e.getMark();
                    if (pos_ == 2 || pos_ == 5) {
                        pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                        comp.setCaretPosition(pos_);
                    } else if (pos_m == 2 || pos_m == 5) {
                        pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                        comp.setCaretPosition(pos_m);
                    }
                    if (pos_ > 0) {
                        String str_ = comp.getText();
                        char c = str_.charAt(pos_ - 1);
                        if (c == '.')
                            c = str_.charAt(pos_ - 2);
                        if (!Character.isDigit(c)) {
                            pos_ = oldPos;
                            comp.setCaretPosition(pos_);
                        }
                        str_ = str_.substring(0, pos_);
                        int m = pos_;
                        int i = str_.indexOf("д");
                        if (i >= 0)
                            m = i;
                        else {
                            int j = str_.indexOf("м");
                            if (j >= 0)
                                m = Math.min(m, j);
                            else {
                                int k = str_.indexOf("г");
                                if (k >= 0) m = Math.min(m, k);
                            }
                        }
                        if (m < pos_)
                            comp.setCaretPosition(m);
                    }
                    comp.oldCaretPos_ = pos_;
                } finally {
                    selfChange_ = false;
                }
            }
        }
    }

    static class DateFocusListener extends FocusAdapter {
        public void focusGained(FocusEvent e) {
            DateField comp = (DateField) e.getSource();
            CaretEvent ce = new CaretEvent (comp) {
                public int getDot() {
                    return 10;
                }

                public int getMark() {
                    return 10;
                }
            };
            comp.fireCaretUpdate(ce);
        }
    }

    static class DateDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null) {
                return;
            }

            if ((offs + str.length()) > 10)
                return;

            int currLength = getLength();
            int length = str.length();

            if (length != 10 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            }

            if (currLength > offs) {
                int l = currLength > offs + length ? length : currLength - offs;
                super.remove(offs, l);
            }

            super.insertString(offs, str, a);
        }

        public void remove(int offs, int len) throws BadLocationException {
            if (len == 1 && (offs == 2 || offs == 5))
                --offs;
            super.insertString(offs + len, MASK_.substring(offs, offs + len), null);
            super.remove(offs, len);
        }
    }
}