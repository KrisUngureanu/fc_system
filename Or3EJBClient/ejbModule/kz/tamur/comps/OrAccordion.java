package kz.tamur.comps;

import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.getExpReturn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import kz.tamur.comps.models.AccordionPropertyRoot;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.PropertyReestr;
import kz.tamur.comps.models.Types;
import kz.tamur.comps.ui.Accordion;
import kz.tamur.comps.ui.CollapsiblePanel;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.AccordionAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class OrAccordion.
 * 
 * @author Lebedev Sergey
 */
public class OrAccordion extends Accordion implements OrGuiComponent, MouseTarget {

    /** uuid. */
    protected String UUID;

    /** xml. */
    private Element xml;

    /** mode. */
    private int mode;

    /** frame. */
    private OrFrame frame;

    /** props. */
    private PropertyNode props = new AccordionPropertyRoot();

    /** prop title. */
    private PropertyNode propTitle = props.getChild("titleN");

    /** constraints. */
    private GridBagConstraints constraints;

    /** is selected. */
    private boolean isSelected;

    /** list listeners. */
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();

    /** listeners. */
    private EventListenerList listeners = new EventListenerList();

    /** titles. */
    private Map<Integer, String> titles = new HashMap<Integer, String>();
    /** The description. */
    private byte[] description;
    /** The description uid. */
    private String descriptionUID;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    /** The is copy. */
    private boolean isCopy;
    /** The standart border. */
    private Border standartBorder;

