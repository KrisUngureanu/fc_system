package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SpacerPropertyRoot;
import kz.tamur.rt.adapters.ComponentAdapter;

import org.jdom.Element;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Spacer extends JLabel implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int STEP = 5;

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrFrame frame;
    private int type;
    private OrGuiContainer guiParent;
	private String varName;
    public static PropertyNode PROPS = new SpacerPropertyRoot();

    Spacer(Element xml, int type, int mode, OrFrame frame) {
        super(" ");
        this.xml = xml;
        this.type = type;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        PropertyNode ps = PROPS.getChild("pos");
        if (type == HORIZONTAL) {
            setPropertyValue(new PropertyValue(
                    GridBagConstraints.HORIZONTAL, ps.getChild("fill")));
            PropertyNode pn = ps.getChild("weightx");
            pv = getPropertyValue(pn);
            if (pv.isNull()) {
                setPropertyValue(new PropertyValue(1, pn));
            }
            setPropertyValue(new PropertyValue(15, ps.getChild("pref").getChild("height")));
            setPropertyValue(new PropertyValue(15, ps.getChild("max").getChild("height")));
            setPropertyValue(new PropertyValue(15, ps.getChild("min").getChild("height")));
            setPreferredSize(new Dimension(getPreferredSize().width, 10));
            setMaximumSize(new Dimension(getMaximumSize().width, 10));
            setMinimumSize(new Dimension(getMinimumSize().width, 10));
        } else {
            setPropertyValue(new PropertyValue(
                    GridBagConstraints.VERTICAL, ps.getChild("fill")));
            PropertyNode pn = ps.getChild("weighty");
            pv = getPropertyValue(pn);
            if (pv.isNull()) {
                setPropertyValue(new PropertyValue(1, pn));
            }
            setPropertyValue(new PropertyValue(15, ps.getChild("pref").getChild("width")));
            setPropertyValue(new PropertyValue(15, ps.getChild("max").getChild("width")));
            setPropertyValue(new PropertyValue(15, ps.getChild("min").getChild("width")));
            setPreferredSize(new Dimension(10, getPreferredSize().height));
            setMaximumSize(new Dimension(10, getMaximumSize().height));
            setMinimumSize(new Dimension(10, getMinimumSize().height));
        }
        if (mode == Mode.RUNTIME) {
            setFocusable(false);
        } else {
            setFocusable(true);
        }
    }

    public int getType() {
        return type;
    }

    public void paint(Graphics g) {
        if (mode == Mode.DESIGN) {
            g.setColor(Color.gray);
            int w = 0;
            int h = 0;
            if (type == HORIZONTAL) {
                w = getWidth();
                h = getHeight();
                //g.drawLine(0, h / 2 - 1, w - 1, h / 2 - 1);
                g.fillRect(0, 0, 4, h);
                g.fillRect(w - 4, 0, 4, h);
            } else {
                h = getWidth();
                w = getHeight();
                //g.drawLine(h / 2 - 1, 0, h / 2 - 1, w - 1);
                g.fillRect(0, 0, h, 4);
                g.fillRect(0, w - 4, h, 4);
            }
            int[] p = {0, 0};
            int[] n = {0, 0};
            int i = 0;
            for (i = 0; i <= w / STEP; i++) {
                n[0] = i * STEP - 1;
                n[1] =  (i % 2 == 0) ? h - 1 : 0;
                if (type == HORIZONTAL) {
                    g.drawLine(p[0], p[1], n[0], n[1]);
                } else {
                    g.drawLine(p[1], p[0], n[1], n[0]);
                }
                p[0] = n[0];
                p[1] = n[1];
            }
            n[0] += w % (STEP);
            n[1] =  (i % 2 == 0) ? h : 0;
            if (type == HORIZONTAL) {
                g.drawLine(p[0], p[1], n[0], n[1]);
            } else {
                g.drawLine(p[1], p[0], n[1], n[0]);
            }
            if (isSelected) {
                kz.tamur.rt.Utils.drawRects(this, g);
            }
        } else {
            super.paint(g);
        }
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

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        if ("title".equals(prop.getName())) {
            setText(value.stringValue());
        }
    }

    public Element getXml() {
        return xml;
    }

    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    public void setLangId(long langId) {
        updateText();
    }

    public int getMode() {
        return mode;
    }

    //For copy process
    public boolean isCopy() {
        return false;
    }

    public void setCopy(boolean copy) {

    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        guiParent = parent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    //
    public int getTabIndex() {
        return -1;
    }

    private void updateText() {
        PropertyValue pv = null;
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            setText(pv.stringValue());
        }
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

    public byte[] getDescription() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}

    public String getVarName() {
        return varName;
    }

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
    public void setAttention(boolean attention) {}
}
