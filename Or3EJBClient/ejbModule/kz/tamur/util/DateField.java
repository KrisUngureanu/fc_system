package kz.tamur.util;

import static kz.tamur.comps.Constants.DD_MM;

import static kz.tamur.comps.Constants.DD_MM_YYYY;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS_SSS;
import static kz.tamur.comps.Constants.HH_MM;
import static kz.tamur.comps.Constants.HH_MM_SS;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Time;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import kz.tamur.comps.Constants;

import com.cifs.or2.kernel.KrnDate;
import kz.tamur.util.Funcs;

public class DateField extends JTextField {
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    public String MASK_ = res.getString("mask");
    public String MASK_1 = res.getString("mask1");
    public String MASK_2 = res.getString("mask2");
    public String MASK_3 = res.getString("mask3");
    public String MASK_4 = res.getString("mask4");
    public String MASK_5 = res.getString("mask5");
    public String MASK_6 = res.getString("mask6");
    private String charD = res.getString("charD");
    private String charM = res.getString("charM");
    private String charG = res.getString("charG");
    private String charCH = res.getString("charCH");
    private String charMM = res.getString("charMM");
    private String charS = res.getString("charS");
    private String charSS = res.getString("charSS");
    private final CaretListener CARET_LISTENER_ = new DateCaretListener();
    private final FocusListener FOCUS_LISTENER_ = new DateFocusListener();

    private int oldCaretPos_ = 0;

    protected int dateFormat;

    public DateField() {
        super(10);
        addCaretListener(CARET_LISTENER_);
    }

