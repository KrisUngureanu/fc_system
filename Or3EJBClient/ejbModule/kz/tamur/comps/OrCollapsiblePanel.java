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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import kz.tamur.comps.models.CollapsiblePanelPropertyRoot;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.CollapsiblePanel;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.CollapsiblePanelAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class OrCollapsiblePanel.
 * 
 * @author Lebedev Sergey
 */
public class OrCollapsiblePanel extends CollapsiblePanel implements OrGuiComponent, MouseTarget {

    /** Идентификатор компонента. */
    protected String UUID;
    /** XML компонента. */
    private Element xml;
    /** Режим выполнения компонента. */
    private int mode;
    /** frame. */
    private OrFrame frame;
    /** Фабрика компонентов. */
    private Factory fm;
    /** props. */
    public static PropertyNode PROPS = new CollapsiblePanelPropertyRoot();
    /** constraints. */
    private GridBagConstraints constraints;
    /** is selected. */
    private boolean isSelected;
    /** list listeners. */
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    /** The title uid. */
    private String titleUID;
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
    /** Адаптер . */
    private CollapsiblePanelAdapter adapter;
    /** is help click. */
    private boolean isHelpClick = false;
    /** Файловое имя иконки, используемое в web-интерфейсе. */
    private String webNameIcon;

