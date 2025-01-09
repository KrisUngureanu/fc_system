package kz.tamur.util;

import javax.swing.text.*;
import javax.swing.*;

import java.util.Locale;
import java.util.StringTokenizer;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrTextField;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;

public class OrTextDocument extends PlainDocument {
    private String incChars_, oldIncChars_;
    private String excChars_, oldExcChars_;

    private String[] incTokens_;
    private String[] excTokens_;

    private int charsLimit_;

    private int mode_;

    private JComponent comp_;

    private boolean isUpperCase = true;
    private boolean isUpperAllChar = false;

    public OrTextDocument(JComponent comp, int mode) {
        comp_ = comp;
        mode_ = mode;
        if (comp instanceof OrTextField) {
            PropertyNode pn =
                    ((OrTextField)comp).getProperties().getChild(
                            "constraints").getChild("upperCase");
            PropertyValue pv = ((OrTextField)comp).getPropertyValue(pn);
            if (!pv.isNull()) {
                isUpperCase = pv.booleanValue();
            }
            pn = ((OrTextField)comp).getProperties().getChild(
                            "constraints").getChild("upperAllChar");
            pv = ((OrTextField)comp).getPropertyValue(pn);
            if (!pv.isNull()) {
                isUpperAllChar = pv.booleanValue();
            } else {
                isUpperAllChar = ((Boolean)pn.getDefaultValue()).booleanValue();
            }
        }
    }

    private String[] getTokens(String chars, String delim) {
        if (chars == null)
            return new String[0];

        StringTokenizer st = new StringTokenizer(chars, delim);
        String[] res = new String[st.countTokens()];
        for (int i = 0; i < res.length; i++)
            res[i] = st.nextToken();
        return res;
    }

    public String getExcChars() {
        return excChars_;
    }

    public String getIncChars() {
        return incChars_;
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null)
            return;

        if (mode_ == Mode.RUNTIME) {
            if (charsLimit_ > 0 && charsLimit_ < getLength() + str.length()) {
                comp_.getToolkit().beep();
                return;
            }

            char[] chars = str.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (!(incTokens_.length == 0 || check(incTokens_, ch))
                        || check(excTokens_, ch)) {
                    comp_.getToolkit().beep();
                    return;
                }
            }
        }
        if (isUpperCase && getLength() == 0 && str.length() == 1) {
            char firstChar = str.charAt(0);
            str = "" + Character.toUpperCase(firstChar);
        }
        if (isUpperAllChar) {
            str = str.toUpperCase(Constants.OK);
        }
        super.insertString(offs, str, a);
    }

    private boolean check(String[] tokens, char ch) {
        boolean res = false;
        for (int j = 0; j < tokens.length; j++) {
            char[] tchs = tokens[j].toCharArray();
            for (int i = 0; i < tchs.length && !res; i++) {
                char beg = tchs[i];
                if (i + 1 == tchs.length) {
                    if (i == 0)
                        res = (ch == beg);
                } else
                    res = (ch >= beg && ch <= tchs[i + 1]);
            }
        }
        return res;
    }

    public void setCharsLimit(int count) {
        charsLimit_ = count;
    }

    public void setExcludeChars(String exlude) {
        excTokens_ = getTokens(exlude, ";");
    }

    public void setIncludeChars(String include) {
        incTokens_ = getTokens(include, ";");
    }
    
    public int getCharsLimit(){
    	return charsLimit_;
    }
    
    
}
