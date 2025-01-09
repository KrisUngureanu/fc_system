package kz.tamur.comps;

import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Utils.getExpReturn;
import static kz.tamur.comps.Utils.processStdCompProperties;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.models.HyperPopupPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OrHyperPopup extends OrTransparentButton implements OrGuiComponent {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new HyperPopupPropertyRoot();
    private static final PropertyNode FULL_PROPS = new HyperPopupPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;

    private OrGuiContainer guiParent;
    /** Идентификатор строки с подсказкой */
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

    private boolean isClearBtnExists;
    private boolean isHelpClick = false;

    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private JLabel deleteLabel = new JLabel();
    private Color fontColor;
    private byte[] description;
    private String descriptionUID;
    private ImageIcon icon;
    private boolean iconVisible = true;
    private String varName;
    private int typeView = Constants.DIALOG;
    private int posIcon;
    private String base64Icon;
    private boolean showOnTopPan;
    private int positionOnTopPan;

    OrHyperPopup(Element xml, int mode, OrFrame frame, boolean isEditor) {
        super("OrHyperPopup");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);

        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
        PropertyValue pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            posIcon = pv.intValue();
        } else {
            posIcon = GridBagConstraints.EAST;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        }

        deleteLabel.setIcon(kz.tamur.rt.Utils.getImageIconExt("DeleteValue", ".png"));
        deleteLabel.setToolTipText("Удалить значение");
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        // description = PropertyHelper.getDescription(this);
        setMargin(Constants.INSETS_1);
        setLayout(new BorderLayout());
        add(deleteLabel, BorderLayout.EAST);
        if (mode == Mode.RUNTIME) {
            // Всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
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
        if (this.mode == DESIGN) {
            pv = getPropertyValue(PROPS.getChild("toolTip"));
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
                    if (toolTip != null && toolTip.length > 0) {
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
                            System.out.println("Ошибка при разборе ToolTip на компоненте " + UUID);
                            e.printStackTrace();
                        } catch (IOException e) {
                            System.out.println("Ошибка при разборе ToolTip на компоненте " + UUID);
                            e.printStackTrace();
                        }
                    }
                }
            }
            // setEditable(false);
            setEnabled(false);
        }

        // Тип отображаемого интерфейса
        pv = getPropertyValue(PROPS.getChild("pov").getChild("typeView"));
        if (pv.isNull()) {
            typeView = Constants.DIALOG;
            setPropertyValue(new PropertyValue(typeView, PROPS.getChild("pov").getChild("typeView")));
        } else {
            typeView = pv.intValue();
        }
        updateProperties();
        if (mode != Mode.DESIGN) {
            addMouseListener(new HyperMouseAdapter());
        } else {
            setEnabled(false);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

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

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        processStdCompProperties(this, value);
        // Utils.processBorderProperties(this, frame);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();

        if ("title".equals(name)) {
            Pair p = value.resourceStringValue();
            setText((String) p.second);
        } else if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            fontColor = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(fontColor);
        } else if ("image".equals(name)) {
            byte[] b = value.getImageValue();
            icon = Utils.processCreateImage(b);
            setIcon(iconVisible ? icon : null);
            if (b != null && b.length > 0) {
                base64Icon = new String(Base64.encode(b));
            }
        } else if ("opaque".equals(name)) {
            setTransparent(!value.booleanValue());
            setOpaque(value.booleanValue());
            repaint();
        } else if ("showIcon".equals(name)) {
            if (value.booleanValue()) {
                iconVisible = true;
                setIcon(icon);
            } else {
                iconVisible = false;
                setIcon(null);
            }
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Utils.getLightSysColor() : value.colorValue();
            setBackground(val);
        } else if ("alignmentText".equals(name)) {
            setHorizontalAlignment(value.intValue());
        } else if ("clearBtnShow".equals(name)) {
            isClearBtnExists = value.booleanValue();
            deleteLabel.setVisible(isClearBtnExists);
        } else if ("anchorImage".equals(prop.getName())) {
            posIcon = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage")).intValue();
            setIcon(getIcon());
        } else if ("toolTip".equals(name)) {
            if (value.isNull()) {
                toolTipUid = null;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null && toolTip.length > 0) {
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
        } else if ("positionOnTopPan".equals(prop.getName())) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(prop.getName())) {
            showOnTopPan = value.booleanValue();
        }
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

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
        // Utils.processBorderProperties(this, frame);
    }

    private void updateProperties() {
        updateDynProp();
        PropertyValue pv = null;
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            title = (String) p.second;
            titleUID = (String) p.first;
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }

        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForeground(fontColor);
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setTransparent(!pv.booleanValue());
            setOpaque(pv.booleanValue());
            repaint();
        } else {
            setTransparent(false);
            setOpaque(true);
            repaint();
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }
        pv = getPropertyValue(pn.getChild("showIcon"));
        if (!pv.isNull()) {
            iconVisible = pv.booleanValue();
        } else {
            setPropertyValue(new PropertyValue(iconVisible = true, pn.getChild("showIcon")));
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("image"));
        if (pv.isNull()) {
            if (mode == Mode.RUNTIME) {
                String iconName = MainFrame.iconsSettings.get("iconHyperPopup");
                icon = kz.tamur.rt.Utils.getImageIconFull(iconName);
                if (icon == null) {
                    icon = kz.tamur.rt.Utils.getImageIcon("VSlider");
                }
            } else {
                icon = kz.tamur.rt.Utils.getImageIcon("VSlider");
            }
        } else {
            byte[] b = pv.getImageValue();
            icon = Utils.processCreateImage(b);
            if (b != null && b.length > 0) {
                base64Icon = new String(Base64.encode(b));
            }
        }
        if (iconVisible) {
            setIcon(icon);
        }

        PropertyNode pnode = pn.getChild("alignmentText");
        pv = getPropertyValue(pnode);
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        } else {
            setHorizontalAlignment(((Integer) pnode.getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("clearBtnShow"));
        isClearBtnExists = pv.booleanValue();
        deleteLabel.setVisible(isClearBtnExists);
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
    }

    public int getMode() {
        return mode;
    }

    private class HyperMouseAdapter extends MouseAdapter {

        private void mouse(boolean isEnter) {
            if (!isHelpClick) {
                // isHelpClick = false;
                // } else {
                PropertyNode pn = getProperties().getChild("view").getChild("font");
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
                    if (fontColor != null) {
                        setForeground(fontColor);
                    } else {
                        setForeground(Color.black);
                    }
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            mouse(false);
            isHelpClick = false;
            super.mouseExited(e);
        }

        public void mouseEntered(MouseEvent e) {
            mouse(true);
            super.mouseEntered(e);
        }
    }

    public boolean isClearBtnExists() {
        return isClearBtnExists;
    }

    public String getBase64Icon() {
        return base64Icon;
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
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public String getBorderTitleUID() {
        return null;
    }

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

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        deleteLabel.setEnabled(getText().length() > 0 && isEnabled);
    }

    public void addDeleteMouseListener(MouseListener listener) {
        deleteLabel.addMouseListener(listener);
    }

    public void setCalculatedColorFont(Color c) {
        fontColor = c;
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public boolean isIconVisible() {
        return iconVisible;
    }

    public ComponentAdapter getAdapter() {
        return null;
    }

    public String getVarName() {
        return varName;
    }

    public void setValue(Object value) {
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

    public int getTypeView() {
        return typeView;
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

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    @Override
    public Container getTopLevelAncestor() {
        if (listListeners.size() == 0) {
            return super.getTopLevelAncestor();
        } else {
            return (Container) InterfaceManagerFactory.instance().getManager();
        }
    }

    @Override
    public void setIcon(Icon icon) {
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

    /**
     * Возвращает позицию иконки
     * 
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
    }

    /*
     * public String getToolTipText() {
     * return toolTipExprText;
     * }
     */
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