    public Object getValue() {
        java.util.Date res = null;
        String str = getText();
        try {
            switch (dateFormat) {
            case DD_MM_YYYY:
                if (!(str.contains(charD) || str.contains(charM) || str.contains(charG))) {
                    res = Funcs.getDateFormat().parse(str);
                }
                break;
            case DD_MM_YYYY_HH_MM:
                if (!(str.contains(charD) || str.contains(charM) || str.contains(charG)
                        || str.contains(charCH) || str.contains(charMM))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            case DD_MM_YYYY_HH_MM_SS:
                if (!(str.contains(charD) || str.contains(charM) || str.contains(charG)
                        || str.contains(charCH) || str.contains(charMM) || str.contains(charS))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            case DD_MM_YYYY_HH_MM_SS_SSS:
                if (!(str.contains(charD) || str.contains(charM) || str.contains(charG)
                        || str.contains(charCH) || str.contains(charMM) || str.contains(charS) || str.contains(charSS))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            case HH_MM_SS:
                if (!(str.contains(charCH) || str.contains(charMM) || str.contains(charS))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            case HH_MM:
                if (!(str.contains(charCH) || str.contains(charMM))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            case DD_MM:
                if (!(str.contains(charD) || str.contains(charM))) {
                    res = new Time(Funcs.getDateFormat(dateFormat).parse(str).getTime());
                }
                break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res != null ? new KrnDate(res.getTime()) : null;
    }

    public void setValue(Object value) {
        setText(toText(value));
    }

    public String toText(Object value) {
        String str = "";
        switch (dateFormat) {
        case DD_MM_YYYY:
            str = value == null ? MASK_ : Funcs.getDateFormat().format(value);
            break;
        case DD_MM_YYYY_HH_MM:
            str = value == null ? MASK_1 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        case DD_MM_YYYY_HH_MM_SS:
            str = value == null ? MASK_2 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        case DD_MM_YYYY_HH_MM_SS_SSS:
            str = value == null ? MASK_3 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        case HH_MM_SS:
            str = value == null ? MASK_4 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        case HH_MM:
            str = value == null ? MASK_5 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        case DD_MM:
            str = value == null ? MASK_6 : Funcs.getDateFormat(dateFormat).format(value);
            break;
        }
        return str;
    }

    protected Document createDefaultModel() {
        return new DateDocument();
    }

    class DateCaretListener implements CaretListener {
        private boolean selfChange_ = false;

        public void caretUpdate(CaretEvent e) {
            if (!selfChange_) {
                try {
                    selfChange_ = true;
                    DateField comp = (DateField) e.getSource();
                    int oldPos = comp.oldCaretPos_;
                    int pos_ = e.getDot();
                    int pos_m = e.getMark();

                    switch (dateFormat) {
                    case DD_MM_YYYY:
                        if (pos_ == 2 || pos_ == 5) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2 || pos_m == 5) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                        break;
                    case DD_MM_YYYY_HH_MM:
                        if (pos_ == 2 || pos_ == 5 || pos_ == 10 || pos_ == 13) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2 || pos_m == 5 || pos_m == 10 || pos_m == 13) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }

                        break;
                    case DD_MM_YYYY_HH_MM_SS:
                        if (pos_ == 2 || pos_ == 5 || pos_ == 10 || pos_ == 13 || pos_ == 16) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2 || pos_m == 5 || pos_m == 10 || pos_m == 13 || pos_m == 16) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                        break;
                    case DD_MM_YYYY_HH_MM_SS_SSS:
                        if (pos_ == 2 || pos_ == 5 || pos_ == 10 || pos_ == 13 || pos_ == 16 || pos_ == 19) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2 || pos_m == 5 || pos_m == 10 || pos_m == 13 || pos_m == 16 || pos_m == 19) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                        break;
                    case HH_MM_SS:
                        if (pos_ == 2 || pos_ == 5) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2 || pos_m == 5) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                        break;
                    case HH_MM:
                        if (pos_ == 2) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                        break;
                    case DD_MM:
                        if (pos_ == 2) {
                            pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
                            comp.setCaretPosition(pos_);
                        } else if (pos_m == 2) {
                            pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
                            comp.setCaretPosition(pos_m);
                        }
                    }
                    if (pos_ > 0) {
                        String str_ = comp.getText();
                        char c = str_.charAt(pos_ - 1);
                        if (c == '.' || c == ' ' || c == ':') {
                            c = str_.charAt(pos_ - 2);
                        }
                        if (!Character.isDigit(c)) {
                            pos_ = oldPos;
                            comp.setCaretPosition(pos_);
                        }
                        str_ = str_.substring(0, pos_);
                        int m = pos_;
                        int i = str_.indexOf(charD);
                        if (i >= 0)
                            m = i;
                        else {
                            int j = str_.indexOf(charM);
                            if (j >= 0)
                                m = Math.min(m, j);
                            else {
                                int k = str_.indexOf(charG);
                                if (k >= 0) {
                                    m = Math.min(m, k);
                                } else {
                                    int z = str_.indexOf(charCH);
                                    if (z >= 0) {
                                        m = Math.min(m, z);
                                    } else {
                                        int s = str_.indexOf(charMM);
                                        if (s >= 0) {
                                            m = Math.min(m, s);
                                        } else {
                                            int v = str_.indexOf(charSS);
                                            if (v >= 0) {
                                                m = Math.min(m, v);
                                            }
                                        }
                                    }
                                }
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

    class DateFocusListener extends FocusAdapter {

        public void focusGained(FocusEvent e) {
            DateField comp = (DateField) e.getSource();
            CaretEvent ce = new CaretEvent(comp) {
                public int getDot() {
                    switch (dateFormat) {
                    case DD_MM_YYYY:
                        return 10;
                    case DD_MM_YYYY_HH_MM:
                        return 16;
                    case DD_MM_YYYY_HH_MM_SS:
                        return 19;
                    case DD_MM_YYYY_HH_MM_SS_SSS:
                        return 23;
                    case HH_MM_SS:
                        return 8;
                    case HH_MM:
                    case DD_MM:
                        return 5;
                    default:
                        return 10;
                    }
                }

                public int getMark() {
                    switch (dateFormat) {
                    case DD_MM_YYYY:
                        return 10;
                    case DD_MM_YYYY_HH_MM:
                        return 16;
                    case DD_MM_YYYY_HH_MM_SS:
                        return 19;
                    case DD_MM_YYYY_HH_MM_SS_SSS:
                        return 23;
                    case HH_MM_SS:
                        return 8;
                    case HH_MM:
                    case DD_MM:
                        return 5;
                    default:
                        return 10;
                    }
                }
            };
            comp.fireCaretUpdate(ce);
        }
    }

    class DateDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            if (dateFormat == DD_MM_YYYY && (offs + str.length()) > 10) {
                return;
            }
            if (dateFormat == DD_MM_YYYY_HH_MM && (offs + str.length()) > 16) {
                return;
            }
            if (dateFormat == DD_MM_YYYY_HH_MM_SS && (offs + str.length()) > 19) {
                return;
            }
            if (dateFormat == DD_MM_YYYY_HH_MM_SS_SSS && (offs + str.length()) > 23) {
                return;
            }
            if (dateFormat == HH_MM_SS && (offs + str.length()) > 8) {
                return;
            }
            if (dateFormat == HH_MM && (offs + str.length()) > 5) {
                return;
            }
            if (dateFormat == DD_MM && (offs + str.length()) > 5) {
                return;
            }

            int currLength = getLength();
            int length = str.length();

            if (dateFormat == DD_MM_YYYY && length != 10 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == DD_MM_YYYY_HH_MM && length != 16 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == DD_MM_YYYY_HH_MM_SS && length != 19 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == DD_MM_YYYY_HH_MM_SS_SSS && length != 23 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == HH_MM_SS && length != 8 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == HH_MM && length != 5 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            } else if (dateFormat == DD_MM && length != 5 && offs != 0) {
                char[] chs = str.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    if (!Character.isDigit(chs[i]))
                        return;
                }
            }

            String temp = getText(0, offs) + str;
            if (dateFormat < 4 ||dateFormat==6) {
                if (temp.length() > 0) {
                    String dm = temp.substring(0, 1);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 3)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 1) {
                    String dm = temp.substring(0, 2);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint < 1 || dmint > 31)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 3) {
                    String dm = temp.substring(3, 4);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 1)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 4) {
                    String dm = temp.substring(3, 5);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint < 1 || dmint > 12)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 9) {
                    String dm = temp.substring(6, 10);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint < 1)
                            return;
                    } catch (Exception e) {
                    }
                }

                if (temp.length() > 11) {
                    String dm = temp.substring(11, 12);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 2)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 12) {
                    String dm = temp.substring(11, 13);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 23)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 14) {
                    String dm = temp.substring(14, 15);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 5)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 17) {
                    String dm = temp.substring(17, 18);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 5)
                            return;
                    } catch (Exception e) {
                    }
                }
            } else {
                if (temp.length() > 0) {
                    String dm = temp.substring(0, 1);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 2)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 1) {
                    String dm = temp.substring(0, 2);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 23)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 3) {
                    String dm = temp.substring(3, 4);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 5)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 4) {
                    String dm = temp.substring(3, 5);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 59)
                            return;
                    } catch (Exception e) {
                    }
                }
                if (temp.length() > 7) {
                    String dm = temp.substring(6, 8);
                    try {
                        int dmint = Integer.parseInt(dm);
                        if (dmint > 59)
                            return;
                    } catch (Exception e) {
                    }
                }
            }

            if (currLength > offs) {
                int l = currLength > offs + length ? length : currLength - offs;
                super.remove(offs, l);
            }

            super.insertString(offs, str, a);
        }

        public void remove(int offs, int len) throws BadLocationException {
            String mask = null;
            switch (dateFormat) {
            case DD_MM_YYYY:
                if (len == 1 && (offs == 2 || offs == 5)) {
                    --offs;
                }
                mask = MASK_;
                break;
            case DD_MM_YYYY_HH_MM:
                if (len == 1 && (offs == 2 || offs == 5 || offs == 10 || offs == 13)) {
                    --offs;
                }
                mask = MASK_1;
                break;
            case DD_MM_YYYY_HH_MM_SS:
                if (len == 1 && (offs == 2 || offs == 5 || offs == 10 || offs == 13 || offs == 16)) {
                    --offs;
                }
                mask = MASK_2;
                break;
            case DD_MM_YYYY_HH_MM_SS_SSS:
                if (len == 1 && (offs == 2 || offs == 5 || offs == 10 || offs == 13 || offs == 16 || offs == 19)) {
                    --offs;
                }
                mask = MASK_3;
                break;
            case HH_MM_SS:
                if (len == 1 && (offs == 2 || offs == 5)) {
                    --offs;
                }
                mask = MASK_4;
                break;
            case HH_MM:
                if (len == 1 && offs == 2) {
                    --offs;
                }
                mask = MASK_5;
                break;
            case DD_MM:
                if (len == 1 && offs == 2) {
                    --offs;
                }
                mask = MASK_6;
                break;
            }
            if (len > mask.length()) {
                super.remove(0, len);
            } else {
                super.insertString(offs + len, mask.substring(offs, offs + len), null);
                super.remove(offs, len);
            }
        }
    }

    public int getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(int dateFormat)
    {
        this.dateFormat = dateFormat;
    }
    public void setLangId(long langId) {
        LangItem langItem = LangItem.getById(langId);
        res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(langItem.code) ? "kk" : "ru"));
        String str = getText();
        if (MASK_.equals(str)) {
            setText(res.getString("mask"));
        }
        if (MASK_1.equals(str)) {
            setText(res.getString("mask1"));
        }
        if (MASK_2.equals(str)) {
            setText(res.getString("mask2"));
        }
        if (MASK_3.equals(str)) {
            setText(res.getString("mask3"));
        }
        if (MASK_4.equals(str)) {
            setText(res.getString("mask4"));
        }
        if (MASK_5.equals(str)) {
            setText(res.getString("mask5"));
        }
        if (MASK_6.equals(str)) {
            setText(res.getString("mask6"));
        }
        MASK_ = res.getString("mask");
        MASK_1 = res.getString("mask1");
        MASK_2 = res.getString("mask2");
        MASK_3 = res.getString("mask3");
        MASK_4 = res.getString("mask4");
        MASK_5 = res.getString("mask5");
        MASK_6 = res.getString("mask6");
        charD = res.getString("charD");
        charM = res.getString("charM");
        charG = res.getString("charG");
        charCH = res.getString("charCH");
        charMM = res.getString("charMM");
        charS = res.getString("charS");
        charSS = res.getString("charSS");
        repaint();
    }
}