package kz.tamur.rt.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.OrWebGISPanel;

import com.cifs.or2.kernel.KrnException;

public class GISPanelAdapter extends ContainerAdapter {

    private OrPanelComponent panel;
    private boolean isEn = true;

    private OrCalcRef titleRef;
    private OrCalcRef layersRef;
    private OrCalcRef boundsRef;
    private OrCalcRef selectionsRef;
    private OrCalcRef formulaRef;
    
    public GISPanelAdapter(OrFrame frame, OrPanelComponent panel, boolean isEditor) throws KrnException {
        super(frame, panel, isEditor);
        this.panel = panel;
        loadReports();
        PropertyNode pov = panel.getProperties().getChild("pov"); 
        PropertyNode pn = pov.getChild("activity").getChild("enabled");
        PropertyValue pv = panel.getPropertyValue(pn);
        if (!pv.isNull()) {
            isEn = pv.booleanValue();
        } else {
            isEn = ((Boolean)pn.getDefaultValue()).booleanValue();
        }
        setEnabled(isEn);
        setTitleRef(panel);
        setFormulaRef(panel);
        setLayersRef(panel);
        setBoundsRef(panel);
        setSelectionsRef(panel);
    }

    public boolean isEnabled() {
        return isEn;
    }

    public OrPanelComponent getPanel() {
        return panel;
    }

    public void clear() {}

    private void setTitleRef(OrGuiComponent c) {
        PropertyNode dynamicTitle = c.getProperties().getChild("dynamicTitle");
        if (dynamicTitle != null) {
            PropertyNode  pn = dynamicTitle.getChild("expr");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
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
    
    private void setLayersRef(OrGuiComponent c) {
        PropertyNode map = c.getProperties().getChild("map");
        if (map != null) {
            PropertyNode  pn = map.getChild("layers");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Выражение";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                        	layersRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                        	layersRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void setBoundsRef(OrGuiComponent c) {
        PropertyNode map = c.getProperties().getChild("map");
        if (map != null) {
            PropertyNode  pn = map.getChild("bounds");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Выражение";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                        	boundsRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                        	boundsRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void setSelectionsRef(OrGuiComponent c) {
        PropertyNode map = c.getProperties().getChild("map");
        if (map != null) {
            PropertyNode  pn = map.getChild("selections");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Выражение";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                        	selectionsRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                        	selectionsRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void setFormulaRef(OrGuiComponent c) {
        PropertyNode map = c.getProperties().getChild("map");
        if (map != null) {
            PropertyNode  pn = map.getChild("formula");
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
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void loadReports() {
        PropertyNode prop = panel.getProperties();
        PropertyNode rprop = prop.getChild("reports");
        PropertyValue pv = panel.getPropertyValue(rprop);
        if (!pv.isNull()) {
            Object value = pv.objectValue();
            if (value instanceof ReportRecord) {
                frame.setRootReport((ReportRecord)value);
            } else if (value instanceof ReportRecord[]) {
                ReportRecord[] reports = (ReportRecord[]) value;
                for (int i = 0; i<reports.length; i++) {
                    ReportPrinter rp = new ReportPrinterAdapter(frame, panel, reports[i]);
                    frame.addReport(rp);
                }
            }
        }
    }

    public void fillData(org.json.JSONObject json) {
        PropertyNode map = panel.getProperties().getChild("map");
        if (map != null) {
            PropertyNode  pn = map.getChild("onSelect");
            if (pn != null) {
                PropertyValue pv = panel.getPropertyValue(pn);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
					try {
						propertyName = "Свойство: Выражение";
						fx = pv.stringValue();
						if (fx.trim().length() > 0) {
							Map<String, Object> vc = new HashMap<String, Object>();
							vc.put("JSON", json);
							Utils.evalExp(fx, frame, this, vc);
						}
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (ref == titleRef) {
    		if (titleRef.getValue(langId) != null) {
    			if (((WebComponent) panel).getParent() == null) {
    				((WebPanel) panel).sendChangeProperty("rootPanelTitle", titleRef.getValue(langId).toString());
    			}
            }
        } else  if (ref == layersRef) {
    		if (layersRef.getValue(langId) != null) {
    			((OrWebGISPanel) panel).setLayers(layersRef.getValue(langId));
            }
        } else  if (ref == boundsRef) {
    		if (boundsRef.getValue(langId) != null) {
    			((OrWebGISPanel) panel).setBounds(boundsRef.getValue(langId));
            }
        } else  if (ref == selectionsRef) {
    		if (selectionsRef.getValue(langId) != null) {
    			((OrWebGISPanel) panel).setSelections(selectionsRef.getValue(langId));
            }
        } else  if (ref == formulaRef) {
    		if (formulaRef.getValue(langId) != null) {
    			((OrWebGISPanel) panel).setFormula((List) formulaRef.getValue(langId));
            }
        }
    	super.valueChanged(e);
    }
}