package kz.tamur.rt.adapters;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.web.component.OrWebTextField;

import com.cifs.or2.kernel.KrnException;

public class TextFieldAdapter extends ComponentAdapter {

    private OrTextComponent textField;
    private OrRef copyRef, attentionRef;
    private RadioGroupManager groupManager = new RadioGroupManager();
    boolean isCellError = false;
    int errorCellRow = -1;
    int errorCellColumn = -1;
    int ErrorType = -1;
    
    public TextFieldAdapter(OrFrame frame, OrTextComponent textField, boolean isEditor) throws KrnException {
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
            copyRef = OrRef.createRef(str, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
        }
        isCellError = isEditor;
        setАttentionRef(textField);
    }

    public void valueChanged(OrRefEvent e) {
    	OrRef ref = e.getRef();
        if (ref == attentionRef) {
			((OrWebTextField) textField).sendChangeProperty("textFieldAttention", attentionRef.getValue(langId).toString());
        }
        super.valueChanged(e);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {}
    
    public void setАttentionRef(OrGuiComponent c) {
		PropertyValue pv = ((OrWebTextField) c).getPropertyValue(((OrWebTextField) c).getProperties().getChild("pov").getChild("activity").getChild("attention"));
		String attentionExpr = null;
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }
		if (attentionExpr != null && attentionExpr.length() > 0) {
			try {
				propertyName = "Свойство: Поведение.Активность.Внимание";
				attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
				attentionRef.addOrRefListener(this);
			} catch (Exception e) {
				showErrorNessage(e.getMessage() + attentionExpr);
				log.error(e, e);
			}
		}
	}

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        textField.setEnabled(isEnabled);
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
}