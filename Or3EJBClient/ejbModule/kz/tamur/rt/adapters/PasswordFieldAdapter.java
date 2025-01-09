package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.RadioGroupManager;

import com.cifs.or2.kernel.KrnException;

public class PasswordFieldAdapter extends ComponentAdapter {

    private OrPasswordField textField;
    private OrRef copyRef;
    private RadioGroupManager groupManager = new RadioGroupManager();
    OrCellEditor editor_;
    boolean isCellError = false;
    int errorCellRow = -1;
    int errorCellColumn = -1;
    int ErrorType = -1;

    public PasswordFieldAdapter(OrFrame frame, OrPasswordField textField, boolean isEditor) throws KrnException
            {
        super(frame, textField, isEditor);
        this.textField = textField;

        // Настройка поведения
        PropertyNode rootNode = textField.getProperties();
        PropertyNode behavNode = rootNode.getChild("pov");
        // Копируемый атрибут
        PropertyNode cpPathNode = behavNode.getChild("copy").getChild("copyPath");
        PropertyValue pv = textField.getPropertyValue(cpPathNode);
        String str = pv.isNull() ? null : pv.stringValue();
        if (str != null && str.length() > 0) {
            propertyName = "Свойство: Копируемый атрибут";
            copyRef = OrRef.createRef(
            		str, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR,
            		frame);
        }
                isCellError = isEditor;

/*
        if (dataRef != null) {
            dataRef.setActive(this.textField.isEditable());
        }
*/
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {
    }

    public OrRef getRef() {
        return dataRef;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        textField.setEnabled(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            textField.setValue("");
        }
    }

    public OrRef getCopyRef() {
    	return copyRef;
    }

    public void doCopy() {
        if (copyRef != null) {
            try {
                OrRef ref = dataRef;
                OrRef.Item item = copyRef.getItem(langId);
                Object value = (item != null) ? item.getCurrent() : null;
                if (ref.getItem(langId) == null)
                    ref.insertItem(0, value, null, PasswordFieldAdapter.this, false);
                else
                    ref.changeItem(value, PasswordFieldAdapter.this, null);
                if (isCellError) {
                    textField.setText((String)value);
                    OrCellEditor editor = textField.getCellEditor();
                    editor.stopCellEdit();
                    editor.cancelCellEditing();

                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }
}