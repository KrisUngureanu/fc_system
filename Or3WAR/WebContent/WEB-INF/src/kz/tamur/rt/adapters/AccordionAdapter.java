package kz.tamur.rt.adapters;

import java.util.HashMap;
import java.util.Map;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.util.Pair;
import kz.tamur.web.component.OrWebAccordion;

import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class AccordionAdapter extends ContainerAdapter {

    private OrGuiComponent accordion;
    private Map<Integer, OrCalcRef> titleRefs = new HashMap<Integer, OrCalcRef>();

    public AccordionAdapter(OrFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
        accordion = c;
        PropertyNode propTitle = c.getProperties().getChild("titleN");
        PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("titleN").getChild("countPanel"));
		if (!pv.isNull()) {
			int countPanel = pv.intValue();
			String iS;
			String tnn;
			String dtnn;
			for (int i = 0; i < countPanel; i++) {
				iS = String.valueOf(i);
				tnn = "title_" + iS;
				dtnn = "dynamicTitle_" + iS;
				PropertyNode titleProp = new PropertyNode(propTitle, tnn, Types.RSTRING, null, false, null);
				PropertyNode dynamicTitleProp = new PropertyNode(propTitle, dtnn, Types.EXPR, null, false, null);
				String title = "";
				pv = c.getPropertyValue(titleProp);
				if (!pv.isNull()) {
					Pair p = pv.resourceStringValue();
					title = frame.getString((String) p.first);
				}
				if (title.length() == 0) {
					pv = c.getPropertyValue(dynamicTitleProp);
					if (!pv.isNull()) {
						String titleExpr = pv.stringValue(frame.getKernel());
						if (titleExpr != null && titleExpr.length() > 0) {
				        	try {
				                propertyName = "Свойство: titleN.Динамический заголовок " + i;
				                OrCalcRef titleRef = new OrCalcRef(titleExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
				                titleRef.addOrRefListener(this);
				                titleRefs.put(i, titleRef);
				            } catch (Exception e) {
				                showErrorNessage(e.getMessage() + titleExpr);
				                e.printStackTrace();
				            }
				    	}
					}
				}
			}
		}
    }
    
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (titleRefs.containsValue(ref)) {
            for (Integer i : titleRefs.keySet()) {
            	OrCalcRef titleRef = titleRefs.get(i);
                if (titleRef.equals(ref)) {
            		if (titleRef.getValue(langId) != null) {
            			String title = titleRef.getValue(langId).toString();
    		            JsonObject accordionPanelDynTitle = new JsonObject();
    		            accordionPanelDynTitle.add("index", i);
    		            accordionPanelDynTitle.add("title", title);
    		            ((OrWebAccordion) accordion).sendChangeProperty("accordionPanelDynTitle", accordionPanelDynTitle);
                    }
                	break;
                }
            }
        }
    	super.valueChanged(e);
    } 

    @Override
    public void clear() {}
    
    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        accordion.setEnabled(isEnabled);
    }
}