package kz.tamur.rt.adapters;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;

import com.cifs.or2.kernel.KrnException;


public class PanelAdapter extends ContainerAdapter {

    private OrPanel panel;
    private boolean isEn = true;
    
    private OrCalcRef titleRef;

    public PanelAdapter(UIFrame frame, OrPanel panel, boolean isEditor) throws KrnException {
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
    }

    public boolean isEnabled() {
        return isEn;
    }

    public OrPanel getPanel() {
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
    
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;
        if (ref == titleRef) {
    		if (titleRef.getValue(langId) != null) {
    			panel.setTitle(titleRef.getValue(langId).toString());
            }
        }
    	super.valueChanged(e);
    }
}