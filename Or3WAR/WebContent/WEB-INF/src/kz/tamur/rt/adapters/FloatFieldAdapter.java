package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.*;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

public class FloatFieldAdapter extends ComponentAdapter {

    private OrTextComponent floatField;
    private boolean selfChange = false;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private OrRef copyRef;

    public FloatFieldAdapter(OrFrame frame, OrTextComponent floatField, boolean isEditor)
            throws KrnException {
        super(frame, floatField, isEditor);
        this.floatField = floatField;
        this.floatField.setXml(null);
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
                updateParamFilters(value);
                selfChange = false;
            }
        }
        if ( radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        floatField.setEnabled(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            floatField.setValue(null);
        }
    }

    public Number update(Number value) {
        boolean sch = selfChange;
        if (!sch) {
            try {
                selfChange = true;
                Number newValue = (Number)changeValue(value);
                return newValue;
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                selfChange = sch;
            }
        }
        return value;
    }

    public void copyPerformed() {
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
                    floatField.setValue(value);
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setCopyRef(OrRef copyRef) {
        this.copyRef = copyRef;
    }

    public OrTextComponent getFloatField() {
        return floatField;
    }
}
