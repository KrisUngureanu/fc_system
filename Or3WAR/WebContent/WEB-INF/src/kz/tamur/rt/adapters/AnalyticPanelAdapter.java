package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.OrFrame;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;

public class AnalyticPanelAdapter extends ContainerAdapter {
	
	//private OrCalcRef formulaRef;
	private OrPanelComponent panel;
	//private String xAxis, yAxis, zAxis, namesValue;

	public AnalyticPanelAdapter(OrFrame frame, OrPanelComponent panel, boolean isEditor) throws KrnException {
		super(frame, panel, isEditor);
		this.panel = panel;
		
		/*PropertyNode props = panel.getProperties();

		PropertyNode pn = props.getChild("analytic").getChild("xaxis");
		PropertyValue pv =  panel.getPropertyValue(pn);
        xAxis = pv.stringValue();
        
        pn = props.getChild("analytic").getChild("yaxis");
        pv =  panel.getPropertyValue(pn);
        yAxis = pv.stringValue();
        
        pn = props.getChild("analytic").getChild("zaxis");
        pv =  panel.getPropertyValue(pn);
        zAxis = pv.stringValue();
        
		setFormulaRef(panel);*/
	}
	
	/*private void setFormulaRef(OrGuiComponent c) {
        PropertyNode map = c.getProperties().getChild("analytic");
        if (map != null) {
            PropertyNode pn = map.getChild("formula");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Выражение";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                        	formulaRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                        	formulaRef.addOrRefListener(this);

                            Map<String, Object> params = new HashMap<String, Object>();
                            
                            if (namesValue != null && namesValue.trim().length() > 0) {
                            	Map<String, Object> vc = new HashMap<String, Object>();
	                            Utils.evalExp(namesValue, frame, this, vc);
	                            JSONObject names = (JSONObject) vc.get("RETURN"); 
	                            for (String name : names.keySet()) {
	                            	params.put(name, names.get(name));
	                            }
	                        } else {
	                        	params.put("X", xAxis);
	                        	params.put("Y", yAxis);
	                        }
	                    	
                        	formulaRef.setParams(params);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/
	
	/*@Override
    public void valueChanged(OrRefEvent e) {
		OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (ref == formulaRef) {
    		if (formulaRef.getValue(langId) != null) {
    			((OrWebAnalyticPanel) panel).setFormula((JSONArray) formulaRef.getValue(langId));
            }
        }
    	super.valueChanged(e);
	}*/

}
