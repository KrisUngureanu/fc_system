package kz.tamur.comps;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.ReportPrinterPropertyRoot;
import kz.tamur.guidesigner.reports.ReportNode;
import kz.tamur.rt.adapters.ComponentAdapter;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;

public class OrReportPrinter extends JButton implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new ReportPrinterPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrFrame frame;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private ReportNode reportNode;

    public OrReportPrinter(Element xml, int mode, OrFrame frame) {
        super("OrReportPrinter");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        updateProperties();
        setIcon(kz.tamur.rt.Utils.getImageIcon("ReportPrinter"));
        if (mode != Mode.DESIGN) {
            setVisible(false);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return PropertyHelper.getConstraints(PROPS, xml);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        if ("bases".equals(prop.getName())) {
        	List<KrnObject> bases = null;
        	if (reportNode != null) {
        		bases = reportNode.getBases();
        	}
            Map<Long, String> m = new TreeMap<Long, String>();
            if (bases != null && bases.size() > 0) {
                for (int i = 0; i < bases.size(); i++) {
                    KrnObject base = (KrnObject)bases.get(i);
                    try {
                        String[] titles = Kernel.instance().getStrings(base, "наименование", 0, 0);
                        m.put(base.id, titles.length > 0 ? titles[0] : "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new PropertyValue(m, "Структура баз", prop);
        } else
            return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        if ("bases".equals(value.getProperty().getName())) {
            Map val = value.objectsValue();
            if (val != null && val.size() > 0) {
                Set keys = val.keySet();
                Iterator it = keys.iterator();
                long[] ids = new long[keys.size()];
                for (int i = 0; i < keys.size(); i++) {
                    ids[i] = (Long) it.next();
                }
                KrnObject[] oldVals = kz.tamur.rt.Utils.getObjectsByIds("Структура баз", ids);
                reportNode.setBases(oldVals);
            } else
                reportNode.setBases(null);
        } else {
            PropertyHelper.setPropertyValue(value, xml, frame);
            Utils.processStdCompProperties(this, value);
            PropertyNode prop = value.getProperty();
            if ("title".equals(prop.getName())) {
                setText(value.stringValue());
            } else if ("enabled".equals(prop.getName())) {
                setEnabled(value.booleanValue());
            }
        }
    }
    public PropertyValue getPropertyValue(String propId){
      PropertyValue  res = getPropertyValue(PROPS.getChild(propId));
    return res;
    }

    public void setPropertyValue(String propId,Object value){
        setPropertyValue(new PropertyValue(value,PROPS.getChild(propId)));
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public void setLangId(long langId) {
        updateProperties();
    }

    private void updateProperties() {
        PropertyValue pv = null;
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            setText(pv.stringValue());
        }
/*
        pv = getPropertyValue(getProperties().getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
*/
    }

    public int getMode() {
        return mode;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return null;
    }

    public Dimension getMaxSize() {
        return null;
    }

    public Dimension getMinSize() {
        return null;
    }

    public String getBorderTitleUID() {
        return null;
    }

    //
    public int getTabIndex() {
        return -1;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    public byte[] getDescription() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setReportNode(ReportNode reportNode) {
        this.reportNode = reportNode;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    public String getVarName() {
        return null;
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
    }  
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    
    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }
}
