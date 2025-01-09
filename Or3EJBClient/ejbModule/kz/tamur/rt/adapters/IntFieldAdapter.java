package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.*;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.RadioGroupManager;


import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.StringTokenizer;
import java.text.ParseException;


public class IntFieldAdapter extends ComponentAdapter {

    private OrIntField intField;
    private boolean selfChange = false;
    private RadioGroupManager groupManager = new RadioGroupManager();
    OrCellEditor editor_;
    private int charsLimit = 0;
    boolean isModified = false;
    private OrRef copyRef;
    private boolean justFocusGained;
    private boolean formatting;
    
    public IntFieldAdapter(UIFrame frame, OrIntField intField, final boolean isEditor)
            throws KrnException {
        super(frame, intField, isEditor);
        this.intField = intField;
        
        formatting = intField.isFormatting();
        
        PropertyNode proot = intField.getProperties();
        PropertyValue pv = intField.getPropertyValue(
                proot.getChild("constraints").getChild("charsNumber"));
        if (!pv.isNull()) {
            charsLimit = pv.intValue();
            if (charsLimit > 0)
                ((OrIntField.IntFormatter)intField.getFormatter()).getFormat()
                        .setMaximumIntegerDigits(charsLimit);
        }

        IntDocument doc = new IntDocument(intField, charsLimit);
        pv = intField.getPropertyValue(proot.getChild("constraints").getChild("exclude"));
        doc.setExcludeChars(pv.isNull() ? "" : pv.stringValue());
        
        pv = intField.getPropertyValue(proot.getChild("constraints").getChild("include"));
        doc.setIncludeChars(pv.isNull() ? "" : pv.stringValue());
              
        this.intField.setDocument(doc);
//        this.intField.getDocument().addDocumentListener(this);

        this.intField.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName()) &&
                            IntFieldAdapter.this.intField.isEditable()) {
                    boolean sch = selfChange;
                    if (!sch) {
                        isModified = true;
                        try {
                            selfChange = true;
                            Number value = (Number)evt.getNewValue();
                            Object newValue = changeValue(value);
                            IntFieldAdapter.this.intField.setValue(newValue);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        } finally {
                            selfChange = sch;
                        }
                    }
                }
            }
        });

        if (dataRef != null && !isEditor)
            kz.tamur.rt.Utils.setComponentFocusCircle(this.intField);
        if (!isEditor) {
            this.intField.addFocusListener(new DefaultFocusAdapter(this));
        }
        this.intField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                if (!isEditor && IntFieldAdapter.this.intField.getText().length() > 0)
                    justFocusGained = true;
            }
        });
        //Копируемый атрибут
        String copyRefPath = intField.getCopyRefPath();
        if (copyRefPath != null && !"".equals(copyRefPath)) {
            try {
                propertyName = "Свойство: Копируемый атрибут";
                copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(),
                        OrRef.TR_CLEAR, frame);
                CopyAdapter adapter = new CopyAdapter();
                intField.getCopyBtn().setCopyAdapter(adapter);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        this.intField.setXml(null);
    }

    private void checkAutoSelect() {
        if (justFocusGained && intField.isDeleteOnType()) {
            intField.setSelectionStart(0);
            intField.setSelectionEnd(intField.getText().length());
            intField.getCaret().setSelectionVisible(true);
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
                if (value instanceof Number && !(value instanceof Long)) {
                    value = new Long(((Number) value).intValue());
                }
                intField.setValue(value);
                //intField.setText((value != null) ? value.toString() : "");
                updateParamFilters(value);
                selfChange = false;
            }
        }
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public OrIntField getIntField() {
        return intField;
    }

    class OrIntCellEditor extends OrCellEditor {
        public Object getCellEditorValue() {
            String s = intField.getText();
            s = s.replaceAll("[\\s\\xa0]", "");;
            Long res = null;

            if (s.length() <= 0)
                res = new Long(0);
            else
                res = Long.valueOf(s, 10);


            //Object v = intField.getValue();
            //if (v instanceof Number) {
            //    res = ((Number) v).longValue();
            //}
            return res;
        }
        
        private String remove(String str, char ch) {
        	StringBuilder res = new StringBuilder(str.length());
        	char[] chars = str.toCharArray();
        	for (char c : chars) {
        		if (c != ch) {
        			res.append(c);
        		}
        	}
        	return res.toString();
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            valueChanged(new OrRefEvent(dataRef, -1, -1, null));
            TableModel tmodel = table.getModel();
            if (tmodel instanceof TreeTableAdapter.RtTreeTableModel) {
                row = ((TreeTableAdapter.RtTreeTableModel) tmodel).getActualRow(row);
            }
            setState(new Integer(row), new Integer(0));
            if (intField.isDeleteOnType()) {
                intField.setSelectionStart(0);
                intField.setSelectionEnd(intField.getText().length());
                intField.getCaret().setSelectionVisible(true);
            } else {
                intField.getCaret().setVisible(true);
            }
            return intField;
        }

        public Object getValueFor(Object obj) {
            return ((OrRef.Item) obj).getCurrent();
        }

        public boolean stopCellEditing() {
            boolean res = checkUnique();
        	if (res) {
                if (intField.isEditable())
                    try {
                        intField.commitEdit();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                if(!intField.hasFocus()){
                    intField.requestFocus();
                    intField.getParent().requestFocus();
                }
	            return super.stopCellEditing();
        	} else {
        		res = showDuplicate();
                return res;
        	}
        }
    }

    //IntCell Editor
    public OrCellEditor getCellEditor() {
        if (editor_ == null) {
            editor_ = new OrIntCellEditor();
            intField.addActionListener(editor_);
            intField.setBorder(BorderFactory.createEmptyBorder());
        }
        return editor_;
    }

/*
    private class IntDocument extends PlainDocument {
        private JComponent comp_;
        private int charsLimit_;

        public IntDocument(JComponent comp, int charsLimit) {
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
*/


    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        intField.setEnabled(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            intField.setValue(null);
        }
    }

    private class CopyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (copyRef != null) {
                try {
                    OrRef ref = dataRef;
                    OrRef.Item item = copyRef.getItem(langId);
                    Object value = (item != null) ? item.getCurrent() : null;
                    if (ref.getItem(langId) == null)
                        ref.insertItem(0, value, null, IntFieldAdapter.this, false);
                    else
                        ref.changeItem(value, IntFieldAdapter.this, null);
                    if (isEditor()) {
                        //OrCellEditor editor = dateField.getCellEditor();
                        intField.setValue(value);
                        //intField.setText(String.valueOf(value));
                        editor_.stopCellEditing();
                        //editor_.cancelCellEditing();
                    }
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class IntDocument extends PlainDocument {
        private JComponent comp_;
        private int charsLimit_;
        private String[] incTokens_;
        private String[] excTokens_;

        public IntDocument(JComponent comp, int charsLimit) {
            comp_ = comp;
            charsLimit_ = charsLimit;
        }

        public void setExcludeChars(String exlude) {
            excTokens_ = getTokens(exlude, ";");
        }

        public void setIncludeChars(String include) {
            incTokens_ = getTokens(include, ";");
        }

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null) {
                return;
            }
            int newLength = getLength() + str.length();
            if (charsLimit_ > 0 && charsLimit_ < newLength - (newLength - 1) /4) {
                comp_.getToolkit().beep();
                return;
            }

            char[] upper = str.toCharArray();
            for (int i = 0; i < upper.length; i++) {
                char ch = upper[i];
                if (!(incTokens_.length == 0 || check(incTokens_, ch))
                        || check(excTokens_, ch)) {
                    comp_.getToolkit().beep();
                    return;
                }
            }

            for (int i = 0; i < upper.length; i++) {
                if (!Character.isDigit(upper[i]) && (upper[i] != '-' && upper[i] != ' ' && upper[i] != 160)) {
                    comp_.getToolkit().beep();
                    return;
                }
            }

            super.insertString(offs, str, a);

            for (int i = getLength() - 1; i >= 0; i--) {
                String s = getText(i, 1);
                int k = getLength() - i;
                if (k%4 != 3 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                } else if (k % 4 == 3 && i > 0 && !(" ".equals(s) || "\u00a0".equals(s))) {
                    // Если включено форматирование текста то добавить пробел после третьего разряда
                    if (formatting) {
                        super.insertString(i, " ", a);
                    }
                }
            }

            checkAutoSelect();
        }

        public void remove(int offs, int len) throws BadLocationException {
            int k = getLength() - offs - 1;
            if (k%4 == 3 && formatting) {
                --offs;
            }

            super.remove(offs, len);
            for (int i = getLength() - 1; i >= 0; i--) {
                String s = getText(i, 1);
                k = getLength() - i - 1;
                if (k%4 != 3 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                    i++;
                } else if (k%4 == 3 && i == 0 && (" ".equals(s) || "\u00a0".equals(s))) {
                    super.remove(i, 1);
                    i++;
                } else if (k%4 == 3 && getLength() > 3 && !(" ".equals(s) || "\u00a0".equals(s))) {
                    if (formatting) {
                        super.insertString(i + 1, " ", null);
                    }
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
}
