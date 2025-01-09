package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.models.ButtonPropertyRoot;
import kz.tamur.comps.ui.button.OrButtonUI;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.comps.ui.ext.Timer;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import kz.tamur.rt.Utils;
import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.getExpReturn;
import static kz.tamur.comps.Utils.processStdCompProperties;
import static kz.tamur.comps.Utils.processBorderProperties;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OrButton extends OrTransparentButton implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new ButtonPropertyRoot();
    private static final PropertyNode FULL_PROPS = new ButtonPropertyRoot();
    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private String expression;
    private OrGuiContainer parent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    private String titleExpr = null;
    private String titleExprText = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());
    private int tabIndex;
    private boolean isHelpClick = false;
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private Map borderProps = new TreeMap();
    private String titleUID;
    private String descriptionUID;
    private byte[] description;
    private boolean isDefaultButton;
    private Color fontColor;
    private String base64Icon;
    private String varName;
    private boolean hasSetAttr = false;
    private int posIcon;
    private boolean showOnTopPan;
    private int positionOnTopPan;
    private boolean attention;
    private Timer timer;
    private boolean timerBg;
    private Color defBg;
    private MouseAdapter actL;
    
    OrButton(Element xml, int mode, OrFrame frame) {
        super("OrButton");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        actL = new MouseAdapter() {
            private void mouse(boolean isEnter) {
                if (isEnter) {
                    setForeground(Color.blue);
                    setCursor(Constants.HAND_CURSOR);
                } else {
                    setForeground(fontColor == null ? Color.black : fontColor);
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }

            public void mouseExited(MouseEvent e) {
                mouse(false);
            }

            public void mouseEntered(MouseEvent e) {
                mouse(true);
            }
        };
        if (mode == Mode.RUNTIME) {
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
        
        if (mode == DESIGN) {
            setEnabled(false);
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
	                            setToolTipText(toolTipExprText.trim());
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

        updateProperties();
        defBg = getUI().getStaticTopBg();
        if (mode == Mode.RUNTIME) {
            

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    timerBg = true;
                    getUI().setStaticTopBg(defBg);
                    timer.stop();
                }
            });
        
            
            timer = new Timer("flasher", 200, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (timerBg) {
                        timerBg = false;
                        getUI().setStaticTopBg(Color.RED);
                    } else {
                        timerBg = true;
                        getUI().setStaticTopBg(defBg);
                    }
                    repaint();
                    if (!attention) {
                        timerBg = true;
                        getUI().setStaticTopBg(defBg);
                        timer.stop();
                    }
                }
            });
        }
    }

    
    @Override
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
        processBorderProperties(this, frame);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("title".equals(name)) {
            Pair p = value.resourceStringValue();
            setText((String) p.second);
        } else if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            fontColor = value == null ? Color.BLACK : value.colorValue();
            setForeground(fontColor);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Utils.getLightSysColor() : value.colorValue();
            setBackground(val);
        } else if ("enabled".equals(name)) {
            setEnabled(value.booleanValue());
        } else if ("image".equals(name)) {
            byte[] b = value.getImageValue();
            ImageIcon icon = Utils.processCreateImage(b);
            setIcon(icon);
            if (b != null && b.length > 0) {
                base64Icon = new String(Base64.encode(b));
            }
        } else if ("alignmentText".equals(name)) {
            setHorizontalAlignment(value.intValue());
            PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                setText(frame.getString((String) p.first));
            }
        } else if ("opaque".equals(name)) {
            setTransparent(!value.booleanValue());
            setOpaque(value.booleanValue());
            repaint();
            if (!value.booleanValue()) {
                addMouseListener(actL);
            }else {
                removeMouseListener(actL);
            }
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
        } else if ("anchorImage".equals(prop.getName())) {
            posIcon = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage")).intValue();
            setIcon(getIcon());
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
                description = (byte[])p.second;
            }
        }
        processBorderProperties(this, frame);
    }

    public String getBase64Icon() {
    	return base64Icon;
    }

    private void updateProperties() {
        updateDynProp();
        processBorderProperties(this, frame);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String)p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        
        PropertyNode pn = PROPS.getChild("titleN").getChild("expr");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            titleExpr = pv.stringValue();
        }
        
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String)p.first;
            description = (byte[])p.second;
        }

        pn = PROPS.getChild("pos").getChild("anchorImage");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            posIcon = pv.intValue();
        } else {
            posIcon = GridBagConstraints.WEST;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        }
        
        
        pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        } else {
            setHorizontalAlignment(((Integer)pn.getChild("alignmentText").getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        
        pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            setIcon(Utils.processCreateImage(pv.getImageValue()));
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("foto");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                webNameIcon = name.toString();
                base64Icon = new String(Base64.encode(b)); // FIXME убрать!
            }
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForeground(fontColor);
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setTransparent(!pv.booleanValue());
            setOpaque(pv.booleanValue());
            repaint();
            if (!pv.booleanValue()) {
                addMouseListener(actL);
            }else {
                removeMouseListener(actL);
            }
        } else {
            setTransparent(false);
            setOpaque(true);
            repaint();
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }

        pv = getPropertyValue(pn.getChild("formula"));
        hasSetAttr = false;
        if (!pv.isNull()) {
            expression = pv.stringValue();
        	hasSetAttr = expression.contains("setAttr(");
        }

        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        }
        
        pv = getPropertyValue(pn.getChild("defaultButton"));
        if (!pv.isNull()) {
            isDefaultButton = pv.booleanValue();
        }
    }

    public int getMode() {
        return mode;
    }

    public String getExpression() {
        return expression;
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
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

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}

	
	public void setAdapter(ComponentAdapter adapter) {
	}
	
	public boolean isDefaultButton() {
		return isDefaultButton;
	}

	public String getVarName() {
		return varName;
	}
	
    public boolean hasSetAttr() {
    	return hasSetAttr;
    }
    
    void updateTitle() {
        if (titleExpr != null && !titleExpr.isEmpty()) {
            String titleExprText_ = getExpReturn(titleExpr, frame, getAdapter());
            if (titleExprText_ != null) {
                if (titleExprText_.isEmpty()) {
                	titleExprText_ = null;    
                }
                setText(titleExprText_);
                titleExprText = titleExprText_;
            }
        }
    }
    
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = getExpReturn(toolTipExpr, frame, getAdapter());
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

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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

    public String getToolTip() {
    	return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
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
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
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
       this.attention = attention;
       if (this.attention) {
           timer.start();
       }
    }
    
    @Override
    public OrButtonUI getUI() {
        return (OrButtonUI)super.getUI();
    }
}