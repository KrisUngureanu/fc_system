package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.*;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

public class IntFieldAdapter extends ComponentAdapter {

    private OrTextComponent intField;
    private boolean selfChange = false;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private OrRef copyRef;

    public IntFieldAdapter(OrFrame frame, OrTextComponent intField, boolean isEditor)
            throws KrnException {
        super(frame, intField, isEditor);
        this.intField = intField;
        this.intField.setXml(null);
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
                    value = (long) ((Number) value).intValue();
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
    }

    public String update(String s) {
        if (!selfChange) {
            //isModified = true;
            selfChange = true;
            Object value = null;
            try {
                if (s.length() <= 0)
                    value = null;
                else
                    value = new Long(s);

                String newValue = (String)changeValue(value);
                return newValue;

            } catch (Exception ex) {
            	log.error(ex, ex);
            } finally {
                selfChange = false;
            }
        }
        return s;
    }


    public OrTextComponent getIntField() {
        return intField;
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


    public void copyPerformed() {
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
                    intField.setValue(String.valueOf(value));
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

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
            intField.setValue("");
        }
    }

    public void setCopyRef(OrRef copyRef) {
        this.copyRef = copyRef;
    }

    public boolean isSelfChange() {
        return selfChange;
    }

    public void setSelfChange(boolean selfChange) {
        this.selfChange = selfChange;
    }
}
