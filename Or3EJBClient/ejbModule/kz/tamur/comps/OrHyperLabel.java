package kz.tamur.comps;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.HyperLabelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OrHyperLabel extends OrTransparentButton implements OrGuiComponent {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new HyperLabelPropertyRoot();
    private static final PropertyNode FULL_PROPS = new HyperLabelPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;

    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());
    private int tabIndex;
    private boolean isHelpClick = false;
    private boolean isBlockErrors = false;
    private boolean isArchiv = false;
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String title;
    private String titleUID;
    private byte[] description;
    private String descriptionUID;
    private String varName;
    private boolean visibleArrow;
    private int posIcon;
    private boolean showOnTopPan;
    private int positionOnTopPan;

    OrHyperLabel(Element xml, int mode, OrFrame frame, boolean isEditor) {
        super("OrHyperLabel");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);
        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
        PropertyValue pv = getPropertyValue(pn);
        if (pv.isNull()) {
            posIcon = GridBagConstraints.EAST;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        } else {
            posIcon = pv.intValue();
        }

        pn = PROPS.getChild("view").getChild("visibleArrow");
        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            visibleArrow = true;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        } else {
            visibleArrow = pv.booleanValue();
        }
        setContentAreaFilled(false);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        updateText();
        if (mode != Mode.DESIGN) {
            addMouseListener(new HyperMouseAdapter());
            addFocusListener(new DefaultFocusAdapter(this));
        }
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
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
        if (mode == Mode.DESIGN) {
            updateDynProp();
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
            	if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
	                toolTipUid = (String) pv.resourceStringValue().first;
	                byte[] toolTip = frame.getBytes(toolTipUid);
	                if (toolTip != null && toolTip.length > 0) {
	                    setToolTipText(new String(toolTip));
	                    SAXBuilder builder = new SAXBuilder();
	                    InputStream is = new ByteArrayInputStream(toolTip);
	                    try {
	                        Element var_doc = builder.build(is).getRootElement();
	                        if (var_doc.getName().equals("html")) {
	                            XMLOutputter outp = new XMLOutputter();
	                            outp.setFormat(Format.getCompactFormat());
	                            StringWriter sw = new StringWriter();
	                            outp.output(var_doc.getChild("body").getContent(), sw);
	                            StringBuffer sb = sw.getBuffer();
	                            toolTipContent = sb.toString();
	                            toolTipExprText = var_doc.getChild("body").getValue();
	                        }
	                    } catch (JDOMException e) {
	                        e.printStackTrace();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
                }
            }
            setEnabled(false);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    @Override
    public Element getXml() {
        return xml;
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
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
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("title".equals(name)) {
            setText((String) value.resourceStringValue().second);
        } else if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("image".equals(name)) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if (value == null || value.isNull()) {
                setIcon(kz.tamur.rt.Utils.getImageIcon("VSlider"));
                setWebNameIcon("VSlider.gif");
            } else {
                byte[] b = value.getImageValue();
                setIcon(Utils.processCreateImage(b));
                if (b != null && b.length > 0) {
                    StringBuilder fileName = new StringBuilder();
                    fileName.append("hlb");
                    kz.tamur.rt.Utils.getHash(b, fileName);
                    fileName.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                    webNameIcon = fileName.toString();
                }
            }
        } else if ("visibleArrow".equals(name)) {
            PropertyValue pv = getPropertyValue(PROPS.getChild("view").getChild("visibleArrow"));
            visibleArrow = pv.booleanValue();
            if (visibleArrow) {
                pv = getPropertyValue(PROPS.getChild("view").getChild("image"));
                setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                if (value == null || pv.isNull()) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("VSlider"));
                    setWebNameIcon("VSlider.gif");
                } else {
                    byte[] b = value.getImageValue();
                    setIcon(Utils.processCreateImage(b));
                    if (b != null && b.length > 0) {
                        StringBuilder fileName = new StringBuilder();
                        fileName.append("hlb");
                        kz.tamur.rt.Utils.getHash(b, fileName);
                        fileName.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                        setWebNameIcon(fileName.toString());
                    }

                }
            } else {
                super.setIcon(null);
                setWebNameIcon(null);
            }
        } else if ("anchorImage".equals(name)) {
            posIcon = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage")).intValue();
            setIcon(getIcon());
        } else if ("toolTip".equals(name)) {
            if (value == null || value.isNull()) {
                toolTipUid = null;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null && toolTip.length > 0) {
                    setToolTipText(new String(toolTip));
                    SAXBuilder builder = new SAXBuilder();
                    InputStream is = new ByteArrayInputStream(toolTip);
                    try {
                        Element var_doc = builder.build(is).getRootElement();
                        if (var_doc.getName().equals("html")) {
                            XMLOutputter outp = new XMLOutputter();
                            outp.setFormat(Format.getPrettyFormat());
                            StringWriter sw = new StringWriter();
                            outp.output(var_doc.getChild("body").getContent(), sw);
                            StringBuffer sb = sw.getBuffer();
                            toolTipContent = sb.toString();
                            toolTipExprText = var_doc.getChild("body").getValue();
                        }
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ("positionOnTopPan".equals(name)) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(name)) {
            showOnTopPan = value.booleanValue();
        }
    }

    @Override
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    @Override
    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            title = frame.getString(titleUID);
            setText(title);
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
            if (!pv.isNull()) {
                setText(pv.stringValue());
            }
            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
    }

    private void updateText() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        PropertyValue pv = null;
        pv = getPropertyValue(PROPS.getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            title = (String) p.second;
            titleUID = (String) p.first;
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        PropertyNode pn = PROPS.getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("image"));
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        if (pv.isNull()) {
            setIcon(kz.tamur.rt.Utils.getImageIcon("VSlider"));
            webNameIcon = "VSlider.gif";
        } else {
            byte[] b = pv.getImageValue();
            setIcon(Utils.processCreateImage(b));
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("hlb");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                webNameIcon = name.toString();
            }
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("isBlockErrors"));
        if (!pv.isNull()) {
            isBlockErrors = pv.booleanValue();
        }
        pv = getPropertyValue(pn.getChild("activity").getChild("isArchiv"));
        if (pv.isNull()) {
            isArchiv = ((Boolean) pn.getChild("editIfc").getDefaultValue()).booleanValue();
        } else {
            isArchiv = pv.booleanValue();
        }

        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();

        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }
    }

    @Override
    public int getMode() {
        return mode;
    }

    public boolean isBlockErrors() {
        return isBlockErrors;
    }

    private class HyperMouseAdapter extends MouseAdapter {

        private void mouse(boolean isEnter) {
            if (!isHelpClick) {
                PropertyNode pn = PROPS.getChild("view").getChild("font");
                PropertyValue pv = null;
                if (isEnter) {
                    pv = getPropertyValue(pn.getChild("lightFontColor"));
                    if (!pv.isNull()) {
                        setForeground(pv.colorValue());
                    } else {
                        setForeground(Color.blue);
                    }
                    setCursor(Constants.HAND_CURSOR);
                } else {
                    pv = getPropertyValue(pn.getChild("fontColor"));
                    if (!pv.isNull()) {
                        setForeground(pv.colorValue());
                    } else {
                        setForeground(Color.black);
                    }
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            mouse(false);
            super.mouseExited(e);
        }

        public void mouseEntered(MouseEvent e) {
            mouse(true);
            super.mouseEntered(e);
        }
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    @Override
    public void setXml(Element xml) {
        this.xml = xml;
    }

    @Override
    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    @Override
    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return tabIndex;
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

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public boolean isArchiv() {
        return isArchiv;
    }

    @Override
    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    @Override
    public String getVarName() {
        return varName;
    }

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

    @Override
    public void setIcon(Icon icon) {
        if (visibleArrow) {
            switch (posIcon) {
            case GridBagConstraints.EAST: // иконка справа
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.LEFT);
                break;
            case GridBagConstraints.WEST: // иконка Слева
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.RIGHT);
                break;
            case GridBagConstraints.NORTH: // иконка Сверху
                setVerticalTextPosition(SwingConstants.BOTTOM);
                setHorizontalTextPosition(SwingConstants.CENTER);
                break;
            case GridBagConstraints.SOUTH: // иконка Снизу
                setVerticalTextPosition(SwingConstants.TOP);
                setHorizontalTextPosition(SwingConstants.CENTER);
                break;
            }
            super.setIcon(icon);
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
    public void setListListeners(java.util.List<OrGuiComponent> listListeners, java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
    }

    /**
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
    }

    /**
     * @return the visibleArrow
     */
    public boolean isVisibleArrow() {
        return visibleArrow;
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    public String getToolTip() {
        return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
    }

    @Override
    public void updateDynProp() {
        if (mode != Mode.RUNTIME) {
            showOnTopPan = getPropertyValue(PROPS.getChild("web").getChild("showOnTopPan")).booleanValue();
            PropertyNode dependentNode = FULL_PROPS.getChild("web").getChild("positionOnTopPan");
            if (showOnTopPan) {
                PropertyNode node = PROPS.getChild("web").getChild("positionOnTopPan");
                if (node == null) {
                    PROPS.getChild("web").addChild(dependentNode);
                }
                positionOnTopPan = getPropertyValue(dependentNode).intValue();
            } else {
                PROPS.getChild("web").removeChild("positionOnTopPan");
            }
        }
    }

    /**
     * @return the showOnTopPan
     */
    @Override
    public boolean isShowOnTopPan() {
        return showOnTopPan;
    }

    /**
     * @return the positionOnTopPan
     */
    @Override
    public int getPositionOnTopPan() {
        return positionOnTopPan;
    }

    @Override
    public void setAttention(boolean attention) {
    }
}
