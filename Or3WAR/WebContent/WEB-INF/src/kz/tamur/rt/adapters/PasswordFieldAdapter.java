package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.web.component.OrWebPasswordField;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

public class PasswordFieldAdapter extends ComponentAdapter {

    private OrTextComponent textField;
    private OrRef copyRef;
    private RadioGroupManager groupManager = new RadioGroupManager();
    boolean isCellError = false;
    int errorCellRow = -1;
    int errorCellColumn = -1;
    int ErrorType = -1;

    public PasswordFieldAdapter(OrFrame frame, OrTextComponent textField, boolean isEditor) throws KrnException
            {
        super(frame, textField, isEditor);
        this.textField = textField;

        // Настройка поведения
        PropertyNode rootNode = textField.getProperties();
        PropertyNode behavNode = rootNode.getChild("pov");
        // Копируемый атрибут
        PropertyNode cpPathNode = behavNode.getChild("copy").getChild("copyPath");
        PropertyValue pv = textField.getPropertyValue(cpPathNode);
        String str = pv.isNull() ? null : pv.stringValue(frame.getKernel());
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
    
    public Object changeValue(Object value) throws Exception {
        if (!selfChange) {
            // Триггер "До модификации"
            value = doBeforeModification(value);
            if (value != null) {
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
	                if (dataRef != null) {
	                    OrRef.Item item = dataRef.getItem(langId);
	                    try {
	                        selfChange = true;
	                        if (item != null && value != null) {
	                            dataRef.changeItem(value, this, this);
	                        } else if (item != null && value == null) {
	                            dataRef.deleteItem(this, this);
	                        } else {
	                            dataRef.insertItem(0, value, this, this, false);
	                        }
	                    } finally {
	                        selfChange = false;
	                    }
	                }
	                updateParamFilters(value);
            	} catch (Exception e) {
            		log.error(e, e);
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
            	}
                // Обновляем текущее значение
                this.value = value;
                // Триггер "После модификации"
                doAfterModification();
            } else {
            	((OrWebPasswordField)comp).setValue(this.value);
            }
        }
        return value;
    }

}