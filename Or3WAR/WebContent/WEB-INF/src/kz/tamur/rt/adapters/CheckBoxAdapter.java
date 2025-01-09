package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.WebFrame;
import kz.tamur.or3.client.comps.interfaces.OrCheckBoxComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CheckBoxAdapter extends ComponentAdapter {

    private OrCheckBoxComponent checkBox;
    private boolean selfChange = false;
    private ASTStart afterModAction;
    private boolean isModified = false;

    public CheckBoxAdapter(OrFrame frame, OrCheckBoxComponent checkBox, boolean isEditor) throws KrnException {
        super(frame, checkBox, isEditor);
        this.checkBox = checkBox;
        PropertyNode proot = checkBox.getProperties();
        PropertyValue pv = checkBox.getPropertyValue(proot.getChild("pov").getChild("afterModAction"));
        String afterExpr = null;
        if (!pv.isNull()) {
            afterExpr = pv.stringValue(frame.getKernel());
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            long ifcId = ((WebFrame) frame).getObj().id;
            String key = ((WebComponent) checkBox).getId() + "_" + OrLang.AFTER_MODIF_TYPE;
            afterModAction = ClientOrLang.getStaticTemplate(ifcId, key, afterExpr, getLog());
            try {
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == dataRef || ref == calcRef) {
                selfChange = true;
                Object item = ref.getValue(langId);
                Number value = (item != null) ? (Number) item : new Long(0);
                updateParamFilters(value);
                checkBox.setSelected((value != null && value.longValue() == 1));
                selfChange = false;
            }
        }
    }

    public void clear() {
    }

    public void itemStateChanged(Number value) {
        try {
            if (!selfChange) {
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
	                isModified = true;
	                if (dataRef != null) {
	                    OrRef.Item item = dataRef.getItem(langId);
	                    if (item != null)
	                        dataRef.changeItem(value, this, this);
	                    else
	                        dataRef.insertItem(0, value, this, this, false);
	                }
	                updateParamFilters(value.longValue() == 0 ? null : value);
            	} catch (Exception e) {
            		log.error(e, e);
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
            	}
                changeValue(value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object changeValue(Object value) throws Exception {
        if (isModified) {
            isModified = false;
            if (afterModAction != null) {
                ClientOrLang orlang = new ClientOrLang(CheckBoxAdapter.this.frame);
                Map<String, Object> vc = new HashMap<String, Object>();
                vc.put("THIS", checkBox);

                if (dataRef != null && dataRef.isColumn()) {
                    OrRef p = dataRef;
                    while (p != null && p.isColumn()) {
                        p = p.getParent();
                    }
                    if (p != null && p.getItem(0) != null) {
                        Object obj = p.getItem(0).getCurrent();
                        vc.put("SELOBJ", obj);
                    }
                }
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    orlang.evaluate(afterModAction, vc, CheckBoxAdapter.this, new Stack<String>());
                } catch (Exception ex) {
                    Util.showErrorMessage(CheckBoxAdapter.this.checkBox, ex.getMessage(), "Действие после модификации");
                	log.error("Ошибка при выполнении формулы 'Действие после модификации' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(ex, ex);
                } finally {
	                if (calcOwner)
	                    OrCalcRef.makeCalculations();
                }
            }
        }
        return value;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        checkBox.setEnabled(isEnabled);
    }
    
    public void filterParamChanged(String fuid, String pid, List<?> values){
    	super.filterParamChanged(fuid, pid, values);
        if (value == null || Boolean.FALSE.equals(value) || (value instanceof Number && ((Number)value).intValue() == 0)) {
            checkBox.setValue(false);
        }
        if(Boolean.TRUE.equals(value) || (value instanceof Number && ((Number)value).intValue() == 1)){
        	checkBox.setValue(true);
        }
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            checkBox.setValue(false);
        }
    }
}
