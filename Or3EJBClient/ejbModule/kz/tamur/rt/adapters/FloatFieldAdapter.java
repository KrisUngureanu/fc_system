package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.rt.RadioGroupManager;
import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.StringTokenizer;

public class FloatFieldAdapter extends ComponentAdapter {

    private OrFloatField floatField;
    private boolean selfChange = false;
    private RadioGroupManager groupManager = new RadioGroupManager();
    OrCellEditor editor_;
    private boolean isModified = false;
    private OrRef copyRef;
    private boolean justFocusGained;

    public FloatFieldAdapter(UIFrame frame, OrFloatField floatField, final boolean isEditor)
            throws KrnException {
        super(frame, floatField, isEditor);
        this.floatField = floatField;
        if (dataRef != null && !isEditor)
            kz.tamur.rt.Utils.setComponentFocusCircle(this.floatField);
        if (!isEditor) {
            this.floatField.addFocusListener(new DefaultFocusAdapter(this));
        }

        this.floatField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
/*                if (isModified) {
                    isModified = false;
                    try {
                        doAfterModification();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                super.focusLost(e);*/
            }

            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (!isEditor && FloatFieldAdapter.this.floatField.getText().length() > 0)
                    justFocusGained = true;
            }
        });

        OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)floatField.getFormatter();
        char separator = ft.getFmt().getDecimalFormatSymbols().getDecimalSeparator();
        int limitFract = ft.getFmt().getMaximumFractionDigits();
        PropertyNode proot = floatField.getProperties();
        PropertyValue pv = floatField.getPropertyValue(
                proot.getChild("constraints").getChild("charsNumber"));
        int limit = 0;
        if (!pv.isNull()) {
            limit = pv.intValue();
            if (limit > 0)
                ft.getFmt().setMaximumIntegerDigits(limit);
        }

        FloatDocument doc = new FloatDocument(floatField, separator, limit, limitFract);
        pv = floatField.getPropertyValue(proot.getChild("constraints").getChild("exclude"));
        if (!pv.isNull()) {
            doc.setExcludeChars(pv.stringValue());
        } else {
            doc.setExcludeChars("");
        }
        pv = floatField.getPropertyValue(proot.getChild("constraints").getChild("include"));
        if (!pv.isNull()) {
            doc.setIncludeChars(pv.stringValue());
        } else {
            doc.setIncludeChars("");
        }

        this.floatField.setDocument(doc);
        this.floatField.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName()) &&
                            FloatFieldAdapter.this.floatField.isEditable()) {
                    boolean sch = selfChange;
                    if (!sch) {
                        isModified = true;
                        try {
                            selfChange = true;
                            Number value = (Number)evt.getNewValue();
                            changeValue(value);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        } finally {
                            selfChange = sch;
                        }
                        
/*                        try {
                            selfChange = true;
                            Number value = (Number)evt.getNewValue();
                            Number oldValue = (Number)evt.getOldValue();
                            if (value != null || oldValue != null) {
                                if (dataRef != null) {
                                    OrRef.Item item = dataRef.getItem(langId);
                                    if (item != null)
                                        dataRef.changeItem(value, FloatFieldAdapter.this, this);
                                    else
                                        dataRef.insertItem(0, value, this,
                                                FloatFieldAdapter.this, false);
                                }
                                updateParamFilters(value);
                            }
                        } catch (KrnException e1) {
                            e1.printStackTrace();
                        } finally {
                            selfChange = sch;
                        }*/
                    }
                }
            }
        });
        //Копируемый атрибут
        String copyRefPath = floatField.getCopyRefPath();
        if (copyRefPath != null && !"".equals(copyRefPath)) {
            try {
                propertyName = "Свойство: Копируемый атрибут";
                copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(),
                        OrRef.TR_CLEAR, frame);
                CopyAdapter adapter = new CopyAdapter();
                floatField.getCopyBtn().setCopyAdapter(adapter);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        this.floatField.setXml(null);
    }

    private void checkAutoSelect() {
        if (justFocusGained && floatField.isDeleteOnType()) {
            floatField.setSelectionStart(0);
            floatField.setSelectionEnd(floatField.getText().length());
            floatField.getCaret().setSelectionVisible(true);
            justFocusGained = false;
        }
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (!selfChange && e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == dataRef || ref == calcRef) {
                selfChange = true;
                Object value = ref.getValue(langId);
                if (value != null && value instanceof Number)
                    floatField.setValue(value);
                else {
                    floatField.setValue(null);
                }
                isModified = true;
                updateParamFilters(value);
                selfChange = false;
            }
        }
        if ( radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        floatField.setEditable(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            floatField.setText("");
        }
    }

    class OrFloatCellEditor extends OrCellEditor {
            public Object getCellEditorValue() {
                Number val = (Number)floatField.getValue();
                if (val != null && val.doubleValue() > 0) {
                    return val.doubleValue();
                } else {
                    return null;
                }
            }

            public Component getTableCellEditorComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    int row,
                    int column
                    ) {
                valueChanged(new OrRefEvent(dataRef, -1, -1, null));
                if (floatField.isDeleteOnType()) {
                    floatField.setSelectionStart(0);
                    floatField.setSelectionEnd(floatField.getText().length());
                    floatField.getCaret().setSelectionVisible(true);
                } else {
                    floatField.getCaret().setVisible(true);
                }
                return floatField;
            }

        public Object getValueFor(Object obj) {
            return ((OrRef.Item) obj).getCurrent();
        }

        public boolean stopCellEditing() {

            try {
                if (floatField.isEditable())
                    floatField.commitEdit();
                if(!floatField.hasFocus()){
                    floatField.requestFocus();
                    floatField.getParent().requestFocus();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return super.stopCellEditing();
        }
    }

    public OrCellEditor getCellEditor() {
        if (editor_ == null) {
            editor_ = new OrFloatCellEditor();
            floatField.addActionListener(editor_);
            floatField.setBorder(BorderFactory.createEmptyBorder());
        }
        return editor_;
    }

    private class CopyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (copyRef != null) {
                try {
                    OrRef ref = dataRef;
                    OrRef.Item item = copyRef.getItem(langId);
                    Object value = (item != null) ? item.getCurrent() : null;
                    if (ref.getItem(langId) == null)
                        ref.insertItem(0, value, null, FloatFieldAdapter.this, false);
                    else
                        ref.changeItem(value, FloatFieldAdapter.this, null);
                    if (isEditor()) {
                        //OrCellEditor editor = dateField.getCellEditor();
                        floatField.setValue(value);
                        editor_.stopCellEditing();
                        //editor_.cancelCellEditing();
                    }
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class FloatDocument extends PlainDocument {
        private JComponent comp_;
        private char separator;
        private int charsLimit_;
        private int fcharsLimit_;
        private String[] incTokens_;
        private String[] excTokens_;

        public FloatDocument(JComponent comp, char separator, int limit, int flimit) {
            comp_ = comp;
            this.separator = separator;
            this.charsLimit_ = limit;
            this.fcharsLimit_ = flimit;
        }

        public void setExcludeChars(String exlude) {
            excTokens_ = getTokens(exlude, ";");
        }

        public void setIncludeChars(String include) {
            incTokens_ = getTokens(include, ";");
        }

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null)
                return;

            str = str.replace('.', separator);
            int sepIndex = str.indexOf(separator);
            char[] upper = str.toCharArray();

            for (int i = 0; i < upper.length; i++) {
                char ch = upper[i];
                if (!(incTokens_.length == 0 || check(incTokens_, ch))
                        || check(excTokens_, ch)) {
                    comp_.getToolkit().beep();
                    return;
                }
            }

            String txt = getText(0, getLength());
            for (int i = 0; i < upper.length; i++) {
                if (upper[i] == separator && txt.indexOf(separator) > -1) {
                    comp_.getToolkit().beep();
                    return;
                }
                if (!Character.isDigit(upper[i]) && (upper[i] != '-' && upper[i] != separator && upper[i] != ' ' && upper[i] != 160)) {
                    comp_.getToolkit().beep();
                    return;
                }
            }

            int intLength = (txt.indexOf(separator) > -1) ? txt.indexOf(separator) : getLength();
            int fractLength = (txt.indexOf(separator) > -1) ? getLength() - txt.indexOf(separator) - 1: 0;

            int newLength = intLength;

            if (offs > intLength && sepIndex == -1) {
                fractLength += str.length();
            }
            if (offs <= intLength && sepIndex == -1) {
                newLength += str.length();
            }

            if (charsLimit_ > 0 && charsLimit_ < newLength - (newLength - 1)/4) {
                comp_.getToolkit().beep();
                return;
            }

            if (fcharsLimit_ < fractLength || (sepIndex > -1 && fcharsLimit_ == 0)) {
                comp_.getToolkit().beep();
                return;
            }

            super.insertString(offs, str, a);

            for (int i = intLength - 1; i >= 0; i--) {
                String s = getText(i, 1);
                int k = intLength - i;
                if (k%4 != 3 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                } else if (k%4 == 3 && i > 0 && !(" ".equals(s) || "\u00a0".equals(s))) {
                    super.insertString(i, " ", a);
                }
                txt = getText(0, getLength());
                intLength = (txt.indexOf(separator) > -1) ? txt.indexOf(separator) : getLength();
            }

            checkAutoSelect();
        }

        public void remove(int offs, int len) throws BadLocationException {
            String txt = getText(0, getLength());
            int intLength = (txt.indexOf(separator) > -1) ? txt.indexOf(separator) : getLength();
            int k = intLength - offs - 1;

            if (offs < intLength && offs > 0 && k%4 == 3) offs--;
            super.remove(offs, len);

            txt = getText(0, getLength());
            intLength = (txt.indexOf(separator) > -1) ? txt.indexOf(separator) : getLength();
            for (int i = intLength - 1; i >= 0; i--) {
                String s = getText(i, 1);
                k = intLength - i - 1;
                if (k%4 != 3 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                    i++;
                } else if (k%4 == 3 && i==0 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                    i++;
                } else if (k%4 == 3 && intLength > 3 && !(" ".equals(s) || "\u00a0".equals(s))) {
                    super.insertString(i + 1, " ", null);
                }
                txt = getText(0, getLength());
                intLength = (txt.indexOf(separator) > -1) ? txt.indexOf(separator) : getLength();
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
    }

    public OrFloatField getFloatField() {
        return floatField;
    }
}
