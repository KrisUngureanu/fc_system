package kz.tamur.rt.adapters;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrTextField;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.util.expr.Editor;

public class ButtonAdapter extends ComponentAdapter implements ActionListener {

    private OrButton button;
    private ASTStart template;
    private boolean isCursor;
    
    protected OrCalcRef titleRef;
    
    public ButtonAdapter(UIFrame frame, OrButton button, boolean isEditor) throws KrnException {
        super(frame, button, isEditor);
        String expr = button.getExpression();
        if (expr != null) {
            isCursor = expr.contains("waitCursor")||expr.contains("defaultCursor");
            template = OrLang.createStaticTemplate(expr);
            Editor e = new Editor(expr);
            ArrayList<String> paths = e.getRefPaths();
            for (int j = 0; j < paths.size(); ++j) {
                String path = paths.get(j);
                OrRef a = OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),OrRef.TR_CLEAR, frame);
                a.addOrRefListener(this);
            }
        }
        this.button = button;
        this.button.addActionListener(this);
        setTitleRef(button);
    }
    
    public boolean hasSetAttr() {
    	return button.hasSetAttr();
    }

    public void clear() {}

    public void actionPerformed(ActionEvent e) {
        
    	Container cont = button.getTopLevelAncestor();

        // Если фокус находится на текстовом поле, то сохраняем значение этого поля перед выполнением формулы кнопки.
    	if (cont instanceof Window) {
    		Component comp = ((Window)cont).getFocusOwner();
    		if (comp instanceof OrTextField) {
    			((OrTextField)comp).saveValue();
    		}
    	}
    	
        if (button.isHelpClick()) {
            button.setHelpClick(false);
        } else {
            if (template != null) {
                if (!isCursor) {
                    CursorToolkit.startWaitCursor(cont);
                }
                ClientOrLang jep = new ClientOrLang(frame);
                Map vc = new HashMap();
                vc.put("OBJS", Funcs.makeList(((UIFrame)frame).getInitialObjs()));
                try {
                    boolean calcOwner = OrCalcRef.setCalculations();
                    jep.evaluate(template, vc, this, new Stack<String>());
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Util.showErrorMessage(button, ex.getMessage(), "Выражение");
                }
                if (!isCursor) {
                    CursorToolkit.stopWaitCursor(cont);
                }
            }
        }
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        button.setEnabled(isEnabled);
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
    
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (ref == titleRef) {
    		if (titleRef.getValue(langId) != null) {
                button.setText(titleRef.getValue(langId).toString());
            }
        }
    	super.valueChanged(e);
    }
}