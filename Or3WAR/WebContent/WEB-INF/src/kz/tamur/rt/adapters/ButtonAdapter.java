package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrButtonComponent;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebButton;
import kz.tamur.web.component.WebFrame;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.util.expr.Editor;

public class ButtonAdapter extends ComponentAdapter {

    private OrButtonComponent button;
    ASTStart template;
    private boolean needPass = false;
    
    protected OrCalcRef titleRef, attentionRef;

    public ButtonAdapter(OrFrame frame, OrButtonComponent button, boolean isEditor) throws KrnException {
        super(frame, button, isEditor);
        String expr = button.getExpression();
        if (expr != null) {
            if (expr.contains("requestPassword"))
                needPass = true;
        	long ifcId = ((WebFrame)frame).getObj().id;
        	String key = ((WebComponent)button).getId() + "_" + OrLang.ACTION_TYPE;
        	template = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
            Editor e = new Editor(expr);
            ArrayList<String> paths = e.getRefPaths();
            for (String path : paths) {
                OrRef a = OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                a.addOrRefListener(this);
            }
        }
        this.button = button;
        this.button.setXml(null);
        setTitleRef(button);
        setАttentionRef(button);
    }
    
    public boolean hasSetAttr() {
    	return button.hasSetAttr();
    }

    public void clear() {
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        button.setEnabled(isEnabled);
    }

    public void buttonPressed() {
        if (template != null) {
        	String threadName = Thread.currentThread().getName();
        	try {
        		Thread.currentThread().setName(threadName + "(UI: " + ((WebFrame)frame).getObj().uid + ")(BUTTON: " + getUUID() + ")");
	            ClientOrLang jep = new ClientOrLang(frame);
	            Map<String, Object> vc = new HashMap<String, Object>();
	            vc.put("OBJS", Funcs.makeList(((WebFrame)frame).getInitialObjs()));
	            
	            boolean calcOwner = OrCalcRef.setCalculations();
	            try {
	                jep.evaluate(template, vc, this, new Stack<String>());
	            } catch (Exception ex) {
	                if (!(ex instanceof ProcessException)) {
	                	log.error("Ошибка при выполнении формулы 'Выражение' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
	                    log.error(ex, ex);
	                    Util.showErrorMessage(button, ex.getMessage(), "Выражение");
	                }
	            } finally {
	                if (calcOwner)
	                    OrCalcRef.makeCalculations();
	            }
        	} finally {
        		Thread.currentThread().setName(threadName);
        	}
        }
    }
    
    public void setTitleRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("titleN");
        if (rprop != null) {
            PropertyNode  pn1 = rprop.getChild("expr");
            if (pn1 != null) {
                PropertyValue pv = c.getPropertyValue(pn1);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Выражение";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                        	titleRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                        	titleRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void setАttentionRef(OrGuiComponent c) {
    	String attentionExpr = ((OrWebButton) c).getaAttentionExpr();
    	if (attentionExpr != null && attentionExpr.length() > 0) {
        	try {
                propertyName = "Свойство: Поведение.Активность.Внимание";
	            attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
	            attentionRef.addOrRefListener(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + attentionExpr);
                e.printStackTrace();
            }
    	}
    }    
    
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (ref == titleRef) {
    		if (titleRef.getValue(langId) != null) {
    			((OrWebButton) button).sendChangeProperty("text", titleRef.getValue(langId).toString());
            }
        } else if (ref == attentionRef) {
			((OrWebButton) button).sendChangeProperty("buttonAttention", attentionRef.getValue(langId).toString());
        }
    	super.valueChanged(e);
    }    

    public boolean isNeedPass() {
        return needPass;
    }
}