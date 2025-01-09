package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SliderPropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.AttributeTypeChecker;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class OrSliderField extends JSlider implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new SliderPropertyRoot();
    private int mode;
    private Element xml;
    private boolean isSelected;
    private OrFrame frame;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
	private String varName;

    OrSliderField(Element xml, int mode, OrFrame frame) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
               PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
               if (!pv.isNull()) {
                   if (pv.objectValue() instanceof Expression) {
                       try {
                           toolTipExpr = ((Expression) pv.objectValue()).text;
                           toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                           if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                               setToolTipText(toolTipExprText);
                           }
                       } catch (Exception e) {
                           System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                       }
                   } else {
                       toolTipUid = (String) pv.resourceStringValue().first;
                       byte[] toolTip = frame.getBytes(toolTipUid);
                       if (toolTip != null) {
                           setToolTipText(new String(toolTip));
                       }
                   }
               } 
               addMouseListener(new MouseAdapter() {
                   public void mouseEntered(MouseEvent e) {
                       updateToolTip();
                   }
               });
           }
        updateProperties();
        if (this.mode == Mode.DESIGN) {
            setEnabled(false);
        }
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public GridBagConstraints getConstraints() {
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml);
        }
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
        PropertyNode prop = value.getProperty();
        if ("data".equals(prop.getName())) {
            AttributeTypeChecker.instance().check(value, new long[] { AttributeTypeChecker.INTEGER_TYPE });
        }
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        String name = prop.getName();
        if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Color.WHITE : value.colorValue();
            setBackground(val);
        }
        updateProperties();
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        }
    }

    private void updateProperties() {
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
/*
        pn = getProperties().getChild("border").getChild("borderType");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setBorder(pv.borderValue());
        } else {
            setBorder((Border)pn.getDefaultValue());
        }
*/
        pn = PROPS.getChild("pov").getChild("slider").getChild("sliderOrientation");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setOrientation(pv.intValue());
        }
        pn = PROPS.getChild("pov").getChild("slider").getChild("sliderLabels");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setPaintLabels(pv.booleanValue());
        }
        pn = PROPS.getChild("pov").getChild("slider").getChild("sliderTicks");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setPaintTicks(pv.booleanValue());
        }
        pn = PROPS.getChild("pov").getChild("slider").getChild("sliderSnapTicks");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setSnapToTicks(pv.booleanValue());
        }
        pn = PROPS.getChild("pov").getChild("slider").getChild("sliderTrack");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setPaintTrack(pv.booleanValue());
        }
        pn = PROPS.getChild("pov").getChild("slider").getChild("ticks");
        pv = getPropertyValue(pn.getChild("sliderMin"));
        if (!pv.isNull()) {
            setMinimum(pv.intValue());
        }
        pv = getPropertyValue(pn.getChild("sliderMax"));
        if (!pv.isNull()) {
            setMaximum(pv.intValue());
        }
        pv = getPropertyValue(pn.getChild("sliderStepMin"));
        if (!pv.isNull()) {
            setMinorTickSpacing(pv.intValue());
        }
        pv = getPropertyValue(pn.getChild("sliderStepMax"));
        if (!pv.isNull()) {
            setMajorTickSpacing(pv.intValue());
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(getProperties().getChild(
                    "pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
            pn = getProperties().getChild("pov");
            pv = getPropertyValue(pn.getChild("tabIndex"));
            if (!pv.isNull()) {
                tabIndex = pv.intValue();
            } else {
                tabIndex = pv.intValue();
            }
        }
        //Utils.processBorderProperties(this, langId);
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
        if (mode == Mode.RUNTIME) {
            return prefSize;
        } else {
            return PropertyHelper.getPreferredSize(this);
        }
    }

    public Dimension getMaxSize() {
        if (mode == Mode.RUNTIME) {
            return maxSize;
        } else {
            return PropertyHelper.getMaximumSize(this);
        }
    }

    public Dimension getMinSize() {
        if (mode == Mode.RUNTIME) {
            return minSize;
        } else {
            return PropertyHelper.getMinimumSize(this);
        }
    }


    //
    public int getTabIndex() {
        return tabIndex;
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

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    public String getVarName() {
        return varName;
    }

    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
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