    /** The copy border. */
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());

    /** gui parent. */
    private OrGuiContainer guiParent;
    /** The var name. */
    private String varName;

    /** adapter. */
    private AccordionAdapter adapter;

    /** count panel. */
    private int countPanel = 0;

    /** fm. */
    private Factory fm;

    /** starting. */
    private boolean starting;

    private Map<Integer, String> webNameIcons = new HashMap<Integer, String>();

    /**
     * Конструктор класса or accordion.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param fm
     *            the fm
     * @param frame
     *            the frame
     * @throws KrnException
     *             the krn exception
     */
    public OrAccordion(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        this.fm = fm;
        UUID = PropertyHelper.getUUID(this);
        init();
        if (mode == RUNTIME) {
            this.xml = null;
        }
    }

    /**
     * Inits the.
     * 
     * @throws KrnException
     *             the krn exception
     */
    private void init() throws KrnException {
        if (mode == RUNTIME) {
            adapter = new AccordionAdapter(frame, this, false);
            // всплывающая подсказка
            PropertyValue pv = getPropertyValue(props.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = getExpReturn(toolTipExpr, frame, getAdapter());
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
        setOpaque(false);
        if (mode == DESIGN) {
            MouseDelegator delegator = new MouseDelegator(this);
            List<CollapsiblePanel> panels = getPanes();
            for (CollapsiblePanel panel : panels) {
                panel.getTitleComponent().addMouseListener(delegator);
                panel.getTitleComponent().addMouseMotionListener(delegator);
                panel.getHeaderPanel().addMouseListener(delegator);
                panel.getHeaderPanel().addMouseMotionListener(delegator);
            }
        }
    }

    /**
     * Inits the content.
     * 
     * @param panel
     *            the panel
     */
    private void initContent(OrPanel panel) {
        panel.setGuiParent(getGuiParent());
        PropertyNode pn = panel.getProperties().getChild("pos");
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("pref").getChild("width")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("pref").getChild("height")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("min").getChild("width")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("min").getChild("height")));
        panel.setPropertyValue(new PropertyValue(GridBagConstraints.BOTH, pn.getChild("fill")));
    }

    /**
     * Expand.
     */
    public void expand() {
        // TODO СДЕЛАТЬ!
    }

    /**
     * Получить content.
     * 
     * @return content
     */
    public List<OrPanel> getContent() {
        List<CollapsiblePanel> cPanels = getPanes();
        List<OrPanel> panels = null;
        if (cPanels != null && cPanels.size() > 0) {
            panels = new ArrayList<OrPanel>();
            for (CollapsiblePanel panel : cPanels) {
                panels.add((OrPanel) panel.getContent());
            }
        }
        return panels;
    }

    /**
     * Fire property modified.
     */
    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener) list[i]).propertyModified(this);
        }
    }

    /**
     * Adds the listeners children.
     * 
     * @param root
     *            the root
     */
    private void addListenersChildren(Component root) {
        if (root instanceof OrGuiComponent) {
            OrGuiComponent comp = (OrGuiComponent) root;
            comp.setComponentChange(this);
        }
        Component[] comps = ((JComponent) root).getComponents();
        if (comps != null) {
            for (Component comp : comps) {
                if (comp != null && comp instanceof JComponent) {
                    addListenersChildren(comp);
                }
            }
        }
    }

    /**
     * Update properties.
     */
    private void updateProperties() {
        starting = true;
        updateDynProp();
        starting = false;
        constraints = PropertyHelper.getConstraints(props, xml);
        PropertyValue pv = null;
        pv = getPropertyValue(props.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(props.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        PropertyNode pn = propTitle.getChild("orientation");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setOrientation(pv.enumValue());
        } else {
            setPropertyValue(new PropertyValue((EnumValue) pn.getDefaultValue(), pn));
        }
        List<CollapsiblePanel> panes = getPanes();
        pn = propTitle.getChild("font");
        pv = getPropertyValue(pn.getChild("fontG"));
        if (!pv.isNull()) {
            for (CollapsiblePanel panel : panes) {
                panel.setTitleFont(pv.fontValue());
            }
        } else {
            setPropertyValue(new PropertyValue(Utils.getDefaultFont(), pn.getChild("fontG")));
        }

        pv = getPropertyValue(pn.getChild("fontColor"));
        if (!pv.isNull()) {
            for (CollapsiblePanel panel : panes) {
                panel.setTitleFontColor(pv.colorValue());
            }
        } else {
            setPropertyValue(new PropertyValue(Utils.getDefaultFontColor(), pn.getChild("fontColor")));
        }
        pn = propTitle.getChild("alignmentText");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            for (CollapsiblePanel panel : panes) {
                panel.setTitleAlignmentText(pv.enumValue());
            }
        } else {
            setPropertyValue(new PropertyValue((EnumValue) pn.getDefaultValue(), pn));
        }

        pn = props.getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
        setMultiplySelectionAllowed(getPropertyValue(pn.getChild("multiselection")).booleanValue());
        pv = getPropertyValue(pn.getChild("expandPanel"));
        if (pv.isNull()) {
            List<CollapsiblePanel> panels = getPanes();
            for (CollapsiblePanel panel : panels) {
                panel.collapse();
            }
        } else {
            getPanelAt(pv.enumValue());
            updateExpandState(pv.enumValue());
        }
        // градиентная заливка компонента
        pv = getPropertyValue(props.getChild("gradient"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(new GradientColor("-1, -3355393, 1, 1, 0, 50, 1"), props.getChild("gradient")));
        } else {
            setGradient((GradientColor) pv.objectValue());
        }
        pv = getPropertyValue(props.getChild("transparent"));
        if (!pv.isNull()) {
            // прозрачность компонента(да/нет)
            setOpaque(!pv.booleanValue());
        }

    }

    /**
     * Update tool tip.
     */
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }

    public Map<Integer, String> getWebNameIcons() {
        return webNameIcons;
    }

    @Override
    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(props, xml);
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

    @Override
    public PropertyNode getProperties() {
        return props;
    }

    
    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    
    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        String name = value.getProperty().getName();
        if (name.contains("title_")) {
            int index = Integer.parseInt(name.substring(name.indexOf("_") + 1));
            Pair p = value.resourceStringValue();
            setTitleAt(index, (String) p.second);
        } else if (name.contains("icon_")) {
            int index = Integer.parseInt(name.substring(name.indexOf("_") + 1));
            if (value == null || value.isNull()) {
                setIconAt(index, null);
                webNameIcons.put(index, null);
            } else {
                byte[] b = value.getImageValue();
                ImageIcon icon = Utils.processCreateImage(b);
                setIconAt(index, icon);
                if (b != null && b.length > 0) {
                    StringBuilder fileName = new StringBuilder();
                    fileName.append("ico");
                    kz.tamur.rt.Utils.getHash(b, fileName);
                    fileName.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                    webNameIcons.put(index, fileName.toString());
                }
            }
        } else if ("orientation".equals(name)) {
            setOrientation(value.enumValue());
        } else if ("fontG".equals(name)) {
            List<CollapsiblePanel> panes = getPanes();
            for (CollapsiblePanel panel : panes) {
                panel.setTitleFont(value.fontValue());
            }
        } else if ("fontColor".equals(name)) {
            List<CollapsiblePanel> panes = getPanes();
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            for (CollapsiblePanel panel : panes) {
                panel.setTitleFontColor(val);
            }
        } else if ("alignmentText".equals(name)) {
            List<CollapsiblePanel> panes = getPanes();
            for (CollapsiblePanel panel : panes) {
                panel.setTitleAlignmentText(value.enumValue());
            }
        } else if ("multiselection".equals(name)) {
            setMultiplySelectionAllowed(value.booleanValue());
        } else if ("gradient".equals(name)) {
            setGradient(value == null || value.isNull() ? null : (GradientColor) value.objectValue());
        } else if ("transparent".equals(name)) {
            setOpaque(!value.booleanValue());
        }
    }

    
    @Override
    public void setOpaque(boolean isOpaque) {
        List<CollapsiblePanel> panels = getPanes();
        if (panels != null && panels.size() != 0) {
            for (int i = 0; i < panels.size(); i++) {
                CollapsiblePanel panel = panels.get(i);
                if (panel.getHeaderPanel() != null) {
                    panel.getHeaderPanel().setOpaque(isOpaque);
                }
            }
        }
    }

    
    @Override
    public void setGradient(GradientColor gradient) {
        List<CollapsiblePanel> panels = getPanes();
        if (panels != null && panels.size() != 0) {
            for (int i = 0; i < panels.size(); i++) {
                CollapsiblePanel panel = panels.get(i);
                if (panel.getHeaderPanel() != null) {
                    panel.getHeaderPanel().setGradient(gradient);
                }
            }
        }
    }

    
    @Override
    public Element getXml() {
        return xml;
    }

    
    @Override
    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    
    @Override
    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        } else {
            PropertyValue pv = getPropertyValue(props.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        List<CollapsiblePanel> panels = getPanes();
        for (int i = 0; i < panels.size(); i++) {
            CollapsiblePanel panel = panels.get(i);
            if (mode == Mode.RUNTIME || mode == Mode.DESIGN) {
                setTitleAt(i, frame.getString(titles.get(i)));
            }
            ((OrPanel) panel.getContent()).setLangId(langId);
        }
    }

    
    @Override
    public int getMode() {
        return mode;
    }

    
    @Override
    public boolean isCopy() {
        return isCopy;
    }

    
    @Override
    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    
    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    
    @Override
    public void setGuiParent(OrGuiContainer parent) {
        guiParent = parent;

    }

    
    @Override
    public void setXml(Element xml) {
        this.xml = xml;
    }

    
    @Override
    public Dimension getPrefSize() {
        return null;
    }

    
    @Override
    public Dimension getMaxSize() {
        return null;
    }

    
    @Override
    public Dimension getMinSize() {
        return null;
    }

    
    @Override
    public String getUUID() {
        return UUID;
    }

    
    @Override
    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    
    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    
    @Override
    public String getVarName() {
        return varName;
    }

    
    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }

    
    @Override
    public void setListListeners(List<OrGuiComponent> listListeners, List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setListListeners(listListeners, listForDel);
            }
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
        PropertyValue pv = getPropertyValue(propTitle.getChild("countPanel"));
        int newCount;
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(3, propTitle.getChild("countPanel")));
            newCount = 3;
        } else {
            newCount = pv.enumValue();
        }

        List<Element> ePanels;
        pv = getPropertyValue(props.getChild("panels"));
        boolean init = pv.isNull();
        boolean isAdd = init || !starting;
        ePanels = init ? new ArrayList<Element>(newCount) : pv.elementValue().getChildren();
        String iS;
        if (newCount > countPanel) {
            PropertyNode t;
            String tnn;
            String dtnn;
            String inn;
            String tc;
            Element element;
            OrPanel panel;
            String titleUID = null;
            for (int i = countPanel; i < newCount; i++) {
                iS = String.valueOf(i);
                tnn = "title_" + iS;
                dtnn = "dynamicTitle_" + iS;
                inn = "icon_" + iS;
                t = new PropertyNode(propTitle, tnn, Types.RSTRING, null, false, null);
                PropertyReestr.registerProperty(t);
                new PropertyNode(propTitle, dtnn, Types.EXPR, null, false, null);
                new PropertyNode(propTitle, inn, Types.IMAGE, null, false, null);
                if (isAdd) {
                    tc = "Панель " + iS;
                    element = new Element("Component").setAttribute("class", "Panel");
                    ePanels.add(element);
                } else {
                    Pair p = getPropertyValue(propTitle.getChild(tnn)).resourceStringValue();
                    titleUID = (String) p.first;
                    tc = frame.getString(titleUID);
                    titles.put(i, titleUID);
                    element = ePanels.get(i);
                }
                try {
                    panel = (OrPanel) fm.create(element, mode, frame);
                    panel.setGuiParent(this.getGuiParent());
                    panel.setDelete(false);
                    if (isAdd) {
                        initContent(panel);
                    }
                    addListenersChildren(panel);
                    addPane(tc, (Component) panel);
                    getPanelAt(i).setOpaque(false);
                    if (init) {
                        PropertyHelper.addProperty(new PropertyValue(panel.getXml(), props.getChild("panels")), xml);
                        setPreferredSize(new Dimension(50, 50));
                    }
                    pv = getPropertyValue(propTitle.getChild(inn));
                    if (!pv.isNull()) {
                        byte[] b = pv.getImageValue();
                        ImageIcon icon = Utils.processCreateImage(b);
                        setIconAt(i, icon);
                        if (b != null && b.length > 0) {
                            StringBuilder fileName = new StringBuilder();
                            fileName.append("ico");
                            kz.tamur.rt.Utils.getHash(b, fileName);
                            fileName.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                            webNameIcons.put(i, fileName.toString());
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                if (mode == Mode.DESIGN) {
                    setPropertyValue(new PropertyValue(new Pair(titleUID, tc), propTitle.getChild(tnn)));
                }
            }
        } else if (newCount < countPanel) {
            for (int i = countPanel-1; i >= newCount; i--) {
                iS = String.valueOf(i);
                PropertyReestr.unRegisterProperty(propTitle.getChild("title_" + iS).getFullPath());
                propTitle.removeChild("title_" + iS);
                propTitle.removeChild("dynamicTitle_" + iS);
                propTitle.removeChild("icon_" + iS);
                PropertyHelper.removeProperty(new PropertyValue(((OrPanel) getContentAt(i)).getXml(), props.getChild("panels")), xml);
                removePane(i);
            }
        } else {
            return;
        }
        countPanel = newCount;
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

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    
    @Override
    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    
    @Override
    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }
    @Override
    public void setTitleAt(int index, String title) {
        if (title.contains("@")){
            StringBuilder out = new StringBuilder();
            out.append("<html>").append(title.replaceAll("@", "<br>"));
            super.setTitleAt(index, out.toString());
        } else {
            super.setTitleAt(index, title);
        }
    }
}