    /**
     * Конструктор класса or collapsible panel.
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
    public OrCollapsiblePanel(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        super();
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        this.fm = fm;
        UUID = PropertyHelper.getUUID(this);
        init();
    }

    /**
     * Инициализация компонента.
     * 
     * @param fm
     *            Фабрика компонентов
     * @throws KrnException
     *             the krn exception
     */
    private void init() throws KrnException {
        if (mode == RUNTIME) {
            adapter = new CollapsiblePanelAdapter(frame, this, false);
            // всплывающая подсказка
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
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
        Element panel;
        PropertyValue pv = getPropertyValue(PROPS.getChild("panel"));
        boolean init = pv.isNull();
        if (!init) {
            List<Element> children = pv.elementValue().getChildren();
            panel = children.get(0);
        } else {
            panel = new Element("Component");
            panel.setAttribute("class", "Panel");
        }
        updateDefaultTitleComponent();
        updateProperties();
        OrGuiComponent c = fm.create(panel, mode, frame);
        c.setGuiParent(this.getGuiParent());
        setContent((Component)c);
        super.setOpaque(false);
        if (init) {
            initContent();
            PropertyHelper.addProperty(new PropertyValue(getContent().getXml(), PROPS.getChild("panel")), xml);
        }
        getContent().setDelete(false);
        addListenersChildren(getContent());
        if (mode == DESIGN) {
            MouseDelegator delegator = new MouseDelegator(this);
            getTitleComponent().addMouseListener(delegator);
            getTitleComponent().addMouseMotionListener(delegator);
            getHeaderPanel().addMouseListener(delegator);
            getHeaderPanel().addMouseMotionListener(delegator);
        }
    }

    /**
     * Inits the content.
     */
    private void initContent() {
        OrPanel panel = getContent();
        panel.setGuiParent(getGuiParent());
        PropertyNode pn = panel.getProperties().getChild("pos");
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("pref").getChild("width")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("pref").getChild("height")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("min").getChild("width")));
        panel.setPropertyValue(new PropertyValue(100, pn.getChild("min").getChild("height")));
        panel.setPropertyValue(new PropertyValue(GridBagConstraints.BOTH, pn.getChild("fill")));
    }

    /**
     * Update properties.
     */
    private void updateProperties() {
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        PropertyValue pv = null;
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        PropertyNode pn = PROPS.getChild("titleN").getChild("alignmentText");
        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue((EnumValue) pn.getDefaultValue(), pn));
        } else {
            setTitleAlignmentText(pv.enumValue());
        }

        pn = PROPS.getChild("titleN").getChild("titleAlign");
        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue((EnumValue) pn.getDefaultValue(), pn));
        } else {
            setTitlePanePostion(pv.enumValue());
        }
        pn = PROPS.getChild("titleN");
        pv = getPropertyValue(pn.getChild("icon"));
        if (!pv.isNull()) {
            setIcon(Utils.processCreateImage(pv.getImageValue()));
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("ico");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                webNameIcon = name.toString();
            }
        }
        pn = pn.getChild("font");
        pv = getPropertyValue(pn.getChild("fontG"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(Utils.getDefaultFont(), pn.getChild("fontG")));
        } else {
            setTitleFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("fontColor"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(Utils.getDefaultFontColor(), pn.getChild("fontColor")));
        } else {
            setTitleFontColor(pv.colorValue());
        }

        pv = getPropertyValue(PROPS.getChild("title"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(new Pair(null, "Collapsible Panel"), PROPS.getChild("title")));
        } else {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            setTitle(frame.getString(titleUID));
        }
        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
        pv = getPropertyValue(pn.getChild("expandAll"));
        if (!pv.isNull()) {
            if (pv.booleanValue()) {
                expand();
            } else {
                collapse();
            }
        }
        // градиентная заливка компонента
        pv = getPropertyValue(PROPS.getChild("gradient"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(new GradientColor("-1, -3355393, 1, 1, 0, 50, 1"), PROPS.getChild("gradient")));
        } else {
            setGradient((GradientColor) pv.objectValue());
        }
        pv = getPropertyValue(PROPS.getChild("transparent"));
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
     * Установить help click.
     * 
     * @param helpClick
     *            новое значение help click
     */
    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    /**
     * Получить web name icon.
     * 
     * @return the webNameIcon
     */
    public String getWebNameIcon() {
        return webNameIcon;
    }
    
    
    @Override
    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    
    @Override
    public void setSelected(boolean isSelected) {
        if (mode == DESIGN && isSelected) {
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
        return PROPS;
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
        if ("title".equals(name)) {
            Pair p = value.resourceStringValue();
            setTitle((String) p.second);
        } else if ("titleAlign".equals(name)) {
            setTitlePanePostion(value.enumValue());
        } else if ("fontG".equals(name)) {
            setTitleFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setTitleFontColor(val);
        } else if ("alignmentText".equals(name)) {
            setTitleAlignmentText(value.enumValue());
        } else if ("icon".equals(name)) {
            if (value == null || value.isNull()) {
                setIcon(null);
                webNameIcon = null;
            } else {
                byte[] b = value.getImageValue();
                setIcon(Utils.processCreateImage(b));
                if (b != null && b.length > 0) {
                    StringBuilder fileName = new StringBuilder();
                    fileName.append("ico");
                    kz.tamur.rt.Utils.getHash(b, fileName);
                    fileName.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                    webNameIcon = fileName.toString();
                }
            }
        } else if ("gradient".equals(name)) {
            setGradient(value == null || value.isNull() ? null : (GradientColor) value.objectValue());
        } else if ("transparent".equals(name)) {
            setOpaque(!value.booleanValue());
        }
    }

    
    @Override
    public void setOpaque(boolean isOpaque) {
        if (getHeaderPanel() != null) {
            getHeaderPanel().setOpaque(isOpaque);
        }
    }

    
    @Override
    public void setGradient(GradientColor gradient) {
        getHeaderPanel().setGradient(gradient);
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
        if (mode == RUNTIME) {
            setTitle(frame.getString(titleUID));
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        getContent().setLangId(langId);
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
        return super.getToolTipText();
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

    
    @Override
    public OrPanel getContent() {
        return (OrPanel) super.getContent();
    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    
    public kz.tamur.comps.ui.label.OrLabel getTitleComponent() {
        return (kz.tamur.comps.ui.label.OrLabel) super.getTitleComponent();
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
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        getHeaderPanel().setToolTipText(text);
        getTitleComponent().setToolTipText(text);
    }
    
    @Override
    public void setTitle(String title) {
        if (title.contains("@")){
            StringBuilder out = new StringBuilder();
            out.append("<html>").append(title.replaceAll("@", "<br>"));
            super.setTitle(out.toString());
        } else {
            super.setTitle(title);
        }
    }
}